🌐 Simulador de Roteador - Protocolo Distance Vector
English | Português

Português
📋 Descrição
Implementação de um simulador de roteador que utiliza o protocolo de roteamento por vetor de distâncias (Distance Vector / RIP). O sistema simula uma rede de roteadores que trocam informações de roteamento via UDP, implementando funcionalidades como:

Troca automática de tabelas de roteamento
Split Horizon para evitar loops
Detecção de falhas de roteadores
Encaminhamento de mensagens entre roteadores
🚀 Funcionalidades
Descoberta Automática: Roteadores anunciam sua presença na rede
Atualização Dinâmica: Tabelas de roteamento são atualizadas automaticamente
Detecção de Falhas: Identifica roteadores inativos (timeout de 15s)
Split Horizon: Previne loops de roteamento
Encaminhamento de Mensagens: Roteia mensagens entre roteadores através da melhor rota
📦 Requisitos
Java 8 ou superior
Arquivo roteadores.txt com lista de IPs dos roteadores vizinhos
🔧 Configuração
Crie um arquivo roteadores.txt com os IPs dos roteadores (um por linha):

192.168.1.1
192.168.1.2
192.168.1.3
▶️ Como Executar
bash
# Compilar
javac Roteador_2.java

# Executar (um terminal para cada roteador)
java Roteador_2 192.168.1.1
java Roteador_2 192.168.1.2
java Roteador_2 192.168.1.3
💻 Comandos Disponíveis
tabela - Exibe a tabela de roteamento atual
enviar <IP> <mensagem> - Envia mensagem para outro roteador
sair - Encerra o roteador
📊 Exemplo de Uso
> tabela
===== TABELA =====
Destino         | Met | Saida
----------------------------------
192.168.1.2     | 1   | 192.168.1.2
192.168.1.3     | 2   | 192.168.1.2
==================================

> enviar 192.168.1.3 Olá, tudo bem?
Enviado para 192.168.1.3 via 192.168.1.2
🔍 Detalhes Técnicos
Porta UDP: 6000
Intervalo de atualização: 10 segundos
Timeout de falha: 15 segundos
Métrica: Contagem de saltos (hop count)
📝 Protocolo de Mensagens
@<IP> - Anúncio de novo roteador
*<IP>;<métrica> - Atualização de tabela de roteamento
!<origem>;<destino>;<mensagem> - Mensagem de dados
English
📋 Description
Implementation of a router simulator using the Distance Vector routing protocol (RIP). The system simulates a network of routers that exchange routing information via UDP, implementing features such as:

Automatic routing table exchange
Split Horizon to prevent loops
Router failure detection
Message forwarding between routers
🚀 Features
Automatic Discovery: Routers announce their presence on the network
Dynamic Updates: Routing tables are automatically updated
Failure Detection: Identifies inactive routers (15s timeout)
Split Horizon: Prevents routing loops
Message Forwarding: Routes messages between routers through the best path
📦 Requirements
Java 8 or higher
roteadores.txt file with list of neighbor router IPs
🔧 Setup
Create a roteadores.txt file with router IPs (one per line):

192.168.1.1
192.168.1.2
192.168.1.3
▶️ How to Run
bash
# Compile
javac Roteador_2.java

# Run (one terminal per router)
java Roteador_2 192.168.1.1
java Roteador_2 192.168.1.2
java Roteador_2 192.168.1.3
💻 Available Commands
tabela - Display current routing table
enviar <IP> <message> - Send message to another router
sair - Shutdown router
📊 Usage Example
> tabela
===== TABELA =====
Destino         | Met | Saida
----------------------------------
192.168.1.2     | 1   | 192.168.1.2
192.168.1.3     | 2   | 192.168.1.2
==================================

> enviar 192.168.1.3 Hello, how are you?
Enviado para 192.168.1.3 via 192.168.1.2
🔍 Technical Details
UDP Port: 6000
Update Interval: 10 seconds
Failure Timeout: 15 seconds
Metric: Hop count
📝 Message Protocol
@<IP> - New router announcement
*<IP>;<metric> - Routing table update
!<source>;<destination>;<message> - Data message
📄 Licença / License
Este projeto é de código aberto e está disponível sob a licença MIT.

This project is open source and available under the MIT License.

👥 Contribuições / Contributions
Contribuições são bem-vindas! Sinta-se à vontade para abrir issues ou pull requests.

Contributions are welcome! Feel free to open issues or pull requests.

