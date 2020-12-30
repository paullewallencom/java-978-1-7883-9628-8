package org.deeplearning4j.examples.recurrent.regression;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import org.datavec.api.records.reader.SequenceRecordReader;
import org.datavec.api.records.reader.impl.csv.CSVSequenceRecordReader;
import org.datavec.api.split.NumberedFileInputSplit;
import org.datavec.api.util.ClassPathResource;
import org.deeplearning4j.datasets.datavec.SequenceRecordReaderDataSetIterator;
import org.deeplearning4j.eval.RegressionEvaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.logging.Level;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.nd4j.linalg.factory.Nd4j;

/**
 * Copied from https://github.com/deeplearning4j/dl4j-examples
 *
 * This example was inspired by Jason Brownlee's regression examples for Keras,
 * found here:
 * http://machinelearningmastery.com/time-series-prediction-lstm-recurrent-neural-networks-python-keras/
 *
 * It demonstrates single time step regression using LSTM
 */
public class SingleTimestepRegressionExampleCommonsMath {

    private static final Logger LOGGER = LoggerFactory.getLogger(SingleTimestepRegressionExampleCommonsMath.class);

    public static void main(String[] args) throws Exception {

        File baseDir = new ClassPathResource("/rnnRegression").getFile();
        int miniBatchSize = 32;

        // ----- Load the training data -----
        SequenceRecordReader trainReader = new CSVSequenceRecordReader(0, ";");
        trainReader.initialize(new NumberedFileInputSplit(baseDir.getAbsolutePath() + "/passengers_train_%d.csv", 0, 0));

        //For regression, numPossibleLabels is not used. Setting it to -1 here
        DataSetIterator trainIter = new SequenceRecordReaderDataSetIterator(trainReader, miniBatchSize, -1, 1, true);

        SequenceRecordReader testReader = new CSVSequenceRecordReader(0, ";");
        testReader.initialize(new NumberedFileInputSplit(baseDir.getAbsolutePath() + "/passengers_test_%d.csv", 0, 0));
        DataSetIterator testIter = new SequenceRecordReaderDataSetIterator(testReader, miniBatchSize, -1, 1, true);

        //Create data set from iterator here since we only have a single data set
        DataSet trainData = trainIter.next();
        DataSet testData = testIter.next();

        //Normalize data, including labels (fitLabel=true)
        NormalizerMinMaxScaler normalizer = new NormalizerMinMaxScaler(0, 1);
        normalizer.fitLabel(true);
        normalizer.fit(trainData);              //Collect training data statistics

        normalizer.transform(trainData);
        normalizer.transform(testData);

        // ----- Configure the network -----
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(140)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .iterations(1)
                .weightInit(WeightInit.XAVIER)
                .updater(Updater.NESTEROVS).momentum(0.9)
                .learningRate(0.0015)
                .list()
                .layer(0, new GravesLSTM.Builder().activation(Activation.TANH).nIn(1).nOut(10)
                        .build())
                .layer(1, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY).nIn(10).nOut(1).build())
                .build();

        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();

        net.setListeners(new ScoreIterationListener(20));

        // ----- Train the network, evaluating the test set performance at each epoch -----
        int nEpochs = 300;

        for (int i = 0; i < nEpochs; i++) {
            net.fit(trainData);
            LOGGER.info("Epoch " + i + " complete. Time series evaluation:");

            //Run regression evaluation on our single column input
            RegressionEvaluation evaluation = new RegressionEvaluation(1);
            INDArray features = testData.getFeatureMatrix();

            INDArray lables = testData.getLabels();
            INDArray predicted = net.output(features, false);

            evaluation.evalTimeSeries(lables, predicted);

            //Just do sout here since the logger will shift the shift the columns of the stats
            System.out.println(evaluation.stats());
        }

        //Init rrnTimeStemp with train data and predict test data
        net.rnnTimeStep(trainData.getFeatureMatrix());
        INDArray predicted = net.rnnTimeStep(testData.getFeatureMatrix());

        //Non-example prediction, forecast more data.
        final int[] shape = predicted.shape();
        System.out.println("Shape is " + Arrays.toString(shape));
        final double[] numbers = predicted.data().asDouble();
        System.out.println("Data is " + Arrays.toString(numbers));
        //Number of data points coming out will match
        //number of points going in.
        final int TOTAL = 10;
        final INDArray demo = Nd4j.create(shape[0], shape[1], TOTAL);
        final double[] newFakeData = new double[TOTAL];
        for (int i = 0; i < TOTAL; i++) {
            final double d = numbers[numbers.length - 1 - i];
            newFakeData[i] = d;
        }
        demo.data().setData(newFakeData);
        INDArray externalPrediction = net.rnnTimeStep(demo);
        //END PREDICT

        //Revert data back to original values for plotting
        normalizer.revert(trainData);
        normalizer.revert(testData);
        normalizer.revertLabels(predicted);
        normalizer.revertLabels(externalPrediction);

        INDArray trainFeatures = trainData.getFeatures();
        INDArray testFeatures = testData.getFeatures();
        //Create plot with out data
        XYSeriesCollection c = new XYSeriesCollection();
        createSeries(c, trainFeatures, 0, "Train data");
        createSeries(c, testFeatures, 99, "Actual test data");
        createSeries(c, predicted, 100, "Predicted test data");
        //External prediction also
        createSeries(c, externalPrediction, 100 + predicted.shape()[2], "Predicted test data");

        plotViaCommonsMath(c);

        plotDataset(c);

        LOGGER.info("----- Example Complete -----");
    }

    private static void createSeries(XYSeriesCollection seriesCollection, INDArray data, int offset, String name) {
        int nRows = data.shape()[2];
        XYSeries series = new XYSeries(name);
        for (int i = 0; i < nRows; i++) {
            series.add(i + offset, data.getDouble(i));
        }
        seriesCollection.addSeries(series);
    }

    /**
     * Generate an xy plot of the datasets provided.
     */
    private static void plotDataset(XYSeriesCollection c) {

        String title = "Regression example";
        String xAxisLabel = "Timestep";
        String yAxisLabel = "Number of passengers";
        PlotOrientation orientation = PlotOrientation.VERTICAL;
        boolean legend = true;
        boolean tooltips = false;
        boolean urls = false;
        JFreeChart chart = ChartFactory.createXYLineChart(title, xAxisLabel, yAxisLabel, c, orientation, legend, tooltips, urls);

        // get a reference to the plot for further customisation...
        final XYPlot plot = chart.getXYPlot();

        // Auto zoom to fit time series in initial window
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoRange(true);

        JPanel panel = new ChartPanel(chart);

        JFrame f = new JFrame();
        f.add(panel);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.pack();
        f.setTitle("Training Data");

        RefineryUtilities.centerFrameOnScreen(f);
        f.setVisible(true);
    }

    private static void plotViaCommonsMath(XYSeriesCollection c) {
        XYSeries series = new XYSeries("LinearPlot");
        try (InputStream in = SingleTimestepRegressionExampleCommonsMath.class.getClassLoader().getResourceAsStream("rnnRegression/passengers_raw.csv");
                BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            final SimpleRegression regression = new SimpleRegression(true);
            int lines=0;
            while (reader.ready()) {
                final String line = reader.readLine();
                lines++;
                final String[] split = line.split(",");
                final double x = lines;
                final double y = Double.parseDouble(split[0]);
                regression.addData(x,y);
            }
            final double intercept = regression.getIntercept();
            final double slope = regression.getSlope();
            
            System.out.println(String.format("y = %fx + %f", slope, intercept));
            series.add(0, intercept);
            series.add(lines, intercept + lines * slope);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(SingleTimestepRegressionExampleCommonsMath.class.getName()).log(Level.SEVERE, "Unable to read", ex);
        }
        c.addSeries(series);
    }
}
