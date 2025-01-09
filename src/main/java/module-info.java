module com.novasoftware {
    requires javafx.web;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires java.sql;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires usb.api;
    requires MaterialFX;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.ikonli.materialdesign2;
    requires java.net.http;
    requires ZSDK.API;
    requires fontawesomefx;
    requires org.flywaydb.core;
    requires java.prefs;
    requires com.google.protobuf;
    requires org.apache.logging.log4j;
    requires org.apache.pdfbox;
    requires javafx.swing;
    requires freemarker;
    requires spring.context.support;
    requires jakarta.mail;
    requires spring.security.crypto;
    requires com.zaxxer.hikari;

    exports com.novasoftware.tools.domain.model to javafx.fxml;
    exports com.novasoftware.tools.domain.service to javafx.fxml;
    exports com.novasoftware.config.infrastructure.http.controller.config;
    opens com.novasoftware.config.infrastructure.http.controller.config to javafx.fxml;
    exports com.novasoftware.shared.loading;
    opens com.novasoftware.shared.loading to javafx.fxml;
    exports com.novasoftware.tools.infrastructure.http.controller.tools;
    opens com.novasoftware.tools.infrastructure.http.controller.tools to javafx.fxml;
    exports com.novasoftware.user.infra.http.controller.auth;
    opens com.novasoftware.user.infra.http.controller.auth to javafx.fxml;
    exports com.novasoftware.spreadsheet.infrastructure.http.controller.spreadsheet to javafx.fxml;
    opens com.novasoftware.spreadsheet.infrastructure.http.controller.spreadsheet to javafx.fxml;
    exports com.novasoftware.base.layout to javafx.fxml;
    opens com.novasoftware.base.layout to javafx.fxml;
    exports com.novasoftware.base.ui.view to javafx.fxml;
    opens com.novasoftware.base.ui.view to javafx.fxml;
    exports com.novasoftware;
    opens com.novasoftware to javafx.fxml;
    exports com.novasoftware.shared.database.queryBuilder to javafx.fxml;
    exports com.novasoftware.shared.database.environment to javafx.fxml;
}