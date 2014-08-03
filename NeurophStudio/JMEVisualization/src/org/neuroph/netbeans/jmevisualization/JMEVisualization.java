/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuroph.netbeans.jmevisualization;


import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.UpdateControl;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import javax.swing.JPanel;
import org.openide.util.Exceptions;

/**
 * JME based Visualization Component
 * 
 * @author Milos Randjic
 * @author Zoran Sevarac
 */
public class JMEVisualization extends SimpleApplication {

    private int width;
    private int height;
    private static JPanel visualizationPanel;
    private ScheduledThreadPoolExecutor executor;
    
    @Override
    public void simpleInitApp() {
        rootNode.addControl(new UpdateControl());                     
        rootNode.rotate(1.57f, 0, 3.14f);
        flyCam.setDragToRotate(true);
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(80);
        flyCam.setZoomSpeed(10);     
        
//        CoordinateSystem coordinateSys = new CoordinateSystem(1);
//        Geometry coordinateSystem = coordinateSys.generatePlanes(10);
//        coordinateSystem.setMaterial(new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"));
//        rootNode.attachChild(coordinateSystem); // rootNode from  SimpleApplication
        
    }
        
    private JmeCanvasContext jmeCanvasContext;

    public JmeCanvasContext getJmeCanvasContext() {
        return jmeCanvasContext;
    }

    public void setJmeCanvasContext(JmeCanvasContext jmeCanvasContext) {
        this.jmeCanvasContext = jmeCanvasContext;
    }
           
    // ovde se na top component stavlja jme canvas koji crta grafike
    public void startApplication() {
        AppSettings settings = new AppSettings(true);
        settings.setWidth(getWidth());
        settings.setHeight(getHeight());

        setSettings(settings);
        createCanvas();

        jmeCanvasContext = (JmeCanvasContext) getContext();
        jmeCanvasContext.setSystemListener(this);
        jmeCanvasContext.getCanvas().setPreferredSize(new Dimension(getWidth(), getHeight()));

        this.startCanvas();
    }
    
//    public void addGeometry(final Geometry geometry) {
//        
//        
//        rootNode.getControl(UpdateControl.class).enqueue(new Callable<Geometry>() {
//                
//                @Override
//                public Geometry call() throws Exception {
//                    rootNode.attachChild(geometry.clone());
//                    return null;
//                }
//            });   
//    }
    
   
    public int getWidth() {
        return width;
    }

    public void setWidth(int aWidth) {
        width = aWidth;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int aHeight) { 
        height = aHeight;
    }
   
    public static JPanel getVisualizationPanel() {
        return visualizationPanel;
    }

    public static void setVisualizationPanel(JPanel visualizationPanel) {
        JMEVisualization.visualizationPanel = visualizationPanel;
    }

    public ScheduledThreadPoolExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(ScheduledThreadPoolExecutor executor) {
        this.executor = executor;
    }

}
