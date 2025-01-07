package com.novasoftware.shared.util.notification;

import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class NotificationUtil {
    public static Notifications pushNotify(String title, String message) {
        Notifications notification = Notifications.create()
                .title(title)
                .text(message)
                .hideAfter(Duration.seconds(5))
                .position(Pos.TOP_RIGHT)
                .graphic(null);

        return notification;
    }
}
