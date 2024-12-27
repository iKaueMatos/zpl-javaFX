package com.zpl.zpl.infrastructure.http.controller;

import java.io.IOException;

import com.zpl.zpl.ui.StageInitializer;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class LoginScreenController {
    @FXML
    private MFXTextField usernameField;
    @FXML
    private MFXPasswordField passwordField;
    @FXML
    private MFXButton loginButton;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleLogin() throws IOException {
        StageInitializer.initialize(stage);
    }

    @FXML
    private void handleForgotPassword() {
        // Add logic to handle forgotten password
    }
}