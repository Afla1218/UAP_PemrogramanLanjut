package org.example.data;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.util.Units;
import org.example.model.Pemasukan;
import org.example.model.Pengeluaran;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WordHandler {

    // Path Files
    public static final String TOTAL_UANG_FILE = "TotalUang.docx";
    public static final String PEMASUKAN_FILE = "RiwayatPemasukan.docx";
    public static final String PENGELUARAN_FILE = "RiwayatPengeluaran.docx";
    public static final String BUKTI_DIR = "bukti_pengeluaran";

    // Save Total Uang
    public static void saveTotalUang(double total) throws IOException {
        XWPFDocument document = new XWPFDocument();
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(String.valueOf(total));

        try (FileOutputStream out = new FileOutputStream(TOTAL_UANG_FILE)) {
            document.write(out);
        }
        document.close();
    }

    // Load Total Uang
    public static double loadTotalUang() {
        double total = 0.0;
        File file = new File(TOTAL_UANG_FILE);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 XWPFDocument document = new XWPFDocument(fis)) {
                XWPFParagraph paragraph = document.getParagraphArray(0);
                if (paragraph != null) {
                    String text = paragraph.getText();
                    total = Double.parseDouble(text);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Gagal memuat total uang dari Word.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        return total;
    }

    // Save Pemasukan
    public static void savePemasukan(Pemasukan pemasukan) throws IOException {
        XWPFDocument document;
        File file = new File(PEMASUKAN_FILE);
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
        row.getCell(0).setText(String.valueOf(pemasukan.getId()));
        row.getCell(1).setText(String.format("%.2f", pemasukan.getJumlah()));
        row.getCell(2).setText(pemasukan.getDeskripsi());
        row.getCell(3).setText(pemasukan.getTanggal());

        // Simpan ke list
        try (FileOutputStream out = new FileOutputStream(file)) {
            document.write(out);
        }

        document.close();
    }

    // Load Pemasukan
    public static List<Pemasukan> loadPemasukan() {
        List<Pemasukan> list = new ArrayList<>();
        File file = new File(PEMASUKAN_FILE);
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
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Gagal memuat riwayat pemasukan dari Word.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        return list;
    }

    // Save Daftar Pemasukan
    public static void savePemasukanList(List<Pemasukan> list) throws IOException {
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

        try (FileOutputStream out = new FileOutputStream(PEMASUKAN_FILE)) {
            document.write(out);
        }
        document.close();
    }

    // Save Pengeluaran
    public static void savePengeluaran(Pengeluaran pengeluaran) throws IOException {
        XWPFDocument document;
        File file = new File(PENGELUARAN_FILE);
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
        row.getCell(0).setText(String.valueOf(pengeluaran.getId()));
        row.getCell(1).setText(String.format("%.2f", pengeluaran.getJumlah()));
        row.getCell(2).setText(pengeluaran.getDeskripsi());
        row.getCell(3).setText(pengeluaran.getTanggal());

        // Menambahkan gambar ke sel "Bukti"
        XWPFTableCell cellBukti = row.getCell(4);
        if (pengeluaran.getBukti() != null && !pengeluaran.getBukti().isEmpty()) {
            // Menghapus teks default
            cellBukti.removeParagraph(0);

            XWPFParagraph para = cellBukti.addParagraph();
            XWPFRun run = para.createRun();

            // Path gambar
            String imagePath = BUKTI_DIR + "/" + pengeluaran.getBukti();
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                try (FileInputStream is = new FileInputStream(imgFile)) {
                    String imgFileName = imgFile.getName();
                    int imgFormat = getImageFormat(imgFileName);
                    if (imgFormat == -1) {
                        JOptionPane.showMessageDialog(null, "Format gambar tidak didukung: " + imgFileName, "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        // Menyisipkan gambar ke dalam Word
                        run.addPicture(is, imgFormat, imgFileName, Units.toEMU(100), Units.toEMU(100)); // Ukuran gambar 100x100 EMU
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Gagal menyisipkan gambar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } else {
                cellBukti.setText("Bukti tidak ditemukan.");
            }
        } else {
            cellBukti.setText("Tidak ada bukti.");
        }

        // Menambahkan ke list
        try (FileOutputStream out = new FileOutputStream(file)) {
            document.write(out);
        }

        document.close();
    }

    // Load Pengeluaran
    public static List<Pengeluaran> loadPengeluaran() {
        List<Pengeluaran> list = new ArrayList<>();
        File file = new File(PENGELUARAN_FILE);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 XWPFDocument document = new XWPFDocument(fis)) {
                List<XWPFTable> tables = document.getTables();
                if (tables.isEmpty()) {
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
                    p.setBukti(buktiText.equals("Tidak ada bukti.") || buktiText.equals("Bukti tidak ditemukan.") ? "" : buktiText);
                    list.add(p);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Gagal memuat riwayat pengeluaran dari Word.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        return list;
    }

    // Save Daftar Pengeluaran
    public static void savePengeluaranList(List<Pengeluaran> list) throws IOException {
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
                String imagePath = BUKTI_DIR + "/" + p.getBukti();
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

        try (FileOutputStream out = new FileOutputStream(PENGELUARAN_FILE)) {
            document.write(out);
        }
        document.close();
    }

    // Get Image Format
    private static int getImageFormat(String imgFileName) {
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
}
