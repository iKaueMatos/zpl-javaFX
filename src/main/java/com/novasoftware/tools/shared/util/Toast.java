package com.novasoftware.tools.shared.util;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Toast {

    public static void show(Stage stage, String message, int durationMillis) {
        Popup popup = new Popup();

        VBox toastRoot = new VBox();
        toastRoot.setPadding(new Insets(10));
        toastRoot.setBackground(new Background(new BackgroundFill(Color.web("#323232"), new CornerRadii(5), Insets.EMPTY)));
        toastRoot.setBorder(new Border(new BorderStroke(Color.web("#ffffff"), BorderStrokeStyle.SOLID, new CornerRadii(5), BorderWidths.DEFAULT)));
        toastRoot.setSpacing(5);

        Label label = new Label(message);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        toastRoot.getChildren().add(label);

        popup.getContent().add(toastRoot);

        Scene scene = stage.getScene();
        double x = scene.getWindow().getX() + scene.getWidth() - toastRoot.prefWidth(-1) - 30;
        double y = scene.getWindow().getY() + scene.getHeight() - 100;

        popup.show(stage, x, y);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), toastRoot);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), toastRoot);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.millis(durationMillis));

        fadeOut.setOnFinished(e -> popup.hide());

        SequentialTransition sequential = new SequentialTransition(fadeIn, fadeOut);
        sequential.play();
    }
}
