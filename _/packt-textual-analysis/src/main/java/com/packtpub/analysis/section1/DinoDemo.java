package com.packtpub.analysis.section1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Erik Costlow
 * Dinosaur data from https://www.autodeskresearch.com/publications/samestats
 */
public class DinoDemo extends Application {
    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Datasaurus Dozen");
        final ScatterChart<Number, Number> chart = dinoValues();
        VBox.setVgrow(chart, Priority.ALWAYS);
        final VBox pane = new VBox(chart);

        Scene scene = new Scene(pane, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private static ScatterChart<Number, Number> dinoValues() throws IOException{
        final NumberAxis xAxis = new NumberAxis(0, 100, 10);
        final NumberAxis yAxis = new NumberAxis(0, 100, 25);
        final ScatterChart<Number, Number> sc = new ScatterChart<>(xAxis, yAxis);
        final XYChart.Series dino = new XYChart.Series();
        sc.getData().add(dino);
        try(InputStream in = DinoDemo.class.getResourceAsStream("dino.tsv");
                final InputStreamReader inReader = new InputStreamReader(in);
                final BufferedReader reader = new BufferedReader(inReader)){
            reader.readLine();//ignore first line
            final List<XYChart.Data<Number, Number>> list = reader.lines()
                    .peek(System.out::println)
                    .map(line -> line.split("\\t"))
                    .map(array -> new XYChart.Data<Number, Number>(Double.valueOf(array[1]), Double.valueOf(array[2])))
                    .collect(Collectors.toList());
            dino.getData().addAll(list);
        }
        return sc;
    }
}
