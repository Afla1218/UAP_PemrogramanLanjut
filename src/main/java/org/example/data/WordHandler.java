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
    public static final String BUKTI_DIR = "bukti_pengeluaran"; // Ensure this folder exists

    public WordHandler(String string, String string1, String string2, String string3) {
        // Constructor can be expanded if needed
    }

    // ----------------------------
    // 1. SAVE & LOAD TOTAL UANG
    // ----------------------------

    /**
     * Saves the total money to a Word document.
     *
     * @param total The total amount of money to save.
     * @throws IOException If an I/O error occurs.
     */
    public static void saveTotalUang(double total) throws IOException {
        // Overwrite file: create new doc
        XWPFDocument document = new XWPFDocument();
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(String.valueOf(total));

        try (FileOutputStream out = new FileOutputStream(TOTAL_UANG_FILE)) {
            document.write(out);
            System.out.println("Total uang saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving total uang: " + e.getMessage());
            throw e;
        } finally {
            document.close();
        }
    }

    /**
     * Loads the total money from a Word document.
     *
     * @return The total amount of money. Returns 0.0 if the file does not exist or contains invalid data.
     */
    public static double loadTotalUang() {
        double total = 0.0;
        File file = new File(TOTAL_UANG_FILE);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 XWPFDocument document = new XWPFDocument(fis)) {
                XWPFParagraph paragraph = document.getParagraphArray(0);
                if (paragraph != null) {
                    String text = paragraph.getText().trim();
                    if (!text.isEmpty()) {
                        try {
                            total = Double.parseDouble(text);
                            System.out.println("Total uang loaded: " + total);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid number format in total uang file. Returning default value 0.0.");
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error reading total uang file. Returning default value 0.0.");
                e.printStackTrace();
            }
        } else {
            System.out.println("Total uang file does not exist. Returning default value 0.0.");
        }
        return total;
    }

    // ----------------------------
    // 2. SAVE & LOAD PEMASUKAN
    // ----------------------------

    /**
     * Saves a single Pemasukan (income) entry to a Word document.
     *
     * @param pemasukan The Pemasukan object to save.
     * @throws IOException If an I/O error occurs.
     */
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

        // Data row
        XWPFTableRow row = table.createRow();
        row.getCell(0).setText(String.valueOf(pemasukan.getId()));
        row.getCell(1).setText(String.format("%.2f", pemasukan.getJumlah()));
        row.getCell(2).setText(pemasukan.getDeskripsi());
        row.getCell(3).setText(pemasukan.getTanggal());

        // Write to file
        try (FileOutputStream out = new FileOutputStream(PEMASUKAN_FILE)) {
            document.write(out);
            System.out.println("Pemasukan saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving pemasukan: " + e.getMessage());
            throw e;
        } finally {
            document.close();
        }
    }

    /**
     * Loads the list of Pemasukan (income) entries from a Word document.
     *
     * @return A list of Pemasukan objects. Returns an empty list if the file does not exist or contains invalid data.
     */
    public static List<Pemasukan> loadPemasukan() {
        List<Pemasukan> list = new ArrayList<>();
        File file = new File(PEMASUKAN_FILE);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 XWPFDocument document = new XWPFDocument(fis)) {
                List<XWPFTable> tables = document.getTables();
                if (!tables.isEmpty()) {
                    XWPFTable table = tables.get(0);
                    // Skip header row (index 0), start from row 1
                    for (int i = 1; i < table.getNumberOfRows(); i++) {
                        XWPFTableRow row = table.getRow(i);
                        Pemasukan pm = new Pemasukan();
                        try {
                            pm.setId(Long.parseLong(row.getCell(0).getText().trim()));
                            pm.setJumlah(Double.parseDouble(row.getCell(1).getText().trim()));
                            pm.setDeskripsi(row.getCell(2).getText().trim());
                            pm.setTanggal(row.getCell(3).getText().trim());
                            list.add(pm);
                        } catch (Exception e) {
                            System.err.println("Error parsing row data in pemasukan file. Skipping row.");
                        }
                    }
                    System.out.println("Pemasukan loaded successfully. Total records: " + list.size());
                }
            } catch (Exception e) {
                System.err.println("Error reading pemasukan file. Returning empty list.");
                e.printStackTrace();
            }
        } else {
            System.out.println("Pemasukan file does not exist. Returning empty list.");
        }
        return list;
    }

    /**
     * Saves a list of Pemasukan (income) entries to a Word document.
     *
     * @param list The list of Pemasukan objects to save.
     * @throws IOException If an I/O error occurs.
     */
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
            System.out.println("Pemasukan list saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving pemasukan list: " + e.getMessage());
            throw e;
        } finally {
            document.close();
        }
    }

    // ----------------------------
    // 3. SAVE & LOAD PENGELUARAN
    // ----------------------------

    /**
     * Saves a single Pengeluaran (expense) entry to a Word document, including handling of bukti (proof) images.
     *
     * @param pengeluaran The Pengeluaran object to save.
     * @throws IOException If an I/O error occurs.
     */
    public static void savePengeluaran(Pengeluaran pengeluaran) throws IOException {
        // Ensure bukti_pengeluaran directory exists
        File buktiDir = new File(BUKTI_DIR);
        if (!buktiDir.exists()) {
            if (buktiDir.mkdirs()) {
                System.out.println("Created bukti_pengeluaran directory.");
            } else {
                System.err.println("Failed to create bukti_pengeluaran directory.");
                throw new IOException("Failed to create bukti_pengeluaran directory.");
            }
        }

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

        // Data row
        XWPFTableRow row = table.createRow();
        row.getCell(0).setText(String.valueOf(pengeluaran.getId()));
        row.getCell(1).setText(String.format("%.2f", pengeluaran.getJumlah()));
        row.getCell(2).setText(pengeluaran.getDeskripsi());
        row.getCell(3).setText(pengeluaran.getTanggal());

        // Bukti cell
        XWPFTableCell cellBukti = row.getCell(4);
        if (pengeluaran.getBukti() != null && !pengeluaran.getBukti().isEmpty()) {
            cellBukti.removeParagraph(0);

            XWPFParagraph para = cellBukti.addParagraph();
            XWPFRun run = para.createRun();
            // Write the bukti filename
            run.setText(pengeluaran.getBukti());

            // Insert image
            String imagePath = BUKTI_DIR + "/" + pengeluaran.getBukti();
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                try (FileInputStream is = new FileInputStream(imgFile)) {
                    int imgFormat = getImageFormat(imgFile.getName());
                    if (imgFormat != -1) {
                        run.addPicture(is, imgFormat, imgFile.getName(),
                                Units.toEMU(100), Units.toEMU(100));
                        System.out.println("Inserted image: " + imagePath);
                    } else {
                        cellBukti.setText("Format gambar tidak didukung.");
                        System.err.println("Unsupported image format for file: " + imagePath);
                    }
                } catch (Exception ex) {
                    cellBukti.setText("Gagal menyisipkan gambar.");
                    System.err.println("Error inserting image: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                cellBukti.setText("Bukti tidak ditemukan.");
                System.err.println("Image file not found: " + imagePath);
            }
        } else {
            cellBukti.setText("Tidak ada bukti.");
            System.out.println("No bukti provided for pengeluaran.");
        }

        try (FileOutputStream out = new FileOutputStream(PENGELUARAN_FILE)) {
            document.write(out);
            System.out.println("Pengeluaran saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving pengeluaran: " + e.getMessage());
            throw e;
        } finally {
            document.close();
        }
    }

    /**
     * Loads the list of Pengeluaran (expense) entries from a Word document, including handling of bukti (proof) images.
     *
     * @return A list of Pengeluaran objects. Returns an empty list if the file does not exist or contains invalid data.
     */
    public static List<Pengeluaran> loadPengeluaran() {
        List<Pengeluaran> list = new ArrayList<>();
        File file = new File(PENGELUARAN_FILE);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 XWPFDocument document = new XWPFDocument(fis)) {

                List<XWPFTable> tables = document.getTables();
                if (!tables.isEmpty()) {
                    XWPFTable table = tables.get(0);
                    // Skip header row (index 0), start from row 1
                    for (int i = 1; i < table.getNumberOfRows(); i++) {
                        XWPFTableRow row = table.getRow(i);
                        Pengeluaran p = new Pengeluaran();
                        try {
                            p.setId(Long.parseLong(row.getCell(0).getText().trim()));
                            p.setJumlah(Double.parseDouble(row.getCell(1).getText().trim()));
                            p.setDeskripsi(row.getCell(2).getText().trim());
                            p.setTanggal(row.getCell(3).getText().trim());

                            // Handle bukti
                            XWPFTableCell cellBukti = row.getCell(4);
                            String buktiText = cellBukti.getText().trim();
                            // Check for error messages
                            if (buktiText.contains("tidak ditemukan")
                                    || buktiText.contains("Format gambar")
                                    || buktiText.contains("Gagal")
                                    || buktiText.equals("Tidak ada bukti.")) {
                                p.setBukti("");
                                System.out.println("Bukti not found or invalid for pengeluaran ID: " + p.getId());
                            } else {
                                p.setBukti(buktiText); // e.g., "electronics.png"
                                System.out.println("Bukti loaded: " + p.getBukti() + " for pengeluaran ID: " + p.getId());
                            }
                            list.add(p);
                        } catch (Exception e) {
                            System.err.println("Error parsing row data in pengeluaran file. Skipping row.");
                        }
                    }
                    System.out.println("Pengeluaran loaded successfully. Total records: " + list.size());
                }
            } catch (Exception e) {
                System.err.println("Error reading pengeluaran file. Returning empty list.");
                e.printStackTrace();
            }
        } else {
            System.out.println("Pengeluaran file does not exist. Returning empty list.");
        }
        return list;
    }

    /**
     * Saves a list of Pengeluaran (expense) entries to a Word document, including handling of bukti (proof) images.
     *
     * @param list The list of Pengeluaran objects to save.
     * @throws IOException If an I/O error occurs.
     */
    public static void savePengeluaranList(List<Pengeluaran> list) throws IOException {
        // Ensure bukti_pengeluaran directory exists
        File buktiDir = new File(BUKTI_DIR);
        if (!buktiDir.exists()) {
            if (buktiDir.mkdirs()) {
                System.out.println("Created bukti_pengeluaran directory.");
            } else {
                System.err.println("Failed to create bukti_pengeluaran directory.");
                throw new IOException("Failed to create bukti_pengeluaran directory.");
            }
        }

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
                // Write bukti filename
                run.setText(p.getBukti());

                String imagePath = BUKTI_DIR + "/" + p.getBukti();
                File imgFile = new File(imagePath);
                if (imgFile.exists()) {
                    try (FileInputStream is = new FileInputStream(imgFile)) {
                        int imgFormat = getImageFormat(imgFile.getName());
                        if (imgFormat != -1) {
                            run.addPicture(is, imgFormat, imgFile.getName(),
                                    Units.toEMU(100), Units.toEMU(100));
                            System.out.println("Inserted image: " + imagePath);
                        } else {
                            cellBukti.setText("Format gambar tidak didukung.");
                            System.err.println("Unsupported image format for file: " + imagePath);
                        }
                    } catch (Exception ex) {
                        cellBukti.setText("Gagal menyisipkan gambar.");
                        System.err.println("Error inserting image: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                } else {
                    cellBukti.setText("Bukti tidak ditemukan.");
                    System.err.println("Image file not found: " + imagePath);
                }
            } else {
                cellBukti.setText("Tidak ada bukti.");
                System.out.println("No bukti provided for pengeluaran.");
            }
        }

        try (FileOutputStream out = new FileOutputStream(PENGELUARAN_FILE)) {
            document.write(out);
            System.out.println("Pengeluaran list saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving pengeluaran list: " + e.getMessage());
            throw e;
        } finally {
            document.close();
        }
    }

    // ----------------------------
    // UTIL: getImageFormat
    // ----------------------------

    /**
     * Determines the image format based on the file extension.
     *
     * @param imgFileName The name of the image file.
     * @return The corresponding Apache POI image format constant, or -1 if unsupported.
     */
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
