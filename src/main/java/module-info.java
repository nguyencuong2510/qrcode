module com.example.qrcodegenerate {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires core;


    opens com.example.qrcodegenerate to javafx.fxml;
    exports MainApplication;
}