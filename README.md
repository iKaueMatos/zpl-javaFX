# Nova Tools

## Visão Geral
O Nova Tools é um software desktop multifuncional desenvolvido para facilitar diversas tarefas do dia a dia. Uma das principais funcionalidades é o Gerador de Arquivos ZPL, uma aplicação Java que permite a criação e manipulação de arquivos ZPL (Zebra Programming Language). Com esta ferramenta, os usuários podem gerar conteúdo ZPL a partir de suas entradas e convertê-lo para os formatos ZPL e PDF.

## Funcionalidades
- Gerar arquivos ZPL a partir de conteúdo definido pelo usuário.
- Converter arquivos ZPL para o formato PDF.
- Interface amigável construída com JavaFX.
- Persistência de dados para arquivos ZPL.

## Estrutura do Projeto
```
zpl-file-generator
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── com
│   │   │   │   └── novasoftware
│   │   │   │       ├── tools
│   │   │   │       │   ├── NovaSoftwareToolsApplication.java
│   │   │   │       │   ├── infrastructure
│   │   │   │       │   │   ├── database
│   │   │   │       │   │   │   ├── DatabaseInitializer.java
│   │   │   │       │   │   │   └── DatabaseManager.java
│   │   │   │       │   │   ├── http
│   │   │   │       │   │     └── controller
│   │   │   │       │   │           └── auth
│   │   │   │       │   │             └── LoginController.java
│   │   │   │       │   ├── ui
│   │   │   │       │   │   ├── util
│   │   │   │       │   │   │   └── CustomAlert.java
│   │   │   │       │   │   ├── view
│   │   │   │       │   │   │   ├── MainLayoutView.java
│   │   │   │       │   │   │   └── MainScreen.java
│   │   │   │       │   │   └── StageInitializer.java
│   │   │   │       │   └── usecase
│   │   │   │       │       ├── LabelGenerator.java
│   │   │   │       │       └── SpreadsheetReader.java
│   │   └── resources
│   │       └── view
│   │           ├── assets
│   │           │   ├── logo-app.jpg
│   │           │   └── logo.png
│   │           ├── css
│   │           │   └── tool-zpl.css
│   │           ├── fxml
│   │           │   ├── configuration_screen.fxml
│   │           │   ├── loading_screen.fxml
│   │           │   ├── login_screen.fxml
│   │           │   ├── main_layout_screen.fxml
│   │           │   ├── tool_import_spreadsheet_screen.fxml
│   │           │   └── tool_zpl_tag.fxml
├── build.gradle
├── LICENSE
└── README.md
```

## Instruções de Configuração
1. Clone o repositório para sua máquina local.
2. Navegue até o diretório do projeto.
3. Construa o projeto usando Gradle:
   ```sh
   ./gradlew build
   ```
4. Execute a aplicação:
   ```sh
   ./gradlew run
   ```

## Diretrizes de Uso
- Inicie a aplicação para acessar a interface do usuário.
- Insira o conteúdo ZPL desejado nos campos fornecidos.
- Clique no botão "Gerar ZPL" para criar um arquivo ZPL.
- Use a opção "Converter para PDF" para gerar uma versão PDF do arquivo ZPL.

## Contribuindo
Contribuições são bem-vindas! Por favor, envie um pull request ou abra uma issue para quaisquer melhorias ou correções de bugs.

## Licença
Este projeto está licenciado sob a Licença MIT. Veja o arquivo LICENSE para mais detalhes.