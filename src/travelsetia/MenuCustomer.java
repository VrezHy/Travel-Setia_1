/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package travelsetia;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author dimas
 */
public class MenuCustomer extends javax.swing.JFrame {

    /**
     * Creates new form MenuCustomer
     */
    private DefaultTableModel model = new DefaultTableModel();
    private Connection conn;

    public MenuCustomer() {
        initComponents();
        txtCariPenerbangan.setBackground(new java.awt.Color(0, 0, 0, 1));
        txtTotalBayar.setBackground(new java.awt.Color(0, 0, 0, 1));
        conn = Koneksi.bukaKoneksi();
        String sql = "SELECT p.idPesawat, p.namaPesawat, b.idBandara, b.namaBandara AS kotaKeberangkatan, p.destinasi, jp.tanggalKeberangkatan, p.kursiTersedia, p.harga , p.statusKursi\n"
                + "FROM pesawat p \n"
                + "LEFT JOIN booking bo ON p.idPesawat = bo.idPesawat \n"
                + "LEFT JOIN bandara b ON p.destinasi = b.kota\n"
                + "LEFT JOIN jadwalpenerbangan jp ON p.idPesawat = jp.idPesawat\n"
                + "ORDER BY jp.tanggalKeberangkatan ASC;";
        try {
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            // Process the ResultSet and display data in JTable
            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new Object[]{"ID Pesawat", "Nama Pesawat", "Kota Keberangkatan", "Destinasi", "Tanggal Keberangkatan", "Kursi Tersedia", "Harga", "Status Kursi"});

            while (rs.next()) {

                String statusKursi;
                int IdPesawat = rs.getInt("idPesawat");
                if (rs.getInt("kursiTersedia") > 0) {
                    statusKursi = "ada";
                } else {
                    statusKursi = "habis";
                }
                
                updateStatusKursi(IdPesawat, statusKursi);
                
                model.addRow(new Object[]{
                    rs.getInt("idPesawat"),
                    rs.getString("namaPesawat"),
                    rs.getString("kotaKeberangkatan"),
                    rs.getString("destinasi"),
                    rs.getString("tanggalKeberangkatan"),
                    rs.getString("kursiTersedia"),
                    rs.getInt("harga"),
                    statusKursi
                });
            }

            // Set the model to your JTable
            jTablePesawat.setModel(model);
            jTablePesawat.setDefaultEditor(Object.class, null);
            jTablePesawat.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        } catch (Exception ex) {
            System.out.println("Error : " + ex.getMessage());
        }
    }

    private void updateTextFields() {
        int row = jTablePesawat.getSelectedRow();
        row = jTablePesawat.convertRowIndexToModel(row);
        DefaultTableModel model = (DefaultTableModel) jTablePesawat.getModel();
        String namaPesawat = model.getValueAt(row, 1) != null ? model.getValueAt(row, 1).toString() : "";
        String kotaKeberangkatan = model.getValueAt(row, 2) != null ? model.getValueAt(row, 2).toString() : "";
        String destinasi = model.getValueAt(row, 3) != null ? model.getValueAt(row, 3).toString() : "";
        String waktuKeberangkatan = model.getValueAt(row, 4) != null ? model.getValueAt(row, 4).toString() : "";
        String kursiTersedia = model.getValueAt(row, 5) != null ? model.getValueAt(row, 5).toString() : "";
        String harga = model.getValueAt(row, 6) != null ? model.getValueAt(row, 6).toString() : "";

        tfMaskapai.setText(namaPesawat);
        tfKotaKeberangkatan.setText(kotaKeberangkatan);
        tfDestinasi.setText(destinasi);
        tfTanggalBerangkat.setText(waktuKeberangkatan);
        tfkursiTersedia.setText(kursiTersedia);
        txtTotalBayar.setText(harga);

        updateTotalPrice();
    }

    private void updateTotalPrice() {
        int jumlahTiket = Integer.parseInt(CBtiketPenumpang.getSelectedItem().toString());
        int selectedRow = jTablePesawat.getSelectedRow();
        if (selectedRow != -1) {
            int harga = Integer.parseInt(jTablePesawat.getValueAt(selectedRow, 6).toString());
            int totalHarga = harga * jumlahTiket;
            txtTotalBayar.setText(String.valueOf(totalHarga));
        } else {
            txtTotalBayar.setText("0");
        }
    }
    
    private void updateStatusKursi(int idPesawat, String statusKursi) {
    String sql = "UPDATE pesawat SET statusKursi = ? WHERE idPesawat = ?";
    try {
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, statusKursi);
        pst.setInt(2, idPesawat);
        pst.executeUpdate();
    } catch (SQLException e) {
        System.out.println("Error updating status kursi: " + e.getMessage());
    }
}

    private void prosesBooking() {
        int jumlahTiket = Integer.parseInt(CBtiketPenumpang.getSelectedItem().toString());
        int selectedRow = jTablePesawat.getSelectedRow();
        if (selectedRow != -1) {
            int idPesawat = Integer.parseInt(jTablePesawat.getValueAt(selectedRow, 0).toString());
            int kursiTersedia = Integer.parseInt(jTablePesawat.getValueAt(selectedRow, 5).toString());

            if (kursiTersedia >= jumlahTiket) {
                int kursiBaru = kursiTersedia - jumlahTiket;
                updateDatabase(idPesawat, kursiBaru);
                catatPembelian(idPesawat, jumlahTiket); // Tambahkan ini untuk mencatat pembelian tiket
                JOptionPane.showMessageDialog(this, "berhasil!");
            } else {
                JOptionPane.showMessageDialog(this, "Maaf, tiket untuk penerbangan ini sudah habis.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        } else {
           JOptionPane.showMessageDialog(this, "Tidak ada baris yang dipilih!.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void catatPembelian(int idPesawat, int jumlahTiket) {
        String sqlInsert = "INSERT INTO booking (idPesawat, jumlahTiket) VALUES (?, ?)";
        try {
            PreparedStatement pst = conn.prepareStatement(sqlInsert);
            pst.setInt(1, idPesawat);
            pst.setInt(2, jumlahTiket);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error inserting pembelian tiket: " + e.getMessage());
        }
    }

    private void updateDatabase(int idPesawat, int kursiBaru) {
        String sqlUpdate = "UPDATE pesawat SET kursiTersedia = ? WHERE idPesawat = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(sqlUpdate);
            pst.setInt(1, kursiBaru);
            pst.setInt(2, idPesawat);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating database: " + e.getMessage());
        }
    }

    private void printReceipt() {
    int selectedRow = jTablePesawat.getSelectedRow();
    if (selectedRow != -1) {
        int kursiTersedia = Integer.parseInt(jTablePesawat.getValueAt(selectedRow, 5).toString());
        
        if (kursiTersedia > 0) {
            int jumlahTiket = Integer.parseInt(CBtiketPenumpang.getSelectedItem().toString());
            String totalHarga = txtTotalBayar.getText();
            String maskapai = tfMaskapai.getText();
            String kotaKeberangkatan = tfKotaKeberangkatan.getText();
            String destinasi = tfDestinasi.getText();
            String tanggalBerangkat = tfTanggalBerangkat.getText();

            String message = String.format(
                    "Jumlah Tiket: %s\nTotal Harga: %s\nMaskapai: %s\nKota Keberangkatan: %s\nDestinasi: %s\nTanggal Berangkat: %s\n",
                    jumlahTiket, totalHarga, maskapai, kotaKeberangkatan, destinasi, tanggalBerangkat
            );

            JOptionPane.showMessageDialog(this, message, " Rincian Pembayaran", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Maaf, tiket untuk penerbangan ini sudah habis.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(this, "Pilih penerbangan terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
    }
}

     

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jButton3 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLogout = new javax.swing.JButton();
        tfff9 = new javax.swing.JPanel();
        txtTotalBayar = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnPesan = new javax.swing.JButton();
        btnCetakPembayaran = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        tfMaskapai = new javax.swing.JTextField();
        tfKotaKeberangkatan = new javax.swing.JTextField();
        tfDestinasi = new javax.swing.JTextField();
        tfTanggalBerangkat = new javax.swing.JTextField();
        tfkursiTersedia = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        CBtiketPenumpang = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTablePesawat = new javax.swing.JTable();
        txtCariPenerbangan = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        BackroundCustomer = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        exit = new javax.swing.JLabel();
        minimize = new javax.swing.JLabel();

        jPanel2.setBackground(new java.awt.Color(0, 204, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("ID :");

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Asal :");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Tujuan :");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Maskapai :");

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Harga :");

        jLabel9.setText("Nama Pemesan :");

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Tanggal Pemesanan :");

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Jumlah Tiket :");

        jButton1.setText("Pesan");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Reset");

        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jTextField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField4ActionPerformed(evt);
            }
        });

        jButton3.setText("Cetak Pembayaran");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel11))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel7)
                                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel9)))
                                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField1)
                            .addComponent(jTextField2)
                            .addComponent(jTextField3)
                            .addComponent(jTextField4)
                            .addComponent(jTextField5)
                            .addComponent(jTextField6)
                            .addComponent(jComboBox2, 0, 158, Short.MAX_VALUE)
                            .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton2)))))
                .addGap(23, 23, 23))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addGap(53, 53, 53))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(53, 114, 239));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBackground(new java.awt.Color(0, 204, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLogout.setText("Logout");
        jLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLogoutActionPerformed(evt);
            }
        });
        jPanel5.add(jLogout, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 590, 90, 40));

        tfff9.setBackground(new java.awt.Color(53, 114, 239));
        tfff9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtTotalBayar.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        txtTotalBayar.setForeground(new java.awt.Color(255, 255, 255));
        txtTotalBayar.setBorder(null);
        txtTotalBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTotalBayarActionPerformed(evt);
            }
        });
        tfff9.add(txtTotalBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 230, 280, 40));

        jLabel1.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Kursi Tersedia");
        tfff9.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 230, 150, 40));

        btnPesan.setText("Pesan Sekarang");
        btnPesan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesanActionPerformed(evt);
            }
        });
        tfff9.add(btnPesan, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 280, 130, -1));

        btnCetakPembayaran.setText("Cetak Pembayaran");
        btnCetakPembayaran.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCetakPembayaranActionPerformed(evt);
            }
        });
        tfff9.add(btnCetakPembayaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 310, -1, -1));

        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("______________________________________");
        tfff9.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 260, 270, -1));

        tfMaskapai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfMaskapaiActionPerformed(evt);
            }
        });
        tfff9.add(tfMaskapai, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 30, 370, 40));
        tfff9.add(tfKotaKeberangkatan, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 80, 370, 40));

        tfDestinasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfDestinasiActionPerformed(evt);
            }
        });
        tfff9.add(tfDestinasi, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 130, 370, 40));
        tfff9.add(tfTanggalBerangkat, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 180, 370, 40));

        tfkursiTersedia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfkursiTersediaActionPerformed(evt);
            }
        });
        tfff9.add(tfkursiTersedia, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 230, 370, 40));

        jLabel3.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Jumlah Tiket Penumpang");
        tfff9.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 150, 210, 40));

        jLabel16.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Maskapai");
        tfff9.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(112, 30, 80, 40));

        jLabel17.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("Lokasi Keberangkatan");
        tfff9.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 80, 180, 40));

        jLabel18.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Destinasi");
        tfff9.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(112, 130, 80, 40));

        jLabel19.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Tanggal Berangkat");
        tfff9.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 180, 160, 40));

        CBtiketPenumpang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4" }));
        CBtiketPenumpang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CBtiketPenumpangActionPerformed(evt);
            }
        });
        tfff9.add(CBtiketPenumpang, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 150, 70, 40));

        jLabel20.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Total Bayar");
        tfff9.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 200, 100, 30));

        jPanel5.add(tfff9, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 220, 990, 360));

        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("________________________________________");
        jPanel5.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 40, 280, -1));

        jTablePesawat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Maskapai", "Lokasi Keberangkatan", "Destinasi", "Tanggal Keberangkatan", "Status Kursi"
            }
        ));
        jTablePesawat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTablePesawatMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTablePesawat);

        jPanel5.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 60, 990, 140));

        txtCariPenerbangan.setFont(new java.awt.Font("Verdana", 0, 14)); // NOI18N
        txtCariPenerbangan.setForeground(new java.awt.Color(255, 255, 255));
        txtCariPenerbangan.setBorder(null);
        txtCariPenerbangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCariPenerbanganActionPerformed(evt);
            }
        });
        jPanel5.add(txtCariPenerbangan, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 20, 280, 30));

        jButton5.setForeground(new java.awt.Color(53, 114, 239));
        jButton5.setText("Cari...");
        jButton5.setBorder(null);
        jButton5.setBorderPainted(false);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel5.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1090, 20, 70, 30));

        jLabel13.setFont(new java.awt.Font("Verdana", 0, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Cari penerbangan pesawatmu sekarang!");
        jPanel5.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 20, 380, 40));

        BackroundCustomer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image_icon/pexels-jerry-wang-2135752-3768652.jpg"))); // NOI18N
        BackroundCustomer.setText("jLabel1");
        jPanel5.add(BackroundCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -7, 1320, 680));

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 101, 1320, 670));

        jLabel12.setFont(new java.awt.Font("Verdana", 1, 48)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("TRAVEL SETIA");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 40, -1, 50));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Image_icon/icons8-palm-tree-50.png"))); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 0, 50, 60));

        exit.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        exit.setForeground(new java.awt.Color(255, 255, 255));
        exit.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        exit.setText("x");
        exit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exitMouseClicked(evt);
            }
        });
        jPanel1.add(exit, new org.netbeans.lib.awtextra.AbsoluteConstraints(1270, 0, 40, 40));

        minimize.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        minimize.setForeground(new java.awt.Color(255, 255, 255));
        minimize.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        minimize.setText("-");
        minimize.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                minimizeMouseClicked(evt);
            }
        });
        jPanel1.add(minimize, new org.netbeans.lib.awtextra.AbsoluteConstraints(1230, 0, 40, 40));

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
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void exitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitMouseClicked
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_exitMouseClicked

    private void minimizeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_minimizeMouseClicked
        // TODO add your handling code here:
        this.setExtendedState(MenuCustomer.ICONIFIED);
    }//GEN-LAST:event_minimizeMouseClicked

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        DefaultTableModel BTCari = (DefaultTableModel) jTablePesawat.getModel();
        TableRowSorter<DefaultTableModel> Cari = new TableRowSorter<>(BTCari);
        jTablePesawat.setRowSorter(Cari);
        String searchText = txtCariPenerbangan.getText();
    
        if (searchText.trim().length() == 0) {
            Cari.setRowFilter(null);
        } else{
            Cari.setRowFilter(RowFilter.regexFilter("(?i)"+searchText));
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void txtCariPenerbanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCariPenerbanganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCariPenerbanganActionPerformed

    private void btnPesanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesanActionPerformed
        prosesBooking();
    }//GEN-LAST:event_btnPesanActionPerformed

    private void txtTotalBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalBayarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalBayarActionPerformed

    private void tfMaskapaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfMaskapaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfMaskapaiActionPerformed

    private void tfDestinasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfDestinasiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfDestinasiActionPerformed

    private void tfkursiTersediaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfkursiTersediaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfkursiTersediaActionPerformed

    private void jTablePesawatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTablePesawatMouseClicked
        updateTextFields();
    }//GEN-LAST:event_jTablePesawatMouseClicked

    private void CBtiketPenumpangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CBtiketPenumpangActionPerformed
        updateTotalPrice();
    }//GEN-LAST:event_CBtiketPenumpangActionPerformed

    private void btnCetakPembayaranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCetakPembayaranActionPerformed
        printReceipt();
    }//GEN-LAST:event_btnCetakPembayaranActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        tfMaskapai.setEditable(false);
        tfKotaKeberangkatan.setEditable(false);
        tfDestinasi.setEditable(false);
        tfTanggalBerangkat.setEditable(false);
        tfkursiTersedia.setEditable(false);
        txtTotalBayar.setEditable(false);
    }//GEN-LAST:event_formComponentShown

    private void jLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLogoutActionPerformed
        Login1 Login1Frame = new Login1();
        Login1Frame.setVisible(true);
        Login1Frame.pack();
        Login1Frame.setLocationRelativeTo(null);
        this.dispose();
    }//GEN-LAST:event_jLogoutActionPerformed

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
            java.util.logging.Logger.getLogger(MenuCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MenuCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MenuCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuCustomer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuCustomer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BackroundCustomer;
    private javax.swing.JComboBox<String> CBtiketPenumpang;
    private javax.swing.JButton btnCetakPembayaran;
    private javax.swing.JButton btnPesan;
    private javax.swing.JLabel exit;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JButton jLogout;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTablePesawat;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JLabel minimize;
    private javax.swing.JTextField tfDestinasi;
    private javax.swing.JTextField tfKotaKeberangkatan;
    private javax.swing.JTextField tfMaskapai;
    private javax.swing.JTextField tfTanggalBerangkat;
    private javax.swing.JPanel tfff9;
    private javax.swing.JTextField tfkursiTersedia;
    private javax.swing.JTextField txtCariPenerbangan;
    private javax.swing.JTextField txtTotalBayar;
    // End of variables declaration//GEN-END:variables
}
