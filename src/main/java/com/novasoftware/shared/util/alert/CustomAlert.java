package com.novasoftware.shared.util.alert;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class CustomAlert {
    public static void showInfoAlert(Stage owner, String title, String message) {
        createAndShowDialog(owner, title, message, AlertType.INFORMATION, null);
    }

    public static void showWarningAlert(Stage owner, String title, String message) {
        createAndShowDialog(owner, title, message, AlertType.WARNING, null);
    }

    public static void showErrorAlert(Stage owner, String title, String message) {
        createAndShowDialog(owner, title, message, AlertType.ERROR, null);
    }

    public static void showConfirmationAlert(Stage owner, String title, String message, Runnable onConfirm) {
        createAndShowDialog(owner, title, message, AlertType.CONFIRMATION, onConfirm);
    }

    private static void createAndShowDialog(Stage owner, String title, String message, AlertType alertType, Runnable onConfirm) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setContentText(message);

            if (owner != null) {
                alert.initOwner(owner);
            }

            if (alertType == AlertType.CONFIRMATION) {
                ButtonType confirmButton = new ButtonType("Confirmar");
                ButtonType cancelButton = new ButtonType("Cancelar");
                alert.getButtonTypes().setAll(confirmButton, cancelButton);

                alert.showAndWait().ifPresent(response -> {
                    if (response == confirmButton && onConfirm != null) {
                        onConfirm.run();
                    }
                });
            } else {
                alert.showAndWait();
            }
        });
    }
}
