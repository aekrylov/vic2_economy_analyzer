open module vic2sgea {
    requires java.logging;
    requires javafx.controls;
    requires javafx.fxml;

    requires com.google.gson;
    requires supercsv;
    requires supercsv.dozer;

    exports org.victoria2.tools.vic2sgea.gui;
}