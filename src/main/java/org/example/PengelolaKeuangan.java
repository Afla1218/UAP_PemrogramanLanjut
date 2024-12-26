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
        try {
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
        } catch (Exception e) {
            showErrorDialog("Terjadi kesalahan saat memulai aplikasi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        try {
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

            btnTambahPemasukan.addActionListener(e -> {
                try {
                    cardLayout.show(mainPanel, "TambahPemasukan");
                } catch (Exception ex) {
                    showErrorDialog("Terjadi kesalahan saat membuka panel Tambah Pemasukan: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });

            btnTambahPengeluaran.addActionListener(e -> {
                try {
                    cardLayout.show(mainPanel, "TambahPengeluaran");
                } catch (Exception ex) {
                    showErrorDialog("Terjadi kesalahan saat membuka panel Tambah Pengeluaran: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });

            navPanel.add(btnTambahPemasukan);
            navPanel.add(btnTambahPengeluaran);

            panel.add(navPanel, BorderLayout.SOUTH);
        } catch (Exception e) {
            showErrorDialog("Terjadi kesalahan di Home Panel: " + e.getMessage());
            e.printStackTrace();
        }

        return panel;
    }

    private JPanel createTambahPemasukanPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        try {
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

                    totalUang += jumlah;
                    updateTotalUang();
                    JOptionPane.showMessageDialog(this, "Pemasukan berhasil ditambahkan.");
                    // Anda dapat menyimpan deskripsi ini ke file atau database jika diperlukan
                    txtJumlah.setText("");
                    txtDeskripsi.setText("");
                } catch (NumberFormatException ex) {
                    showErrorDialog("Jumlah uang harus berupa angka.");
                } catch (Exception ex) {
                    showErrorDialog("Terjadi kesalahan saat menyimpan pemasukan: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });

            btnKembali.addActionListener(e -> {
                try {
                    cardLayout.show(mainPanel, "Home");
                } catch (Exception ex) {
                    showErrorDialog("Terjadi kesalahan saat kembali ke Home: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });

            btnPanel.add(btnSimpan);
            btnPanel.add(btnKembali);

            panel.add(btnPanel, BorderLayout.SOUTH);
        } catch (Exception e) {
            showErrorDialog("Terjadi kesalahan di Panel Tambah Pemasukan: " + e.getMessage());
            e.printStackTrace();
        }

        return panel;
    }

    private JPanel createTambahPengeluaranPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        try {
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
                    showErrorDialog("Jumlah uang harus berupa angka.");
                } catch (IOException ex) {
                    showErrorDialog("Gagal menyimpan ke Word: " + ex.getMessage());
                    ex.printStackTrace();
                } catch (Exception ex) {
                    showErrorDialog("Terjadi kesalahan saat menyimpan pengeluaran: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });

            btnKembali.addActionListener(e -> {
                try {
                    cardLayout.show(mainPanel, "Home");
                } catch (Exception ex) {
                    showErrorDialog("Terjadi kesalahan saat kembali ke Home: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });

            btnPanel.add(btnSimpan);
            btnPanel.add(btnKembali);

            panel.add(btnPanel, BorderLayout.SOUTH);
        } catch (Exception e) {
            showErrorDialog("Terjadi kesalahan di Panel Tambah Pengeluaran: " + e.getMessage());
            e.printStackTrace();
        }

        return panel;
    }

    private void updateTotalUang() {
        try {
            lblTotalUang.setText("Total Uang: Rp " + String.format("%.2f", totalUang));
        } catch (Exception e) {
            showErrorDialog("Terjadi kesalahan saat memperbarui total uang: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void savePengeluaranToWord(double jumlah, String deskripsi, File gambar) throws IOException {
        XWPFDocument document = null;
        try {
            document = new XWPFDocument();

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
                    showErrorDialog("Terjadi kesalahan saat menambahkan gambar: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Menyimpan dokumen
            String fileName = "Bukti_Pengeluaran_" + System.currentTimeMillis() + ".docx";
            try (FileOutputStream out = new FileOutputStream(fileName)) {
                document.write(out);
            } catch (IOException e) {
                showErrorDialog("Terjadi kesalahan saat menyimpan dokumen Word: " + e.getMessage());
                throw e; // Rethrow exception after handling
            }
        } catch (Exception e) {
            showErrorDialog("Terjadi kesalahan saat membuat dokumen Word: " + e.getMessage());
            e.printStackTrace();
            throw e; // Rethrow exception after handling
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    showErrorDialog("Terjadi kesalahan saat menutup dokumen Word: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
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

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
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
