/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.packtpub.analysis.section4;

import com.github.sarxos.webcam.Webcam;
import java.awt.image.BufferedImage;
import java.util.List;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.face.detection.CLMDetectedFace;
import org.openimaj.image.processing.face.detection.CLMFaceDetector;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.FaceDetector;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.image.processing.face.util.CLMDetectedFaceRenderer;
import org.openimaj.image.processing.face.util.SimpleDetectedFaceRenderer;

/**
 *
 * @author Erik Costlow
 */
public class FacialDetection extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Webcam webcam = Webcam.getDefault();
        webcam.open();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> webcam.close()));
        final Button b = new Button("Take picture");

        final ImageView normalPicture = new ImageView();
        final ImageView detectedFace = new ImageView();
        final ImageView pointFace = new ImageView();

        FaceDetector<DetectedFace, FImage> basicFace = new HaarCascadeDetector(40);
        final SimpleDetectedFaceRenderer simpleFace = new SimpleDetectedFaceRenderer();
        final CLMFaceDetector coolDetector = new CLMFaceDetector();
        final CLMDetectedFaceRenderer coolRenderer = new CLMDetectedFaceRenderer();

        b.setOnAction(event -> {
            BufferedImage image = webcam.getImage();
            //final Image img = new Image("");
            final Image img = SwingFXUtils.toFXImage(image, null);
            normalPicture.setImage(img);

            final MBFImage simpleScanImage = ImageUtilities.createMBFImage(image, true);
            List<DetectedFace> faces = basicFace.detectFaces(simpleScanImage.flatten());
            if (!faces.isEmpty()) {
                final DetectedFace face = faces.get(0);
                simpleFace
                        .drawDetectedFace(simpleScanImage, 2, face);
                final BufferedImage newBuf = ImageUtilities.createBufferedImage(simpleScanImage);
                final Image recognizedImage = SwingFXUtils.toFXImage(newBuf, null);
                detectedFace.setImage(recognizedImage);
            }

            final MBFImage coolScanImage = ImageUtilities.createMBFImage(image, true);
            final List<CLMDetectedFace > coolFaces = coolDetector.detectFaces(coolScanImage.flatten());
            System.out.println(coolFaces.size());
            if (!coolFaces.isEmpty()) {
                final CLMDetectedFace face = coolFaces.get(0);
                coolRenderer.drawDetectedFace(coolScanImage, 2, face);
                final BufferedImage newBuf = ImageUtilities.createBufferedImage(coolScanImage);
                final Image recognizedImage = SwingFXUtils.toFXImage(newBuf, null);
                pointFace.setImage(recognizedImage);
            }
        });

        final GridPane grid = new GridPane();
        grid.add(new Label("Normal Photo"), 0, 0);
        grid.add(normalPicture, 0, 1);
        grid.add(new Label("Detected Face"), 1, 0);
        grid.add(detectedFace, 1, 1);
        grid.add(new Label("Detected Face"), 1, 2);
        grid.add(pointFace, 1, 3);
        final VBox vb = new VBox(grid, b);

        Scene scene = new Scene(vb, 1024, 768);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
