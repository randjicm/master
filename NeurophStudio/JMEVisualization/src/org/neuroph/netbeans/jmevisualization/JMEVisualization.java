/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuroph.netbeans.jmevisualization;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.control.UpdateControl;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Line;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import java.awt.Dimension;
import java.util.concurrent.Callable;
import org.nugs.graph3d.api.Range;

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
                
                Arrow xArrow = new Arrow(new Vector3f(Vector3f.UNIT_X.x*110, Vector3f.UNIT_X.y*110, Vector3f.UNIT_X.z*110));
                Arrow yArrow = new Arrow(new Vector3f(Vector3f.UNIT_Y.x*110, Vector3f.UNIT_Y.y*110, Vector3f.UNIT_Y.z*110));
                Arrow zArrow = new Arrow(new Vector3f(Vector3f.UNIT_Z.x*110, Vector3f.UNIT_Z.y*110, Vector3f.UNIT_Z.z*110));
               
                xArrow.setLineWidth(4f);
                yArrow.setLineWidth(4f);
                zArrow.setLineWidth(4f);
                
                Geometry xArrowG = new Geometry("xArrow", xArrow);
                Geometry yArrowG = new Geometry("yArrow", yArrow);
                Geometry zArrowG = new Geometry("zArrow", zArrow);
                
                Material xMaterial = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
                xMaterial.setColor("Color", ColorRGBA.Red);
                
                Material yMaterial = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
                yMaterial.setColor("Color", ColorRGBA.Green);
                
                Material zMaterial = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
                zMaterial.setColor("Color", ColorRGBA.Blue);
                
                xArrowG.setMaterial(xMaterial);
                yArrowG.setMaterial(yMaterial);
                zArrowG.setMaterial(zMaterial);
                
                rootNode.attachChild(xArrowG);
                rootNode.attachChild(yArrowG);
                rootNode.attachChild(zArrowG);
                rootNode.attachChild(coordinateSystem);
                getJmeCanvasContext().getCanvas().requestFocus();

                return null;
            }
        });
    }
    
//        public void attachCoordinateSystem(final Range rangeX, final Range rangeY, final Range rangeZ, final int gridDensity, final boolean displayCoordinateAxis) {
//        rootNode.getControl(UpdateControl.class).enqueue(new Callable<Geometry>() {
//
//            @Override
//            public Geometry call() throws Exception {
//                int maxX;
//                int maxY;
//                int maxZ;
//                
//                if(Math.abs(rangeX.getMax())>Math.abs(rangeX.getMin())){
//                    maxX = (int) Math.abs(rangeX.getMax());
//                }else{
//                    maxX = (int) Math.abs(rangeX.getMin());
//                }
//                
//                if(Math.abs(rangeY.getMax())>Math.abs(rangeY.getMin())){
//                    maxY = (int) Math.abs(rangeY.getMax());
//                }else{
//                    maxY = (int) Math.abs(rangeY.getMin());
//                }
//                
//                if(Math.abs(rangeZ.getMax())>Math.abs(rangeZ.getMin())){
//                    maxZ = (int) Math.abs(rangeZ.getMax());
//                }else{
//                    maxZ = (int) Math.abs(rangeZ.getMin());
//                }
//                
//                
//                Geometry xPlane = new Geometry("xPlane", new Grid(maxX*gridDensity, maxX*gridDensity, gridDensity));
//                Geometry yPlane = new Geometry("yPlane", new Grid(maxY*gridDensity, maxY*gridDensity, gridDensity));
//                Geometry zPlane = new Geometry("zPlane", new Grid(maxZ*gridDensity, maxZ*gridDensity, gridDensity));
//                
//                Material m = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
//                xPlane.setMaterial(m);
//                yPlane.setMaterial(m);
//                zPlane.setMaterial(m);
//                
//                int totalXLength = ((int) (Math.abs(rangeX.getMin())+ Math.abs(rangeX.getMax())))*100;
//                int totalYLength = ((int) (Math.abs(rangeY.getMin())+ Math.abs(rangeY.getMax())))*100;
//                int totalZLength = ((int) (Math.abs(rangeZ.getMin())+ Math.abs(rangeZ.getMax())))*100;
//                
//                
//                rootNode.attachChild(xPlane);
//                rootNode.attachChild(yPlane);
//                rootNode.attachChild(zPlane);
//                
//                
////                CoordinateSystem coordinateSys = new CoordinateSystem(range);
////                Geometry coordinateSystem = coordinateSys.generatePlanes(gridDensity);
////                coordinateSystem.setMaterial(new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md"));
////                rootNode.attachChild(coordinateSystem);
//                getJmeCanvasContext().getCanvas().requestFocus();
//
//                return null;
//            }
//        });
//    }

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
