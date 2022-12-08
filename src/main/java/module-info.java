open module vic2sgea {
    requires java.logging;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;

    requires gson;
    requires supercsv;
    requires supercsv.dozer;

    exports org.victoria2.tools.vic2sgea.gui;
}