/* Menggabungkan Tambah Pemasukan dengan Riwayat Pemasukan dan Tambah Pengeluaran dengan Riwayat Pengeluaran */
package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
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
    private List<Pemasukan> pemasukanList = new ArrayList<>();
    private List<Pengeluaran> pengeluaranList = new ArrayList<>();

    public PengelolaKeuangan() {
        setTitle("Pengelola Keuangan Pribadi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);

        // Memuat total uang dan riwayat pemasukan & pengeluaran
        loadTotalUangFromWord();
        pemasukanList = loadPemasukanFromWord();
        pengeluaranList = loadPengeluaranFromWord();

        // Initialize CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize Panels
        mainPanel.add(createHomePanel(), "Home");
        mainPanel.add(createPemasukanPanel(), "Pemasukan"); // Panel Terintegrasi Pemasukan
        mainPanel.add(createPengeluaranPanel(), "Pengeluaran"); // Panel Terintegrasi Pengeluaran

        add(mainPanel);

        // Show Home Panel initially
        cardLayout.show(mainPanel, "Home");
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        JPanel header = new JPanel(new GridLayout(3, 1, 15, 15));
        lblProgramName = new JLabel("Program Pengelola Keuangan Pribadi", SwingConstants.CENTER);
        lblProgramName.setFont(new Font("Arial", Font.BOLD, 28));

        lblUserName = new JLabel("Nama: Tabuarasa", SwingConstants.CENTER);
        lblUserName.setFont(new Font("Arial", Font.PLAIN, 20));

        lblTotalUang = new JLabel("Total Uang: Rp " + String.format("%.2f", totalUang), SwingConstants.CENTER);
        lblTotalUang.setFont(new Font("Arial", Font.PLAIN, 20));

        header.add(lblProgramName);
        header.add(lblUserName);
        header.add(lblTotalUang);

        panel.add(header, BorderLayout.CENTER);

        // Navigation Buttons
        JPanel navPanel = new JPanel(new GridLayout(2, 1, 20, 20)); // Ubah grid menjadi 2 baris
        navPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton btnPemasukan = new JButton("Pemasukan");
        btnPemasukan.setFont(new Font("Arial", Font.PLAIN, 18));
        JButton btnPengeluaran = new JButton("Pengeluaran");
        btnPengeluaran.setFont(new Font("Arial", Font.PLAIN, 18));

        btnPemasukan.addActionListener(e -> {
            pemasukanList = loadPemasukanFromWord(); // Refresh data
            cardLayout.show(mainPanel, "Pemasukan");
        });
        btnPengeluaran.addActionListener(e -> {
            pengeluaranList = loadPengeluaranFromWord(); // Refresh data
            cardLayout.show(mainPanel, "Pengeluaran");
        });

        navPanel.add(btnPemasukan);
        navPanel.add(btnPengeluaran);

        panel.add(navPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Panel Terintegrasi Pemasukan
    private JPanel createPemasukanPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Pemasukan", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 26));

        panel.add(lblTitle, BorderLayout.NORTH);

        // Split Pane untuk Form dan Tabel
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.4); // Proporsi awal

        // Panel Form Tambah Pemasukan
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Tambah Pemasukan"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblJumlah = new JLabel("Jumlah Uang:");
        lblJumlah.setFont(new Font("Arial", Font.PLAIN, 18));
        JTextField txtJumlah = new JTextField(20);

        JLabel lblDeskripsi = new JLabel("Deskripsi:");
        lblDeskripsi.setFont(new Font("Arial", Font.PLAIN, 18));
        JTextField txtDeskripsi = new JTextField(20);

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

        // Tombol Simpan dan Reset
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnSimpan = new JButton("Simpan");
        btnSimpan.setFont(new Font("Arial", Font.PLAIN, 16));
        JButton btnReset = new JButton("Reset");
        btnReset.setFont(new Font("Arial", Font.PLAIN, 16));

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
                totalUang += jumlah;
                updateTotalUang();
                saveTotalUangToWord(); // Menyimpan total uang
                savePemasukanToWord(jumlah, deskripsi); // Menyimpan riwayat pemasukan
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

        btnPanel.add(btnSimpan);
        btnPanel.add(btnReset);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        // Panel Tabel Riwayat Pemasukan
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(BorderFactory.createTitledBorder("Riwayat Pemasukan"));

        String[] columnNames = {"ID", "Jumlah", "Deskripsi", "Tanggal"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            // Membuat kolom ID tidak editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Semua kolom tidak bisa diedit langsung
            }
        };
        JTable table = new JTable(tableModel);
        populatePemasukanTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        historyPanel.add(scrollPane, BorderLayout.CENTER);

        // Tombol Kembali
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnKembali = new JButton("Kembali");
        btnKembali.setFont(new Font("Arial", Font.PLAIN, 16));
        btnKembali.addActionListener(e -> cardLayout.show(mainPanel, "Home"));
        backPanel.add(btnKembali);

        historyPanel.add(backPanel, BorderLayout.SOUTH);

        // Listener untuk klik dua kali pada tabel untuk menghapus entri
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Double klik untuk menghapus
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        long id = (Long) table.getValueAt(row, 0);
                        int confirm = JOptionPane.showConfirmDialog(panel, "Apakah Anda yakin ingin menghapus entri ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            // Hapus dari daftar
                            pemasukanList.removeIf(p -> p.getId() == id);
                            try {
                                savePemasukanListToWord(pemasukanList);
                                // Refresh tabel
                                tableModel.setRowCount(0);
                                populatePemasukanTable(tableModel);
                                JOptionPane.showMessageDialog(panel, "Entri berhasil dihapus.");
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(panel, "Gagal menyimpan perubahan.", "Error", JOptionPane.ERROR_MESSAGE);
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

        splitPane.setTopComponent(formPanel);
        splitPane.setBottomComponent(historyPanel);

        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    // Panel Terintegrasi Pengeluaran
    private JPanel createPengeluaranPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Pengeluaran", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 26));

        panel.add(lblTitle, BorderLayout.NORTH);

        // Split Pane untuk Form dan Tabel
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5); // Proporsi awal

        // Panel Form Tambah Pengeluaran
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Tambah Pengeluaran"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblJumlah = new JLabel("Jumlah Uang:");
        lblJumlah.setFont(new Font("Arial", Font.PLAIN, 18));
        JTextField txtJumlah = new JTextField(20);

        JLabel lblDeskripsi = new JLabel("Deskripsi:");
        lblDeskripsi.setFont(new Font("Arial", Font.PLAIN, 18));
        JTextField txtDeskripsi = new JTextField(20);

        JLabel lblBukti = new JLabel("Bukti Pengeluaran:");
        lblBukti.setFont(new Font("Arial", Font.PLAIN, 18));
        JButton btnPilihGambar = new JButton("Pilih Gambar");
        btnPilihGambar.setFont(new Font("Arial", Font.PLAIN, 16));
        JLabel lblGambar = new JLabel("Tidak ada gambar dipilih.");
        lblGambar.setFont(new Font("Arial", Font.ITALIC, 16));

        final File[] selectedImage = {null};

        btnPilihGambar.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Image Files", "jpg", "png", "jpeg", "bmp");
            fileChooser.setFileFilter(filter);
            int returnVal = fileChooser.showOpenDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                selectedImage[0] = fileChooser.getSelectedFile();
                lblGambar.setText(selectedImage[0].getName());
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

        // Tombol Simpan dan Reset
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnSimpan = new JButton("Simpan");
        btnSimpan.setFont(new Font("Arial", Font.PLAIN, 16));
        JButton btnReset = new JButton("Reset");
        btnReset.setFont(new Font("Arial", Font.PLAIN, 16));

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

                if (jumlah > totalUang) {
                    JOptionPane.showMessageDialog(this, "Jumlah pengeluaran melebihi total uang.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Menyimpan bukti pengeluaran jika ada
                String bukti = null;
                if (selectedImage[0] != null) {
                    bukti = selectedImage[0].getName();
                    // Menyalin gambar ke folder "bukti_pengeluaran"
                    File destDir = new File("bukti_pengeluaran");
                    if (!destDir.exists()) destDir.mkdirs();
                    File destFile = new File(destDir, bukti);
                    try (FileInputStream fis = new FileInputStream(selectedImage[0]);
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

                // Simpan ke Word
                savePengeluaranToWord(jumlah, deskripsi, bukti);

                totalUang -= jumlah;
                updateTotalUang();
                saveTotalUangToWord(); // Menyimpan total uang
                JOptionPane.showMessageDialog(this, "Pengeluaran berhasil ditambahkan dan disimpan ke Word.");
                // Reset fields
                txtJumlah.setText("");
                txtDeskripsi.setText("");
                selectedImage[0] = null;
                lblGambar.setText("Tidak ada gambar dipilih.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Jumlah uang harus berupa angka.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan ke Word.", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        btnReset.addActionListener(e -> {
            txtJumlah.setText("");
            txtDeskripsi.setText("");
            selectedImage[0] = null;
            lblGambar.setText("Tidak ada gambar dipilih.");
        });

        btnPanel.add(btnSimpan);
        btnPanel.add(btnReset);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        // Panel Tabel Riwayat Pengeluaran
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(BorderFactory.createTitledBorder("Riwayat Pengeluaran"));

        String[] columnNames = {"ID", "Jumlah", "Deskripsi", "Tanggal", "Bukti"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            // Membuat kolom ID tidak editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Semua kolom tidak bisa diedit langsung
            }
        };
        JTable table = new JTable(tableModel);
        populatePengeluaranTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        historyPanel.add(scrollPane, BorderLayout.CENTER);

        // Tombol Edit dan Kembali
        JPanel btnPanelHistory = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton btnEdit = new JButton("Edit");
        btnEdit.setFont(new Font("Arial", Font.PLAIN, 16));
        JButton btnKembali = new JButton("Kembali");
        btnKembali.setFont(new Font("Arial", Font.PLAIN, 16));

        btnEdit.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                long id = (Long) table.getValueAt(selectedRow, 0);
                Pengeluaran p = pengeluaranList.stream().filter(pe -> pe.getId() == id).findFirst().orElse(null);
                if (p != null) {
                    showEditPengeluaranDialog(p, tableModel, selectedRow);
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Silakan pilih entri yang ingin diedit.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnKembali.addActionListener(e -> cardLayout.show(mainPanel, "Home"));

        btnPanelHistory.add(btnEdit);
        btnPanelHistory.add(btnKembali);

        historyPanel.add(btnPanelHistory, BorderLayout.SOUTH);

        // Listener untuk klik dua kali pada tabel untuk menghapus entri
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Double klik untuk menghapus
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        long id = (Long) table.getValueAt(row, 0);
                        int confirm = JOptionPane.showConfirmDialog(panel, "Apakah Anda yakin ingin menghapus entri ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            // Hapus dari daftar
                            Pengeluaran p = pengeluaranList.stream().filter(pe -> pe.getId() == id).findFirst().orElse(null);
                            if (p != null) {
                                totalUang += p.getJumlah(); // Mengembalikan uang ke total
                                updateTotalUang();
                                try {
                                    saveTotalUangToWord(); // Menyimpan total uang
                                } catch (IOException exSave) {
                                    JOptionPane.showMessageDialog(panel, "Gagal menyimpan total uang.", "Error", JOptionPane.ERROR_MESSAGE);
                                    exSave.printStackTrace();
                                }
                                pengeluaranList.remove(p);
                                try {
                                    savePengeluaranListToWord(pengeluaranList);
                                    // Refresh tabel
                                    tableModel.setRowCount(0);
                                    populatePengeluaranTable(tableModel);
                                    JOptionPane.showMessageDialog(panel, "Entri berhasil dihapus.");
                                } catch (IOException exSaveList) {
                                    JOptionPane.showMessageDialog(panel, "Gagal menyimpan perubahan.", "Error", JOptionPane.ERROR_MESSAGE);
                                    exSaveList.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        });

        splitPane.setTopComponent(formPanel);
        splitPane.setBottomComponent(historyPanel);

        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private void populatePemasukanTable(DefaultTableModel tableModel) {
        tableModel.setRowCount(0); // Clear existing data
        for (Pemasukan p : pemasukanList) {
            Object[] row = {p.getId(), p.getJumlah(), p.getDeskripsi(), p.getTanggal()};
            tableModel.addRow(row);
        }
        System.out.println("Tabel pemasukan di-populate dengan " + pemasukanList.size() + " entri.");
    }

    private void populatePengeluaranTable(DefaultTableModel tableModel) {
        tableModel.setRowCount(0); // Clear existing data
        for (Pengeluaran p : pengeluaranList) {
            Object[] row = {p.getId(), p.getJumlah(), p.getDeskripsi(), p.getTanggal(), p.getBukti()};
            tableModel.addRow(row);
        }
        System.out.println("Tabel pengeluaran di-populate dengan " + pengeluaranList.size() + " entri.");
    }

    private void updateTotalUang() {
        lblTotalUang.setText("Total Uang: Rp " + String.format("%.2f", totalUang));
    }

    private void saveTotalUangToWord() throws IOException {
        XWPFDocument document = new XWPFDocument();
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(String.valueOf(totalUang));

        try (FileOutputStream out = new FileOutputStream("TotalUang.docx")) {
            document.write(out);
        }
        document.close();
        System.out.println("Total uang disimpan: " + totalUang);
    }

    private void loadTotalUangFromWord() {
        File file = new File("TotalUang.docx");
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 XWPFDocument document = new XWPFDocument(fis)) {
                XWPFParagraph paragraph = document.getParagraphArray(0);
                if (paragraph != null) {
                    String text = paragraph.getText();
                    totalUang = Double.parseDouble(text);
                    System.out.println("Total uang dimuat: " + totalUang);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal memuat total uang dari Word.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            System.out.println("File TotalUang.docx tidak ditemukan. Menginisialisasi total uang ke 0.");
            totalUang = 0.0;
        }
    }

    private void savePemasukanToWord(double jumlah, String deskripsi) throws IOException {
        XWPFDocument document;
        File file = new File("RiwayatPemasukan.docx");
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                document = new XWPFDocument(fis);
            }
        } else {
            document = new XWPFDocument();
            // Menambahkan header tabel
            XWPFTable table = document.createTable();
            XWPFTableRow header = table.getRow(0);
            header.getCell(0).setText("ID");
            header.addNewTableCell().setText("Jumlah");
            header.addNewTableCell().setText("Deskripsi");
            header.addNewTableCell().setText("Tanggal");
        }

        // Menambahkan baris baru
        XWPFTable table = document.getTables().get(0);
        XWPFTableRow row = table.createRow();
        long id = System.currentTimeMillis();
        row.getCell(0).setText(String.valueOf(id)); // ID unik
        row.getCell(1).setText(String.format("%.2f", jumlah));
        row.getCell(2).setText(deskripsi);
        row.getCell(3).setText(new java.util.Date().toString());

        // Menambahkan ke list
        Pemasukan p = new Pemasukan();
        p.setId(id);
        p.setJumlah(jumlah);
        p.setDeskripsi(deskripsi);
        p.setTanggal(new java.util.Date().toString());
        pemasukanList.add(p);

        try (FileOutputStream out = new FileOutputStream(file)) {
            document.write(out);
        }

        document.close();
        System.out.println("Pemasukan ditambahkan: " + jumlah + ", " + deskripsi);
    }

    private List<Pemasukan> loadPemasukanFromWord() {
        List<Pemasukan> list = new ArrayList<>();
        File file = new File("RiwayatPemasukan.docx");
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 XWPFDocument document = new XWPFDocument(fis)) {
                XWPFTable table = document.getTables().get(0);
                for (int i = 1; i < table.getNumberOfRows(); i++) { // Mulai dari 1 untuk melewati header
                    XWPFTableRow row = table.getRow(i);
                    Pemasukan pm = new Pemasukan();
                    pm.setId(Long.parseLong(row.getCell(0).getText()));
                    pm.setJumlah(Double.parseDouble(row.getCell(1).getText()));
                    pm.setDeskripsi(row.getCell(2).getText());
                    pm.setTanggal(row.getCell(3).getText());
                    list.add(pm);
                }
                System.out.println("Berhasil memuat " + list.size() + " entri pemasukan dari RiwayatPemasukan.docx");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal memuat riwayat pemasukan dari Word.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            System.out.println("File RiwayatPemasukan.docx tidak ditemukan.");
        }
        return list;
    }

    private void savePemasukanListToWord(List<Pemasukan> list) throws IOException {
        XWPFDocument document = new XWPFDocument();
        XWPFTable table = document.createTable();

        // Header
        XWPFTableRow header = table.getRow(0);
        header.getCell(0).setText("ID");
        header.addNewTableCell().setText("Jumlah");
        header.addNewTableCell().setText("Deskripsi");
        header.addNewTableCell().setText("Tanggal");

        // Data
        for (Pemasukan p : list) {
            XWPFTableRow row = table.createRow();
            row.getCell(0).setText(String.valueOf(p.getId()));
            row.getCell(1).setText(String.format("%.2f", p.getJumlah()));
            row.getCell(2).setText(p.getDeskripsi());
            row.getCell(3).setText(p.getTanggal());
        }

        try (FileOutputStream out = new FileOutputStream("RiwayatPemasukan.docx")) {
            document.write(out);
        }
        document.close();
        System.out.println("Riwayat pemasukan disimpan dengan " + list.size() + " entri.");
    }

    private void savePengeluaranToWord(double jumlah, String deskripsi, String bukti) throws IOException {
        XWPFDocument document;
        File file = new File("RiwayatPengeluaran.docx");
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                document = new XWPFDocument(fis);
            }
        } else {
            document = new XWPFDocument();
            // Menambahkan header tabel
            XWPFTable table = document.createTable();
            XWPFTableRow header = table.getRow(0);
            header.getCell(0).setText("ID");
            header.addNewTableCell().setText("Jumlah");
            header.addNewTableCell().setText("Deskripsi");
            header.addNewTableCell().setText("Tanggal");
            header.addNewTableCell().setText("Bukti");
        }

        // Menambahkan baris baru
        XWPFTable table = document.getTables().get(0);
        XWPFTableRow row = table.createRow();
        long id = System.currentTimeMillis();
        row.getCell(0).setText(String.valueOf(id)); // ID unik
        row.getCell(1).setText(String.format("%.2f", jumlah));
        row.getCell(2).setText(deskripsi);
        row.getCell(3).setText(new java.util.Date().toString());

        // Menambahkan gambar ke sel "Bukti"
        XWPFTableCell cellBukti = row.getCell(4);
        if (bukti != null && !bukti.isEmpty()) {
            // Menghapus teks default
            cellBukti.removeParagraph(0);

            XWPFParagraph para = cellBukti.addParagraph();
            XWPFRun run = para.createRun();

            // Path gambar
            String imagePath = "bukti_pengeluaran/" + bukti;
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                try (FileInputStream is = new FileInputStream(imgFile)) {
                    String imgFileName = imgFile.getName();
                    int imgFormat = getImageFormat(imgFileName);
                    if (imgFormat == -1) {
                        JOptionPane.showMessageDialog(this, "Format gambar tidak didukung: " + imgFileName, "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        // Menyisipkan gambar ke dalam Word
                        run.addPicture(is, imgFormat, imgFileName, Units.toEMU(100), Units.toEMU(100)); // Ukuran gambar 100x100 EMU
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Gagal menyisipkan gambar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } else {
                cellBukti.setText("Bukti tidak ditemukan.");
            }
        } else {
            cellBukti.setText("Tidak ada bukti.");
        }

        // Menambahkan ke list
        Pengeluaran p = new Pengeluaran();
        p.setId(id);
        p.setJumlah(jumlah);
        p.setDeskripsi(deskripsi);
        p.setTanggal(new java.util.Date().toString());
        p.setBukti(bukti != null ? bukti : "");
        pengeluaranList.add(p);

        try (FileOutputStream out = new FileOutputStream(file)) {
            document.write(out);
        }

        document.close();
        System.out.println("Pengeluaran ditambahkan: " + jumlah + ", " + deskripsi + ", Bukti: " + bukti);
    }

    private List<Pengeluaran> loadPengeluaranFromWord() {
        List<Pengeluaran> list = new ArrayList<>();
        File file = new File("RiwayatPengeluaran.docx");
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 XWPFDocument document = new XWPFDocument(fis)) {
                List<XWPFTable> tables = document.getTables();
                if (tables.isEmpty()) {
                    System.out.println("Tidak ada tabel ditemukan dalam RiwayatPengeluaran.docx");
                    return list;
                }
                XWPFTable table = tables.get(0);
                for (int i = 1; i < table.getNumberOfRows(); i++) { // Mulai dari 1 untuk melewati header
                    XWPFTableRow row = table.getRow(i);
                    Pengeluaran p = new Pengeluaran();
                    p.setId(Long.parseLong(row.getCell(0).getText()));
                    p.setJumlah(Double.parseDouble(row.getCell(1).getText()));
                    p.setDeskripsi(row.getCell(2).getText());
                    p.setTanggal(row.getCell(3).getText());

                    // Mengambil nama file bukti dari sel ke-4
                    XWPFTableCell cellBukti = row.getCell(4);
                    String buktiText = cellBukti.getText();
                    p.setBukti(buktiText.equals("Tidak ada bukti.") ? "" : buktiText);
                    list.add(p);
                }
                System.out.println("Berhasil memuat " + list.size() + " entri pengeluaran dari RiwayatPengeluaran.docx");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal memuat riwayat pengeluaran dari Word.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            System.out.println("File RiwayatPengeluaran.docx tidak ditemukan.");
        }
        return list;
    }

    private void savePengeluaranListToWord(List<Pengeluaran> list) throws IOException {
        XWPFDocument document = new XWPFDocument();
        XWPFTable table = document.createTable();

        // Header
        XWPFTableRow header = table.getRow(0);
        header.getCell(0).setText("ID");
        header.addNewTableCell().setText("Jumlah");
        header.addNewTableCell().setText("Deskripsi");
        header.addNewTableCell().setText("Tanggal");
        header.addNewTableCell().setText("Bukti");

        // Data
        for (Pengeluaran p : list) {
            XWPFTableRow row = table.createRow();
            row.getCell(0).setText(String.valueOf(p.getId()));
            row.getCell(1).setText(String.format("%.2f", p.getJumlah()));
            row.getCell(2).setText(p.getDeskripsi());
            row.getCell(3).setText(p.getTanggal());

            XWPFTableCell cellBukti = row.getCell(4);
            if (p.getBukti() != null && !p.getBukti().isEmpty()) {
                // Menghapus teks default
                cellBukti.removeParagraph(0);

                XWPFParagraph para = cellBukti.addParagraph();
                XWPFRun run = para.createRun();

                // Path gambar
                String imagePath = "bukti_pengeluaran/" + p.getBukti();
                File imgFile = new File(imagePath);
                if (imgFile.exists()) {
                    try (FileInputStream is = new FileInputStream(imgFile)) {
                        String imgFileName = imgFile.getName();
                        int imgFormat = getImageFormat(imgFileName);
                        if (imgFormat == -1) {
                            cellBukti.setText("Format gambar tidak didukung.");
                        } else {
                            // Menyisipkan gambar ke dalam Word
                            run.addPicture(is, imgFormat, imgFileName, Units.toEMU(100), Units.toEMU(100)); // Ukuran gambar 100x100 EMU
                        }
                    } catch (Exception ex) {
                        cellBukti.setText("Gagal menyisipkan gambar.");
                        ex.printStackTrace();
                    }
                } else {
                    cellBukti.setText("Bukti tidak ditemukan.");
                }
            } else {
                cellBukti.setText("Tidak ada bukti.");
            }
        }

        try (FileOutputStream out = new FileOutputStream("RiwayatPengeluaran.docx")) {
            document.write(out);
        }
        document.close();
        System.out.println("Riwayat pengeluaran disimpan dengan " + list.size() + " entri.");
    }

    private int getImageFormat(String imgFileName) {
        int format;
        String lower = imgFileName.toLowerCase();
        if (lower.endsWith(".emf")) format = Document.PICTURE_TYPE_EMF;
        else if (lower.endsWith(".wmf")) format = Document.PICTURE_TYPE_WMF;
        else if (lower.endsWith(".pict")) format = Document.PICTURE_TYPE_PICT;
        else if (lower.endsWith(".jpeg") || lower.endsWith(".jpg")) format = Document.PICTURE_TYPE_JPEG;
        else if (lower.endsWith(".png")) format = Document.PICTURE_TYPE_PNG;
        else if (lower.endsWith(".dib")) format = Document.PICTURE_TYPE_DIB;
        else if (lower.endsWith(".gif")) format = Document.PICTURE_TYPE_GIF;
        else if (lower.endsWith(".tiff")) format = Document.PICTURE_TYPE_TIFF;
        else if (lower.endsWith(".eps")) format = Document.PICTURE_TYPE_EPS;
        else if (lower.endsWith(".bmp")) format = Document.PICTURE_TYPE_BMP;
        else if (lower.endsWith(".wpg")) format = Document.PICTURE_TYPE_WPG;
        else format = -1;
        return format;
    }

    private void showEditPengeluaranDialog(Pengeluaran p, DefaultTableModel tableModel, int row) {
        JDialog dialog = new JDialog(this, "Edit Pengeluaran", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblJumlah = new JLabel("Jumlah Uang:");
        lblJumlah.setFont(new Font("Arial", Font.PLAIN, 18));
        JTextField txtJumlah = new JTextField(String.valueOf(p.getJumlah()));
        txtJumlah.setColumns(20);

        JLabel lblDeskripsi = new JLabel("Deskripsi:");
        lblDeskripsi.setFont(new Font("Arial", Font.PLAIN, 18));
        JTextField txtDeskripsi = new JTextField(p.getDeskripsi());
        txtDeskripsi.setColumns(20);

        JLabel lblBukti = new JLabel("Bukti Pengeluaran:");
        lblBukti.setFont(new Font("Arial", Font.PLAIN, 18));
        JTextField txtBukti = new JTextField(p.getBukti());
        txtBukti.setEditable(false);
        txtBukti.setColumns(20);
        JButton btnPilihGambar = new JButton("Pilih Gambar");
        btnPilihGambar.setFont(new Font("Arial", Font.PLAIN, 16));
        JLabel lblGambar = new JLabel("Tidak ada gambar dipilih.");
        lblGambar.setFont(new Font("Arial", Font.ITALIC, 16));

        final String[] buktiBaru = {p.getBukti()};
        final File[] selectedImage = {null};

        btnPilihGambar.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Image Files", "jpg", "png", "jpeg", "bmp");
            fileChooser.setFileFilter(filter);
            int returnVal = fileChooser.showOpenDialog(dialog);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                selectedImage[0] = fileChooser.getSelectedFile();
                lblGambar.setText(selectedImage[0].getName());
                buktiBaru[0] = selectedImage[0].getName();
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

        dialog.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));

        JButton btnSimpan = new JButton("Simpan");
        btnSimpan.setFont(new Font("Arial", Font.PLAIN, 18));
        JButton btnBatal = new JButton("Batal");
        btnBatal.setFont(new Font("Arial", Font.PLAIN, 18));

        btnSimpan.addActionListener(e -> {
            try {
                double jumlah = Double.parseDouble(txtJumlah.getText().trim());
                String deskripsi = txtDeskripsi.getText().trim();
                String bukti = txtBukti.getText().trim();

                if (jumlah <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Jumlah uang harus lebih besar dari 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (deskripsi.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Deskripsi tidak boleh kosong.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Jika ada gambar baru yang dipilih, salin ke folder
                if (selectedImage[0] != null) {
                    bukti = selectedImage[0].getName();
                    // Menyalin gambar ke folder "bukti_pengeluaran"
                    File destDir = new File("bukti_pengeluaran");
                    if (!destDir.exists()) destDir.mkdirs();
                    File destFile = new File(destDir, bukti);
                    try (FileInputStream fis = new FileInputStream(selectedImage[0]);
                         FileOutputStream fos = new FileOutputStream(destFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = fis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    } catch (IOException exCopy) {
                        JOptionPane.showMessageDialog(dialog, "Gagal menyimpan gambar bukti.", "Error", JOptionPane.ERROR_MESSAGE);
                        exCopy.printStackTrace();
                        return;
                    }
                }

                // Update total uang
                double selisih = p.getJumlah() - jumlah;
                totalUang += selisih;
                updateTotalUang();
                saveTotalUangToWord();

                // Update pengeluaran
                p.setJumlah(jumlah);
                p.setDeskripsi(deskripsi);
                p.setBukti(bukti);
                p.setTanggal(new java.util.Date().toString());

                // Simpan kembali ke Word
                savePengeluaranListToWord(pengeluaranList);

                // Update tabel
                tableModel.setValueAt(jumlah, row, 1);
                tableModel.setValueAt(deskripsi, row, 2);
                tableModel.setValueAt(p.getTanggal(), row, 3);
                tableModel.setValueAt(bukti, row, 4);

                JOptionPane.showMessageDialog(dialog, "Pengeluaran berhasil diperbarui.");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Jumlah uang harus berupa angka.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(dialog, "Gagal menyimpan data.", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        btnBatal.addActionListener(e -> dialog.dispose());

        btnPanel.add(btnSimpan);
        btnPanel.add(btnBatal);

        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        // Pastikan Apache POI library ada di classpath
        SwingUtilities.invokeLater(() -> {
            PengelolaKeuangan app = new PengelolaKeuangan();
            app.setVisible(true);
        });
    }
}

class Pemasukan {
    private long id;
    private double jumlah;
    private String deskripsi;
    private String tanggal;

    // Constructors
    public Pemasukan() {}

    public Pemasukan(long id, double jumlah, String deskripsi, String tanggal) {
        this.id = id;
        this.jumlah = jumlah;
        this.deskripsi = deskripsi;
        this.tanggal = tanggal;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public double getJumlah() { return jumlah; }
    public void setJumlah(double jumlah) { this.jumlah = jumlah; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }
}

class Pengeluaran {
    private long id;
    private double jumlah;
    private String deskripsi;
    private String tanggal;
    private String bukti; // Nama file gambar

    // Constructors
    public Pengeluaran() {}

    public Pengeluaran(long id, double jumlah, String deskripsi, String tanggal, String bukti) {
        this.id = id;
        this.jumlah = jumlah;
        this.deskripsi = deskripsi;
        this.tanggal = tanggal;
        this.bukti = bukti;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public double getJumlah() { return jumlah; }
    public void setJumlah(double jumlah) { this.jumlah = jumlah; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }

    public String getBukti() { return bukti; }
    public void setBukti(String bukti) { this.bukti = bukti; }
}
