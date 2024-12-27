package com.zpl.zpl.ui;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginScreen {
    private Stage stage;

    public LoginScreen(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        VBox root = new VBox(10);
        MFXTextField usernameField = new MFXTextField();
        usernameField.setPromptText("Username");
        MFXPasswordField passwordField = new MFXPasswordField();
        passwordField.setPromptText("Password");
        MFXButton loginButton = new MFXButton("Login");

        loginButton.setOnAction(event -> {
            try {
                StageInitializer.initialize(stage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        root.getChildren().addAll(usernameField, passwordField, loginButton);
        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.show();
    }
}
