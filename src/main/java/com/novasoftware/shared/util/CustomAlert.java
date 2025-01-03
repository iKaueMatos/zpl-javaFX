package com.novasoftware.shared.util;

import io.github.palexdev.materialfx.controls.MFXButton;

import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.enums.DialogType;
import io.github.palexdev.materialfx.enums.ScrimPriority;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Map;

public class CustomAlert {

    public static void showInfoAlert(Stage owner, String title, String message) {
        createAndShowDialog(owner, title, message, DialogType.INFO, null);
    }

    public static void showWarningAlert(Stage owner, String title, String message) {
        createAndShowDialog(owner, title, message, DialogType.WARNING, null);
    }

    public static void showErrorAlert(Stage owner, String title, String message) {
        createAndShowDialog(owner, title, message, DialogType.ERROR, null);
    }

    public static void showConfirmationAlert(Stage owner, String title, String message, Runnable onConfirm) {
        createAndShowDialog(owner, title, message, DialogType.INFO, onConfirm);
    }

    private static void createAndShowDialog(Stage owner, String title, String message, DialogType type, Runnable onConfirm) {
        Platform.runLater(() -> {
            MFXGenericDialog dialogContent = MFXGenericDialogBuilder.build()
                    .setContentText(message)
                    .makeScrollable(true)
                    .get();

            MFXStageDialog dialog = MFXGenericDialogBuilder.build(dialogContent)
                    .toStageDialogBuilder()
                    .initOwner(owner)
                    .initModality(Modality.APPLICATION_MODAL)
                    .setDraggable(true)
                    .setTitle(title)
                    .setOwnerNode((Pane) owner.getScene().getRoot())
                    .setScrimPriority(ScrimPriority.WINDOW)
                    .setScrimOwner(true)
                    .get();

            if (onConfirm != null) {
                dialogContent.addActions(
                        Map.entry(new MFXButton("Confirmar"), event -> {
                            onConfirm.run();
                            dialog.close();
                        }),
                        Map.entry(new MFXButton("Fechar"), event -> dialog.close())
                );
            }

            dialogContent.setMaxSize(400, 200);
            dialog.show();
        });
    }
}
