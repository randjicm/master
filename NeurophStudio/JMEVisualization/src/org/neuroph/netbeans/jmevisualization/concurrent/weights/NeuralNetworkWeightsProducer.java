/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuroph.netbeans.jmevisualization.concurrent.weights;

import java.util.concurrent.BlockingQueue;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.netbeans.jmevisualization.concurrent.Producer;
import org.neuroph.netbeans.visual.NeuralNetAndDataSet;

/**
 *
 * @author Milos Randjic
 */
public class NeuralNetworkWeightsProducer extends Producer {

    public NeuralNetworkWeightsProducer(NeuralNetAndDataSet neuralNetAndDataSet) {
        super(neuralNetAndDataSet);
    }
    
    public NeuralNetworkWeightsProducer(BlockingQueue sharedQueue, NeuralNetAndDataSet neuralNetAndDataSet) {
        super(sharedQueue, neuralNetAndDataSet);
    }

    public void calculateNeuralNetworkAnswer() {
        
        DataSet dataSet = getNeuralNetAndDataSet().getDataSet();
        NeuralNetwork neuralNetwork = getNeuralNetAndDataSet().getNetwork();
        
        for (DataSetRow dataSetRow : dataSet.getRows()) {           
            neuralNetwork.setInput(dataSetRow.getInput());
            neuralNetwork.calculate();          
        }
        
    }

    @Override
    public void run() {

        try {
            calculateNeuralNetworkAnswer();
            getSharedQueue().put(getNeuralNetAndDataSet().getNetwork());
        } catch (InterruptedException ex) {

        }
    }
}
