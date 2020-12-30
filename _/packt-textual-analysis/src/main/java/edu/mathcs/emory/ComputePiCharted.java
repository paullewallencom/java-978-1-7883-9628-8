/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mathcs.emory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 * http://www.mathcs.emory.edu/~cheung/Courses/170/Syllabus/07/compute-pi.html
 *
 * @author Emory University Math Department
 */
public class ComputePiCharted extends Application {

    private static final double PCT = .0008;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Pi Test");
        final NumberAxis xAxis = new NumberAxis(0, 1, .1);
        final NumberAxis yAxis = new NumberAxis(0, 1, .1);
        final LineChart<Number, Number> lineChart = new LineChart(xAxis, yAxis);
        final ScatterChart<Number, Number> chart = new ScatterChart<>(xAxis, yAxis);

        lineChart.getStylesheets().addAll(getClass().getResource("style.css").toExternalForm());
        final StackPane stacked = new StackPane(chart, lineChart);
        VBox.setVgrow(chart, Priority.ALWAYS);
        final Button button = new Button("Save image");
        button.setOnAction(handler -> {
            final WritableImage image = stacked.snapshot(new SnapshotParameters(), null);
            final BufferedImage swingImage = SwingFXUtils.fromFXImage(image, null);
            try {
                ImageIO.write(swingImage, "PNG", new File("linechart.png"));
            } catch (IOException ex) {
                Logger.getLogger(ComputePiCharted.class.getName()).log(Level.SEVERE, "Unable to write image", ex);
            }
        });
        final VBox pane = new VBox(stacked, button);

        Scene scene = new Scene(pane, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        new Thread(() -> doPi(chart, lineChart)).start();
    }

    private void doPi(ScatterChart<Number, Number> scatter, LineChart<Number, Number> lineChart) {
        final Random random = new Random();
        int nThrows = 0;
        int nSuccess = 0;

        final XYChart.Series<Number, Number> pi = new XYChart.Series();
        pi.setName("Pi");
        pi.setNode(new Circle(.25, Paint.valueOf("#ffff00")));
        final XYChart.Series notPi = new XYChart.Series();
        notPi.setName("Not Pi");

        double x, y;
        //PACKT NOTE: There are a million data points below, so don't visualize.
        for (int i = 0; i < 1000000; i++) {
            x = Math.random();      // Throw a dart			   
            y = Math.random();
            nThrows++;
            final XYChart.Series which;
            if (x * x + y * y <= 1) {
                nSuccess++;
                which = pi;
            } else {
                which = notPi;
            }

            final double showPct = random.nextDouble();
            if (showPct < PCT) {
                XYChart.Data<Number, Number> num = new XYChart.Data<>(x, y);
                which.getData().add(num);
            }
        }

        final double guessed = 4 * (double) nSuccess / (double) nThrows;
        System.out.println("Pi/4 = " + (double) nSuccess / (double) nThrows);
        System.out.println("Pi = " + guessed);
        System.out.println("Actual Pi is " + Math.PI);
        System.out.println(String.format("  Difference is %f%%", ((1 - guessed / Math.PI) * 100)));
        Platform.runLater(() -> {
            scatter.getData().setAll(pi, notPi);

            // 
            final XYChart.Series piNew = new XYChart.Series();
            final XYChart.Series notPiNew = new XYChart.Series();
            piNew.getData().setAll(pi.getData().get(0), pi.getData().get(pi.getData().size()-1));
            notPiNew.getData().setAll(notPi.getData().get(0), notPi.getData().get(notPi.getData().size()-1));
            lineChart.getData().setAll(piNew, notPiNew);
        });
    }
}
