/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.neuroph.netbeans.jmevisualization.concurrent.dataset;

import com.jme3.math.ColorRGBA;
import java.util.ArrayList;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.netbeans.jmevisualization.DataSetVisualizationParameters;
import org.neuroph.netbeans.jmevisualization.IOSettingsDialog;
import org.neuroph.netbeans.jmevisualization.concurrent.Producer;
import org.neuroph.netbeans.visual.NeuralNetAndDataSet;

/**
 *
 * @author Milos Randjic
 */
public class DataSetProducer extends Producer{

    public DataSetProducer(NeuralNetAndDataSet neuralNetAndDataSet) {
        super(neuralNetAndDataSet);
    }

    @Override
    public void run() {
        try {
            DataSet dataSet = getNeuralNetAndDataSet().getDataSet();
            NeuralNetwork neuralNetwork = getNeuralNetAndDataSet().getNetwork();
            
            DataSetVisualizationParameters parameters = new DataSetVisualizationParameters();
            parameters.setDataSet(dataSet);
            parameters.setInputs(IOSettingsDialog.getInstance().getStoredInputs());
            
            ArrayList<ColorRGBA> outputColors = new ArrayList<>(dataSet.size());
            
            for (DataSetRow dataSetRow : dataSet.getRows()) {
                
                neuralNetwork.setInput(dataSetRow.getInput());
                neuralNetwork.calculate();
                
                outputColors.add(getDominantOutputColor(neuralNetwork));

            }
            
            parameters.setOutputColors(outputColors);

            getSharedQueue().put(parameters);
        } catch (InterruptedException ex) {

        }
    }
    
    private ColorRGBA getDominantOutputColor(NeuralNetwork neuralNetwork){
        
        int index = 0;
        double max = Double.MIN_VALUE;
        double[] outputValues = neuralNetwork.getOutput();
        
        for (int i = 0; i < outputValues.length; i++) {            
            if(Math.abs(outputValues[i])>max){
                max = outputValues[i];
                index = i;
            }           
        }
        
        return IOSettingsDialog.getInstance().getOutputColors().get(index);
        
        
    }
    
}
