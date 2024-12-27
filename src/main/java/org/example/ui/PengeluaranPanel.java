package org.example.ui;

import org.example.data.WordHandler;
import org.example.model.Pengeluaran;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PengeluaranPanel extends JPanel {
    private JTextField txtJumlah;
    private JTextField txtDeskripsi;
    private JButton btnPilihGambar;
    private JLabel lblGambar;
    private JButton btnSimpan;
    private JButton btnReset;
    private JButton btnKembali;
    private JButton btnEdit;
    private JButton btnUpdate;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTotalUang;
    private File selectedImage;
    private Pengeluaran pengeluaranDiedit = null; // Untuk menyimpan entri yang sedang diedit

    public PengeluaranPanel(double totalUang, List<Pengeluaran> pengeluaranList, JLabel lblTotalUangLabel, ActionListener kembaliListener) {
        this.lblTotalUang = lblTotalUangLabel;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Pengeluaran", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 26));
        add(lblTitle, BorderLayout.NORTH);

        // Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Tambah Pengeluaran"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblJumlah = new JLabel("Jumlah Uang:");
        lblJumlah.setFont(new Font("Arial", Font.PLAIN, 18));
        txtJumlah = new JTextField(20);

        JLabel lblDeskripsi = new JLabel("Deskripsi:");
        lblDeskripsi.setFont(new Font("Arial", Font.PLAIN, 18));
        txtDeskripsi = new JTextField(20);

        JLabel lblBukti = new JLabel("Bukti Pengeluaran:");
        lblBukti.setFont(new Font("Arial", Font.PLAIN, 18));
        btnPilihGambar = new JButton("Pilih Gambar");
        btnPilihGambar.setFont(new Font("Arial", Font.PLAIN, 16));
        lblGambar = new JLabel("Tidak ada gambar dipilih.");
        lblGambar.setFont(new Font("Arial", Font.ITALIC, 16));

        btnPilihGambar.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Image Files", "jpg", "png", "jpeg", "bmp");
            fileChooser.setFileFilter(filter);
            int returnVal = fileChooser.showOpenDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                selectedImage = fileChooser.getSelectedFile();
                lblGambar.setText(selectedImage.getName());
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(lblJumlah, gbc);

        gbc.gridx = 1;
        formPanel.add(txtJumlah, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(lblDeskripsi, gbc);

        gbc.gridx = 1;
        formPanel.add(txtDeskripsi, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(lblBukti, gbc);

        gbc.gridx = 1;
        JPanel gambarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        gambarPanel.add(btnPilihGambar);
        gambarPanel.add(lblGambar);
        formPanel.add(gambarPanel, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnSimpan = new JButton("Simpan");
        btnSimpan.setFont(new Font("Arial", Font.PLAIN, 16));
        btnReset = new JButton("Reset");
        btnReset.setFont(new Font("Arial", Font.PLAIN, 16));
        btnEdit = new JButton("Edit");
        btnEdit.setFont(new Font("Arial", Font.PLAIN, 16));
        btnEdit.setEnabled(false); // Disabled secara default
        btnUpdate = new JButton("Update");
        btnUpdate.setFont(new Font("Arial", Font.PLAIN, 16));
        btnUpdate.setVisible(false); // Hanya terlihat saat dalam mode edit

        btnPanel.add(btnSimpan);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnReset);
        btnPanel.add(btnEdit);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        splitPane.setTopComponent(formPanel);

        // History Panel
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(BorderFactory.createTitledBorder("Riwayat Pengeluaran"));

        String[] columnNames = {"ID", "Jumlah", "Deskripsi", "Tanggal", "Bukti"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        populateTable(pengeluaranList);

        JScrollPane scrollPane = new JScrollPane(table);
        historyPanel.add(scrollPane, BorderLayout.CENTER);

        // Kembali Button
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnKembali = new JButton("Kembali");
        btnKembali.setFont(new Font("Arial", Font.PLAIN, 16));
        btnKembali.addActionListener(kembaliListener);
        backPanel.add(btnKembali);
        historyPanel.add(backPanel, BorderLayout.SOUTH);

        // Add Selection Listener untuk Tombol Edit
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                btnEdit.setEnabled(true);
            } else {
                btnEdit.setEnabled(false);
            }
        });

        // Tombol Edit Action
        btnEdit.addActionListener(this::actionPerformed);

        // Add Double Click Listener for Deletion
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int rowSelected = table.getSelectedRow();
                    if (rowSelected != -1) {
                        long id = (Long) tableModel.getValueAt(rowSelected, 0);
                        int confirm = JOptionPane.showConfirmDialog(PengeluaranPanel.this, "Apakah Anda yakin ingin menghapus entri ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            try {
                                List<Pengeluaran> updatedList = WordHandler.loadPengeluaran();
                                Pengeluaran pToRemove = updatedList.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
                                if (pToRemove != null) {
                                    // Update total uang
                                    double currentTotal = Double.parseDouble(lblTotalUang.getText().replaceAll("[^\\d.]", ""));
                                    double newTotal = currentTotal + pToRemove.getJumlah();
                                    WordHandler.saveTotalUang(newTotal);
                                    // Remove pengeluaran
                                    updatedList.remove(pToRemove);
                                    WordHandler.savePengeluaranList(updatedList);
                                    // Refresh table
                                    populateTable(WordHandler.loadPengeluaran());
                                    // Update label
                                    lblTotalUang.setText("Total Uang: Rp " + String.format("%.2f", newTotal));
                                    JOptionPane.showMessageDialog(PengeluaranPanel.this, "Entri berhasil dihapus.");
                                }
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(PengeluaranPanel.this, "Gagal menyimpan perubahan.", "Error", JOptionPane.ERROR_MESSAGE);
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

        splitPane.setBottomComponent(historyPanel);
        add(splitPane, BorderLayout.CENTER);

        // Tombol Simpan (Tambah)
        btnSimpan.addActionListener(e -> {
            try {
                double jumlah = Double.parseDouble(txtJumlah.getText().trim());
                String deskripsi = txtDeskripsi.getText().trim();

                if (jumlah <= 0) {
                    JOptionPane.showMessageDialog(this, "Jumlah uang harus lebih besar dari 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (deskripsi.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Deskripsi tidak boleh kosong.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double currentTotal = Double.parseDouble(lblTotalUang.getText().replaceAll("[^\\d.]", ""));
                if (jumlah > currentTotal) {
                    JOptionPane.showMessageDialog(this, "Jumlah pengeluaran melebihi total uang.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Menyimpan bukti pengeluaran jika ada
                String bukti = null;
                if (selectedImage != null) {
                    bukti = selectedImage.getName();
                    // Menyalin gambar ke folder "bukti_pengeluaran"
                    File destDir = new File(WordHandler.BUKTI_DIR);
                    if (!destDir.exists()) destDir.mkdirs();
                    File destFile = new File(destDir, bukti);
                    try (FileInputStream fis = new FileInputStream(selectedImage);
                         FileOutputStream fos = new FileOutputStream(destFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = fis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    } catch (IOException exCopy) {
                        JOptionPane.showMessageDialog(this, "Gagal menyimpan gambar bukti.", "Error", JOptionPane.ERROR_MESSAGE);
                        exCopy.printStackTrace();
                        return;
                    }
                }

                Pengeluaran pengeluaran = new Pengeluaran(System.currentTimeMillis(), jumlah, deskripsi, new java.util.Date().toString(), bukti);
                WordHandler.savePengeluaran(pengeluaran);
                populateTable(WordHandler.loadPengeluaran());

                // Update total uang
                double newTotal = currentTotal - jumlah;
                WordHandler.saveTotalUang(newTotal);
                lblTotalUang.setText("Total Uang: Rp " + String.format("%.2f", newTotal));

                JOptionPane.showMessageDialog(this, "Pengeluaran berhasil ditambahkan dan disimpan ke Word.");
                txtJumlah.setText("");
                txtDeskripsi.setText("");
                selectedImage = null;
                lblGambar.setText("Tidak ada gambar dipilih.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Jumlah uang harus berupa angka.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan ke Word.", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        // Tombol Update (Edit)
        btnUpdate.addActionListener(e -> {
            if (pengeluaranDiedit == null) {
                JOptionPane.showMessageDialog(this, "Tidak ada pengeluaran yang sedang diedit.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double jumlahBaru = Double.parseDouble(txtJumlah.getText().trim());
                String deskripsiBaru = txtDeskripsi.getText().trim();

                if (jumlahBaru <= 0) {
                    JOptionPane.showMessageDialog(this, "Jumlah uang harus lebih besar dari 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (deskripsiBaru.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Deskripsi tidak boleh kosong.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double currentTotal = Double.parseDouble(lblTotalUang.getText().replaceAll("[^\\d.]", ""));
                // Hitung total setelah pembaruan: total saat ini + jumlah lama - jumlah baru
                double updatedTotal = currentTotal + pengeluaranDiedit.getJumlah() - jumlahBaru;
                if (updatedTotal < 0) {
                    JOptionPane.showMessageDialog(this, "Jumlah pengeluaran melebihi total uang setelah pembaruan.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Menyimpan bukti pengeluaran jika ada
                String buktiBaru = pengeluaranDiedit.getBukti();
                if (selectedImage != null) {
                    buktiBaru = selectedImage.getName();
                    // Menyalin gambar ke folder "bukti_pengeluaran"
                    File destDir = new File(WordHandler.BUKTI_DIR);
                    if (!destDir.exists()) destDir.mkdirs();
                    File destFile = new File(destDir, buktiBaru);
                    try (FileInputStream fis = new FileInputStream(selectedImage);
                         FileOutputStream fos = new FileOutputStream(destFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = fis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    } catch (IOException exCopy) {
                        JOptionPane.showMessageDialog(this, "Gagal menyimpan gambar bukti.", "Error", JOptionPane.ERROR_MESSAGE);
                        exCopy.printStackTrace();
                        return;
                    }
                }

                // Membuat objek pengeluaran yang diperbarui
                Pengeluaran pengeluaranBaru = new Pengeluaran(
                        pengeluaranDiedit.getId(),
                        jumlahBaru,
                        deskripsiBaru,
                        new java.util.Date().toString(),
                        buktiBaru
                );

                // Memuat daftar pengeluaran, memperbarui entri, dan menyimpan kembali
                List<Pengeluaran> list = WordHandler.loadPengeluaran();
                int index = list.indexOf(pengeluaranDiedit);
                if (index != -1) {
                    list.set(index, pengeluaranBaru);
                    WordHandler.savePengeluaranList(list);
                    populateTable(WordHandler.loadPengeluaran());

                    // Update total uang
                    WordHandler.saveTotalUang(updatedTotal);
                    lblTotalUang.setText("Total Uang: Rp " + String.format("%.2f", updatedTotal));

                    JOptionPane.showMessageDialog(this, "Pengeluaran berhasil diperbarui.");

                    // Reset form dan mode edit
                    txtJumlah.setText("");
                    txtDeskripsi.setText("");
                    selectedImage = null;
                    lblGambar.setText("Tidak ada gambar dipilih.");
                    btnSimpan.setVisible(true);
                    btnUpdate.setVisible(false);
                    pengeluaranDiedit = null;
                } else {
                    JOptionPane.showMessageDialog(this, "Pengeluaran tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Jumlah uang harus berupa angka.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal memperbarui pengeluaran.", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        // Tombol Reset
        btnReset.addActionListener(e -> {
            txtJumlah.setText("");
            txtDeskripsi.setText("");
            selectedImage = null;
            lblGambar.setText("Tidak ada gambar dipilih.");
            // Jika dalam mode edit, batalkan mode edit
            if (pengeluaranDiedit != null) {
                btnSimpan.setVisible(true);
                btnUpdate.setVisible(false);
                pengeluaranDiedit = null;
            }
        });
    }

    private void populateTable(List<Pengeluaran> list) {
        tableModel.setRowCount(0);
        for (Pengeluaran p : list) {
            Object[] row = {p.getId(), p.getJumlah(), p.getDeskripsi(), p.getTanggal(), p.getBukti()};
            tableModel.addRow(row);
        }
    }

    private void actionPerformed(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            long id = (Long) tableModel.getValueAt(selectedRow, 0);
            List<Pengeluaran> list = WordHandler.loadPengeluaran();
            pengeluaranDiedit = list.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
            if (pengeluaranDiedit != null) {
                // Mengisi form dengan data pengeluaran yang dipilih
                txtJumlah.setText(String.valueOf(pengeluaranDiedit.getJumlah()));
                txtDeskripsi.setText(pengeluaranDiedit.getDeskripsi());
                if (pengeluaranDiedit.getBukti() != null && !pengeluaranDiedit.getBukti().isEmpty()) {
                    selectedImage = new File(WordHandler.BUKTI_DIR, pengeluaranDiedit.getBukti());
                    lblGambar.setText(selectedImage.getName());
                } else {
                    selectedImage = null;
                    lblGambar.setText("Tidak ada gambar dipilih.");
                }
                // Ubah UI ke mode edit
                btnSimpan.setVisible(false);
                btnUpdate.setVisible(true);
                btnEdit.setEnabled(false);
            }
        }
    }
}
