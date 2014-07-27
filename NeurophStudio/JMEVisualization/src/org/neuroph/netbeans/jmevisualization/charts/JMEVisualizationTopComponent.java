package org.neuroph.netbeans.jmevisualization.charts;

import java.awt.FlowLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.netbeans.api.settings.ConvertAsProperties;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.netbeans.jmevisualization.JMEVisualization;
import org.neuroph.netbeans.jmevisualization.charts.graphs.JMEDatasetHistogram3D;
import org.neuroph.netbeans.jmevisualization.charts.graphs.JMEDatasetScatter3D;
import org.neuroph.netbeans.jmevisualization.charts.graphs.JMEWeightsHistogram3D;
import org.neuroph.netbeans.jmevisualization.concurrent.Consumer;
import org.neuroph.netbeans.jmevisualization.concurrent.Producer;
import org.neuroph.netbeans.jmevisualization.concurrent.ProducerConsumer;
import org.neuroph.netbeans.jmevisualization.concurrent.weights.NeuralNetworkWeightsConsumer;
import org.neuroph.netbeans.jmevisualization.concurrent.weights.NeuralNetworkWeightsProducer;
import org.neuroph.netbeans.visual.NeuralNetAndDataSet;
import org.neuroph.netbeans.visual.TrainingController;
import org.neuroph.nnet.learning.LMS;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.neuroph.netbeans.jmevisualization//JMEVisualization//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "JMEVisualizationTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "org.neuroph.netbeans.jmevisualization.JMEVisualizationTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_JMEVisualizationAction",
        preferredID = "JMEVisualizationTopComponent")
@Messages({
    "CTL_JMEVisualizationAction=JMEVisualization",
    "CTL_JMEVisualizationTopComponent=JMEVisualization Window",
    "HINT_JMEVisualizationTopComponent=This is a JMEVisualization window"
})
public final class JMEVisualizationTopComponent extends TopComponent implements LearningEventListener{
    
    private static JMEVisualizationTopComponent instance;
    private static final String PREFERRED_ID = "JMEVisualizationTopComponent";
    private InstanceContent content;
    private AbstractLookup aLookup;
    private DropTargetListener dtListener;
    private DropTarget dropTarget;
    private int acceptableActions = DnDConstants.ACTION_COPY;
    private NeuralNetwork neuralNetwork;
    private DataSet trainingSet;
    private NeuralNetAndDataSet neuralNetAndDataSet;
    private TrainingController trainingController;
    private Thread firstCalculation = null;
    private int iterationCounter = 0;
    private ArrayList<Double[]> neuralNetworkInputs;
    private ArrayList<Double> setValues;
    private ProducerConsumer producerConsumer;
    private boolean trainingPermission = false;
    
    private JMEVisualizationTopComponent() {
        initComponents();
        setName(Bundle.CTL_JMEVisualizationTopComponent());
        setToolTipText(Bundle.HINT_JMEVisualizationTopComponent());
        content = new InstanceContent();
        aLookup = new AbstractLookup(content);
        this.dtListener = new DTListener();
        this.dropTarget = new DropTarget(
                this,
                this.acceptableActions,
                this.dtListener,
                true);
    }
    
    /**
     * Gets default instance. Do not use directly: reserved for *.settings files
     * only, i.e. deserialization routines; otherwise you could get a
     * non-deserialized instance. To obtain the singleton instance, use
     * {@link #findInstance}.
     */
    public static synchronized JMEVisualizationTopComponent getDefault() {
        if (instance == null) {
            instance = new JMEVisualizationTopComponent();
        }
        return instance;
    }
    
    /**
     * Obtain the MultiLayerPerceptronClassificationSampleTopComponent instance.
     * Never call {@link #getDefault} directly!
     */
    public static synchronized JMEVisualizationTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(JMEVisualizationTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof JMEVisualizationTopComponent) {
            return (JMEVisualizationTopComponent) win;
        }
        Logger.getLogger(JMEVisualizationTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID
                + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }
    
    @Override
    public Lookup getLookup() {
        return new ProxyLookup(new Lookup[]{
            super.getLookup(),
            aLookup
        });
    }
    
    @Override
    public void handleLearningEvent(LearningEvent le) {
        
        iterationCounter++;

        if (iterationCounter % 10 == 0) {
            
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    producerConsumer.startThreading();
                    jmeCanvas.requestFocus();//request focus to force repaint
                }
            });

        }

    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        visualizationPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        visualizationPanel.setBackground(new java.awt.Color(255, 255, 255));
        visualizationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(JMEVisualizationTopComponent.class, "JMEVisualizationTopComponent.visualizationPanel.border.title"))); // NOI18N
        visualizationPanel.setPreferredSize(new java.awt.Dimension(640, 480));

        javax.swing.GroupLayout visualizationPanelLayout = new javax.swing.GroupLayout(visualizationPanel);
        visualizationPanel.setLayout(visualizationPanelLayout);
        visualizationPanelLayout.setHorizontalGroup(
            visualizationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 661, Short.MAX_VALUE)
        );
        visualizationPanelLayout.setVerticalGroup(
            visualizationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(JMEVisualizationTopComponent.class, "JMEVisualizationTopComponent.jPanel1.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(JMEVisualizationTopComponent.class, "JMEVisualizationTopComponent.jButton1.text")); // NOI18N
        jButton1.setEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, org.openide.util.NbBundle.getMessage(JMEVisualizationTopComponent.class, "JMEVisualizationTopComponent.jButton2.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2)
                    .addComponent(jButton1))
                .addContainerGap(97, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jButton1)
                .addGap(18, 18, 18)
                .addComponent(jButton2)
                .addContainerGap(364, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(visualizationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 665, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(visualizationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                drawSampleDataset();
                jmeCanvas.requestFocus(); // request focus to force repaint
            }
        });          
         
    }//GEN-LAST:event_jButton2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel visualizationPanel;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {            
                jmeVisualization = new JMEVisualization();
                jmeVisualization.setWidth(getVisualizationPanel().getWidth()-15);
                jmeVisualization.setHeight(getVisualizationPanel().getHeight()-30);

                jmeVisualization.startApplication();
                jmeCanvas = jmeVisualization.getJmeCanvasContext().getCanvas();
                
                getVisualizationPanel().setLayout(new FlowLayout());
                getVisualizationPanel().add(jmeCanvas);
                getVisualizationPanel().revalidate();
            }
        });    

    }
    
    java.awt.Canvas jmeCanvas;
    JMEVisualization jmeVisualization;

    @Override
    public void componentClosed() {
        trainingPermission = false;
        jmeVisualization.stop();
        getVisualizationPanel().remove(jmeCanvas); // proveri da li ovo radi ocekivano!!!!        
        getVisualizationPanel().revalidate();
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
    
    public JPanel getVisualizationPanel(){
        return visualizationPanel;
    }

    public void drawWeightsHistogram(NeuralNetwork neuralNetwork) {
      JMEWeightsHistogram3D jmeWeightsHistogram3D = new JMEWeightsHistogram3D(neuralNetwork, jmeVisualization);           
      jmeWeightsHistogram3D.createGraph();
    }
    
    
    // created demo dataset
    public DataSet createSphereDataSet() {

        DataSet d = new DataSet(3, 1);
        Random r = new Random();
        for (int i = 1; i <= 8000; i++) {

            double x = Math.round(r.nextGaussian() * 100);
            double y = Math.round(r.nextGaussian() * 100);
            double z = Math.round(r.nextGaussian() * 100);

            if ((getCategoryMembership(x, y, z, 0, 0, 0, 100, 100, 100)) >= 0.5) {
                //d.addRow(new double[]{x, y, z}, new double[]{1.0});
            } else {
                d.addRow(new double[]{x, y, z}, new double[]{0.0});
            }

        }

        return d;
    }    
    
    public void drawSampleHistogram() {
      JMEDatasetHistogram3D jmeDataSetHistogram = new JMEDatasetHistogram3D(createSphereDataSet(), jmeVisualization);           
      jmeDataSetHistogram.createGraph();
    }
    
    public void drawSampleDataset() {
        JMEDatasetScatter3D jmeDataSetScatter = new JMEDatasetScatter3D(createSphereDataSet(), jmeVisualization);
        jmeDataSetScatter.createGraph();
    }
    
    private double getCategoryMembership(double randomX, double randomY, double randomZ, double x, double y, double z, double a, double b, double c) {
        return (randomX - x) * (randomX - x) / (a * a) + (randomY - y) * (randomY - y) / (b * b) + (randomZ - z) * (randomZ - z) / (c * c);
    } 

    class DTListener implements DropTargetListener {

        @Override
        public void dragEnter(DropTargetDragEvent dtde) {
            dtde.acceptDrag(dtde.getDropAction());
        }

        @Override
        public void dragExit(DropTargetEvent dte) {
        }

        @Override
        public void dragOver(DropTargetDragEvent dtde) {
            dtde.acceptDrag(dtde.getDropAction());
        }

        @Override
        public void dropActionChanged(DropTargetDragEvent dtde) {
            dtde.acceptDrag(dtde.getDropAction());
        }

        @Override
        public void drop(DropTargetDropEvent e) {
            try {
                
                Transferable t = e.getTransferable();
                DataFlavor dataFlavor = t.getTransferDataFlavors()[1];                              
                DataObject dataObject = (DataObject) t.getTransferData(dataFlavor);
                
                DataSet dataSet = dataObject.getLookup().lookup(DataSet.class);//get the object from lookup listener
                NeuralNetwork nnet = dataObject.getLookup().lookup(NeuralNetwork.class);//get the object from lookup listener
                        
                if (dataSet != null) {
                    trainingSet = dataSet;
                }

                if (nnet != null) {
                    neuralNetwork = nnet;
                }

                if(neuralNetwork != null && trainingSet != null){
                    
                    trainingPermission = true;
                    removeContent();
                    trainingPreprocessing();
                    
                    addContent();
                    
                    Producer producer = new NeuralNetworkWeightsProducer(neuralNetAndDataSet);
                    Consumer consumer = new NeuralNetworkWeightsConsumer(jmeVisualization);
                    
                    producerConsumer = new ProducerConsumer(1000, producer, consumer);
                    
                    JMEVisualizationTopComponent.this.requestActive();
                    
                }
                
                e.dropComplete(true);
            } catch (UnsupportedFlavorException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
    
    /*
     * Collects all the information needed for training neural network
     */
    public void trainingPreprocessing() {
        neuralNetAndDataSet = new NeuralNetAndDataSet(neuralNetwork, trainingSet);
        trainingController = new TrainingController(neuralNetAndDataSet);
        neuralNetwork.getLearningRule().addListener(this);//adds learning rule to observer
        trainingController.setLmsParams(0.7, 0.01, 0);
        LMS learningRule = (LMS) this.neuralNetAndDataSet.getNetwork().getLearningRule();
        if (learningRule instanceof MomentumBackpropagation) {
            ((MomentumBackpropagation) learningRule).setMomentum(0.2);
        }
    }
    
    public void removeContent() {
        try {
            content.remove(neuralNetAndDataSet);
            content.remove(trainingController);
            JMEVisualizationTopComponent.this.requestActive();
        } catch (Exception ex) {
        }
        
    }
    
    public void addContent(){
        
        content.add(neuralNetAndDataSet);
        content.add(trainingController);
        JMEVisualizationTopComponent.this.requestActive();
        
    }    
}
