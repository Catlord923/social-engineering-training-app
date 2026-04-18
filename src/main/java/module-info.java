module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires java.desktop;
    requires org.slf4j;

    opens com.example to javafx.fxml;
    opens com.example.model to javafx.base;
    opens com.example.controller to javafx.fxml;

    exports com.example;
    exports com.example.controller;
}
