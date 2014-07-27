/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuroph.netbeans.jmevisualization.concurrent.weights;

import java.util.concurrent.BlockingQueue;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.netbeans.jmevisualization.JMEVisualization;
import org.neuroph.netbeans.jmevisualization.charts.graphs.JMEWeightsHistogram3D;
import org.neuroph.netbeans.jmevisualization.concurrent.Consumer;

/**
 *
 * @author Milos Randjic
 */
public class NeuralNetworkWeightsConsumer extends Consumer {

    public NeuralNetworkWeightsConsumer(JMEVisualization jmeVisualization) {
        super(jmeVisualization);
    }

    public NeuralNetworkWeightsConsumer(BlockingQueue sharedQueue, JMEVisualization jmeVisualization) {
        super(sharedQueue, jmeVisualization);
    }

    @Override
    public void run() {
        while (true) {
            try {
                NeuralNetwork nnet = (NeuralNetwork) getSharedQueue().take();
                drawWeightsHistogram(nnet);
            } catch (InterruptedException ex) {
            }
        }
    }

    private void drawWeightsHistogram(NeuralNetwork neuralNetwork) {
        JMEWeightsHistogram3D jmeWeightsHistogram3D = new JMEWeightsHistogram3D(neuralNetwork, getJmeVisualization());
        jmeWeightsHistogram3D.createGraph();
    }

}
