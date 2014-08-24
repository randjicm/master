/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.neuroph.netbeans.jmevisualization.charts.graphs;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.netbeans.charts.graphs3d.Graph3DBuilder;
import org.neuroph.netbeans.jmevisualization.JMEVisualization;
import org.neuroph.netbeans.jmevisualization.charts.providers.WeightsDataProvider3D;
import org.nugs.graph2d.api.Attribute;
import org.nugs.graph3d.api.Point3D;

/**
 *
 * @author Milos Randjic
 */
public class JMEWeightsHistogram3D extends Graph3DBuilder<Void, Point3D.Float>{
    
    private NeuralNetwork neuralNetwork;
    private JMEVisualization jmeVisualization;
    
    public JMEWeightsHistogram3D(NeuralNetwork neuralNetwork, JMEVisualization jmeVisualization){
        super();
        dataProvider3D = new WeightsDataProvider3D(neuralNetwork);
        this.neuralNetwork = neuralNetwork;
        this.jmeVisualization = jmeVisualization;
        
    }

    public JMEWeightsHistogram3D(JMEVisualization jmeVisualization) {
        super();       
        this.jmeVisualization = jmeVisualization;
        this.jmeVisualization.getRootNode().rotate(1.57f, 0, 3.14f);
    }

    public NeuralNetwork getNeuralNetwork() {
        return neuralNetwork;
    }

    public void setNeuralNetwork(NeuralNetwork neuralNetwork) {
        this.neuralNetwork = neuralNetwork;
        dataProvider3D = new WeightsDataProvider3D(this.neuralNetwork);
    }

    public JMEVisualization getJmeVisualization() {
        return jmeVisualization;
    }

    public void setJmeVisualization(JMEVisualization jmeVisualization) {
        this.jmeVisualization = jmeVisualization;
    }
    
    @Override
    public String toString() {
        return "Weights Histogram";
    }
    
    @Override
    public Void createGraph() {
        setAttribute1(new Attribute(0, false, "Attribute"));
        setAttribute2(new Attribute(1, false, "Attribute"));
        setAttribute3(new Attribute(2, false, "Attribute"));
        
        // get points to display
        Point3D.Float[] points3D = (Point3D.Float[]) dataProvider3D.getData(attribute1, attribute2, attribute3);
        
        // create jme scatter graph with these points
        JMEHistogram3DFactory jmeHistogramFactory = new JMEHistogram3DFactory(jmeVisualization);
        jmeHistogramFactory.createHistogram3D(points3D);
        jmeVisualization.getJmeCanvasContext().getCanvas().requestFocus();
        return null;
    }
    
    
}
