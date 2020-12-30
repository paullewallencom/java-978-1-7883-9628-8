/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.packtpub.analysis.section4;

import java.io.IOException;
import java.net.URL;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.edges.CannyEdgeDetector;

/**
 *
 * Shortened copy from http://openimaj.org/tutorial/processing-your-first-image.html
 */
public class PatternRecognition {
    public static void main(String[] args) throws IOException{
        MBFImage image = ImageUtilities.readMBF(new URL("http://static.openimaj.org/media/tutorial/sinaface.jpg"));
        image.processInplace(new CannyEdgeDetector());
        DisplayUtilities.display(image);
    }
}
