package gui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import main.Report;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * By Anton Krylov (anthony.kryloff@gmail.com)
 * Date: 2/2/17 12:05 AM
 *
 * Base controller for chart windows
 */
public class ChartsController extends BaseController {
    protected final Scene scene;
    protected GridPane grid;
    protected Report report;

    public ChartsController(Report report) {
        this.report = report;
        this.grid = new GridPane();

        //grid.setAlignment(Pos.CENTER);
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(grid);

        scene = new Scene(scrollPane, 1200, 950);

    }

    /**
     * Adds chart to grid
     *
     * @param pieChartData chart data
     * @param i            column index
     * @param j            row index
     * @param title        chart title
     * @param onEnter      function that returns caption when mouse enters an item
     * @param onClick      consumer called when an item is clicked
     * @return the created chart
     */
    protected PieChart addChart(List<PieChart.Data> pieChartData, int i, int j, String title,
                                Function<PieChart.Data, String> onEnter, Consumer<PieChart.Data> onClick) {
        final PieChart chart = new PieChart(FXCollections.observableList(pieChartData));

        chart.setStartAngle(90);
        chart.setLegendVisible(false);
        chart.setLabelsVisible(false);

        chart.setTitle(title);
        final Label caption = new Label("");
        //todo move to css
        caption.setTextFill(Color.DARKORANGE);
        caption.setStyle("-fx-font: 20 arial;");

        grid.add(chart, i, j);
        grid.add(caption, i, j + 1);

        for (final PieChart.Data data : chart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED,
                    e -> caption.setText(onEnter.apply(data)));
            data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED,
                    e -> onClick.accept(data));
        }

        return chart;
    }

    public Scene getScene() {
        return scene;
    }
}
