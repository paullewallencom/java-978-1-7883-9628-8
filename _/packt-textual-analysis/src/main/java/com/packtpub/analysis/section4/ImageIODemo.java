/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.packtpub.analysis.section4;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

/**
 *
 * @author Erik Costlow
 */
public class ImageIODemo {

    public static void main(String[] args) throws IOException {
        System.out.println("Image formats that can be processed:");
        Arrays.stream(ImageIO.getReaderFormatNames())
                .map(str -> "  " + str)
                .forEach(System.out::println);

        
        try (InputStream in = ImageIODemo.class
                .getResourceAsStream("packtPub_mapt.jpg")) {
            final BufferedImage imageIn = ImageIO.read(in);

            convert(imageIn);
            
            shrink(imageIn);
            
            subImage(imageIn);
        }
        
        
    }
    
    private static void convert(BufferedImage imageIn) throws IOException{
        final Path outputPath = Paths.get("target", "converted.png");
        System.out.println("Converting to " + outputPath);
        try( final OutputStream out = Files.newOutputStream(outputPath)){
        final Graphics2D graphics = imageIn.createGraphics();
            graphics.setTransform(AffineTransform.getRotateInstance(.6));
            graphics.setFont(new Font("Arial", Font.BOLD, 24));
            graphics.setColor(Color.YELLOW);
            graphics.drawString("Converted!", 5, 20);
            graphics.dispose();
            ImageIO.write(imageIn, "PNG", out);
        }
    }

    private static void shrink(BufferedImage original) throws IOException {
        final Path outputPath = Paths.get("target", "shrunk.png");
        try (final OutputStream out = Files.newOutputStream(outputPath)) {
            System.out.println("Shrinking to " + outputPath);

            final int newWidth=original.getWidth() / 4;
            final int newHeight=original.getHeight() / 4;
            final Image scaled = original.getScaledInstance(newWidth, newHeight,
                    Image.SCALE_SMOOTH);
            
            BufferedImage shrunkImage = new BufferedImage(newWidth, newHeight,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D newGraphics = shrunkImage.createGraphics();
            newGraphics.drawImage(scaled, 0, 0, null);
            newGraphics.dispose();
            
            ImageIO.write(shrunkImage, "PNG", out);
        }
    }
    
    private static void subImage(BufferedImage original) throws IOException {
        final String format = "jpg";
        final Path outputPath = Paths.get("target", "subImage." + format);
        try (final OutputStream out = Files.newOutputStream(outputPath)) {
            System.out.println("Making subImage to " + outputPath);

            final int startX = 170, startY = 210,
                    width = 200, height = 100;
            
            final BufferedImage sub = original.getSubimage(startX, startY, width, height);
            final ImageWriter writer = ImageIO.getImageWritersByFormatName(format).next();
            ImageWriteParam iwp = writer.getDefaultWriteParam();
            iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            iwp.setCompressionQuality(.15f);   // 0 means high compress, 1 high quality
            ImageIO.write(sub, format, out);
        }
    }
}
