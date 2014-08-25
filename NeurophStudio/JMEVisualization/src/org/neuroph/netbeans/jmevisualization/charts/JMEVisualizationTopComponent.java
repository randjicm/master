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
import java.util.Random;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.netbeans.api.settings.ConvertAsProperties;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.netbeans.jmevisualization.IOSettingsDialog;
import org.neuroph.netbeans.jmevisualization.JMEVisualization;
import org.neuroph.netbeans.jmevisualization.charts.graphs.JMEDatasetHistogram3D;
import org.neuroph.netbeans.jmevisualization.charts.graphs.JMEDatasetScatter3D;
import org.neuroph.netbeans.jmevisualization.charts.graphs.JMEWeightsHistogram3D;
import org.neuroph.netbeans.jmevisualization.concurrent.Consumer;
import org.neuroph.netbeans.jmevisualization.concurrent.Producer;
import org.neuroph.netbeans.jmevisualization.concurrent.ProducerConsumer;
import org.neuroph.netbeans.jmevisualization.concurrent.dataset.DataSetConsumer;
import org.neuroph.netbeans.jmevisualization.concurrent.dataset.DataSetProducer;
import org.neuroph.netbeans.jmevisualization.concurrent.weights.NeuralNetworkWeightsConsumer;
import org.neuroph.netbeans.jmevisualization.concurrent.weights.NeuralNetworkWeightsProducer;
import org.neuroph.netbeans.visual.NeuralNetAndDataSet;
import org.neuroph.netbeans.visual.TrainingController;
import org.neuroph.nnet.MultiLayerPerceptron;
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
    private int iterationCounter = 1;
    private ProducerConsumer producerConsumer;
    private java.awt.Canvas jmeCanvas;
    private JMEVisualization jmeVisualization;
    private boolean trainSignal = false;
    private Consumer consumer;
    private Producer producer;
    
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
     * @return 
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
     * @return 
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
            producerConsumer.startProducing();
        }
        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        visualizationPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        radioDataSet = new javax.swing.JRadioButton();
        radioWeights = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        txtInputsSize = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtOutputsSize = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtSize = new javax.swing.JTextField();

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        visualizationPanel.setBackground(new java.awt.Color(255, 255, 255));
        visualizationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(JMEVisualizationTopComponent.class, "JMEVisualizationTopComponent.visualizationPanel.border.title"))); // NOI18N
        visualizationPanel.setPreferredSize(new java.awt.Dimension(640, 480));

        javax.swing.GroupLayout visualizationPanelLayout = new javax.swing.GroupLayout(visualizationPanel);
        visualizationPanel.setLayout(visualizationPanelLayout);
        visualizationPanelLayout.setHorizontalGroup(
            visualizationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 601, Short.MAX_VALUE)
        );
        visualizationPanelLayout.setVerticalGroup(
            visualizationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 468, Short.MAX_VALUE)
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(JMEVisualizationTopComponent.class, "JMEVisualizationTopComponent.jPanel3.border.title"))); // NOI18N

        radioDataSet.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(radioDataSet);
        org.openide.awt.Mnemonics.setLocalizedText(radioDataSet, org.openide.util.NbBundle.getMessage(JMEVisualizationTopComponent.class, "JMEVisualizationTopComponent.radioDataSet.text")); // NOI18N

        radioWeights.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(radioWeights);
        org.openide.awt.Mnemonics.setLocalizedText(radioWeights, org.openide.util.NbBundle.getMessage(JMEVisualizationTopComponent.class, "JMEVisualizationTopComponent.radioWeights.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(JMEVisualizationTopComponent.class, "JMEVisualizationTopComponent.jLabel1.text")); // NOI18N

        txtInputsSize.setText(org.openide.util.NbBundle.getMessage(JMEVisualizationTopComponent.class, "JMEVisualizationTopComponent.txtInputsSize.text")); // NOI18N
        txtInputsSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtInputsSizeActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(JMEVisualizationTopComponent.class, "JMEVisualizationTopComponent.jLabel2.text")); // NOI18N

        txtOutputsSize.setText(org.openide.util.NbBundle.getMessage(JMEVisualizationTopComponent.class, "JMEVisualizationTopComponent.txtOutputsSize.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(JMEVisualizationTopComponent.class, "JMEVisualizationTopComponent.jLabel3.text")); // NOI18N

        txtSize.setText(org.openide.util.NbBundle.getMessage(JMEVisualizationTopComponent.class, "JMEVisualizationTopComponent.txtSize.text")); // NOI18N
        txtSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSizeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(radioDataSet)
                    .addComponent(radioWeights)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtInputsSize, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtOutputsSize, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSize, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(107, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(radioDataSet)
                .addGap(18, 18, 18)
                .addComponent(radioWeights)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtInputsSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtOutputsSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(visualizationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(visualizationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void txtInputsSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtInputsSizeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtInputsSizeActionPerformed

    private void txtSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSizeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSizeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JRadioButton radioDataSet;
    private javax.swing.JRadioButton radioWeights;
    private javax.swing.JTextField txtInputsSize;
    private javax.swing.JTextField txtOutputsSize;
    private javax.swing.JTextField txtSize;
    private javax.swing.JPanel visualizationPanel;
    // End of variables declaration//GEN-END:variables
    
    @Override
    public void componentOpened() {

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                jmeVisualization = new JMEVisualization();
                jmeVisualization.setWidth(getVisualizationPanel().getWidth() - 15);
                jmeVisualization.setHeight(getVisualizationPanel().getHeight() - 30);
                jmeVisualization.startApplication();

                jmeCanvas = jmeVisualization.getJmeCanvasContext().getCanvas();

                getVisualizationPanel().setLayout(new FlowLayout());
                getVisualizationPanel().add(jmeCanvas);
                getVisualizationPanel().revalidate();

            }
        });
    }
    
    @Override
    public void componentClosed() {
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

        DataSet d = new DataSet(3, 2);
        Random r = new Random();
        for (int i = 1; i <= 5000; i++) {

            double x = r.nextGaussian();
            double y = r.nextGaussian();
            double z = r.nextGaussian();
            
            double c1 = (getCategoryMembership(x, y, z, 0, 0, 0, 1, 1, 1));
            if ( c1>= 1) {
                d.addRow(new double[]{x, y, z}, new double[]{1.0, 0.0});
            } else {
                d.addRow(new double[]{x, y, z}, new double[]{0.0, 1.0});
            }

        }

        return d;
    }    
    
    public void drawSampleHistogram() {
      JMEDatasetHistogram3D jmeDataSetHistogram = new JMEDatasetHistogram3D(createSphereDataSet(), jmeVisualization);           
      jmeDataSetHistogram.createGraph();
    }
    
    public void drawDataSet(DataSet dataSet) {
        IOSettingsDialog d = IOSettingsDialog.getInstance();
        JMEDatasetScatter3D jmeDataSetScatter = new JMEDatasetScatter3D(dataSet, d.getStoredInputs(), d.getOutputColors(), jmeVisualization);
        jmeDataSetScatter.createGraph();
    }
    
    private double getCategoryMembership(double randomX, double randomY, double randomZ, double x, double y, double z, double a, double b, double c) {
        return (randomX - x) * (randomX - x) / (a * a) + (randomY - y) * (randomY - y) / (b * b) + (randomZ - z) * (randomZ - z) / (c * c);
    } 

    public java.awt.Canvas getJmeCanvas() {
        return jmeCanvas;
    }

    public void setJmeCanvas(java.awt.Canvas jmeCanvas) {
        this.jmeCanvas = jmeCanvas;
    }

    public boolean isTrainSignal() {
        return trainSignal;
    }

    public void setTrainSignal(boolean trainSignal) {
        this.trainSignal = trainSignal;
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
                int inputs = Integer.parseInt(txtInputsSize.getText());
                int outputs = Integer.parseInt(txtOutputsSize.getText());
                int dataSetSize = Integer.parseInt(txtSize.getText());
                Transferable t = e.getTransferable();
                DataFlavor dataFlavor = t.getTransferDataFlavors()[1];                              
                DataObject dataObject = (DataObject) t.getTransferData(dataFlavor);
                
                DataSet dataSet = dataObject.getLookup().lookup(DataSet.class);//get the object from lookup listener
                NeuralNetwork nnet = dataObject.getLookup().lookup(NeuralNetwork.class);//get the object from lookup listener
                        
                if (dataSet != null) {
                    
                    trainingSet = generateRandomDataSet(inputs, outputs, dataSetSize);//dataSet;
                    
                    if (radioDataSet.isSelected()) {
                        IOSettingsDialog dataSetSettings = IOSettingsDialog.getInstance();
                        dataSetSettings.initializeInformation(trainingSet, jmeVisualization);
                        dataSetSettings.setVisible(true);
                    }

                }

                if (nnet != null) {
                    neuralNetwork = new MultiLayerPerceptron(inputs,4,outputs);//nnet;
                }

                if(neuralNetwork != null && trainingSet != null){
                    
                    trainSignal = true;
                    
                    removeContent();
                    trainingPreprocessing();
                    
                    addContent();
                    
                }
                
                e.dropComplete(true);
            } catch (UnsupportedFlavorException | IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
    
    private static int randInt(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }
    
    public DataSet generateRandomDataSet(int inputsNumber, int outputsNumber, int rows) {
        
        DataSet dataSet = new DataSet(inputsNumber, outputsNumber);        
        for (int i = 1; i <= rows; i++) {            
            double[] inputs = new double[inputsNumber];           
            for (int j = 0; j < inputs.length; j++) {
                inputs[j] = randInt(-100, 100) / 100.0;
            }          
            double[] outputs = new double[outputsNumber];          
            outputs[randInt(0, outputsNumber-1)] = randInt(1, outputsNumber);
            dataSet.addRow(new DataSetRow(inputs, outputs));
        }
        return dataSet;
    }
    
    public void initializeProducerConsumer(int queueSize) {
        
        trainSignal = false;
        if (radioDataSet.isSelected()) {
            consumer = new DataSetConsumer(jmeVisualization);
            producer = new DataSetProducer(neuralNetAndDataSet);
        }
        if (radioWeights.isSelected()) {
            consumer = new NeuralNetworkWeightsConsumer(jmeVisualization);
            producer = new NeuralNetworkWeightsProducer(neuralNetAndDataSet);
        }

        producerConsumer = new ProducerConsumer(queueSize);
        producerConsumer.setConsumer(consumer);
        producerConsumer.setProducer(producer);

        producerConsumer.startConsuming();

    }
    
    /*
     * Collects all the information needed for neural network training
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
