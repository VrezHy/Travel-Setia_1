package travelsetia;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */


/**
 *
 * @author dimas
 */
public class MenuKursi1 extends javax.swing.JPanel {

    /**
     * Creates new form MenuKursi1
     */
    public MenuKursi1() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        iconKursi = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setBackground(new java.awt.Color(53, 114, 239));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 150, 760, -1));

        jLabel2.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Master Data > Kursi");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 0, 150, 30));

        iconKursi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image_icon/Sitting on Chair.png"))); // NOI18N
        add(iconKursi, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 0, -1, 40));

        jButton4.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(102, 153, 255));
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image_icon/Plus.png"))); // NOI18N
        jButton4.setText("Tambah");
        jButton4.setBorder(null);
        jButton4.setPreferredSize(new java.awt.Dimension(78, 25));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 60, 130, 60));

        jButton5.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(102, 153, 255));
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image_icon/Change.png"))); // NOI18N
        jButton5.setText("Ubah");
        jButton5.setBorder(null);
        jButton5.setPreferredSize(new java.awt.Dimension(78, 25));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 60, 130, 60));

        jButton3.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(102, 153, 255));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image_icon/Minus.png"))); // NOI18N
        jButton3.setText("Hapus");
        jButton3.setBorder(null);
        jButton3.setPreferredSize(new java.awt.Dimension(78, 25));
        add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 60, 130, 60));
    }// </editor-fold>//GEN-END:initComponents

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel iconKursi;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
