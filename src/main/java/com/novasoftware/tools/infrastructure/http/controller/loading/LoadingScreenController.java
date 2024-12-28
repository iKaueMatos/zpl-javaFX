package com.novasoftware.tools.infrastructure.http.controller.loading;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

public class LoadingScreenController {

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Label loadingMessage;

    public void updateProgress(double progress) {
        if (progressIndicator != null) {
            progressIndicator.setProgress(progress);
        }
    }

    public void setLoadingMessage(String message) {
        if (loadingMessage != null) {
            loadingMessage.setText(message);
        }
    }

    public void resetProgress() {
        updateProgress(-1);
        setLoadingMessage("Carregando, por favor aguarde...");
    }

    public void setLoadingVisible(boolean visible) {
        if (progressIndicator != null) {
            progressIndicator.setVisible(visible);
        }
        if (loadingMessage != null) {
            loadingMessage.setVisible(visible);
        }
    }
}
