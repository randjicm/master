/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.neuroph.netbeans.jmevisualization;

import com.jme3.math.ColorRGBA;
import java.util.ArrayList;
import org.neuroph.core.data.DataSet;

/**
 *
 * @author Milos Randjic
 */
public class DataSetVisualizationParameters {
       
    
    private DataSet dataSet;
    
    private int[] inputs;
    
    private ArrayList<ColorRGBA> outputColors;

    public DataSetVisualizationParameters(DataSet dataSet, int[] inputs, ArrayList<ColorRGBA> outputColors) {
        this.dataSet = dataSet;
        this.inputs = inputs;
        this.outputColors = outputColors;
    }

    
    public DataSetVisualizationParameters() {
    }
    
    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    public ArrayList<ColorRGBA> getOutputColors() {
        return outputColors;
    }

    public void setOutputColors(ArrayList<ColorRGBA> outputColors) {
        this.outputColors = outputColors;
    }

    public int[] getInputs() {
        return inputs;
    }

    public void setInputs(int[] inputs) {
        this.inputs = inputs;
    }
    
    

    
    
    
    
    
    
    
    
    
    
}
