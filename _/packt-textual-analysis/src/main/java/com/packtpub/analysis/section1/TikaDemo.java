/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.packtpub.analysis.section1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ToXMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 *
 * @see https://tika.apache.org/1.16/examples.html#Parsing_using_the_Auto-Detect_Parser
 */
public class TikaDemo {

    public static void main(String[] args) throws IOException, SAXException, TikaException {
        //autoParseAsText();
        autoParseAsXml();
    }

    private static void autoParseAsText() throws IOException, SAXException, TikaException {
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        try (InputStream stream = TikaDemo.class.getClassLoader().getResourceAsStream("s1-textMining.pptx")) {
            parser.parse(stream, handler, metadata);
            final String s = handler.toString();
            
            System.out.println("===Autodetecting the PPTX===");
            System.out.println(s);
            System.out.println("===End Autodetecting the PPTX===");
        }
    }

    private static void autoParseAsXml() throws IOException, SAXException, TikaException {
        AutoDetectParser parser = new AutoDetectParser();
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ContentHandler handler = new ToXMLContentHandler(bout, "UTF-8");
        
        Metadata metadata = new Metadata();
        try (InputStream stream = TikaDemo.class.getClassLoader().getResourceAsStream("s1-textMining.pptx")) {
            parser.parse(stream, handler, metadata);
            final String s = bout.toString();
            
            System.out.println("===XML the PPTX===");
            System.out.println(s);
            System.out.println("===End XML the PPTX===");
        }
    }
    
}
