module com.gerardgv.posclarity {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    exports com.gerardgv.posclarity.app;
    opens com.gerardgv.posclarity.app to javafx.fxml;
}
