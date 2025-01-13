package com.novasoftware.core.email;

import com.novasoftware.user.infrastructure.http.controller.auth.LoginController;

import java.util.prefs.Preferences;

public class SavedEmail {
    private static final String SAVED_EMAIL_KEY = "savedEmail";

    public static void  saveEmail(String email) {
        Preferences preferences = Preferences.userNodeForPackage(LoginController.class);
        preferences.put(SAVED_EMAIL_KEY, email);
    }

    public static String getSavedEmail() {
        Preferences preferences = Preferences.userNodeForPackage(LoginController.class);
        return preferences.get(SAVED_EMAIL_KEY, null);
    }
}
