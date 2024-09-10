module com.fianco {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.fianco to javafx.fxml;
    exports com.fianco;
}
