package org.example.ui;

import org.example.data.WordHandler;
import org.example.model.Pemasukan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class PemasukanPanel extends JPanel {
    private JTextField txtJumlah;
    private JTextField txtDeskripsi;
    private JButton btnSimpan;
    private JButton btnReset;
    private JButton btnEdit;
    private JButton btnUpdate;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTotalUang;
    private Pemasukan pemasukanDiedit = null; // Untuk menyimpan entri yang sedang diedit
    private double totalUang;
    private List<Pemasukan> pemasukanList;

    public PemasukanPanel(double initialTotalUang, List<Pemasukan> initialPemasukanList, JLabel lblTotalUangLabel, ActionListener kembaliListener) {
        this.totalUang = initialTotalUang;
        this.pemasukanList = initialPemasukanList;
        this.lblTotalUang = lblTotalUangLabel;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Pemasukan", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 26));
        add(lblTitle, BorderLayout.NORTH);

        // Split Pane Vertikal: Form dan Riwayat
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.4);
        splitPane.setDividerSize(2); // Ukuran pembatas

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Tambah Pemasukan"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblJumlah = new JLabel("Jumlah Uang:");
        lblJumlah.setFont(new Font("Arial", Font.PLAIN, 18));
        txtJumlah = new JTextField(20);

        JLabel lblDeskripsi = new JLabel("Deskripsi:");
        lblDeskripsi.setFont(new Font("Arial", Font.PLAIN, 18));
        txtDeskripsi = new JTextField(20);

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
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        splitPane.setTopComponent(formPanel);

        // History Panel
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(BorderFactory.createTitledBorder("Riwayat Pemasukan"));

        String[] columnNames = {"ID", "Jumlah", "Deskripsi", "Tanggal"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        populateTable(pemasukanList);

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
                Optional<Pemasukan> optionalPemasukan = pemasukanList.stream().filter(p -> p.getId() == id).findFirst();
                if (optionalPemasukan.isPresent()) {
                    pemasukanDiedit = optionalPemasukan.get();
                    // Mengisi form dengan data pemasukan yang dipilih
                    txtJumlah.setText(String.valueOf(pemasukanDiedit.getJumlah()));
                    txtDeskripsi.setText(pemasukanDiedit.getDeskripsi());
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
                        int confirm = JOptionPane.showConfirmDialog(PemasukanPanel.this, "Apakah Anda yakin ingin menghapus entri ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            deletePemasukanById(id);
                        }
                    }
                }
            }
        });

        splitPane.setBottomComponent(historyPanel);
        add(splitPane, BorderLayout.CENTER);

        // Tombol Simpan (Tambah)
        btnSimpan.addActionListener(e -> {
            tambahPemasukan();
        });

        // Tombol Update (Edit)
        btnUpdate.addActionListener(e -> {
            updatePemasukan();
        });

        // Tombol Reset
        btnReset.addActionListener(e -> {
            resetForm();
        });

        // Inisialisasi label total uang
        updateTotalUangLabel();
    }

    /**
     * Menambahkan pemasukan baru
     */
    private void tambahPemasukan() {
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

            Pemasukan pemasukan = new Pemasukan(System.currentTimeMillis(), jumlah, deskripsi, new java.util.Date().toString());
            pemasukanList.add(pemasukan);
            totalUang += jumlah;

            // Simpan data
            WordHandler.savePemasukanList(pemasukanList);
            WordHandler.saveTotalUang(totalUang);

            // Perbarui UI
            populateTable(pemasukanList);
            updateTotalUangLabel();

            JOptionPane.showMessageDialog(this, "Pemasukan berhasil ditambahkan.");

            // Reset form
            resetForm();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Jumlah uang harus berupa angka.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data.", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Memperbarui pemasukan yang sedang diedit
     */
    private void updatePemasukan() {
        if (pemasukanDiedit == null) {
            JOptionPane.showMessageDialog(this, "Tidak ada pemasukan yang sedang diedit.", "Error", JOptionPane.ERROR_MESSAGE);
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

            double updatedTotal = totalUang - pemasukanDiedit.getJumlah() + jumlahBaru;
            if (updatedTotal < 0) {
                JOptionPane.showMessageDialog(this, "Total uang tidak boleh negatif setelah pembaruan.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Perbarui objek pemasukan
            pemasukanDiedit.setJumlah(jumlahBaru);
            pemasukanDiedit.setDeskripsi(deskripsiBaru);
            pemasukanDiedit.setTanggal(new java.util.Date().toString());

            // Perbarui total uang
            totalUang = updatedTotal;

            // Simpan data
            WordHandler.savePemasukanList(pemasukanList);
            WordHandler.saveTotalUang(totalUang);

            // Perbarui UI
            populateTable(pemasukanList);
            updateTotalUangLabel();

            JOptionPane.showMessageDialog(this, "Pemasukan berhasil diperbarui.");

            // Reset form dan mode edit
            resetForm();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Jumlah uang harus berupa angka.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui pemasukan.", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Menghapus pemasukan berdasarkan ID
     * @param id ID pemasukan yang akan dihapus
     */
    private void deletePemasukanById(long id) {
        try {
            Optional<Pemasukan> optionalPemasukan = pemasukanList.stream().filter(p -> p.getId() == id).findFirst();
            if (optionalPemasukan.isPresent()) {
                Pemasukan pToRemove = optionalPemasukan.get();
                pemasukanList.remove(pToRemove);
                totalUang -= pToRemove.getJumlah();

                // Simpan data
                WordHandler.savePemasukanList(pemasukanList);
                WordHandler.saveTotalUang(totalUang);

                // Perbarui UI
                populateTable(pemasukanList);
                updateTotalUangLabel();

                JOptionPane.showMessageDialog(this, "Entri berhasil dihapus.");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan perubahan.", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Mengisi tabel dengan data pemasukan
     * @param list Daftar pemasukan
     */
    private void populateTable(List<Pemasukan> list) {
        tableModel.setRowCount(0);
        for (Pemasukan p : list) {
            Object[] row = {p.getId(), p.getJumlah(), p.getDeskripsi(), p.getTanggal()};
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
        if (pemasukanDiedit != null) {
            pemasukanDiedit = null;
            btnSimpan.setVisible(true);
            btnUpdate.setVisible(false);
        }
    }

    /**
     * Memperbarui data panel secara keseluruhan
     * @param totalUang Total uang terbaru
     * @param pemasukanList Daftar pemasukan terbaru
     */
    public void updateData(double totalUang, List<Pemasukan> pemasukanList) {
        this.totalUang = totalUang;
        this.pemasukanList = pemasukanList;
        updateTotalUangLabel();
        populateTable(pemasukanList);
        revalidate();
        repaint();
    }
}
