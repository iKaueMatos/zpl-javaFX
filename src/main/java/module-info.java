module com.zpl.zpl {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires java.sql;
    requires java.desktop;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires usb.api;

    opens com.zpl.zpl to javafx.fxml;
    exports com.zpl.zpl;
    exports com.zpl.zpl.view to javafx.fxml;
    opens com.zpl.zpl.view to javafx.fxml;
    exports com.zpl.zpl.domain.model to javafx.fxml;
    exports com.zpl.zpl.domain.service to javafx.fxml;
    exports com.zpl.zpl.ui to javafx.fxml;
    opens com.zpl.zpl.ui to javafx.fxml;
    exports com.zpl.zpl.infrastructure.database to javafx.fxml;
}