package com.novasoftware.tools.infrastructure.http.controller.auth;

import java.net.URL;

import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class RegisterController extends BaseScreenAuthController {

  @FXML
  private MFXTextField nameField;

  @FXML
  private MFXTextField emailField;

  @FXML
  private MFXPasswordField passwordField;

  @FXML
  private MFXPasswordField confirmPasswordField;
  
  @FXML
  private Label titleLabel;

  private Stage stage;

  @FXML
  private void handleRegister(ActionEvent event) {
      String name = nameField.getText();
      String email = emailField.getText();
      String password = passwordField.getText();
      String confirmPassword = confirmPasswordField.getText();

      if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
          titleLabel.setText("Todos os campos são obrigatórios.");
          return;
      }

      if (!password.equals(confirmPassword)) {
          titleLabel.setText("As senhas não coincidem.");
          return;
      }

      titleLabel.setText("Registro realizado com sucesso!");
  }

  @FXML
  private void handleBackToLogin(ActionEvent event) {
       try {
            URL resource = getClass().getResource("/view/fxml/login_screen.fxml");

            if (resource == null) {
                throw new IllegalArgumentException("Arquivo FXML não encontrado: /view/fxml/login_screen.fxml");
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setWidth(1200);
            stage.setHeight(600);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            System.err.println("Erro ao carregar a tela de registro: " + e.getMessage());
            e.printStackTrace();
        }
  }
}

