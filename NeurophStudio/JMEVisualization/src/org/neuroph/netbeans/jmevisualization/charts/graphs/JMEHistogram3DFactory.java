/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuroph.netbeans.jmevisualization.charts.graphs;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Cylinder;
import java.beans.Beans;
import org.neuroph.netbeans.jmevisualization.JMEVisualization;
import org.nugs.graph3d.api.Histogram3DFactory;
import org.nugs.graph3d.api.Histogram3DProperties;
import org.nugs.graph3d.api.Point3D;

/**
 *
 * @author Milos Randjic
 */
public class JMEHistogram3DFactory implements Histogram3DFactory<Void, Point3D.Float>{
    
    private final JMEVisualization jmeVisualization;    

    public JMEHistogram3DFactory(JMEVisualization jmeVisualization) {
        this.jmeVisualization = jmeVisualization;        
    }
    
    @Override
    public Void createHistogram3D(Point3D.Float[] points, Histogram3DProperties prop) {

        Beans.setDesignTime(false);
        jmeVisualization.detachAllChildren();
        //jmeVisualization.attachCoordinateSystem(1, 10);
        jmeVisualization.attachHistoramGrid(prop.getMaxBarsSize());
        Vector3f[] data = new Vector3f[points.length];
        
        float maxZ = 0;
        for (int i = 1; i < points.length; i++) {
            data[i] = new Vector3f(points[i].getX(), points[i].getY(), points[i].getZ());
            if (Math.abs(data[i].getZ()) > maxZ) {
                maxZ = data[i].getZ();
            }
        }

        for (int i = 1; i < points.length; i++) {
            //x-layers count, y-connections count, z-weight value
            float barHeight = data[i].z * 100 / maxZ;
            final Geometry cylinderGeometry = new Geometry("cylinder " + i, new Cylinder(32, 32, prop.getRadius(), barHeight, true));
            Material m = new Material(jmeVisualization.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            
            if (data[i].z >= 0) {
                m.setColor("Color", ColorRGBA.Red);
            } else {
                m.setColor("Color", ColorRGBA.Blue);
            }
            
            cylinderGeometry.setMaterial(m);
            cylinderGeometry.move(data[i].x * 20, data[i].y * 5, barHeight/2);
            
            jmeVisualization.attachChild(cylinderGeometry);
            
        }
        
        return null;
    }

    @Override
    public Void createHistogram3D(Point3D.Float[] points) {
        return createHistogram3D(points, new Histogram3DProperties());
    }    
}