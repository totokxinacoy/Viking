module com.example.demohiking {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;
    requires java.desktop;

    opens com.example.demohiking to javafx.fxml;
    exports com.example.demohiking;
    exports com.example.demohiking.ADT;
    opens com.example.demohiking.ADT to javafx.fxml;
    exports com.example.demohiking.Connection;
    opens com.example.demohiking.Connection to javafx.fxml;
}