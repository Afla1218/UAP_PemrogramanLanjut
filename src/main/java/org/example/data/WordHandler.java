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

    public static final String TOTAL_UANG_FILE = "TotalUang.docx";
    public static final String PEMASUKAN_FILE = "RiwayatPemasukan.docx";
    public static final String PENGELUARAN_FILE = "RiwayatPengeluaran.docx";
    public static final String BUKTI_DIR = "bukti_pengeluaran"; // pastikan folder ini ada

    public WordHandler(String string, String string1, String string2, String string3) {

    }

    // ----------------------------
    // 1. SAVE & LOAD TOTAL UANG
    // ----------------------------

    public static void saveTotalUang(double total) throws IOException {
        // Overwrite file: buat doc baru
        XWPFDocument document = new XWPFDocument();
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(String.valueOf(total));

        try (FileOutputStream out = new FileOutputStream(TOTAL_UANG_FILE)) {
            document.write(out);
        }
        document.close();
    }

    public static double loadTotalUang() {
        double total = 0.0;
        File file = new File(TOTAL_UANG_FILE);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 XWPFDocument document = new XWPFDocument(fis)) {
                XWPFParagraph paragraph = document.getParagraphArray(0);
                if (paragraph != null) {
                    try {
                        total = Double.parseDouble(paragraph.getText());
                    } catch (NumberFormatException e) {
                        // file korup: biarkan total = 0.0
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Gagal memuat total uang dari Word.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        return total;
    }

    // ----------------------------
    // 2. SAVE & LOAD PEMASUKAN
    // ----------------------------

    public static void savePemasukan(Pemasukan pemasukan) throws IOException {
        // Overwrite file
        XWPFDocument document = new XWPFDocument();
        XWPFTable table = document.createTable();

        // Header
        XWPFTableRow header = table.getRow(0);
        header.getCell(0).setText("ID");
        header.addNewTableCell().setText("Jumlah");
        header.addNewTableCell().setText("Deskripsi");
        header.addNewTableCell().setText("Tanggal");

        // Baris data
        XWPFTableRow row = table.createRow();
        row.getCell(0).setText(String.valueOf(pemasukan.getId()));
        row.getCell(1).setText(String.format("%.2f", pemasukan.getJumlah()));
        row.getCell(2).setText(pemasukan.getDeskripsi());
        row.getCell(3).setText(pemasukan.getTanggal());

        // Tulis ke file
        try (FileOutputStream out = new FileOutputStream(PEMASUKAN_FILE)) {
            document.write(out);
        }
        document.close();
    }

    public static List<Pemasukan> loadPemasukan() {
        List<Pemasukan> list = new ArrayList<>();
        File file = new File(PEMASUKAN_FILE);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 XWPFDocument document = new XWPFDocument(fis)) {

                List<XWPFTable> tables = document.getTables();
                if (!tables.isEmpty()) {
                    XWPFTable table = tables.get(0);
                    // baris ke-0 header, mulai parse dr baris ke-1
                    for (int i = 1; i < table.getNumberOfRows(); i++) {
                        XWPFTableRow row = table.getRow(i);
                        Pemasukan pm = new Pemasukan();
                        pm.setId(Long.parseLong(row.getCell(0).getText()));
                        pm.setJumlah(Double.parseDouble(row.getCell(1).getText()));
                        pm.setDeskripsi(row.getCell(2).getText());
                        pm.setTanggal(row.getCell(3).getText());
                        list.add(pm);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Gagal memuat riwayat pemasukan dari Word.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        return list;
    }

    // Overwrite file dengan list pemasukan
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

    // ----------------------------
    // 3. SAVE & LOAD PENGELUARAN
    // ----------------------------

    public static void savePengeluaran(Pengeluaran pengeluaran) throws IOException {
        // Overwrite file
        XWPFDocument document = new XWPFDocument();
        XWPFTable table = document.createTable();

        // Header
        XWPFTableRow header = table.getRow(0);
        header.getCell(0).setText("ID");
        header.addNewTableCell().setText("Jumlah");
        header.addNewTableCell().setText("Deskripsi");
        header.addNewTableCell().setText("Tanggal");
        header.addNewTableCell().setText("Bukti");

        // Baris data
        XWPFTableRow row = table.createRow();
        row.getCell(0).setText(String.valueOf(pengeluaran.getId()));
        row.getCell(1).setText(String.format("%.2f", pengeluaran.getJumlah()));
        row.getCell(2).setText(pengeluaran.getDeskripsi());
        row.getCell(3).setText(pengeluaran.getTanggal());

        // Kolom “Bukti”
        XWPFTableCell cellBukti = row.getCell(4);
        if (pengeluaran.getBukti() != null && !pengeluaran.getBukti().isEmpty()) {
            cellBukti.removeParagraph(0);

            XWPFParagraph para = cellBukti.addParagraph();
            XWPFRun run = para.createRun();
            // Tulis nama file bukti agar bisa terbaca di load
            run.setText(pengeluaran.getBukti());

            // Sisipkan gambar
            String imagePath = BUKTI_DIR + "/" + pengeluaran.getBukti();
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                try (FileInputStream is = new FileInputStream(imgFile)) {
                    int imgFormat = getImageFormat(imgFile.getName());
                    if (imgFormat != -1) {
                        run.addPicture(is, imgFormat, imgFile.getName(),
                                Units.toEMU(100), Units.toEMU(100));
                    } else {
                        cellBukti.setText("Format gambar tidak didukung.");
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

        try (FileOutputStream out = new FileOutputStream(PENGELUARAN_FILE)) {
            document.write(out);
        }
        document.close();
    }

    public static List<Pengeluaran> loadPengeluaran() {
        List<Pengeluaran> list = new ArrayList<>();
        File file = new File(PENGELUARAN_FILE);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 XWPFDocument document = new XWPFDocument(fis)) {

                List<XWPFTable> tables = document.getTables();
                if (!tables.isEmpty()) {
                    XWPFTable table = tables.get(0);
                    // row 0 header, mulai dr row 1
                    for (int i = 1; i < table.getNumberOfRows(); i++) {
                        XWPFTableRow row = table.getRow(i);
                        Pengeluaran p = new Pengeluaran();
                        p.setId(Long.parseLong(row.getCell(0).getText()));
                        p.setJumlah(Double.parseDouble(row.getCell(1).getText()));
                        p.setDeskripsi(row.getCell(2).getText());
                        p.setTanggal(row.getCell(3).getText());

                        // Kolom Bukti
                        XWPFTableCell cellBukti = row.getCell(4);
                        String buktiText = cellBukti.getText();
                        // Jika berisi keterangan error => set kosong
                        if (buktiText.contains("tidak ditemukan")
                                || buktiText.contains("Format gambar")
                                || buktiText.contains("Gagal")
                                || buktiText.equals("Tidak ada bukti.")) {
                            p.setBukti("");
                        } else {
                            p.setBukti(buktiText); // misal "electronics.png"
                        }
                        list.add(p);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Gagal memuat riwayat pengeluaran dari Word.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        return list;
    }

    public static void savePengeluaranList(List<Pengeluaran> list) throws IOException {
        // Overwrite file
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
            cellBukti.removeParagraph(0);

            XWPFParagraph para = cellBukti.addParagraph();
            XWPFRun run = para.createRun();

            if (p.getBukti() != null && !p.getBukti().isEmpty()) {
                // Tulis nama file
                run.setText(p.getBukti());

                String imagePath = BUKTI_DIR + "/" + p.getBukti();
                File imgFile = new File(imagePath);
                if (imgFile.exists()) {
                    try (FileInputStream is = new FileInputStream(imgFile)) {
                        int imgFormat = getImageFormat(imgFile.getName());
                        if (imgFormat != -1) {
                            run.addPicture(is, imgFormat, imgFile.getName(),
                                    Units.toEMU(100), Units.toEMU(100));
                        } else {
                            cellBukti.setText("Format gambar tidak didukung.");
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

    // ----------------------------
    // UTIL: getImageFormat
    // ----------------------------
    private static int getImageFormat(String imgFileName) {
        String lower = imgFileName.toLowerCase();
        if (lower.endsWith(".emf")) return Document.PICTURE_TYPE_EMF;
        else if (lower.endsWith(".wmf")) return Document.PICTURE_TYPE_WMF;
        else if (lower.endsWith(".pict")) return Document.PICTURE_TYPE_PICT;
        else if (lower.endsWith(".jpeg") || lower.endsWith(".jpg")) return Document.PICTURE_TYPE_JPEG;
        else if (lower.endsWith(".png")) return Document.PICTURE_TYPE_PNG;
        else if (lower.endsWith(".dib")) return Document.PICTURE_TYPE_DIB;
        else if (lower.endsWith(".gif")) return Document.PICTURE_TYPE_GIF;
        else if (lower.endsWith(".tiff")) return Document.PICTURE_TYPE_TIFF;
        else if (lower.endsWith(".eps")) return Document.PICTURE_TYPE_EPS;
        else if (lower.endsWith(".bmp")) return Document.PICTURE_TYPE_BMP;
        else if (lower.endsWith(".wpg")) return Document.PICTURE_TYPE_WPG;
        else return -1;
    }
}