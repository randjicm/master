/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuroph.imagepreprocessing.wizard;

import imagepreprocessing.filter.ImageFilter;
import imagepreprocessing.filter.ImageFilterChain;
import imagepreprocessing.manager.ImageFilterManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.openide.util.Exceptions;

public final class ImageDataSetVisualPanel3 extends JPanel {

    /**
     * Creates new form ImageDataSetVisualPanel3
     */
    public ImageDataSetVisualPanel3() {
        initComponents();
        fillComboBox();
    }

    @Override
    public String getName() {
        return "Choose Image Preprocessing Filter";
    }
    
    public ImageFilterChain getFilterChain(){
        return imagePreprocessingPanel1.getImageFilterChain();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooserFilterChain = new javax.swing.JFileChooser();
        imagePreprocessingPanel1 = new imagepreprocessing.view.ImagePreprocessingPanel();
        jComboBoxFilterChains = new javax.swing.JComboBox();
        jButtonLoadChain = new javax.swing.JButton();
        jButtonSaveChain = new javax.swing.JButton();

        jComboBoxFilterChains.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFilterChainsActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButtonLoadChain, org.openide.util.NbBundle.getMessage(ImageDataSetVisualPanel3.class, "ImageDataSetVisualPanel3.jButtonLoadChain.text")); // NOI18N
        jButtonLoadChain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLoadChainActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButtonSaveChain, org.openide.util.NbBundle.getMessage(ImageDataSetVisualPanel3.class, "ImageDataSetVisualPanel3.jButtonSaveChain.text")); // NOI18N
        jButtonSaveChain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveChainActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(imagePreprocessingPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBoxFilterChains, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSaveChain, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonLoadChain, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(imagePreprocessingPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addComponent(jComboBoxFilterChains, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonLoadChain)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSaveChain)))
                .addContainerGap(35, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBoxFilterChainsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFilterChainsActionPerformed
        ImageFilterChain ifc = (ImageFilterChain) jComboBoxFilterChains.getSelectedItem();
        imagePreprocessingPanel1.getSelectedFiltersLM().clear();
        for (ImageFilter imageFilter : ifc.getFilters()) {
            imagePreprocessingPanel1.getSelectedFiltersLM().addElement(imageFilter);
        }
    }//GEN-LAST:event_jComboBoxFilterChainsActionPerformed

    private void jButtonLoadChainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLoadChainActionPerformed
        ImageFilterChain chain = null;
        int returnVal = jFileChooserFilterChain.showOpenDialog(this);
        File file = null;
        if (returnVal == jFileChooserFilterChain.APPROVE_OPTION) {
            file = jFileChooserFilterChain.getSelectedFile();
            try {

                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                chain = (ImageFilterChain) in.readObject();
                if (ImageFilterManager.getObject().getListOfFilters().isEmpty()) {
                    ImageFilterManager.getObject().addChain(chain);
                    fillComboBox();
                } else {
                    boolean exists = false;
                    for (ImageFilterChain fc : ImageFilterManager.getObject().getListOfFilters()) {
                        if (fc.getChainName().equals(chain.getChainName())) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        ImageFilterManager.getObject().addChain(chain);
                        fillComboBox();
                    }
                }
                in.close();
                fileIn.close();

            } catch (IOException i) {
                JOptionPane.showMessageDialog(this, "Unable to load filter chain");
                i.printStackTrace();
                return;
            } catch (ClassNotFoundException ex) {
                Exceptions.printStackTrace(ex);

            }
        }
    }//GEN-LAST:event_jButtonLoadChainActionPerformed

    private void jButtonSaveChainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveChainActionPerformed

        String chainName = JOptionPane.showInputDialog("Choose file name");
        if (chainName != null) {
            if (chainName.isEmpty()) {
                chainName = "Chain";

            }
            try {
                File f = new File("ImagePreprocessing/serialized/"
                        + chainName + ".ser");
                int i = 1;
                while (true) {
                    if (f.exists() && !f.isDirectory()) {
                        String newName = chainName + "_" + i;
                        f = new File("ImagePreprocessing/serialized/"
                                + newName + ".ser");
                        i++;
                    } else {
                        break;
                    }
                }

                FileOutputStream fileOut
                        = new FileOutputStream(f);

                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                ImageFilterChain chain = new ImageFilterChain();
                chain.setFilters((List<ImageFilter>) imagePreprocessingPanel1.getImageFilterChain().getFilters());
                chainName = f.getName().substring(0, f.getName().indexOf("."));
                chain.setChainName(chainName);
                out.writeObject(chain);
                out.close();
                fileOut.close();
                ImageFilterManager.getObject()
                        .addChain(chain);

                fillComboBox();
                jComboBoxFilterChains.setSelectedItem(chain);
                JOptionPane.showMessageDialog(this, "Filter chain is saved in ImagePreprocessing/serialized/"
                        + chainName + ".ser");
            } catch (Exception i) {
                JOptionPane.showMessageDialog(this, "Unable to save filter chain");
                i.printStackTrace();

            }
        }
    }//GEN-LAST:event_jButtonSaveChainActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private imagepreprocessing.view.ImagePreprocessingPanel imagePreprocessingPanel1;
    private javax.swing.JButton jButtonLoadChain;
    private javax.swing.JButton jButtonSaveChain;
    private javax.swing.JComboBox jComboBoxFilterChains;
    private javax.swing.JFileChooser jFileChooserFilterChain;
    // End of variables declaration//GEN-END:variables

    private void fillComboBox() {
        jComboBoxFilterChains.setModel(new DefaultComboBoxModel(ImageFilterManager.getObject().getListOfFilters().toArray()));
    }
}
