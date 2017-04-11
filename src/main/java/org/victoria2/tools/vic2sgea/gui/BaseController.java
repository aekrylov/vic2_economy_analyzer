package org.victoria2.tools.vic2sgea.gui;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Function;

/**
 * By Anton Krylov (anthony.kryloff@gmail.com)
 * Date: 2/1/17 11:37 PM
 * <p>
 * This class contains some useful helper methods for controllers
 */
public abstract class BaseController {


    protected static <T, S> void setFactory(TableColumn<T, S> column, Function<? super T, S> getter) {
        column.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(getter.apply(features.getValue())));
    }

    protected static <T, S> void setFactory(TableColumn<T, S> column, Function<? super T, S> getter,
                                            StringConverter<S> converter) {
        column.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(getter.apply(features.getValue())));
        setCellFactory(column, converter);
    }

    protected static <T, S> void setCellFactory(TableColumn<T, S> column, StringConverter<S> converter) {
        column.setCellFactory(TextFieldTableCell.forTableColumn(converter));
    }

    protected static void errorAlert(Throwable e, String text) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(text);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String exceptionText = sw.toString();

            Label label = new Label("The exception stacktrace was:");

            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);

            // Set expandable Exception into the dialog pane.
            alert.getDialogPane().setExpandableContent(expContent);

            alert.show();
        });
    }

    public void exit() {
        Platform.exit();
    }
}
