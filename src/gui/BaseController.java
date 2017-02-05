package gui;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

import java.util.function.Function;

/**
 * By Anton Krylov (anthony.kryloff@gmail.com)
 * Date: 2/1/17 11:37 PM
 *
 * This class contains some useful helper methods for controllers
 */
public abstract class BaseController {


    protected static <T, S> void setFactory(TableColumn<T, S> column, String name) {
        column.setCellValueFactory(new PropertyValueFactory<>(name));
    }

    protected static <T, S> void setFactory(TableColumn<T, S> column, Function<? super T, S> getter) {
        column.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(getter.apply(features.getValue())));
    }

    protected static <T, S> void setCellFactory(TableColumn<T, S> column, StringConverter<S> converter) {
        column.setCellFactory(TextFieldTableCell.forTableColumn(converter));
    }
}
