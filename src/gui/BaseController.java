package gui;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

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
}
