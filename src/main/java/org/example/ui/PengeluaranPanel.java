package org.example.ui;

import org.example.data.WordHandler;
import org.example.model.Pengeluaran;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

public class PengeluaranPanel extends JPanel {
    private JTextField txtJumlah;
    private JTextField txtDeskripsi;
    private JButton btnPilihGambar;
    private JLabel lblGambar;
    private JButton btnSimpan;
    private JButton btnReset;
    private JButton btnEdit;
    private JButton btnUpdate;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTotalUang;
    private File selectedImage;
    private Pengeluaran pengeluaranDiedit = null; // Untuk menyimpan entri yang sedang diedit
    private double totalUang;
    private List<Pengeluaran> pengeluaranList;

    public PengeluaranPanel(double initialTotalUang, List<Pengeluaran> initialPengeluaranList, JLabel lblTotalUangLabel, ActionListener kembaliListener) {
        this.totalUang = initialTotalUang;
        this.pengeluaranList = initialPengeluaranList;
        this.lblTotalUang = lblTotalUangLabel;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Pengeluaran", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 26));
        add(lblTitle, BorderLayout.NORTH);

        // Split Pane Vertikal: Form dan Riwayat
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(2); // Ukuran pembatas

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

        // Buttons Panel
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

        // Add Selection Listener untuk Tombol Edit
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                    btnEdit.setEnabled(true);
                } else {
                    btnEdit.setEnabled(false);
                }
            }
        });

        // Tombol Edit Action
        btnEdit.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                long id = (Long) tableModel.getValueAt(selectedRow, 0);
                Optional<Pengeluaran> optionalPengeluaran = pengeluaranList.stream().filter(p -> p.getId() == id).findFirst();
                if (optionalPengeluaran.isPresent()) {
                    pengeluaranDiedit = optionalPengeluaran.get();
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
                    table.clearSelection();
                }
            }
        });

        // Add Double Click Listener for Deletion
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int rowSelected = table.getSelectedRow();
                    if (rowSelected != -1) {
                        long id = (Long) tableModel.getValueAt(rowSelected, 0);
                        int confirm = JOptionPane.showConfirmDialog(PengeluaranPanel.this, "Apakah Anda yakin ingin menghapus entri ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            deletePengeluaranById(id);
                        }
                    }
                }
            }
        });

        splitPane.setBottomComponent(historyPanel);
        add(splitPane, BorderLayout.CENTER);

        // Tombol Simpan (Tambah)
        btnSimpan.addActionListener(e -> {
            tambahPengeluaran();
        });

        // Tombol Update (Edit)
        btnUpdate.addActionListener(e -> {
            updatePengeluaran();
        });

        // Tombol Reset
        btnReset.addActionListener(e -> {
            resetForm();
        });

        // Inisialisasi label total uang
        updateTotalUangLabel();
    }

    /**
     * Menambahkan pengeluaran baru
     */
    private void tambahPengeluaran() {
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

            if (jumlah > totalUang) {
                JOptionPane.showMessageDialog(this, "Jumlah pengeluaran melebihi total uang.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Menyimpan bukti pengeluaran jika ada
            String bukti = null;
            if (selectedImage != null) {
                bukti = System.currentTimeMillis() + "_" + selectedImage.getName();
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
            pengeluaranList.add(pengeluaran);
            totalUang -= jumlah;

            // Simpan data
            WordHandler.savePengeluaranList(pengeluaranList);
            WordHandler.saveTotalUang(totalUang);

            // Perbarui UI
            populateTable(pengeluaranList);
            updateTotalUangLabel();

            JOptionPane.showMessageDialog(this, "Pengeluaran berhasil ditambahkan dan disimpan.");

            // Reset form
            resetForm();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Jumlah uang harus berupa angka.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan pengeluaran.", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Memperbarui pengeluaran yang sedang diedit
     */
    private void updatePengeluaran() {
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

            // Hitung total setelah pembaruan: total saat ini + jumlah lama - jumlah baru
            double updatedTotal = totalUang + pengeluaranDiedit.getJumlah() - jumlahBaru;
            if (updatedTotal < 0) {
                JOptionPane.showMessageDialog(this, "Jumlah pengeluaran melebihi total uang setelah pembaruan.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Menyimpan bukti pengeluaran jika ada
            String buktiBaru = pengeluaranDiedit.getBukti();
            if (selectedImage != null) {
                buktiBaru = System.currentTimeMillis() + "_" + selectedImage.getName();
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

                // Menghapus gambar lama jika ada
                if (pengeluaranDiedit.getBukti() != null && !pengeluaranDiedit.getBukti().isEmpty()) {
                    File oldImage = new File(WordHandler.BUKTI_DIR, pengeluaranDiedit.getBukti());
                    if (oldImage.exists()) {
                        oldImage.delete();
                    }
                }
            }

            // Memperbarui objek pengeluaran
            pengeluaranDiedit.setJumlah(jumlahBaru);
            pengeluaranDiedit.setDeskripsi(deskripsiBaru);
            pengeluaranDiedit.setTanggal(new java.util.Date().toString());
            pengeluaranDiedit.setBukti(buktiBaru);

            // Perbarui total uang
            totalUang = updatedTotal;

            // Simpan data
            WordHandler.savePengeluaranList(pengeluaranList);
            WordHandler.saveTotalUang(totalUang);

            // Perbarui UI
            populateTable(pengeluaranList);
            updateTotalUangLabel();

            JOptionPane.showMessageDialog(this, "Pengeluaran berhasil diperbarui.");

            // Reset form dan mode edit
            resetForm();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Jumlah uang harus berupa angka.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui pengeluaran.", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Menghapus pengeluaran berdasarkan ID
     * @param id ID pengeluaran yang akan dihapus
     */
    private void deletePengeluaranById(long id) {
        try {
            Optional<Pengeluaran> optionalPengeluaran = pengeluaranList.stream().filter(p -> p.getId() == id).findFirst();
            if (optionalPengeluaran.isPresent()) {
                Pengeluaran pToRemove = optionalPengeluaran.get();
                pengeluaranList.remove(pToRemove);
                totalUang += pToRemove.getJumlah();

                // Menghapus gambar jika ada
                if (pToRemove.getBukti() != null && !pToRemove.getBukti().isEmpty()) {
                    File imageFile = new File(WordHandler.BUKTI_DIR, pToRemove.getBukti());
                    if (imageFile.exists()) {
                        imageFile.delete();
                    }
                }

                // Simpan data
                WordHandler.savePengeluaranList(pengeluaranList);
                WordHandler.saveTotalUang(totalUang);

                // Perbarui UI
                populateTable(pengeluaranList);
                updateTotalUangLabel();

                JOptionPane.showMessageDialog(this, "Entri berhasil dihapus.");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan perubahan.", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Mengisi tabel dengan data pengeluaran
     * @param list Daftar pengeluaran
     */
    private void populateTable(List<Pengeluaran> list) {
        tableModel.setRowCount(0);
        for (Pengeluaran p : list) {
            Object[] row = {p.getId(), p.getJumlah(), p.getDeskripsi(), p.getTanggal(), p.getBukti() != null ? p.getBukti() : "Tidak ada"};
            tableModel.addRow(row);
        }
    }

    /**
     * Memperbarui label total uang
     */
    private void updateTotalUangLabel() {
        lblTotalUang.setText("Total Uang: Rp " + String.format("%.2f", totalUang));
    }

    /**
     * Mereset form input dan mode edit
     */
    private void resetForm() {
        txtJumlah.setText("");
        txtDeskripsi.setText("");
        selectedImage = null;
        lblGambar.setText("Tidak ada gambar dipilih.");
        if (pengeluaranDiedit != null) {
            pengeluaranDiedit = null;
            btnSimpan.setVisible(true);
            btnUpdate.setVisible(false);
        }
    }

    /**
     * Memperbarui data panel secara keseluruhan
     * @param totalUang Total uang terbaru
     * @param pengeluaranList Daftar pengeluaran terbaru
     */
    public void updateData(double totalUang, List<Pengeluaran> pengeluaranList) {
        this.totalUang = totalUang;
        this.pengeluaranList = pengeluaranList;
        updateTotalUangLabel();
        populateTable(pengeluaranList);
        revalidate();
        repaint();
    }
}
