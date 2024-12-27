# Gerador de Arquivos ZPL

## Visão Geral
O Gerador de Arquivos ZPL é uma aplicação desktop Java projetada para facilitar a geração e manipulação de arquivos ZPL (Zebra Programming Language). Esta aplicação permite que os usuários criem conteúdo ZPL com base em suas entradas e o convertam em formatos ZPL e PDF.

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
│   │   │   │   └── zpl
│   │   │   │       ├── application
│   │   │   │       │   ├── ZplApplication.java
│   │   │   │       │   └── ZplFileGenerator.java
│   │   │   │       ├── domain
│   │   │   │       │   ├── model
│   │   │   │       │   │   └── ZplFile.java
│   │   │   │       │   └── service
│   │   │   │       │       ├── PrinterService.java
│   │   │   │       │       └── ZplFileService.java
│   │   │   │       ├── infrastructure
│   │   │   │       │   ├── database
│   │   │   │       │   │   ├── DatabaseInitializer.java
│   │   │   │       │   │   └── DatabaseManager.java
│   │   │   │       │   ├── PdfGenerator.java
│   │   │   │       │   └── ZplFileRepository.java
│   │   │   │       ├── ui
│   │   │   │       │   ├── StageInitializer.java
│   │   │   │       │   └── controller
│   │   │   │       │       ├── GenerateZplLabelView.java
│   │   │   │       │       ├── VisualizeZplView.java
│   │   │   │       │       └── ZplFileController.java
│   │   │   │       └── usecase
│   │   │   │           ├── LabelGenerator.java
│   │   │   │           └── SpreadsheetReader.java
│   │   └── resources
│   │       └── view
│   │           ├── GenerateZplLabelView.fxml
│   │           ├── VisualizeZplView.fxml
│   │           └── StartupView.fxml
├── build.gradle
├── LICENSE
└── README.md
```

## Instruções de Configuração
1. Clone o repositório para sua máquina local.
2. Navegue até o diretório do projeto.
3. Construa o projeto usando Gradle:
   ```
   ./gradlew build
   ```
4. Execute a aplicação:
   ```
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