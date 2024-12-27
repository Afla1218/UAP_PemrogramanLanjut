package org.example.unittest;

import org.example.data.WordHandler;
import org.example.model.Pemasukan;
import org.example.model.Pengeluaran;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WordHandlerTest {

    // Define file paths
    private static final String TOTAL_UANG_FILE = "TotalUang.docx";
    private static final String PEMASUKAN_FILE = "RiwayatPemasukan.docx";
    private static final String PENGELUARAN_FILE = "RiwayatPengeluaran.docx";
    private static final String BUKTI_DIR = "bukti_pengeluaran";

    @BeforeAll
    void beforeAll() {
        // Initial cleanup before all tests
        deleteFile(TOTAL_UANG_FILE);
        deleteFile(PEMASUKAN_FILE);
        deleteFile(PENGELUARAN_FILE);
        deleteDirectory(new File(BUKTI_DIR));
    }

    @BeforeEach
    void setUp() {
        // Cleanup before each test
        deleteFile(TOTAL_UANG_FILE);
        deleteFile(PEMASUKAN_FILE);
        deleteFile(PENGELUARAN_FILE);
        deleteDirectory(new File(BUKTI_DIR));
    }

    @AfterEach
    void tearDown() {
        // Cleanup after each test
        deleteFile(TOTAL_UANG_FILE);
        deleteFile(PEMASUKAN_FILE);
        deleteFile(PENGELUARAN_FILE);
        deleteDirectory(new File(BUKTI_DIR));
    }

    private void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("Deleted file: " + filePath);
            } else {
                System.err.println("Failed to delete file: " + filePath);
            }
        }
    }

    private void deleteDirectory(File directoryToBeDeleted) {
        if (directoryToBeDeleted.exists()) {
            File[] allContents = directoryToBeDeleted.listFiles();
            if (allContents != null) {
                for (File file : allContents) {
                    deleteDirectory(file);
                }
            }
            boolean deleted = directoryToBeDeleted.delete();
            if (deleted) {
                System.out.println("Deleted directory: " + directoryToBeDeleted.getPath());
            } else {
                System.err.println("Failed to delete directory: " + directoryToBeDeleted.getPath());
            }
        }
    }

    @Test
    @DisplayName("Load Total Uang When File Does Not Exist")
    void testLoadTotalUangFileNotExist() {
        double total = WordHandler.loadTotalUang();
        assertEquals(0.0, total, "Total uang harus 0.0 ketika file tidak ada.");
    }

    @Test
    @DisplayName("Load Pemasukan When File Does Not Exist")
    void testLoadPemasukanFileNotExist() {
        List<Pemasukan> pemasukanList = WordHandler.loadPemasukan();
        assertTrue(pemasukanList.isEmpty(), "Daftar pemasukan harus kosong ketika file tidak ada.");
    }

    @Test
    @DisplayName("Load Pengeluaran When File Does Not Exist")
    void testLoadPengeluaranFileNotExist() {
        List<Pengeluaran> pengeluaranList = WordHandler.loadPengeluaran();
        assertTrue(pengeluaranList.isEmpty(), "Daftar pengeluaran harus kosong ketika file tidak ada.");
    }

    @Test
    @DisplayName("Save and Load Total Uang")
    void testSaveAndLoadTotalUang() throws IOException {
        double expectedTotal = 1000.5;
        WordHandler.saveTotalUang(expectedTotal);
        double actualTotal = WordHandler.loadTotalUang();
        assertEquals(expectedTotal, actualTotal, "Total uang yang disimpan dan dimuat harus sama.");
    }

    @Test
    @DisplayName("Save and Load Pemasukan")
    void testSaveAndLoadPemasukan() throws IOException {
        Pemasukan pemasukan = new Pemasukan();
        pemasukan.setId(1L);
        pemasukan.setJumlah(500.0);
        pemasukan.setDeskripsi("Salary");
        pemasukan.setTanggal("2024-12-01");

        WordHandler.savePemasukan(pemasukan);
        List<Pemasukan> pemasukanList = WordHandler.loadPemasukan();

        assertEquals(1, pemasukanList.size(), "Jumlah pemasukan yang dimuat harus 1.");
        Pemasukan loadedPemasukan = pemasukanList.get(0);
        assertEquals(pemasukan.getId(), loadedPemasukan.getId(), "ID harus sama.");
        assertEquals(pemasukan.getJumlah(), loadedPemasukan.getJumlah(), "Jumlah harus sama.");
        assertEquals(pemasukan.getDeskripsi(), loadedPemasukan.getDeskripsi(), "Deskripsi harus sama.");
        assertEquals(pemasukan.getTanggal(), loadedPemasukan.getTanggal(), "Tanggal harus sama.");
    }

    @Test
    @DisplayName("Save and Load Pengeluaran Without Bukti")
    void testSaveAndLoadPengeluaranWithoutBukti() throws IOException {
        Pengeluaran pengeluaran = new Pengeluaran();
        pengeluaran.setId(1L);
        pengeluaran.setJumlah(200.0);
        pengeluaran.setDeskripsi("Groceries");
        pengeluaran.setTanggal("2024-12-02");
        pengeluaran.setBukti(""); // No bukti

        WordHandler.savePengeluaran(pengeluaran);
        List<Pengeluaran> pengeluaranList = WordHandler.loadPengeluaran();

        assertEquals(1, pengeluaranList.size(), "Jumlah pengeluaran yang dimuat harus 1.");
        Pengeluaran loadedPengeluaran = pengeluaranList.get(0);
        assertEquals(pengeluaran.getId(), loadedPengeluaran.getId(), "ID harus sama.");
        assertEquals(pengeluaran.getJumlah(), loadedPengeluaran.getJumlah(), "Jumlah harus sama.");
        assertEquals(pengeluaran.getDeskripsi(), loadedPengeluaran.getDeskripsi(), "Deskripsi harus sama.");
        assertEquals(pengeluaran.getTanggal(), loadedPengeluaran.getTanggal(), "Tanggal harus sama.");
        assertEquals("", loadedPengeluaran.getBukti(), "Bukti harus kosong.");
    }

    @Test
    @DisplayName("Save and Load Pengeluaran With Bukti")
    void testSaveAndLoadPengeluaranWithBukti() throws IOException {
        // Ensure bukti_pengeluaran directory exists and contains an image
        File buktiDir = new File(BUKTI_DIR);
        if (!buktiDir.exists()) {
            boolean created = buktiDir.mkdirs();
            if (!created) {
                fail("Failed to create bukti_pengeluaran directory.");
            }
        }

        // Create a dummy image file for testing
        String buktiFileName = "gbw.jpg";
        File buktiFile = new File(buktiDir, buktiFileName);
        if (!buktiFile.exists()) {
            boolean created = buktiFile.createNewFile();
            if (!created) {
                fail("Failed to create dummy bukti image file.");
            }
        }

        Pengeluaran pengeluaran = new Pengeluaran();
        pengeluaran.setId(2L);
        pengeluaran.setJumlah(300.0);
        pengeluaran.setDeskripsi("Electronics");
        pengeluaran.setTanggal("2024-12-03");
        pengeluaran.setBukti(buktiFileName); // With bukti

        WordHandler.savePengeluaran(pengeluaran);
        List<Pengeluaran> pengeluaranList = WordHandler.loadPengeluaran();

        assertEquals(1, pengeluaranList.size(), "Jumlah pengeluaran yang dimuat harus 1.");
        Pengeluaran loadedPengeluaran = pengeluaranList.get(0);
        assertEquals(pengeluaran.getId(), loadedPengeluaran.getId(), "ID harus sama.");
        assertEquals(pengeluaran.getJumlah(), loadedPengeluaran.getJumlah(), "Jumlah harus sama.");
        assertEquals(pengeluaran.getDeskripsi(), loadedPengeluaran.getDeskripsi(), "Deskripsi harus sama.");
        assertEquals(pengeluaran.getTanggal(), loadedPengeluaran.getTanggal(), "Tanggal harus sama.");
        assertEquals(buktiFileName, loadedPengeluaran.getBukti(), "Bukti harus sama.");
    }

    // Additional test methods can be added here

}
