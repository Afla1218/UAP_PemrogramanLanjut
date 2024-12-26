package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.util.Units;

public class PengelolaKeuangan extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Home Screen Components
    private JLabel lblProgramName;
    private JLabel lblUserName;
    private JLabel lblTotalUang;

    // Data
    private double totalUang = 0.0;
    private List<Pemasukan> listPemasukan = new ArrayList<>();
    private List<Pengeluaran> listPengeluaran = new ArrayList<>();

    // Tables
    private JTable tablePemasukan;
    private JTable tablePengeluaran;
    private DefaultTableModel modelPemasukan;
    private DefaultTableModel modelPengeluaran;

    public PengelolaKeuangan() {
        try {
            // Set Nimbus Look and Feel for modern UI
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Jika Nimbus tidak tersedia, gunakan Look and Feel default
        }

        setTitle("Pengelola Keuangan Pribadi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Initialize CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize Panels
        mainPanel.add(createHomePanel(), "Home");
        mainPanel.add(createPemasukanPanel(), "Pemasukan");
        mainPanel.add(createPengeluaranPanel(), "Pengeluaran");

        add(mainPanel);

        // Show Home Panel initially
        cardLayout.show(mainPanel, "Home");
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel header = new JPanel(new GridLayout(3, 1, 10, 10));
        lblProgramName = new JLabel("Program Pengelola Keuangan Pribadi", SwingConstants.CENTER);
        lblProgramName.setFont(new Font("Arial", Font.BOLD, 24));

        lblUserName = new JLabel("Nama: Tabuarasa", SwingConstants.CENTER);
        lblUserName.setFont(new Font("Arial", Font.PLAIN, 18));

        lblTotalUang = new JLabel("Total Uang: Rp " + String.format("%.2f", totalUang), SwingConstants.CENTER);
        lblTotalUang.setFont(new Font("Arial", Font.PLAIN, 18));

        header.add(lblProgramName);
        header.add(lblUserName);
        header.add(lblTotalUang);

        panel.add(header, BorderLayout.CENTER);

        // Navigation Buttons
        JPanel navPanel = new JPanel(new FlowLayout());
        JButton btnPemasukan = new JButton("Kelola Pemasukan");
        JButton btnPengeluaran = new JButton("Kelola Pengeluaran");

        btnPemasukan.setPreferredSize(new Dimension(200, 40));
        btnPengeluaran.setPreferredSize(new Dimension(200, 40));

        btnPemasukan.addActionListener(e -> cardLayout.show(mainPanel, "Pemasukan"));
        btnPengeluaran.addActionListener(e -> cardLayout.show(mainPanel, "Pengeluaran"));

        navPanel.add(btnPemasukan);
        navPanel.add(btnPengeluaran);

        panel.add(navPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPemasukanPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("Kelola Pemasukan", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        header.add(lblTitle, BorderLayout.CENTER);

        JButton btnKembali = new JButton("Kembali");
        btnKembali.setPreferredSize(new Dimension(100, 30));
        btnKembali.addActionListener(e -> cardLayout.show(mainPanel, "Home"));
        header.add(btnKembali, BorderLayout.WEST);

        panel.add(header, BorderLayout.NORTH);

        // Table Pemasukan
        String[] columnPemasukan = {"ID", "Jumlah Uang", "Deskripsi"};
        modelPemasukan = new DefaultTableModel(columnPemasukan, 0);
        tablePemasukan = new JTable(modelPemasukan);
        tablePemasukan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPemasukan = new JScrollPane(tablePemasukan);
        panel.add(scrollPemasukan, BorderLayout.CENTER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblJumlah = new JLabel("Jumlah Uang:");
        JTextField txtJumlah = new JTextField();
        txtJumlah.setPreferredSize(new Dimension(200, 30));

        JLabel lblDeskripsi = new JLabel("Deskripsi:");
        JTextField txtDeskripsi = new JTextField();
        txtDeskripsi.setPreferredSize(new Dimension(200, 30));

        JButton btnTambah = new JButton("Tambah");
        JButton btnEdit = new JButton("Edit");
        JButton btnHapus = new JButton("Hapus");

        // Tambah Komponen ke Form
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
        formPanel.add(btnTambah, gbc);

        gbc.gridx = 1;
        formPanel.add(btnEdit, gbc);

        gbc.gridx = 2;
        formPanel.add(btnHapus, gbc);

        panel.add(formPanel, BorderLayout.SOUTH);

        // Action Listeners
        btnTambah.addActionListener(e -> {
            try {
                String jumlahText = txtJumlah.getText().trim();
                String deskripsi = txtDeskripsi.getText().trim();

                if (jumlahText.isEmpty() || deskripsi.isEmpty()) {
                    showErrorDialog("Semua field harus diisi.");
                    return;
                }

                double jumlah = Double.parseDouble(jumlahText);
                if (jumlah < 0) {
                    showErrorDialog("Jumlah uang tidak boleh negatif.");
                    return;
                }

                Pemasukan pemasukan = new Pemasukan(generateId(), jumlah, deskripsi);
                listPemasukan.add(pemasukan);
                modelPemasukan.addRow(new Object[]{pemasukan.getId(), pemasukan.getJumlah(), pemasukan.getDeskripsi()});
                totalUang += jumlah;
                updateTotalUang();

                JOptionPane.showMessageDialog(this, "Pemasukan berhasil ditambahkan.");
                txtJumlah.setText("");
                txtDeskripsi.setText("");
            } catch (NumberFormatException ex) {
                showErrorDialog("Jumlah uang harus berupa angka.");
            } catch (Exception ex) {
                showErrorDialog("Terjadi kesalahan saat menambah pemasukan: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnEdit.addActionListener(e -> {
            int selectedRow = tablePemasukan.getSelectedRow();
            if (selectedRow == -1) {
                showErrorDialog("Pilih baris yang akan diedit.");
                return;
            }

            try {
                String jumlahText = txtJumlah.getText().trim();
                String deskripsi = txtDeskripsi.getText().trim();

                if (jumlahText.isEmpty() || deskripsi.isEmpty()) {
                    showErrorDialog("Semua field harus diisi.");
                    return;
                }

                double jumlah = Double.parseDouble(jumlahText);
                if (jumlah < 0) {
                    showErrorDialog("Jumlah uang tidak boleh negatif.");
                    return;
                }

                Pemasukan pemasukan = listPemasukan.get(selectedRow);
                totalUang -= pemasukan.getJumlah(); // Kurangi total uang dengan jumlah lama

                pemasukan.setJumlah(jumlah);
                pemasukan.setDeskripsi(deskripsi);
                totalUang += jumlah; // Tambahkan total uang dengan jumlah baru

                // Update tabel
                modelPemasukan.setValueAt(jumlah, selectedRow, 1);
                modelPemasukan.setValueAt(deskripsi, selectedRow, 2);

                updateTotalUang();

                JOptionPane.showMessageDialog(this, "Pemasukan berhasil diperbarui.");
                txtJumlah.setText("");
                txtDeskripsi.setText("");
            } catch (NumberFormatException ex) {
                showErrorDialog("Jumlah uang harus berupa angka.");
            } catch (Exception ex) {
                showErrorDialog("Terjadi kesalahan saat mengedit pemasukan: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnHapus.addActionListener(e -> {
            int selectedRow = tablePemasukan.getSelectedRow();
            if (selectedRow == -1) {
                showErrorDialog("Pilih baris yang akan dihapus.");
                return;
            }

            try {
                Pemasukan pemasukan = listPemasukan.get(selectedRow);
                int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus pemasukan ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    totalUang -= pemasukan.getJumlah();
                    listPemasukan.remove(selectedRow);
                    modelPemasukan.removeRow(selectedRow);
                    updateTotalUang();
                    JOptionPane.showMessageDialog(this, "Pemasukan berhasil dihapus.");
                }
            } catch (Exception ex) {
                showErrorDialog("Terjadi kesalahan saat menghapus pemasukan: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Table Selection Listener
        tablePemasukan.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && tablePemasukan.getSelectedRow() != -1) {
                int selectedRow = tablePemasukan.getSelectedRow();
                txtJumlah.setText(modelPemasukan.getValueAt(selectedRow, 1).toString());
                txtDeskripsi.setText(modelPemasukan.getValueAt(selectedRow, 2).toString());
            }
        });

        return panel;
    }

    private JPanel createPengeluaranPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("Kelola Pengeluaran", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        header.add(lblTitle, BorderLayout.CENTER);

        JButton btnKembali = new JButton("Kembali");
        btnKembali.setPreferredSize(new Dimension(100, 30));
        btnKembali.addActionListener(e -> cardLayout.show(mainPanel, "Home"));
        header.add(btnKembali, BorderLayout.WEST);

        panel.add(header, BorderLayout.NORTH);

        // Table Pengeluaran
        String[] columnPengeluaran = {"ID", "Jumlah Uang", "Deskripsi", "Bukti"};
        modelPengeluaran = new DefaultTableModel(columnPengeluaran, 0);
        tablePengeluaran = new JTable(modelPengeluaran);
        tablePengeluaran.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPengeluaran = new JScrollPane(tablePengeluaran);
        panel.add(scrollPengeluaran, BorderLayout.CENTER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblJumlah = new JLabel("Jumlah Uang:");
        JTextField txtJumlah = new JTextField();
        txtJumlah.setPreferredSize(new Dimension(200, 30));

        JLabel lblDeskripsi = new JLabel("Deskripsi:");
        JTextField txtDeskripsi = new JTextField();
        txtDeskripsi.setPreferredSize(new Dimension(200, 30));

        JLabel lblBukti = new JLabel("Bukti Pengeluaran:");
        JButton btnPilihGambar = new JButton("Pilih Gambar");
        JLabel lblGambar = new JLabel("Tidak ada gambar dipilih.");

        final File[] selectedImage = {null};

        btnPilihGambar.setPreferredSize(new Dimension(150, 30));
        lblGambar.setPreferredSize(new Dimension(200, 30));

        JButton btnTambah = new JButton("Tambah");
        JButton btnEdit = new JButton("Edit");
        JButton btnHapus = new JButton("Hapus");

        // Tambah Komponen ke Form
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
        formPanel.add(btnPilihGambar, gbc);

        gbc.gridx = 2;
        formPanel.add(lblGambar, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(btnTambah, gbc);

        gbc.gridx = 1;
        formPanel.add(btnEdit, gbc);

        gbc.gridx = 2;
        formPanel.add(btnHapus, gbc);

        panel.add(formPanel, BorderLayout.SOUTH);

        // Action Listeners
        btnPilihGambar.addActionListener(e -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "Image Files", "jpg", "png", "jpeg", "bmp");
                fileChooser.setFileFilter(filter);
                int returnVal = fileChooser.showOpenDialog(this);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    selectedImage[0] = fileChooser.getSelectedFile();
                    lblGambar.setText(selectedImage[0].getName());
                }
            } catch (Exception ex) {
                showErrorDialog("Terjadi kesalahan saat memilih gambar: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnTambah.addActionListener(e -> {
            try {
                String jumlahText = txtJumlah.getText().trim();
                String deskripsi = txtDeskripsi.getText().trim();

                if (jumlahText.isEmpty() || deskripsi.isEmpty()) {
                    showErrorDialog("Semua field harus diisi.");
                    return;
                }

                double jumlah = Double.parseDouble(jumlahText);
                if (jumlah < 0) {
                    showErrorDialog("Jumlah uang tidak boleh negatif.");
                    return;
                }

                if (jumlah > totalUang) {
                    showErrorDialog("Jumlah pengeluaran melebihi total uang.");
                    return;
                }

                Pengeluaran pengeluaran = new Pengeluaran(generateId(), jumlah, deskripsi, selectedImage[0]);
                listPengeluaran.add(pengeluaran);
                modelPengeluaran.addRow(new Object[]{pengeluaran.getId(), pengeluaran.getJumlah(), pengeluaran.getDeskripsi(), pengeluaran.getBukti() != null ? pengeluaran.getBukti().getName() : "Tidak ada gambar"});
                totalUang -= jumlah;
                updateTotalUang();

                JOptionPane.showMessageDialog(this, "Pengeluaran berhasil ditambahkan.");
                txtJumlah.setText("");
                txtDeskripsi.setText("");
                selectedImage[0] = null;
                lblGambar.setText("Tidak ada gambar dipilih.");
            } catch (NumberFormatException ex) {
                showErrorDialog("Jumlah uang harus berupa angka.");
            } catch (Exception ex) {
                showErrorDialog("Terjadi kesalahan saat menambah pengeluaran: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnEdit.addActionListener(e -> {
            int selectedRow = tablePengeluaran.getSelectedRow();
            if (selectedRow == -1) {
                showErrorDialog("Pilih baris yang akan diedit.");
                return;
            }

            try {
                String jumlahText = txtJumlah.getText().trim();
                String deskripsi = txtDeskripsi.getText().trim();

                if (jumlahText.isEmpty() || deskripsi.isEmpty()) {
                    showErrorDialog("Semua field harus diisi.");
                    return;
                }

                double jumlah = Double.parseDouble(jumlahText);
                if (jumlah < 0) {
                    showErrorDialog("Jumlah uang tidak boleh negatif.");
                    return;
                }

                Pengeluaran pengeluaran = listPengeluaran.get(selectedRow);
                if (jumlah > (totalUang + pengeluaran.getJumlah())) {
                    showErrorDialog("Jumlah pengeluaran melebihi total uang.");
                    return;
                }

                totalUang += pengeluaran.getJumlah(); // Tambah kembali jumlah lama
                pengeluaran.setJumlah(jumlah);
                pengeluaran.setDeskripsi(deskripsi);
                pengeluaran.setBukti(selectedImage[0]);
                totalUang -= jumlah; // Kurangi dengan jumlah baru

                // Update tabel
                modelPengeluaran.setValueAt(jumlah, selectedRow, 1);
                modelPengeluaran.setValueAt(deskripsi, selectedRow, 2);
                modelPengeluaran.setValueAt(pengeluaran.getBukti() != null ? pengeluaran.getBukti().getName() : "Tidak ada gambar", selectedRow, 3);

                updateTotalUang();

                JOptionPane.showMessageDialog(this, "Pengeluaran berhasil diperbarui.");
                txtJumlah.setText("");
                txtDeskripsi.setText("");
                selectedImage[0] = null;
                lblGambar.setText("Tidak ada gambar dipilih.");
            } catch (NumberFormatException ex) {
                showErrorDialog("Jumlah uang harus berupa angka.");
            } catch (Exception ex) {
                showErrorDialog("Terjadi kesalahan saat mengedit pengeluaran: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        btnHapus.addActionListener(e -> {
            int selectedRow = tablePengeluaran.getSelectedRow();
            if (selectedRow == -1) {
                showErrorDialog("Pilih baris yang akan dihapus.");
                return;
            }

            try {
                Pengeluaran pengeluaran = listPengeluaran.get(selectedRow);
                int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus pengeluaran ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    totalUang += pengeluaran.getJumlah();
                    listPengeluaran.remove(selectedRow);
                    modelPengeluaran.removeRow(selectedRow);
                    updateTotalUang();
                    JOptionPane.showMessageDialog(this, "Pengeluaran berhasil dihapus.");
                }
            } catch (Exception ex) {
                showErrorDialog("Terjadi kesalahan saat menghapus pengeluaran: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Table Selection Listener
        tablePengeluaran.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && tablePengeluaran.getSelectedRow() != -1) {
                int selectedRow = tablePengeluaran.getSelectedRow();
                txtJumlah.setText(modelPengeluaran.getValueAt(selectedRow, 1).toString());
                txtDeskripsi.setText(modelPengeluaran.getValueAt(selectedRow, 2).toString());
                // Gambar tidak di-set secara otomatis untuk pengeditan
            }
        });

        return panel;
    }

    private String generateId() {
        return "ID-" + (System.currentTimeMillis());
    }

    private void updateTotalUang() {
        lblTotalUang.setText("Total Uang: Rp " + String.format("%.2f", totalUang));
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Inner Classes for Data Models
    class Pemasukan {
        private String id;
        private double jumlah;
        private String deskripsi;

        public Pemasukan(String id, double jumlah, String deskripsi) {
            this.id = id;
            this.jumlah = jumlah;
            this.deskripsi = deskripsi;
        }

        public String getId() {
            return id;
        }

        public double getJumlah() {
            return jumlah;
        }

        public void setJumlah(double jumlah) {
            this.jumlah = jumlah;
        }

        public String getDeskripsi() {
            return deskripsi;
        }

        public void setDeskripsi(String deskripsi) {
            this.deskripsi = deskripsi;
        }
    }

    class Pengeluaran {
        private String id;
        private double jumlah;
        private String deskripsi;
        private File bukti;

        public Pengeluaran(String id, double jumlah, String deskripsi, File bukti) {
            this.id = id;
            this.jumlah = jumlah;
            this.deskripsi = deskripsi;
            this.bukti = bukti;
        }

        public String getId() {
            return id;
        }

        public double getJumlah() {
            return jumlah;
        }

        public void setJumlah(double jumlah) {
            this.jumlah = jumlah;
        }

        public String getDeskripsi() {
            return deskripsi;
        }

        public void setDeskripsi(String deskripsi) {
            this.deskripsi = deskripsi;
        }

        public File getBukti() {
            return bukti;
        }

        public void setBukti(File bukti) {
            this.bukti = bukti;
        }
    }

    public static void main(String[] args) {
        // Pastikan Apache POI library ada di classpath
        SwingUtilities.invokeLater(() -> {
            try {
                PengelolaKeuangan app = new PengelolaKeuangan();
                app.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat menjalankan aplikasi: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}
