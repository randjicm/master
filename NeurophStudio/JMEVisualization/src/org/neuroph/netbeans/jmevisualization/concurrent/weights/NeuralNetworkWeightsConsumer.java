/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuroph.netbeans.jmevisualization.concurrent.weights;

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
   
    @Override
    public void run() {
        
        while (true) {
            try {
                NeuralNetwork neuralNetwork = (NeuralNetwork) getSharedQueue().take();
                new JMEWeightsHistogram3D(neuralNetwork, getJmeVisualization()).createGraph();                
            } catch (InterruptedException ex) {
            }
        }     
    }
}
