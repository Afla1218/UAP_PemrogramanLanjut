package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

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

    public PengelolaKeuangan() {
        setTitle("Pengelola Keuangan Pribadi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Initialize CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize Panels
        mainPanel.add(createHomePanel(), "Home");
        mainPanel.add(createTambahPemasukanPanel(), "TambahPemasukan");
        mainPanel.add(createTambahPengeluaranPanel(), "TambahPengeluaran");

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
        lblProgramName.setFont(new Font("Arial", Font.BOLD, 20));

        lblUserName = new JLabel("Nama: Tabuarasa", SwingConstants.CENTER);
        lblUserName.setFont(new Font("Arial", Font.PLAIN, 16));

        lblTotalUang = new JLabel("Total Uang: Rp " + String.format("%.2f", totalUang), SwingConstants.CENTER);
        lblTotalUang.setFont(new Font("Arial", Font.PLAIN, 16));

        header.add(lblProgramName);
        header.add(lblUserName);
        header.add(lblTotalUang);

        panel.add(header, BorderLayout.CENTER);

        // Navigation Buttons
        JPanel navPanel = new JPanel(new FlowLayout());
        JButton btnTambahPemasukan = new JButton("Tambah Pemasukan");
        JButton btnTambahPengeluaran = new JButton("Tambah Pengeluaran");

        btnTambahPemasukan.addActionListener(e -> cardLayout.show(mainPanel, "TambahPemasukan"));
        btnTambahPengeluaran.addActionListener(e -> cardLayout.show(mainPanel, "TambahPengeluaran"));

        navPanel.add(btnTambahPemasukan);
        navPanel.add(btnTambahPengeluaran);

        panel.add(navPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTambahPemasukanPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Tambah Pemasukan", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        JLabel lblJumlah = new JLabel("Jumlah Uang:");
        JTextField txtJumlah = new JTextField();

        JLabel lblDeskripsi = new JLabel("Deskripsi:");
        JTextField txtDeskripsi = new JTextField();

        formPanel.add(lblJumlah);
        formPanel.add(txtJumlah);
        formPanel.add(lblDeskripsi);
        formPanel.add(txtDeskripsi);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout());

        JButton btnSimpan = new JButton("Simpan");
        JButton btnKembali = new JButton("Kembali");

        btnSimpan.addActionListener(e -> {
            try {
                double jumlah = Double.parseDouble(txtJumlah.getText());
                String deskripsi = txtDeskripsi.getText();
                totalUang += jumlah;
                updateTotalUang();
                JOptionPane.showMessageDialog(this, "Pemasukan berhasil ditambahkan.");
                // Anda dapat menyimpan deskripsi ini ke file atau database jika diperlukan
                txtJumlah.setText("");
                txtDeskripsi.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Jumlah uang harus berupa angka.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnKembali.addActionListener(e -> cardLayout.show(mainPanel, "Home"));

        btnPanel.add(btnSimpan);
        btnPanel.add(btnKembali);

        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTambahPengeluaranPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Tambah Pengeluaran", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        JLabel lblJumlah = new JLabel("Jumlah Uang:");
        JTextField txtJumlah = new JTextField();

        JLabel lblDeskripsi = new JLabel("Deskripsi:");
        JTextField txtDeskripsi = new JTextField();

        JLabel lblBukti = new JLabel("Bukti Pengeluaran:");
        JButton btnPilihGambar = new JButton("Pilih Gambar");
        JLabel lblGambar = new JLabel("Tidak ada gambar dipilih.");

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

        formPanel.add(lblJumlah);
        formPanel.add(txtJumlah);
        formPanel.add(lblDeskripsi);
        formPanel.add(txtDeskripsi);
        formPanel.add(lblBukti);
        formPanel.add(btnPilihGambar);
        formPanel.add(new JLabel()); // Empty cell
        formPanel.add(lblGambar);

        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout());

        JButton btnSimpan = new JButton("Simpan");
        JButton btnKembali = new JButton("Kembali");

        btnSimpan.addActionListener(e -> {
            try {
                double jumlah = Double.parseDouble(txtJumlah.getText());
                String deskripsi = txtDeskripsi.getText();

                if (jumlah > totalUang) {
                    JOptionPane.showMessageDialog(this, "Jumlah pengeluaran melebihi total uang.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Simpan ke Word
                savePengeluaranToWord(jumlah, deskripsi, selectedImage[0]);

                totalUang -= jumlah;
                updateTotalUang();
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

        btnKembali.addActionListener(e -> cardLayout.show(mainPanel, "Home"));

        btnPanel.add(btnSimpan);
        btnPanel.add(btnKembali);

        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void updateTotalUang() {
        lblTotalUang.setText("Total Uang: Rp " + String.format("%.2f", totalUang));
    }

    private void savePengeluaranToWord(double jumlah, String deskripsi, File gambar) throws IOException {
        XWPFDocument document = new XWPFDocument();

        // Menambahkan judul
        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun runTitle = title.createRun();
        runTitle.setText("Bukti Pengeluaran");
        runTitle.setBold(true);
        runTitle.setFontSize(20);

        // Menambahkan deskripsi
        XWPFParagraph desc = document.createParagraph();
        XWPFRun runDesc = desc.createRun();
        runDesc.setText("Jumlah Uang: Rp " + String.format("%.2f", jumlah));
        runDesc.addBreak();
        runDesc.setText("Deskripsi: " + deskripsi);
        runDesc.addBreak();

        // Menambahkan gambar jika ada
        if (gambar != null) {
            XWPFParagraph imgPara = document.createParagraph();
            imgPara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun runImg = imgPara.createRun();
            try (FileInputStream fis = new FileInputStream(gambar)) {
                runImg.addPicture(fis, getPictureType(gambar.getName()), gambar.getName(), Units.toEMU(200), Units.toEMU(200));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Menyimpan dokumen
        String fileName = "Bukti_Pengeluaran_" + System.currentTimeMillis() + ".docx";
        try (FileOutputStream out = new FileOutputStream(fileName)) {
            document.write(out);
        }

        document.close();
    }

    private int getPictureType(String fileName) {
        if(fileName.endsWith(".emf")) return XWPFDocument.PICTURE_TYPE_EMF;
        else if(fileName.endsWith(".wmf")) return XWPFDocument.PICTURE_TYPE_WMF;
        else if(fileName.endsWith(".pict")) return XWPFDocument.PICTURE_TYPE_PICT;
        else if(fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")) return XWPFDocument.PICTURE_TYPE_JPEG;
        else if(fileName.endsWith(".png")) return XWPFDocument.PICTURE_TYPE_PNG;
        else if(fileName.endsWith(".dib")) return XWPFDocument.PICTURE_TYPE_DIB;
        else if(fileName.endsWith(".gif")) return XWPFDocument.PICTURE_TYPE_GIF;
        else if(fileName.endsWith(".tiff")) return XWPFDocument.PICTURE_TYPE_TIFF;
        else if(fileName.endsWith(".eps")) return XWPFDocument.PICTURE_TYPE_EPS;
        else if(fileName.endsWith(".bmp")) return XWPFDocument.PICTURE_TYPE_BMP;
        else if(fileName.endsWith(".wpg")) return XWPFDocument.PICTURE_TYPE_WPG;
        else return XWPFDocument.PICTURE_TYPE_PNG;
    }

    public static void main(String[] args) {
        // Pastikan Apache POI library ada di classpath
        SwingUtilities.invokeLater(() -> {
            PengelolaKeuangan app = new PengelolaKeuangan();
            app.setVisible(true);
        });
    }
}
