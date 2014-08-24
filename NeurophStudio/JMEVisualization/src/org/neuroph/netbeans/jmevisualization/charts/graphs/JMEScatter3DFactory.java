package org.neuroph.netbeans.jmevisualization.charts.graphs;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import java.beans.Beans;
import org.neuroph.netbeans.jmevisualization.JMEVisualization;
import org.nugs.graph3d.api.Point3D;
import org.nugs.graph3d.api.Range;
import org.nugs.graph3d.api.Scatter3DFactory;
import org.nugs.graph3d.api.Scatter3DProperties;

/**
 * Creates JME Scatter Graphs
 * 
 * @author Milos Randjic
 */
public class JMEScatter3DFactory implements Scatter3DFactory<Void, Point3D.Float>{
    
    private final JMEVisualization jmeVisualization;    

    public JMEScatter3DFactory(JMEVisualization jmeVisualization) {
        this.jmeVisualization = jmeVisualization;
    }
    
    public void initializeObjects(Point3D.Float[] points, Scatter3DProperties prop){
        Beans.setDesignTime(false);  
        jmeVisualization.detachAllChildren();
        jmeVisualization.attachCoordinateSystem(1, 10);
        
        Vector3f[] data = new Vector3f[points.length];     
        for (int i = 0; i < points.length; i++) {           
            data[i] = new Vector3f(points[i].getX(), points[i].getY(), points[i].getZ());              
        }
               
        Sphere sphere = new Sphere(32, 32, prop.getDotSize());
        for (int i = 0; i < points.length; i++) {
            Geometry sphereGeometry = new Geometry("sphere " + i, sphere);
            Material m = new Material(jmeVisualization.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            sphereGeometry.setMaterial(m);
            sphereGeometry.move(data[i].x*100, data[i].y*100, data[i].z*100);
        
            jmeVisualization.attachChild(sphereGeometry);
        }
    }
    
    
    @Override
    public Void createScatter3D(Point3D.Float[] points, Scatter3DProperties prop) {
        
//        Beans.setDesignTime(false);  
//            
//        for (int i = 0; i < points.length; i++) {
//            Geometry sphere = jmeVisualization.getChild("sphere " + i);
//            ColorRGBA color = prop.getoutputColors().get(i);
//            sphere.getMaterial().setColor("Color", color );
//            //jmeVisualization.updateGeometry(sphere);
//        }
//        jmeVisualization.update();
////        jmeVisualization.updateModelBound();           
        Beans.setDesignTime(false);  
        jmeVisualization.detachAllChildren();
        jmeVisualization.attachCoordinateSystem(1, 10);
        
        Vector3f[] data = new Vector3f[points.length];     
        for (int i = 0; i < points.length; i++) {           
            data[i] = new Vector3f(points[i].getX(), points[i].getY(), points[i].getZ());              
        }
               
        Sphere sphere = new Sphere(32, 32, prop.getDotSize());
        for (int i = 0; i < points.length; i++) {
            Geometry sphereGeometry = new Geometry("sphere " + i, sphere);
            Material m = new Material(jmeVisualization.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            m.setColor("Color", prop.getoutputColors().get(i));
            sphereGeometry.setMaterial(m);
            
            sphereGeometry.move(data[i].x*100, data[i].y*100, data[i].z*100);
        
            jmeVisualization.attachChild(sphereGeometry);
        }
        
        return null;
    }

    @Override
    public Void createScatter3D(Point3D.Float[] points) {
        Range r = new  Range(-100, 100);
        Scatter3DProperties prop = new Scatter3DProperties(r,r,r,1f);
        return createScatter3D(points, prop);
    }
    
   
    
}
