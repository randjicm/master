/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuroph.netbeans.jmevisualization;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.control.UpdateControl;
import com.jme3.scene.debug.Grid;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import java.awt.Dimension;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
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
    private JmeCanvasContext jmeCanvasContext;

    @Override
    public void simpleInitApp() {
        rootNode.addControl(new UpdateControl());
        flyCam.setDragToRotate(true);
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(200);
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

    public void attachChild(final Geometry geometry) {
        rootNode.getControl(UpdateControl.class).enqueue(new Callable<Geometry>() {

            @Override
            public Geometry call() throws Exception {

                rootNode.attachChild(geometry);
                getJmeCanvasContext().getCanvas().requestFocus();
                return null;
            }
        });
    }

    public void detachAllChildren() {

        rootNode.getControl(UpdateControl.class).enqueue(new Callable<Geometry>() {

            @Override
            public Geometry call() throws Exception {
                rootNode.detachAllChildren();
                getJmeCanvasContext().getCanvas().requestFocus();
                return null;
            }
        });
    }

    public void attachCoordinateSystem(final int range, final int gridDensity) {
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

    public void attachHistoramGrid(final int maxBarsLength, final int numberOfBarRows) {
        rootNode.getControl(UpdateControl.class).enqueue(new Callable<Geometry>() {

            @Override
            public Geometry call() throws Exception {

                Geometry xPlane = (Geometry) new Geometry("xPlane", new Grid(maxBarsLength * (numberOfBarRows + 1) + 2, (numberOfBarRows) * 5 - 3, 5)).rotate(-1.57f, 0, 0);
                Geometry yPlane = (Geometry) new Geometry("yPlane", new Grid(maxBarsLength * (numberOfBarRows + 1) + 2, 40, 5)).rotate(-1.57f, -1.57f, 0);
                Geometry zPlane = (Geometry) new Geometry("zPlane", new Grid(40, (numberOfBarRows) * 5 - 3, 5)).rotate(0, 0, 0);

                Material m = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
                xPlane.setMaterial(m);
                yPlane.setMaterial(m);
                zPlane.setMaterial(m);

                yPlane.move(0, 0, -100);
                zPlane.move(0, 0, -100);

                rootNode.attachChild(xPlane);
                rootNode.attachChild(yPlane);
                rootNode.attachChild(zPlane);

                getJmeCanvasContext().getCanvas().requestFocus();

                return null;
            }
        });

    }

    public void updateModelBound() {
        rootNode.getControl(UpdateControl.class).enqueue(new Callable<Geometry>() {

            @Override
            public Geometry call() throws Exception {

                rootNode.updateModelBound();
                return null;
            }
        });
    }

    public Geometry getChild(final String name) {
        try {
            return rootNode.getControl(UpdateControl.class).enqueue(new Callable<Geometry>() {

                @Override
                public Geometry call() throws Exception {
                    Geometry s = (Geometry) rootNode.getChild(name);
                    return s;

                }
            }).get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public void updateGeometry(final Geometry geometry) {
        rootNode.getControl(UpdateControl.class).enqueue(new Callable<Geometry>() {

            @Override
            public Geometry call() throws Exception {
                geometry.updateModelBound();
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
