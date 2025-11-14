import java.io.*;
import java.net.*;
import java.util.*;

public class Roteador_2 {
    private String ip;
    private Map<String, Rota> tabela_roteamento = new HashMap<>();
    private Set<String> vizinhos = new HashSet<>();
    private Map<String, Long> ultima_mensagem = new HashMap<>();
    private DatagramSocket socket;

    static class Rota {
        String destino;
        String saida;
        int metrica;

        Rota(String destino, int metrica, String saida) {
            this.destino = destino;
            this.metrica = metrica;
            this.saida = saida;
        }
    }

    public Roteador_2(String ip_roteador) throws Exception {
        this.ip = ip_roteador;
        socket = new DatagramSocket(6000);
        socket.setBroadcast(true);
        socket.setReuseAddress(true);
        System.out.println("Roteador: " + ip);
    }

    void carregaARquivo() throws Exception {
        Scanner scan = new Scanner(new File("roteadores.txt"));
        while (scan.hasNextLine()) {
            String vizinho = scan.nextLine().trim();
            if (!vizinho.isEmpty() && !vizinho.equals(ip)) {
                vizinhos.add(vizinho);
                tabela_roteamento.put(vizinho, new Rota(vizinho, 1, vizinho));
                ultima_mensagem.put(vizinho, System.currentTimeMillis());
            }
        }
        scan.close();
        printTabelaRoteamento();
    }

    void enviar(String msg, String destino) {
        try {
            byte[] dados = msg.getBytes();
            socket.send(new DatagramPacket(dados, dados.length,
                    InetAddress.getByName(destino), 6000));
        } catch (Exception e) {
        }
    }

    void enviarTabelaRoteamento(String vizinho) {
        StringBuilder msg = new StringBuilder();

        // Sempre inclui a si mesmo na tabela (métrica 0)
        msg.append("*").append(ip).append(";0");

        // Adiciona outras rotas (com Split Horizon)
        for (Rota rota : tabela_roteamento.values()) {
            if (!rota.saida.equals(vizinho))
                msg.append("*").append(rota.destino).append(";").append(rota.metrica);
        }

        if (msg.length() > 0)
            enviar(msg.toString(), vizinho);
    }

    void processar(String msg, String origem) {
        // Atualiza ultima_mensagem para QUALQUER mensagem recebida
        ultima_mensagem.put(origem, System.currentTimeMillis());

        if (msg.startsWith("@")) {
            String novo_ip = msg.substring(1);
            if (!tabela_roteamento.containsKey(novo_ip)) {
                System.out.println("\nRoteador Adicionado a Rede: " + novo_ip);
                tabela_roteamento.put(novo_ip, new Rota(novo_ip, 1, novo_ip));
                vizinhos.add(novo_ip);
                printTabelaRoteamento();
                vizinhos.forEach(this::enviarTabelaRoteamento);
            } else {
                // Mesmo que já conheça, responde com tabela (pode ser reconexão)
                System.out.println("\nRoteador Reconectado: " + novo_ip);
                enviarTabelaRoteamento(novo_ip);
            }
        } else if (msg.startsWith("*")) {
            boolean mudou = false;
            Set<String> recebidas = new HashSet<>();

            for (String parte : msg.split("\\*")) {
                if (parte.isEmpty())
                    continue;
                String[] dados = parte.split(";");
                String dest = dados[0];
                int met = Integer.parseInt(dados[1]) + 1;
                recebidas.add(dest);

                // Ignora rotas para si mesmo
                if (dest.equals(ip))
                    continue;

                Rota atual = tabela_roteamento.get(dest);
                if (atual == null) {
                    System.out.println("Nova Rota adicionada: " + dest);
                    tabela_roteamento.put(dest, new Rota(dest, met, origem));
                    mudou = true;
                } else if (met < atual.metrica) {
                    System.out.println("Rota Atualizada: " + dest + " (" +
                            atual.metrica + "->" + met + ")");
                    tabela_roteamento.put(dest, new Rota(dest, met, origem));
                    mudou = true;
                }
            }

            // Verifica se uma rota não foi anunciada
            Iterator<Map.Entry<String, Rota>> aux = tabela_roteamento.entrySet().iterator();
            while (aux.hasNext()) {
                Map.Entry<String, Rota> rotas = aux.next();
                if (rotas.getValue().saida.equals(origem) &&
                        !recebidas.contains(rotas.getKey()) &&
                        !rotas.getKey().equals(origem)) {
                    System.out.println("Rota Removida: " + rotas.getKey());
                    aux.remove();
                    mudou = true;
                }
            }

            if (mudou) {
                printTabelaRoteamento();
                vizinhos.forEach(this::enviarTabelaRoteamento);
            }
        } else if (msg.startsWith("!")) {
            String[] p = msg.substring(1).split(";", 3);
            System.out.println("\n>>> MENSAGEM <<<");
            System.out.println("De: " + p[0] + " | Para: " + p[1]);
            System.out.println("Texto: " + p[2]);

            if (p[1].equals(ip)) {
                System.out.println("Status: ENTREGUE\n");
            } else {
                Rota rota_aux = tabela_roteamento.get(p[1]);
                if (rota_aux != null) {
                    System.out.println("Status: ENCAMINHANDO via " + rota_aux.saida + "\n");
                    enviar(msg, rota_aux.saida);
                } else {
                    System.out.println("Erro: Sem rota para " + p[1]);
                }
            }
        }
    }

    void printTabelaRoteamento() {
        System.out.println("\n===== TABELA =====");
        System.out.println("Destino         | Met | Saida");
        System.out.println("----------------------------------");
        tabela_roteamento.values().stream()
                .sorted((a, b) -> a.destino.compareTo(b.destino))
                .forEach(r -> System.out.printf("%-15s | %-3d | %s\n",
                        r.destino, r.metrica, r.saida));
        System.out.println("==================================\n");
    }

    void iniciar() {
        // Thread: Envio de tabela a cada 10s
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10000);
                    vizinhos.forEach(this::enviarTabelaRoteamento);
                } catch (Exception e) {
                }
            }
        }).start();

        // Thread: Recepção de mensagens
        new Thread(() -> {
            byte[] buf = new byte[1024];
            while (true) {
                try {
                    DatagramPacket pkt = new DatagramPacket(buf, buf.length);
                    socket.receive(pkt);
                    processar(new String(pkt.getData(), 0, pkt.getLength()),
                            pkt.getAddress().getHostAddress());
                } catch (Exception e) {
                }
            }
        }).start();

        // Thread: Detecção de falhas a cada 5s
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                    long agora = System.currentTimeMillis();
                    List<String> mortos = new ArrayList<>();

                    ultima_mensagem.forEach((v, t) -> {
                        if (agora - t > 15000)
                            mortos.add(v);
                    });

                    for (String morto : mortos) {
                        System.out.println("\n[!] FALHA: " + morto);
                        ultima_mensagem.remove(morto);

                        Iterator<Map.Entry<String, Rota>> it = tabela_roteamento.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<String, Rota> e = it.next();
                            if (e.getKey().equals(morto) || e.getValue().saida.equals(morto)) {
                                System.out.println("[-] Removendo: " + e.getKey());
                                it.remove();
                            }
                        }

                        printTabelaRoteamento();
                        for (String vizinho : vizinhos) {
                            if (!vizinho.equals(morto))
                                enviarTabelaRoteamento(vizinho);
                        }
                    }
                } catch (Exception e) {
                }
            }
        }).start();

        // Thread: Interface do usuário
        new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            System.out.println("\nComandos: tabela | enviar <IP> <msg> | sair\n");

            while (true) {
                System.out.print("> ");
                String cmd = sc.nextLine().trim();

                if (cmd.equals("tabela")) {
                    printTabelaRoteamento();
                } else if (cmd.startsWith("enviar ")) {
                    String[] p = cmd.split(" ", 3);
                    if (p.length >= 3) {
                        Rota r = tabela_roteamento.get(p[1]);
                        if (r == null) {
                            System.out.println("Erro: Sem rota para " + p[1]);
                        } else {
                            enviar("!" + ip + ";" + p[1] + ";" + p[2], r.saida);
                            System.out.println("Enviado para " + p[1] + " via " + r.saida);
                        }
                    }
                } else if (cmd.equals("sair")) {
                    System.exit(0);
                }
            }
        }).start();
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Uso: java Roteador <IP>");
            System.exit(1);
        }

        Roteador_2 new_roteador = new Roteador_2(args[0]);
        new_roteador.carregaARquivo();

        // Anuncia-se para os vizinhos
        for (String v : new_roteador.vizinhos) {
            new_roteador.enviar("@" + args[0], v);
        }

        // Envia tabela imediatamente após anúncio
        try {
            Thread.sleep(1000); // Pequeno delay para garantir que anúncio foi processado
        } catch (Exception e) {
        }

        for (String v : new_roteador.vizinhos) {
            new_roteador.enviarTabelaRoteamento(v);
        }

        new_roteador.iniciar();
    }
}