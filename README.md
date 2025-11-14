# Simulador de Roteador - Protocolo Distance Vector

Simulador de um sistema de roteamento que utiliza o protocolo de roteamento por vetor de distâncias (RIP). O sistema simula uma rede de roteadores que trocam informações de roteamento via UDP, implementando funcionalidades como:

## Requisitos
Java 8 ou superior
Arquivo roteadores.txt com lista de IPs dos roteadores vizinhos
🔧 Configuração
Crie um arquivo roteadores.txt com os IPs dos roteadores (um por linha):

192.168.1.1
192.168.1.2
192.168.1.3
▶️ Como Executar
bash
## Compilar
javac Roteador_2.java

## Executar (um terminal para cada roteador)
java Roteador_2 192.168.1.1
java Roteador_2 192.168.1.2
java Roteador_2 192.168.1.3
 Comandos Disponíveis
tabela - Exibe a tabela de roteamento atual
enviar <IP> <mensagem> - Envia mensagem para outro roteador
sair - Encerra o roteador

## Detalhes Técnicos
Porta UDP: 6000  
Intervalo de atualização: 10 segundos  
Timeout de falha: 15 segundos  
Métrica: Contagem de saltos  
## Protocolo de Mensagens
@<IP> - Anúncio de novo roteador
*<IP>;<métrica> - Atualização de tabela de roteamento
!<origem>;<destino>;<mensagem> - Mensagem de dados
Contributions are welcome! Feel free to open issues or pull requests.

