# Release Notes

## Version 1.0.0

### Overview
This release introduces the initial version of Nova Tools, a multifunctional desktop software designed to facilitate various day-to-day tasks. One of the key features is the ZPL File Generator, a Java application that allows users to create and manipulate ZPL (Zebra Programming Language) files. Users can generate ZPL content based on their inputs and convert it to ZPL and PDF formats.

### New Features
- **ZPL File Generation**: Generate ZPL files from user-defined content.
- **PDF Conversion**: Convert ZPL files to PDF format.
- **User Interface**: User-friendly interface built with JavaFX.
- **Data Persistence**: Persist data for ZPL files.
- **Custom Alerts**: Custom alert dialogs using MaterialFX.
- **Dynamic Content Loading**: Load dynamic content in MainLayoutView.
- **Application Icon and Title**: Set up application icon and title.

### Changes
- **CSS Syntax Corrections**: Corrected CSS syntax errors in FXML files.
- **Image Path Updates**: Updated image paths and loading logic.
- **Error Handling Improvements**: Improved error handling for missing resources.

### Fixes
- **Image Resource Loading**: Fixed issues with image resource loading.
- **CSS Parsing Errors**: Resolved CSS parsing errors.
- **FXML Structure**: Corrected FXML structure for proper content display.

### Project Structure
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
│   │   │   │       │   │   │   └── controller
│   │   │   │       │   │   │       └── auth
│   │   │   │       │   │   │           └── LoginController.java
│   │   │   │       │   │   └── PdfGenerator.java
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
├── README.md
└── RELEASE_NOTES.md
```

### Instructions for Setup
1. Clone the repository to your local machine.
2. Navigate to the project directory.
3. Build the project using Gradle:
   ```sh
   ./gradlew build
   ```
4. Run the application:
   ```sh
   ./gradlew run
   ```

### Usage Guidelines
- Start the application to access the user interface.
- Enter the desired ZPL content in the provided fields.
- Click the "Generate ZPL" button to create a ZPL file.
- Use the "Convert to PDF" option to generate a PDF version of the ZPL file.

### Contributing
Contributions are welcome! Please submit a pull request or open an issue for any improvements or bug fixes.

### License
This project is licensed under the MIT License. See the LICENSE file for more details.
