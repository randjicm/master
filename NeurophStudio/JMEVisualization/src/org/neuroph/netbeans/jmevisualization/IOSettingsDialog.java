/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuroph.netbeans.jmevisualization;

import com.jme3.math.ColorRGBA;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import org.neuroph.core.data.DataSet;
import org.neuroph.netbeans.jmevisualization.charts.graphs.JMEDatasetScatter3D;

/**
 *
 * @author Milos Randjic
 */
public class IOSettingsDialog extends javax.swing.JDialog {

    /**
     * Creates new form InputSettngsDialog
     */
    private IOSettingsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(null);
    }

    private static IOSettingsDialog instance;
    private ArrayList<ColorRGBA> outputColors;
    private JMEVisualization jmeVisualization;
    private DataSet dataSet;

    public static IOSettingsDialog getInstance() {
        if (instance == null) {
            instance =  new IOSettingsDialog(null, true);
        } 
            return instance;
        
    }

    private int[] inputs;

    public void storeInputs(int[] inputs) {
        this.inputs = inputs;
    }

    public int[] getStoredInputs() {
        return inputs;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        comboX = new javax.swing.JComboBox();
        comboY = new javax.swing.JComboBox();
        comboZ = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(IOSettingsDialog.class, "IOSettingsDialog.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(IOSettingsDialog.class, "IOSettingsDialog.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(IOSettingsDialog.class, "IOSettingsDialog.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(IOSettingsDialog.class, "IOSettingsDialog.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(comboX, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(comboY, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(comboZ, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(comboX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(comboY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(comboZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        int x = comboX.getSelectedIndex();
        int y = comboY.getSelectedIndex();
        int z = comboZ.getSelectedIndex();

        if (x == y || x == z || y == z) {
            JOptionPane.showMessageDialog(this, "Please select different inputs.");
            comboX.setSelectedIndex(0);
            comboY.setSelectedIndex(0);
            comboZ.setSelectedIndex(0);
        } else {
            storeInputs(new int[]{x, y, z});
            drawDataSet();           
            dispose();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IOSettingsDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                IOSettingsDialog dialog = new IOSettingsDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    public void initializeInformation(DataSet dataSet, JMEVisualization jmeVisualization) {
        this.dataSet = dataSet;
        this.jmeVisualization = jmeVisualization;
        String[] inputNames = new String[dataSet.getInputSize()];

        for (int i = 0; i < inputNames.length; i++) {
            int k = i + 1;
            inputNames[i] = "Input " + k;

        }

        comboX.setModel(new DefaultComboBoxModel(inputNames));
        comboY.setModel(new DefaultComboBoxModel(inputNames));
        comboZ.setModel(new DefaultComboBoxModel(inputNames));
        
        outputColors = new ArrayList<>(dataSet.getOutputSize());//ColorRGBA[dataSet.getOutputSize()];
        for (int i = 0; i < dataSet.getOutputSize(); i++) {
            float r = (i+1)/dataSet.getOutputSize();
            float g = 1-(i+1)/dataSet.getOutputSize();
            float b = (i+1)*0.3f;
            float a = 0.3f;
            outputColors.add(new ColorRGBA(r,g,b,a));

        }
        
        
    }
    
    private void drawDataSet() {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                JMEDatasetScatter3D jmeDataSetScatter = new JMEDatasetScatter3D(dataSet, getStoredInputs(), calculateOutputColors(), jmeVisualization);
                jmeDataSetScatter.createGraph();
            }
        });
        t.start();

    }
    
    public ArrayList<ColorRGBA> calculateOutputColors(){
        ArrayList<ColorRGBA> colors = new ArrayList<>();
        
        for (int i = 0; i < dataSet.size(); i++) {
            
            double[] outputValues = dataSet.getRowAt(i).getDesiredOutput();
            int index = 0;
            double max = Double.MIN_VALUE;
            for (int j = 0; j < outputValues.length; j++) {
                if (Math.abs(outputValues[j]) > max) {
                    max = outputValues[j];
                    index = j;
                }

            }
            colors.add(getOutputColors().get(index));
            
        }
        return colors;
    }
    
   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox comboX;
    private javax.swing.JComboBox comboY;
    private javax.swing.JComboBox comboZ;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

    public ArrayList<ColorRGBA> getOutputColors() {
        return outputColors;
    }

    public void setOutputColors(ArrayList<ColorRGBA> outputColors) {
        this.outputColors = outputColors;
    }
}
