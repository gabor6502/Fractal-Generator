package GUI;

import SystemOperators.Controller;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;

import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.border.Border;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author elang
 */
public final class FracGUI extends javax.swing.JFrame implements Notifyer{
    private final Dimension SCRN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();//dimensions of user's computer screen 
    private Controller controller; 
    
    //for when user enters bad input and the field needs to reflect this
    private Border badInput = BorderFactory.createLineBorder(Color.red);
    private Border defaultBorder;
    
    private JScrollBar vertBar; //vertical bar to control when a new fractal is rendered
    
    public FracGUI(Controller c) {
        super("GUI");
        setLocation((SCRN_SIZE.width)/6, (SCRN_SIZE.height)/6); //set where the gui will appear on screen
        setResizable(false);

        controller = c;
        initComponents();
        
        //undoes something in initComponents (Netbeans wouldn't let me change it :[ )
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE); //just have it make gui invisible
        
        setInputs();
        defaultBorder = zoomField.getBorder(); //just any border as is setup
        
        //now that components are initialized, can set the vertical scroll bar of the notification area to force scroll to bottom
        vertBar = jScrollPane1.getVerticalScrollBar();
        
        //set the icon for the gui
        setIconImage((new ImageIcon(".\\icon.png")).getImage());
        
    }//end constructor

    @Override
    public void setVisible(boolean vis){
        super.setVisible(vis); //call super to make the frame visible
        
        if(vis) //if want to be visible, need to get scroll bar in notification area to the bottom
            vertBar.setValue(vertBar.getMaximum());
    }//end setVisible
    
    @Override
    public void notify(String msg){
        notificationArea.append(msg+"\n");
        vertBar.setValue(vertBar.getMaximum()); //scroll notification area to bottom (freshest message)
    }//end notify
    
    //fills all inputs with current data from controller
    public void setInputs(){
        
        //fill boxes
        fractalComboBox.setSelectedIndex(controller.getCurrentFractal());
        colourMethodComboBox.setSelectedIndex(controller.getCurrentColouringMethod());
        
        //fill fields
        iterationsField.setText(""+controller.getIterations());
        zoomField.setText(""+controller.getZoom());
        sampleFactorField.setText(""+controller.getSupersampleFactor());
        
        //radio button
        AAradioButton.setSelected(controller.antialiasing());
        
    }//end setInputs
    
    private void massSetEnabled(boolean flag){ //sets enable on everything needed with a given flag
            iterationsField.setEnabled(flag);
            zoomField.setEnabled(flag);
            sampleFactorField.setEnabled(flag);
            
            AAradioButton.setEnabled(flag);
            
            fractalComboBox.setEnabled(flag);
            colourMethodComboBox.setEnabled(flag);
            
            helpButton.setEnabled(flag);
            infoButton.setEnabled(flag);
            restoreButton.setEnabled(flag);
            saveButton.setEnabled(flag);
            goButton.setEnabled(flag);
    }//end massSetEnabled
    
        /**
     * This method is called from within the constructor to initialize the form.WARNING: Do NOT modify this code.
     * The content of this method is always
 regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        infoButton = new javax.swing.JButton();
        helpButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        goButton = new javax.swing.JButton();
        restoreButton = new javax.swing.JButton();
        titleLabel = new javax.swing.JLabel();
        fractalBoxLabel = new javax.swing.JLabel();
        fractalComboBox = new javax.swing.JComboBox<>();
        colourMethodComboBox = new javax.swing.JComboBox<>();
        colouringComboBoxLabel = new javax.swing.JLabel();
        AAradioButton = new javax.swing.JRadioButton();
        jSeparator2 = new javax.swing.JSeparator();
        iterationsLabel = new javax.swing.JLabel();
        iterationsField = new javax.swing.JTextField();
        sampleFactorField = new javax.swing.JTextField();
        zoomLabel = new javax.swing.JLabel();
        zoomField = new javax.swing.JTextField();
        statusUpdateLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        notificationArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        infoButton.setText("Info");
        infoButton.setToolTipText("Information about the application");
        infoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoButtonActionPerformed(evt);
            }
        });

        helpButton.setText("Help");
        helpButton.setToolTipText("How to use the app");
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpButtonActionPerformed(evt);
            }
        });

        saveButton.setText("Save");
        saveButton.setToolTipText("Save the image on screen");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        goButton.setText("GO!");
        goButton.setToolTipText("render the fractal you specified");
        goButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goButtonActionPerformed(evt);
            }
        });

        restoreButton.setText("Restore");
        restoreButton.setToolTipText("restores all fields/selections to their previous values");
        restoreButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restoreButtonActionPerformed(evt);
            }
        });

        titleLabel.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        titleLabel.setText("Fractal Viewer V2");

        fractalBoxLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        fractalBoxLabel.setText("Fractal");

        fractalComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mandelbrot", "Burning Ship" }));
        fractalComboBox.setToolTipText("select a fractal to draw");

        colourMethodComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Escape Times", "Potential", "Normal Map", "White" }));
        colourMethodComboBox.setToolTipText("select a colouring method");

        colouringComboBoxLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        colouringComboBoxLabel.setText("Colouring");

        AAradioButton.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        AAradioButton.setText("Antialias");
        AAradioButton.setToolTipText("toggles antialiasing");

        iterationsLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        iterationsLabel.setText("Iterations");
        iterationsLabel.setToolTipText(null);

        iterationsField.setToolTipText("enter number of iterations here");

        sampleFactorField.setToolTipText("recommended less than 16x for now");

        zoomLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        zoomLabel.setText("Zoom");

        zoomField.setToolTipText("enter in a zoom value here");

        statusUpdateLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        statusUpdateLabel.setText("Status Updates");
        statusUpdateLabel.setToolTipText("updates on the render appear here");

        notificationArea.setColumns(20);
        notificationArea.setRows(5);
        jScrollPane1.setViewportView(notificationArea);

        jLabel1.setText("(factor)");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2)
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(titleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(helpButton, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(infoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(goButton, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(restoreButton, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(fractalBoxLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fractalComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(colouringComboBoxLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(colourMethodComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(zoomLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(zoomField))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(AAradioButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(sampleFactorField, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(iterationsLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(iterationsField, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(statusUpdateLabel)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane1))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(titleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(helpButton)
                    .addComponent(infoButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fractalBoxLabel)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(fractalComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(statusUpdateLabel)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(colouringComboBoxLabel)
                            .addComponent(colourMethodComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(AAradioButton)
                            .addComponent(sampleFactorField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(iterationsLabel)
                            .addComponent(iterationsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(zoomLabel)
                            .addComponent(zoomField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton)
                    .addComponent(goButton)
                    .addComponent(restoreButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        controller.helpMessage();
    }//GEN-LAST:event_helpButtonActionPerformed

    private void infoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoButtonActionPerformed
        
        JOptionPane.showMessageDialog(null, 
              "Fractal Viewer V2: 2022 revision"
            + "\nDesigned and programmed by Elan Gabor"
            + "\nBuilt with Java, Swing, Processing 4(beta)/JOGL2"
            + "\n\n*Special thanks to Dr.Harland, Dr.Miller, and Dr.Young*",
                "Info", JOptionPane.INFORMATION_MESSAGE);
        
    }//GEN-LAST:event_infoButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
       controller.save();
    }//GEN-LAST:event_saveButtonActionPerformed

    private void restoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restoreButtonActionPerformed
       setInputs();
    }//GEN-LAST:event_restoreButtonActionPerformed

    private void goButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goButtonActionPerformed
        
        int iterations=0;
        double zoom=1;
        int sampleFactor=0;
        
        boolean aa=false;
        
        int fracIndex=0;
        int methodIndex=0;
        
        boolean goodInput=true;
        
        //collect number data from fields
        try{
           iterations = Integer.parseInt(iterationsField.getText());
           
           if(iterations <= 0) 
               throw new  NumberFormatException("no negatives");
           
           goodInput=true; //if made it here then reset back to good
           
        }catch(NumberFormatException nfe){
            goodInput = false; //something bad happened here
            iterationsField.setBorder(badInput); //set the bad input for this
            
        }//end try catch
        
        try{
          zoom = Double.parseDouble(zoomField.getText());
          
          if(zoom <= 0.00000) 
               throw new  NumberFormatException("no negatives");
          
          goodInput=true; 
        }catch(NumberFormatException nfe){
            goodInput = false; //something bad happened here
            zoomField.setBorder(badInput); //set the bad input for this
        }//end try catch
        
        try{
            
          sampleFactor = Integer.parseInt(sampleFactorField.getText());
          
          if(sampleFactor <= 0) 
               throw new  NumberFormatException("no negatives");
          
          goodInput=true;
        }catch(NumberFormatException nfe){
            goodInput = false; //something bad happened here
            sampleFactorField.setBorder(badInput); //set the bad input for this
        }//end try catch
       
        
        if(goodInput){
            //reset all borders, all input is good now
            iterationsField.setBorder(defaultBorder);
            zoomField.setBorder(defaultBorder);
            sampleFactorField.setBorder(defaultBorder);
            
            aa=AAradioButton.isSelected();
            
            fracIndex = fractalComboBox.getSelectedIndex();
            methodIndex = colourMethodComboBox.getSelectedIndex();
            /*
            //for now, no normal mapping with BurningShip
            if(fracIndex == 1 && methodIndex == 3){
                methodIndex = 1; //just send it back to escape times
                colourMethodComboBox.setSelectedIndex(methodIndex);
                notify("\nCan't do burning ship and normal map...yet\n");
            }//end if
            */
            
            //disable everything during render
            massSetEnabled(false);
            
            //setup and do render
            controller.setZoom(zoom);
            controller.setIterations(iterations);
            controller.setSupersampleFactor(sampleFactor);
            
            if(!aa == controller.antialiasing()) //if there was a change, must toggle
                controller.toggleAA();
            
            controller.setFractal(fracIndex);
            controller.setColourMethod(methodIndex);
            
            controller.render();
            
            //re-enable
            massSetEnabled(true);
            
        }//end if
        
    }//GEN-LAST:event_goButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton AAradioButton;
    private javax.swing.JComboBox<String> colourMethodComboBox;
    private javax.swing.JLabel colouringComboBoxLabel;
    private javax.swing.JLabel fractalBoxLabel;
    private javax.swing.JComboBox<String> fractalComboBox;
    private javax.swing.JButton goButton;
    private javax.swing.JButton helpButton;
    private javax.swing.JButton infoButton;
    private javax.swing.JTextField iterationsField;
    private javax.swing.JLabel iterationsLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextArea notificationArea;
    private javax.swing.JButton restoreButton;
    private javax.swing.JTextField sampleFactorField;
    private javax.swing.JButton saveButton;
    private javax.swing.JLabel statusUpdateLabel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextField zoomField;
    private javax.swing.JLabel zoomLabel;
    // End of variables declaration//GEN-END:variables
}//end class