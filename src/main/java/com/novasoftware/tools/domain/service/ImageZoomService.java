package com.novasoftware.tools.domain.service;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;

public class ImageZoomService {

    public void setupImageZoom(VBox imageViewContainer, ImageView imageView) {
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setFitWidth(800);
        imageView.setFitHeight(400);

        ScrollPane scrollPane = new ScrollPane(imageView);
        scrollPane.setPannable(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        imageViewContainer.getChildren().clear();
        imageViewContainer.getChildren().add(scrollPane);

        HBox zoomButtonsContainer = new HBox(10);
        zoomButtonsContainer.setAlignment(Pos.CENTER);

        MFXButton zoomInButton = createZoomButton("+", 1.2, imageView, scrollPane);
        MFXButton zoomOutButton = createZoomButton("-", 0.8, imageView, scrollPane);

        zoomButtonsContainer.getChildren().addAll(zoomInButton, zoomOutButton);
        imageViewContainer.getChildren().add(zoomButtonsContainer);

        scrollPane.setOnScroll(event -> {
            double zoomFactor = event.getDeltaY() > 0 ? 1.1 : 0.9;
            applyZoom(imageView, zoomFactor, scrollPane, event.getX(), event.getY());
            event.consume();
        });

        addPanFunctionality(scrollPane);
    }

    private void applyZoom(ImageView imageView, double zoomFactor, ScrollPane scrollPane, double mouseX, double mouseY) {
        // Limites de zoom
        double newScaleX = imageView.getScaleX() * zoomFactor;
        double newScaleY = imageView.getScaleY() * zoomFactor;

        if (newScaleX < 0.5 || newScaleX > 5) return;

        Scale scale = new Scale(newScaleX, newScaleY);
        imageView.getTransforms().clear();
        imageView.getTransforms().add(scale);
        imageView.setFitWidth(scrollPane.getViewportBounds().getWidth());
        imageView.setFitHeight(scrollPane.getViewportBounds().getHeight());

        Point2D mousePosition = imageView.sceneToLocal(mouseX, mouseY);
        double viewportWidth = scrollPane.getViewportBounds().getWidth();
        double viewportHeight = scrollPane.getViewportBounds().getHeight();

        double dx = (mousePosition.getX() - viewportWidth / 2) * (zoomFactor - 1);
        double dy = (mousePosition.getY() - viewportHeight / 2) * (zoomFactor - 1);

        scrollPane.setHvalue(Math.max(0, Math.min(1, scrollPane.getHvalue() + dx / imageView.getBoundsInLocal().getWidth())));
        scrollPane.setVvalue(Math.max(0, Math.min(1, scrollPane.getVvalue() + dy / imageView.getBoundsInLocal().getHeight())));
    }

    private MFXButton createZoomButton(String text, double zoomFactor, ImageView imageView, ScrollPane scrollPane) {
        MFXButton zoomButton = new MFXButton(text);
        zoomButton.setStyle("-fx-font-size: 16px; -fx-background-color: #0A0A0A; -fx-text-fill: #FFFFFF; border-color: #0A0A0A;");
        zoomButton.setPrefWidth(40);
        zoomButton.setPrefHeight(40);
        zoomButton.setOnAction(event -> {
            applyZoom(imageView, zoomFactor, scrollPane, scrollPane.getWidth() / 2, scrollPane.getHeight() / 2);
        });
        return zoomButton;
    }

    private void addPanFunctionality(ScrollPane scrollPane) {
        final Object[] dragContext = new Object[2];

        scrollPane.setOnMousePressed(event -> {
            dragContext[0] = event.getSceneX();
            dragContext[1] = event.getSceneY();
        });

        scrollPane.setOnMouseDragged(event -> {
            double deltaX = (double) dragContext[0] - event.getSceneX();
            double deltaY = (double) dragContext[1] - event.getSceneY();

            scrollPane.setHvalue(scrollPane.getHvalue() + deltaX / scrollPane.getWidth());
            scrollPane.setVvalue(scrollPane.getVvalue() + deltaY / scrollPane.getHeight());

            dragContext[0] = event.getSceneX();
            dragContext[1] = event.getSceneY();
        });
    }
}
