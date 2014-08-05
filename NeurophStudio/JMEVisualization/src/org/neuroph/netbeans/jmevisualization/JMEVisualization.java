/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuroph.netbeans.jmevisualization;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.control.UpdateControl;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import java.awt.Dimension;
import java.util.concurrent.Callable;

/**
 * JME based Visualization Component
 *
 * @author Milos Randjic
 * @author Zoran Sevarac
 */
public class JMEVisualization extends SimpleApplication {

    private int width;
    private int height;
    private JmeCanvasContext jmeCanvasContext;

    @Override
    public void simpleInitApp() {
        rootNode.addControl(new UpdateControl());
        rootNode.rotate(1.57f, 0, 3.14f);
        flyCam.setDragToRotate(true);
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(150);
        flyCam.setZoomSpeed(10);
    }

    public JmeCanvasContext getJmeCanvasContext() {
        return jmeCanvasContext;
    }

    public void setJmeCanvasContext(JmeCanvasContext jmeCanvasContext) {
        this.jmeCanvasContext = jmeCanvasContext;
    }

    // ovde se na top component stavlja jme canvas koji crta grafike
    public void startApplication() {
        AppSettings appSettings = new AppSettings(true);
        appSettings.setWidth(getWidth());
        appSettings.setHeight(getHeight());

        setSettings(appSettings);
        createCanvas();

        jmeCanvasContext = (JmeCanvasContext) getContext();
        jmeCanvasContext.setSystemListener(this);
        jmeCanvasContext.getCanvas().setPreferredSize(new Dimension(getWidth(), getHeight()));

        this.startCanvas();
    }
    
    public void attachChildFromAnotherThread(final Geometry geometry) {
        rootNode.getControl(UpdateControl.class).enqueue(new Callable<Geometry>() {

            @Override
            public Geometry call() throws Exception {

                rootNode.attachChild(geometry);
                getJmeCanvasContext().getCanvas().requestFocus();
                return null;
            }
        });
    }

    public void detachAllChildrenFromAnotherThread() {

        rootNode.getControl(UpdateControl.class).enqueue(new Callable<Geometry>() {

            @Override
            public Geometry call() throws Exception {
                rootNode.detachAllChildren();   
                getJmeCanvasContext().getCanvas().requestFocus();
                return null;
            }
        });
    }
    
    public void attachCoordinateSystem(final int range, final int gridDensity){
        rootNode.getControl(UpdateControl.class).enqueue(new Callable<Geometry>() {

            @Override
            public Geometry call() throws Exception {
                                
                CoordinateSystem coordinateSys = new CoordinateSystem(range);
                Geometry coordinateSystem = coordinateSys.generatePlanes(gridDensity);
                coordinateSystem.setMaterial(new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md"));
                rootNode.attachChild(coordinateSystem);
                getJmeCanvasContext().getCanvas().requestFocus();
                
                return null;
            }
        });
    }

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

}
