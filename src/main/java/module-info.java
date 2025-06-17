module com.example.demohiking {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;

    opens com.example.demohiking to javafx.fxml;
    exports com.example.demohiking;
}