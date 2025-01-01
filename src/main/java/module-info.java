module com.zpl.zpl {
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

    opens com.novasoftware.tools to javafx.fxml;
    exports com.novasoftware.tools;
    opens com.novasoftware.tools.ui.view to javafx.fxml;
    exports com.novasoftware.tools.domain.model to javafx.fxml;
    exports com.novasoftware.tools.domain.service to javafx.fxml;
    exports com.novasoftware.tools.infrastructure.database to javafx.fxml;
    exports com.novasoftware.tools.infrastructure.http.controller.config;
    opens com.novasoftware.tools.infrastructure.http.controller.config to javafx.fxml;
    exports com.novasoftware.tools.infrastructure.http.controller.loading;
    opens com.novasoftware.tools.infrastructure.http.controller.loading to javafx.fxml;
    exports com.novasoftware.tools.ui.view;
    exports com.novasoftware.tools.infrastructure.http.controller.tools;
    opens com.novasoftware.tools.infrastructure.http.controller.tools to javafx.fxml;
    exports com.novasoftware.tools.infrastructure.http.controller.auth;
    opens com.novasoftware.tools.infrastructure.http.controller.auth to javafx.fxml;
    exports com.novasoftware.tools.infrastructure.http.controller.spreadsheet to javafx.fxml;
    opens com.novasoftware.tools.infrastructure.http.controller.spreadsheet to javafx.fxml;
    exports com.novasoftware.base.controller to javafx.fxml;
    opens com.novasoftware.base.controller to javafx.fxml;
}