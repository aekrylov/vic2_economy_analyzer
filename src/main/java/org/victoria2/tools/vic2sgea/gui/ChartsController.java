package org.victoria2.tools.vic2sgea.gui;

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
import org.victoria2.tools.vic2sgea.main.Report;
import org.victoria2.tools.vic2sgea.main.Wrapper;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * By Anton Krylov (anthony.kryloff@gmail.com)
 * Date: 2/2/17 12:05 AM
 * <p>
 * Base controller for chart windows
 */
public class ChartsController extends BaseController {
    protected final Scene scene;
    protected final GridPane grid;
    protected final Report report;

    public static final int NUM_SLICES = 25;
    public static final double VALUE_THRESHOLD = 0.01;

    private int chartCount = 0;

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
        scene.getStylesheets().add("/gui/charts.css");

    }

    /**
     * Adds chart to grid
     *
     * @param slices chart data
     * @param i            column index
     * @param j            row index
     * @param title        chart title
     * @param onEnter      function that returns caption when mouse enters an item
     * @param onClick      consumer called when an item is clicked
     * @return the created chart
     */
    private PieChart addChart(List<ChartSlice> slices, int i, int j, String title,
                              Function<PieChart.Data, String> onEnter, Consumer<PieChart.Data> onClick) {
        slices.sort(Comparator.comparing(ChartSlice::getValue).reversed());

        double totalValue = slices.stream().mapToDouble(ChartSlice::getValue).sum();
        slices = slices.stream()
                .filter(data -> data.getValue() / totalValue > VALUE_THRESHOLD)
                .limit(NUM_SLICES)
                .collect(Collectors.toList());

        double topValue = slices.stream().mapToDouble(ChartSlice::getValue).sum();
        if(totalValue - topValue > .001) {
            ChartSlice others = new ChartSlice("Others", totalValue - topValue, Color.GRAY);
            slices.add(others);
        }

        List<PieChart.Data> pieChartData = slices.stream()
                .map(chartSlice -> chartSlice.data)
                .collect(Collectors.toList());

        final PieChart chart = new PieChart(FXCollections.observableList(pieChartData));

        chart.setStartAngle(90);
        chart.setLegendVisible(false);
        chart.setLabelsVisible(true);

        chart.setTitle(title);
        final Label caption = new Label("");
        caption.getStyleClass().add("chart-caption");

        GridPane subPane = new GridPane();
        subPane.add(chart, 0, 0);
        subPane.add(caption, 0, 1);

        grid.add(subPane, i, j);

        slices.forEach(ChartSlice::applyColor);

        for (final PieChart.Data data : chart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED,
                    e -> caption.setText(onEnter.apply(data)));
            data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED,
                    e -> onClick.accept(data));
        }

        return chart;
    }
    
    protected PieChart addChart(List<ChartSlice> slices, String title,
                                Function<PieChart.Data, String> onEnter, Consumer<PieChart.Data> onClick) {
        int row = (chartCount / 2);
        int column = chartCount % 2;

        chartCount++;

        return addChart(slices, column, row, title, onEnter, onClick);

    }

    public Scene getScene() {
        return scene;
    }

    static class ChartSlice {
        private Color color;
        private final PieChart.Data data;

        public ChartSlice(String name, double value, Color color) {
            this(name, value);
            this.color = color;
        }

        public ChartSlice(String name, double value) {
            data = new PieChart.Data(name, value);
        }

        public void applyColor() {
            if (color == null)
                return;
            String webColor = Wrapper.toWebColor(color);
            data.getNode().setStyle("-fx-pie-color: " + webColor);
        }

        public String getName() {
            return data.getName();
        }

        public double getValue() {
            return data.getPieValue();
        }
    }
}
