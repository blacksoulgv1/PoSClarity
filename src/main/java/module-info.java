module com.gerardgv.posclarity {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;

    exports com.gerardgv.posclarity.app;
    
    opens com.gerardgv.posclarity.app to javafx.fxml;
    opens com.gerardgv.posclarity.controllers to javafx.fxml;
}
