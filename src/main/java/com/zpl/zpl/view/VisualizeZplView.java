package com.zpl.zpl.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class VisualizeZplView implements Initializable {
    @FXML
    private TextArea zplTextArea;
    @FXML
    private ImageView zplImageView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    private void loadZplFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZPL Files", "*.zpl"));
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] data = new byte[(int) file.length()];
                fis.read(data);
                String zplContent = new String(data);
                zplTextArea.setText(zplContent);
                visualizeZpl(zplContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setZplContent(String zplContent) {
        zplTextArea.setText(zplContent);
        visualizeZpl(zplContent);
    }

    private void visualizeZpl(String zplContent) {
        Image zplImage = convertZplToImage(zplContent);
        zplImageView.setImage(zplImage);
    }

    private Image convertZplToImage(String zplContent) {
        return new Image("file:placeholder.png");
    }
}
