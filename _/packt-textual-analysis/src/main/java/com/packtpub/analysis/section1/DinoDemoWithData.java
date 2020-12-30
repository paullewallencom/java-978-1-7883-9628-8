package com.packtpub.analysis.section1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Erik Costlow<br/>
 * Dinosaur data from https://www.autodeskresearch.com/publications/samestats
 * @see https://controlsfx.bitbucket.io/ SpreadsheetView
 */
public class DinoDemoWithData extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Datasaurus Dozen");
        final ScatterChart<Number, Number> chart = dinoValues();

        final Node tableView = makeTableView(chart);
        final Node spreadsheet = makeSpreadsheet(chart);

        final Tab chartTab = new Tab("TableView", tableView);
        final Tab spreadsheetTab = new Tab("Spreadsheet", spreadsheet);
        final TabPane pane = new TabPane(chartTab, spreadsheetTab);
        pane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        VBox.setVgrow(pane, Priority.ALWAYS);
        final VBox vb = new VBox(chart, pane);

        Scene scene = new Scene(vb, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static ScatterChart<Number, Number> dinoValues() throws IOException {
        final NumberAxis xAxis = new NumberAxis(0, 100, 10);
        final NumberAxis yAxis = new NumberAxis(0, 100, 25);
        final ScatterChart<Number, Number> sc = new ScatterChart<>(xAxis, yAxis);
        final XYChart.Series dino = new XYChart.Series();
        sc.getData().add(dino);
        try (InputStream in = DinoDemoWithData.class.getResourceAsStream("dino.tsv");
                final InputStreamReader inReader = new InputStreamReader(in);
                final BufferedReader reader = new BufferedReader(inReader)) {
            reader.readLine();//ignore first line
            final List<XYChart.Data<Number, Number>> list = reader.lines()
                    .map(line -> line.split("\\t"))
                    .map(array -> new XYChart.Data<Number, Number>(Double.valueOf(array[1]), Double.valueOf(array[2])))
                    .collect(Collectors.toList());
            dino.getData().addAll(list);
        }
        return sc;
    }

    private TableView makeTableView(ScatterChart<Number, Number> chart) {
        final TableView<XYChart.Data<Number, Number>> table = new TableView<>();

        final TableColumn<XYChart.Data<Number, Number>, String> xCol = new TableColumn<>("X");
        xCol.setCellValueFactory((param) -> {
            return new SimpleStringProperty(String.valueOf(param.getValue().getXValue()));
        });
//            new PropertyValueFactory<>("xProperty"));
        final TableColumn<XYChart.Data<Number, Number>, String> yCol = new TableColumn<>("Y");
        yCol.setCellValueFactory((param) -> {
            return new SimpleStringProperty(String.valueOf(param.getValue().getYValue()));
        });

        table.getColumns().add(xCol);
        table.getColumns().add(yCol);

        table.setItems(chart.getData().get(0).getData());

        return table;
    }

    private SpreadsheetView makeSpreadsheet(ScatterChart<Number, Number> chart) {
        final ObservableList<XYChart.Data<Number, Number>> data = chart.getData().get(0).getData();

        final int rowCount = data.size();
        final int columnCount = 2;

        GridBase grid = new GridBase(rowCount, columnCount);

        ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
        for (int row = 0; row < grid.getRowCount(); ++row) {
            final ObservableList<SpreadsheetCell> list = FXCollections.observableArrayList();

            final XYChart.Data<Number, Number> curr = data.get(row);
            list.add(SpreadsheetCellType.DOUBLE.createCell(row, 1, 1, 1, curr.getXValue().doubleValue()));
            list.add(SpreadsheetCellType.DOUBLE.createCell(row, 1, 1, 1, curr.getYValue().doubleValue()));

            rows.add(list);
        }
        grid.setRows(rows);
        SpreadsheetView spv = new SpreadsheetView(grid);
        return spv;
    }
}
