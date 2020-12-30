/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.packtpub.analysis.section1;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import java.util.List;
import java.util.Properties;

/**
 * @Author Erik Costlow
 * @see Stanford CoreNLP https://github.com/stanfordnlp/CoreNLP/tree/master/src/edu/stanford/nlp/pipeline/demo
 * @see Bernardo Santos https://github.com/brnrds/SentimentAnalysis-CoreNLP-Examples
 */
public class SentimentDemo {

    public static void main(String[] args) {
        final String sentences = "bad. not good. ok. not bad. good. good excellent. "
            + "I hate the way that vegetables taste, they are bad.";

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        Annotation annotation = new Annotation(sentences);

        pipeline.annotate(annotation);

        //For debugging:
        // pipeline.prettyPrint(annotation, System.out);

        List<CoreMap> sentencesCoreMap = annotation.get(
                CoreAnnotations.SentencesAnnotation.class
        );

        System.out.println("sentences are " + sentencesCoreMap);
        sentencesCoreMap.forEach(sentenceMap -> {
            String sentimentName = sentenceMap.get(SentimentCoreAnnotations.SentimentClass.class);
            System.out.println(sentenceMap + " :: " + sentimentName);
        });
    }

}
