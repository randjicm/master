/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.neuroph.netbeans.jmevisualization.concurrent.simulation;

import com.jme3.math.ColorRGBA;
import java.util.ArrayList;
import java.util.List;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.netbeans.jmevisualization.Combinatorics;
import org.neuroph.netbeans.jmevisualization.DataSetVisualizationParameters;
import org.neuroph.netbeans.jmevisualization.IOSettingsDialog;
import org.neuroph.netbeans.jmevisualization.concurrent.Producer;
import org.neuroph.netbeans.visual.NeuralNetAndDataSet;

/**
 *
 * @author Milos Randjic
 */
public class TrainingSimulatorProducer extends Producer{
    
        private final List<Double[]> simulatedInputs;
        
        public TrainingSimulatorProducer(NeuralNetAndDataSet neuralNetAndDataSet) {
        super(neuralNetAndDataSet);
        simulatedInputs = Combinatorics.Variations.generateVariations(generateSetValues(100, 0.02), neuralNetAndDataSet.getNetwork().getInputsCount(), true);
    }

    @Override
    public void run() {
        try {
            
            
            NeuralNetwork neuralNetwork = getNeuralNetAndDataSet().getNetwork();
            DataSet simulatedDataSet = new DataSet(neuralNetwork.getInputsCount());
            
            for (Double[] input : simulatedInputs) {
                
                double[] compatibleInput = new double[input.length];
                for (int i = 0; i < input.length; i++) {
                    compatibleInput[i] = input[i];
                }
                simulatedDataSet.addRow(compatibleInput);
            }
            
            DataSetVisualizationParameters parameters = new DataSetVisualizationParameters();
            parameters.setDataSet(simulatedDataSet);
            parameters.setInputs(IOSettingsDialog.getInstance().getStoredInputs());
            
            ArrayList<ColorRGBA> outputColors = new ArrayList<>(simulatedDataSet.size());
            
            for (DataSetRow dataSetRow : simulatedDataSet.getRows()) {
                
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
        
        return IOSettingsDialog.getInstance().getOutputColors()[index];
        
        
    }
    
    /*
     * Generates set values from [-k,k] domain, in order to simulate all neural network inputs.
     * This set is later used for generating variations with repetition of class k=numberOfinputs.
     * For each variation (in our case simulated input) we choose exactly 2 inputs for 2D visualization
     */
    private ArrayList<Double> generateSetValues(int size, double coef) {//100, 1/100
        ArrayList<Double> setValues = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            double value = 1 - i * coef;
            setValues.add(value);
        }
        return setValues;
    }
    
}
