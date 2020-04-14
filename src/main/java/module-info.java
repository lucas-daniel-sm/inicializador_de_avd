module inicializador.de.avd.main {
    requires javafx.fxml;
    requires javafx.controls;
    requires com.jfoenix;

    exports org.example to javafx.graphics;
    opens org.example.janelas to javafx.fxml;
}