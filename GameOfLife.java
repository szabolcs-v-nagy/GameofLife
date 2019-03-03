/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gol;
import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Image;
import java.awt.Graphics;
import java.io.File;
import java.util.Arrays;
import javax.swing.SwingUtilities;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Nagy Szabolcs
 */
public class GameOfLife extends javax.swing.JFrame {
    
    //sets the size of the game area, can be adjusted on need
    final int wd=80, hg=45;
    //2 dimensional arrays to store current generation
    boolean[][] currentGen = new boolean[hg][wd];
    //3 dimensional array to store the generations
    boolean[][][] gens = new boolean[1000][hg][wd];
    //conditions for button functions
    boolean play;
    boolean forward;
    boolean back;
    //generation counter
    int count=0;
    //graphic tools
    Image offScreenImage;
    Graphics offScreenGraphics;
    
    //reads file from selected path into a string
    static String readFile(String path, Charset encoding) 
    throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
    
    //Creates new form GameOfLife
    public GameOfLife() {
        initComponents();
        offScreenImage = createImage(jPanel1.getWidth(), jPanel1.getHeight());
        offScreenGraphics = offScreenImage.getGraphics();
        //starts a time based task
        Timer time = new Timer();
        TimerTask task = new TimerTask(){
            public void run(){
                //starting visuals of the game area
                modify();
                //checks if play button was pressed
                if(play){
                    //calculates the next generation
                    for(int i=0;i<hg;i++){
                        for(int j=0;j<wd;j++){
                            gens[count+1][i][j] = evolution(i,j);
                        }
                    }
                    //saves the new generation as the current and storing it
                    for(int i=0;i<hg;i++){
                        for(int j=0;j<wd;j++){
                            currentGen[i][j] = gens[count+1][i][j];
                        }
                    }
                    //visualizes the changes
                    modify();
                    //increases the generation counter and visualizing it
                    count++;
                    jLabel2.setText(Integer.toString(count));
                }
                //checks if forward button was pressed
                //similar process as play
                if(forward){
                    if(!play){
                        for(int i=0;i<hg;i++){
                            for(int j=0;j<wd;j++){
                                gens[count+1][i][j] = evolution(i,j);
                            }
                        }
                        for(int i=0;i<hg;i++){
                            for(int j=0;j<wd;j++){
                                currentGen[i][j] = gens[count+1][i][j];
                            }
                        }

                        modify();
                        count++;
                        jLabel2.setText(Integer.toString(count));
                    }
                    //turns the condition off to stop the process going further
                    forward = false;
                }
                //checks if back button was pressed
                if(back){
                    if(!play){
                        if(count != 0){
                            //sets back the generation to the previous
                            for(int i=0;i<hg;i++)
                                for(int j=0;j<wd;j++)
                                    currentGen[i][j] = gens[count-1][i][j];
                            modify();
                            count--;
                            jLabel2.setText(Integer.toString(count));
                            //removes the last element of the saved generations
                            Arrays.copyOf(gens, gens.length-1);
                        }
                    }
                    //turning the condition off to stop the process going further
                    back = false;
                }
            }
        };
        //sets the game's pace
        time.scheduleAtFixedRate(task, 0, 100);
    }
    
    //implements the rules of Game of Life
    private boolean evolution(int i, int j){
        //number of neighbours
        int neighbours = 0;
        //checks neighbours to the left if there is any
        if(j>0){
            if(currentGen[i][j-1]) neighbours++;
            if(i>0)
                if(currentGen[i-1][j-1]) neighbours++;
            if(i<hg-1)
                if(currentGen[i+1][j-1]) neighbours++;
        }
        //checks neighbours to the right if there is any
        if(j<wd-1){
            if(currentGen[i][j+1]) neighbours++;
            if(i>0)
                if(currentGen[i-1][j+1]) neighbours++;
            if(i<hg-1)
                if(currentGen[i+1][j+1]) neighbours++;
        }
        //checks top neighbour if there is any
        if(i>0)
            if(currentGen[i-1][j]) neighbours++;
        //checks bottom neighbour if there is any
        if(i<hg-1)
            if(currentGen[i+1][j]) neighbours++;
        //alive or dead, if the cell has exactly 3 neighbours, it will be alive in the next generation
        if(neighbours == 3) return true;
        //live cell with 2 neighbour survives
        if(currentGen[i][j] && neighbours == 2) return true;
        //any other case will result in the cell being dead in the next generation
        return false;
    }
    
    //applies visual changes
    private void modify(){
        offScreenGraphics.setColor(jPanel1.getBackground());
        offScreenGraphics.fillRect(0, 0, jPanel1.getWidth(), jPanel1.getHeight());
        for(int i=0;i<hg;i++){
            for(int j=0;j<wd;j++){
                if(currentGen[i][j]){
                    offScreenGraphics.setColor(Color.YELLOW);
                    int x = j*jPanel1.getWidth()/wd;
                    int y = i*jPanel1.getHeight()/hg;
                    offScreenGraphics.fillRect(x, y, jPanel1.getWidth()/wd, jPanel1.getHeight()/hg);
                }
            }
        }
        offScreenGraphics.setColor(Color.BLACK);
        for(int i=1;i<hg;i++){
            int y = i*jPanel1.getHeight()/hg;
            offScreenGraphics.drawLine(0, y, jPanel1.getWidth(), y);
        }
        for(int j=1;j<wd;j++){
            int x = j*jPanel1.getWidth()/wd;
            offScreenGraphics.drawLine(x, 0, x, jPanel1.getHeight());
        }
        
        jPanel1.getGraphics().drawImage(offScreenImage, 0, 0, jPanel1);
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
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(102, 102, 102));
        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel1MouseClicked(evt);
            }
        });
        jPanel1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jPanel1ComponentResized(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 373, Short.MAX_VALUE)
        );

        jButton1.setText("Play");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Reset");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Forward");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Back");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel1.setText("Generation");

        jLabel2.setText(Integer.toString(count));

        jButton5.setText("Load");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 268, Short.MAX_VALUE)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jButton5))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //implements the function of button "Play/Pause"
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //changes the value of play so the process of Game of Life starts or stops
        play = !play;
        //sets the button text accordingly
        if(play)
            jButton1.setText("Pause");
        else
            jButton1.setText("Play");
    }//GEN-LAST:event_jButton1ActionPerformed

    //sets the clicked cell alive or dead for testing purposes
    private void jPanel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseClicked
        /*int j = wd*evt.getX()/jPanel1.getWidth();
        int i = hg*evt.getY()/jPanel1.getHeight();
        currentGen[i][j] = !currentGen[i][j];
        modify();*/
    }//GEN-LAST:event_jPanel1MouseClicked

    //adjusts the screen of Game of Life in case it was modified
    private void jPanel1ComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel1ComponentResized
        offScreenImage = createImage(jPanel1.getWidth(), jPanel1.getHeight());
        offScreenGraphics = offScreenImage.getGraphics();
    }//GEN-LAST:event_jPanel1ComponentResized

    //implements the function of button "Reset"
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        //sets all cell dead
        for(int i=0;i<hg;i++){
            for(int j=0;j<wd;j++){
                currentGen[i][j] = false;
            }
        }
        modify();
        //removes the array of previous generation and recreates it empty
        gens = null;
        gens = new boolean [1000][hg][wd];
        //stops Game of Life in case it was running and sets back "Play/Pause" button to "Play"
        if(play){
            play=!play;
            jButton1.setText("Play");
        }
        //resets the generation counter to zero
        count=0;
        jLabel2.setText(Integer.toString(count));
    }//GEN-LAST:event_jButton2ActionPerformed

    //sets value of forward to true on clicking the button so it calculates the next generation
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        forward = true;
    }//GEN-LAST:event_jButton3ActionPerformed

    //sets the value of back to true on clicking the button so it steps back to the previous generation
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        back = true;
    }//GEN-LAST:event_jButton4ActionPerformed

    //implements the function of button "Load"
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        JFileChooser fc = new JFileChooser();
        //sets default directory to source
        fc.setCurrentDirectory(new java.io.File("."));
        fc.setDialogTitle("Choose your Leica Image File");
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        //sets a filter to show only .lif files
        fc.setFileFilter(new FileNameExtensionFilter("Leica Image Files", "lif"));
        fc.showOpenDialog(null);
        //saves the path of the selected file to a string
        String path = fc.getSelectedFile().getAbsolutePath();
        try {
            String content = readFile(path, StandardCharsets.UTF_8);
            //splits the string at line breaks and stores the result in an array
            String[] lines = content.split("\r\n|\r|\n");
            int[] coordinates = new int[2];
            //Checking for type 1.06
            if((lines[0]).contentEquals("#Life 1.06")){
                //Checking each line
                for(int i=1;i<lines.length;i++){
                    //counts the negative coordinates
                    int negatives=0;
                    Pattern p1 = Pattern.compile("-");
                    Matcher m1 = p1.matcher(lines[i]);
                    while(m1.find())
                        negatives++;

                    //finds the numbers that the line contains
                    Pattern p = Pattern.compile("\\d+");
                    Matcher m = p.matcher(lines[i]);
                    int j = 0;
                    while(m.find()){
                        coordinates[j] = Integer.valueOf(m.group());
                        j++;
                    }

                    //revaluates the numbers according to our 2 dimensional array and whether they were negative or not
                    switch (negatives){
                        case 0:
                            currentGen[(hg/2 - coordinates[0])][(wd/2 - coordinates[1])] = true;
                            gens[0][(hg/2 - coordinates[0])][(wd/2 - coordinates[1])] = true;
                            break;

                        case 1:
                            if(lines[i].indexOf("-") == 3){
                                currentGen[(hg/2 + coordinates[0])][(wd/2 - coordinates[1])] = true;
                                gens[0][(hg/2 + coordinates[0])][(wd/2 - coordinates[1])] = true;
                            }
                            else{
                                currentGen[(hg/2 - coordinates[0])][(wd/2 + coordinates[1])] = true;
                                gens[0][(hg/2 - coordinates[0])][(wd/2 + coordinates[1])] = true;
                            }
                            break;

                        case 2:
                            currentGen[(hg/2 + coordinates[0])][(wd/2 + coordinates[1])] = true;
                            gens[0][(hg/2 + coordinates[0])][(wd/2 + coordinates[1])] = true;
                            break;
                    }
                }
            }
            //Checking for type 1.05
            if((lines[0]).contentEquals("#Life 1.05")){
                //Checking each line
                for(int i=1;i<lines.length;i++){
                    //finds the line that contains the coordinates of the first living cell of the pattern
                    if(lines[i].contains("#P")){
                        //counts negative coordinates
                        int negatives=0;
                        Pattern p1 = Pattern.compile("-");
                        Matcher m1 = p1.matcher(lines[i]);
                        while(m1.find())
                            negatives++;

                        //finds numbers
                        Pattern p = Pattern.compile("\\d+");
                        Matcher m = p.matcher(lines[i]);
                        int j = 0;
                        while(m.find()){
                            coordinates[j] = Integer.valueOf(m.group());
                            j++;
                        }

                        //revaluates the numbers according to our 2 dimensional array and whether they were negative or not
                        switch (negatives){
                            case 0:
                                coordinates[0] = hg/2 - coordinates[0];
                                coordinates[1] = wd/2 - coordinates[1];
                                break;

                            case 1:
                                if(lines[i].indexOf("-") == 3){
                                    coordinates[0] = hg/2 - coordinates[0]*(-1);
                                    coordinates[1] = wd/2 - coordinates[1];
                                }
                                else{
                                    coordinates[0] = hg/2 - coordinates[0];
                                    coordinates[1] = wd/2 - coordinates[1]*(-1);
                                }
                                break;

                            case 2:
                                coordinates[0] = hg/2 - coordinates[0]*(-1);
                                coordinates[1] = wd/2 - coordinates[1]*(-1);
                                break;
                        }
                        //sets the living cells according to the pattern until the next coordinates
                        int k=i+1;
                        while(!lines[k].contains("#P")){
                            for(int l=0;l<lines[k].length();l++){
                                if(lines[k].charAt(l) == '*'){
                                    /*Since the value of k is the line number of the currently read line in our string array, 
                                    it needs to be adjusted accordingly.*/
                                    currentGen[(coordinates[0]+k-i-1)][(coordinates[1]+l)] = true;
                                    gens[0][(coordinates[0]+k-i-1)][(coordinates[1]+l)] = true;
                                    //System.out.println((coordinates[0]+k-i-1)+" "+(coordinates[1]+l));
                                }
                            }
                            k++;
                            //checks if we reached the end of the string array
                            if(k == lines.length)
                                break;
                        }
                        //sets i to the line before the next coordinates so the cycle jumps to that in the next phase
                        i=k-1;
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(GameOfLife.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton5ActionPerformed

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GameOfLife.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GameOfLife.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GameOfLife.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GameOfLife.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GameOfLife().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
