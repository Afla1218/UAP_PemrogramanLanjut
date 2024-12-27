package org.example.ui;

import org.example.data.WordHandler;
import org.example.model.Pemasukan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

public class PemasukanPanel extends JPanel {
    private JTextField txtJumlah;
    private JTextField txtDeskripsi;
    private JButton btnSimpan;
    private JButton btnReset;
    private JButton btnKembali;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTotalUang;

    public PemasukanPanel(double totalUang, List<Pemasukan> pemasukanList, JLabel lblTotalUangLabel, ActionListener kembaliListener) {
        this.lblTotalUang = lblTotalUangLabel;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Pemasukan", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 26));
        add(lblTitle, BorderLayout.NORTH);

        // Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.4);

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

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnSimpan = new JButton("Simpan");
        btnSimpan.setFont(new Font("Arial", Font.PLAIN, 16));
        btnReset = new JButton("Reset");
        btnReset.setFont(new Font("Arial", Font.PLAIN, 16));

        btnPanel.add(btnSimpan);
        btnPanel.add(btnReset);

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

        // Kembali Button
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnKembali = new JButton("Kembali");
        btnKembali.setFont(new Font("Arial", Font.PLAIN, 16));
        btnKembali.addActionListener(kembaliListener);
        backPanel.add(btnKembali);
        historyPanel.add(backPanel, BorderLayout.SOUTH);

        // Add Double Click Listener for Deletion
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int rowSelected = table.getSelectedRow();
                    if (rowSelected != -1) {
                        long id = (Long) tableModel.getValueAt(rowSelected, 0);
                        int confirm = JOptionPane.showConfirmDialog(PemasukanPanel.this, "Apakah Anda yakin ingin menghapus entri ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            try {
                                List<Pemasukan> updatedList = WordHandler.loadPemasukan();
                                updatedList.removeIf(p -> p.getId() == id);
                                WordHandler.savePemasukanList(updatedList);
                                populateTable(updatedList);
                                JOptionPane.showMessageDialog(PemasukanPanel.this, "Entri berhasil dihapus.");
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(PemasukanPanel.this, "Gagal menyimpan perubahan.", "Error", JOptionPane.ERROR_MESSAGE);
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

        splitPane.setBottomComponent(historyPanel);
        add(splitPane, BorderLayout.CENTER);

        // Button Actions
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
                Pemasukan pemasukan = new Pemasukan(System.currentTimeMillis(), jumlah, deskripsi, new java.util.Date().toString());
                WordHandler.savePemasukan(pemasukan);
                populateTable(WordHandler.loadPemasukan());
                lblTotalUang.setText("Total Uang: Rp " + String.format("%.2f", Double.parseDouble(lblTotalUang.getText().replaceAll("[^\\d.]", "")) + jumlah));
                JOptionPane.showMessageDialog(this, "Pemasukan berhasil ditambahkan.");
                txtJumlah.setText("");
                txtDeskripsi.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Jumlah uang harus berupa angka.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data.", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        btnReset.addActionListener(e -> {
            txtJumlah.setText("");
            txtDeskripsi.setText("");
        });
    }

    private void populateTable(List<Pemasukan> list) {
        tableModel.setRowCount(0);
        for (Pemasukan p : list) {
            Object[] row = {p.getId(), p.getJumlah(), p.getDeskripsi(), p.getTanggal()};
            tableModel.addRow(row);
        }
    }
}
