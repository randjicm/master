package org.neuroph.netbeans.jmevisualization.charts.graphs;

import com.jme3.math.ColorRGBA;
import java.util.ArrayList;
import org.neuroph.core.data.DataSet;
import org.neuroph.netbeans.charts.graphs3d.Graph3DBuilder;
import org.neuroph.netbeans.jmevisualization.JMEVisualization;
import org.neuroph.netbeans.jmevisualization.charts.providers.DatasetDataProvider3D;
import org.nugs.graph2d.api.Attribute;
import org.nugs.graph3d.api.Point3D;
import org.nugs.graph3d.api.Scatter3DProperties;

/**
 * Uses JMEScatter3DFactory to create scatter graph for dataset
 * 
 * @author Milos Randjic
 */
public class JMEDatasetScatter3D extends Graph3DBuilder<Void, Point3D.Float>{

    private DataSet dataset;
    private int[] inputs;
    private ArrayList<ColorRGBA> outputColors;
    private JMEVisualization jmeVisualization;
    private Scatter3DProperties properties;
    

    
    public JMEDatasetScatter3D(DataSet dataset, JMEVisualization jmeVisualization) {
        super();
        dataProvider3D = new DatasetDataProvider3D(dataset);
        this.dataset = dataset;
        this.jmeVisualization = jmeVisualization;
    }

    public JMEDatasetScatter3D(DataSet dataset, int[] inputs, ArrayList<ColorRGBA> outputColors, JMEVisualization jmeVisualization) {
        super();
        dataProvider3D = new DatasetDataProvider3D(dataset);
        this.dataset = dataset;
        this.inputs = inputs;
        this.outputColors = outputColors;
        this.jmeVisualization = jmeVisualization;
    }

    public DataSet getDataset() {
        return dataset;
    }

    public void setDataset(DataSet dataset) {
        this.dataset = dataset;
    }
    
     public Scatter3DProperties getProperties() {
        return properties;
    }

    public void setProperties(Scatter3DProperties properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "Scatter";
    }
        
    @Override
    public Void createGraph() { 
        setAttribute1(new Attribute(inputs[0], false, "Attribute"));
        setAttribute2(new Attribute(inputs[1], false, "Attribute"));
        setAttribute3(new Attribute(inputs[2], false, "Attribute"));
        
        properties = new Scatter3DProperties();
        properties.setDotSize(0.6f);
        properties.setxAxeLabel(attribute1.getLabel());
        properties.setyAxeLabel(attribute2.getLabel());
        properties.setzAxeLabel(attribute3.getLabel());  
        properties.setOutputColors(outputColors);

        // get points to display
        Point3D.Float[] points3D = (Point3D.Float[]) dataProvider3D.getData(attribute1, attribute2, attribute3);       
        
        // create jme scatter graph with these points
        JMEScatter3DFactory jmeScatterFactory = new JMEScatter3DFactory(jmeVisualization);
        jmeScatterFactory.createScatter3D(points3D, properties);

        return null;
    }   

    public int[] getInputs() {
        return inputs;
    }

    public void setInputs(int[] inputs) {
        this.inputs = inputs;
    }

    public JMEVisualization getJmeVisualization() {
        return jmeVisualization;
    }

    public void setJmeVisualization(JMEVisualization jmeVisualization) {
        this.jmeVisualization = jmeVisualization;
    }
}