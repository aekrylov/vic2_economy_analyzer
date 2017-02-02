package main;

import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.util.function.Consumer;

/**
 * By Anton Krylov (anthony.kryloff@gmail.com)
 * Date: 2/1/17 11:00 PM
 *
 * JavaFX table row factory implementation that allows for calling specified callback on table row double click
 *
 * usage:
 * {@code table.setRowFactory(new TableRowDoubleClickFactory(product -> {...})) }
 * @param <T> table row data type
 */
public class TableRowDoubleClickFactory<T> implements Callback<TableView<T>, TableRow<T>> {

    private final Consumer<T> function;

    public TableRowDoubleClickFactory(Consumer<T> function) {
        this.function = function;
    }

    @Override
    public TableRow<T> call(TableView<T> tv) {
        TableRow<T> row = new TableRow<>();
        row.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                T item = row.getItem();
                function.accept(item);
            }

        });
        return row;
    }
}
