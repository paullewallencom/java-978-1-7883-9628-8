/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.packtpub.analysis.section4;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionDetector;
import com.github.sarxos.webcam.WebcamMotionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.edges.CannyEdgeDetector;

/**
 *
 * @author erikc_000
 */
public class PatternRec extends Application{

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    private final Webcam webcam;
    private final WebcamMotionDetector detector;
    private boolean isWatching = false;

    public PatternRec() {
        webcam = Webcam.getDefault();
        webcam.open();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> webcam.close()));

        detector = new WebcamMotionDetector(webcam, 25, 1, 1000);
    }

    
    public void start(Stage primaryStage) throws Exception {
        final ImageView imageView = new ImageView();
        final Button b = new Button("Start/stop");
        b.setOnAction(event -> toggleDetector());
        final VBox vb = new VBox(imageView, b);

        final CannyEdgeDetector canny = new CannyEdgeDetector();
        detector.addMotionListener((WebcamMotionEvent wme) -> {
            BufferedImage img = wme.getCurrentImage();
            final MBFImage mbf = ImageUtilities.createMBFImage(img, true);
            mbf.processInplace(new CannyEdgeDetector());
            img = ImageUtilities.createBufferedImage(mbf, img);
            final Image recognizedImage = SwingFXUtils.toFXImage(img, null);
            Platform.runLater(() -> imageView.setImage(recognizedImage));
        });

        Scene scene = new Scene(vb, 1024, 768);
        primaryStage.setScene(scene);
        primaryStage.show();
        toggleDetector();
    }

    void toggleDetector() {
        if (isWatching) {
            detector.stop();
            isWatching = false;
        } else {
            detector.start();
            isWatching = true;
        }
    }
}
