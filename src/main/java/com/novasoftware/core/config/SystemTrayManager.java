package com.novasoftware.core.config;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.novasoftware.core.path.ResourcePaths;
import javafx.application.Platform;
import javafx.stage.Stage;

public class SystemTrayManager {

    private static final Logger LOGGER = Logger.getLogger(SystemTrayManager.class.getName());

    public static void addToSystemTray(Stage primaryStage) {
        if (!SystemTray.isSupported()) {
            JOptionPane.showMessageDialog(null, "System Tray não é suportado no seu sistema.");
            return;
        }

        Platform.runLater(() -> {
            Image image = null;
            try {
                URL imageLoc = SystemTrayManager.class.getResource(ResourcePaths.LOGO_32);
                if (imageLoc == null) {
                    throw new IOException("Imagem não encontrada.");
                }
                image = ImageIO.read(imageLoc);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Erro ao carregar a imagem do System Tray.", e);
                return;
            }

            SystemTray systemTray = SystemTray.getSystemTray();
            TrayIcon trayIcon = new TrayIcon(image, "Nova Software Tools", createPopupMenu(primaryStage));
            trayIcon.setImageAutoSize(true);

            trayIcon.addActionListener(e -> Platform.runLater(() -> {
                if (primaryStage.isIconified()) {
                    primaryStage.setIconified(false);
                }
                primaryStage.show();
            }));

            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        Platform.runLater(() -> {
                            if (primaryStage.isIconified()) {
                                primaryStage.setIconified(false);
                            }
                            primaryStage.show();
                        });
                    }
                }
            });

            try {
                systemTray.add(trayIcon);
            } catch (AWTException e) {
                LOGGER.log(Level.SEVERE, "Erro ao adicionar o ícone ao System Tray.", e);
            }
        });
    }

    private static PopupMenu createPopupMenu(Stage primaryStage) {
        PopupMenu popupMenu = new PopupMenu("Menu de Opções");

        MenuItem exitItem = new MenuItem("Sair");
        exitItem.addActionListener(e -> exitApplication());

        popupMenu.add(exitItem);
        return popupMenu;
    }

    private static void exitApplication() {
        Platform.exit();
        System.exit(0);
    }
}
