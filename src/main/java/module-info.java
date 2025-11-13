module com.gerardgv.posclarity {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.gerardgv.posclarity to javafx.fxml;
    exports com.gerardgv.posclarity;
}
