package com.zpl.zpl.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

import com.zpl.zpl.ui.StageInitializer;

public class StartupView {

    @FXML
    private void startApplication(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/GenerateZplLabelView.fxml"));

            Scene scene = new Scene(loader.load(), 1600, 768);
            
            scene.getStylesheets().add(StageInitializer.class.getResource("/css/dark-theme.css").toExternalForm());
            scene.getRoot().getStyleClass().add("root");

            stage.setScene(scene);
            stage.setTitle("ZPL Label Generator");
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar o arquivo FXML ou CSS!");
        }
    }
}
