module com.example.workfusion {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.workfusion to javafx.fxml;
    exports com.example.workfusion;
}