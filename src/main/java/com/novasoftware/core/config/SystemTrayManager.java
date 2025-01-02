package com.novasoftware.core.config;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javafx.application.Platform;
import javafx.stage.Stage;
import com.novasoftware.routes.Routes;

public class SystemTrayManager {

    public static void addToSystemTray(Stage primaryStage) {
        if (!SystemTray.isSupported()) {
            System.out.println("System Tray não suportado!");
            return;
        }

        Platform.runLater(() -> {
            TrayIcon trayIcon = createTrayIcon(primaryStage);
            SystemTray systemTray = SystemTray.getSystemTray();
            TrayIcon[] existingTrayIcons = systemTray.getTrayIcons();

            if (existingTrayIcons.length == 0) {
                try {
                    systemTray.add(trayIcon);
                } catch (AWTException e) {
                    System.err.println("Erro ao adicionar ícone ao System Tray: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private static TrayIcon createTrayIcon(Stage primaryStage) {
        java.awt.Image image = Toolkit.getDefaultToolkit().getImage(SystemTrayManager.class.getResource(Routes.LOGO_PATH).toExternalForm());

        ActionListener exitListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Platform.exit();
                System.exit(0);
            }
        };

        PopupMenu popupMenu = new PopupMenu();
        MenuItem exitItem = new MenuItem("Sair");
        exitItem.addActionListener(exitListener);
        popupMenu.add(exitItem);

        TrayIcon trayIcon = new TrayIcon(image, "Nova Software Tools", popupMenu);

        trayIcon.addActionListener(e -> Platform.runLater(() -> {
            primaryStage.show();
            primaryStage.setIconified(false);
        }));

        trayIcon.setImageAutoSize(true);
        return trayIcon;
    }
}

