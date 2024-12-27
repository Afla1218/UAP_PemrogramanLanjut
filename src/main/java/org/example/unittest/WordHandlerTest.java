package org.example.unittest;

import org.example.data.WordHandler;
import org.example.model.Pemasukan;
import org.example.model.Pengeluaran;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WordHandlerTest {

    // Temporary directory for testing
    private Path tempDir;
    private WordHandler wordHandler;

    // Paths within the temporary directory
    private Path totalUangPath;
    private Path pemasukanPath;
    private Path pengeluaranPath;
    private Path buktiDirPath;

    @BeforeAll
    public void setup() throws IOException {
        // Create a temporary directory
        tempDir = Files.createTempDirectory("wordhandler_test");

        // Define file paths within the temporary directory
        totalUangPath = tempDir.resolve("TotalUang.docx");
        pemasukanPath = tempDir.resolve("RiwayatPemasukan.docx");
        pengeluaranPath = tempDir.resolve("RiwayatPengeluaran.docx");
        buktiDirPath = tempDir.resolve("bukti_pengeluaran");

        // Create bukti_pengeluaran directory
        Files.createDirectories(buktiDirPath);

        // Initialize WordHandler with temporary paths
        wordHandler = new WordHandler(
                totalUangPath.toString(),
                pemasukanPath.toString(),
                pengeluaranPath.toString(),
                buktiDirPath.toString()
        );
    }

    @AfterAll
    public void teardown() throws IOException {
        // Delete temporary directory recursively
        deleteDirectoryRecursively(tempDir.toFile());
    }

    /**
     * Utility method to delete a directory recursively
     */
    private void deleteDirectoryRecursively(File file) throws IOException {
        if (file.isDirectory()) {
            for (File child : Objects.requireNonNull(file.listFiles())) {
                deleteDirectoryRecursively(child);
            }
        }
        if (!file.delete()) {
            throw new IOException("Failed to delete file or directory: " + file.getAbsolutePath());
        }
    }

    @Test
    @DisplayName("Test Save and Load Total Uang")
    public void testSaveAndLoadTotalUang() {
        double total = 1000.50;
        try {
            wordHandler.saveTotalUang(total);
            double loadedTotal = wordHandler.loadTotalUang();
            assertEquals(total, loadedTotal, 0.001, "Total uang yang dimuat harus sama dengan yang disimpan.");
        } catch (IOException e) {
            fail("IOException terjadi saat menyimpan atau memuat total uang: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test Save and Load Pemasukan")
    public void testSaveAndLoadPemasukan() {
        Pemasukan pemasukan = new Pemasukan();
        pemasukan.setId(1L);
        pemasukan.setJumlah(500.75);
        pemasukan.setDeskripsi("Gaji");
        pemasukan.setTanggal("2024-12-01");

        try {
            wordHandler.savePemasukan(pemasukan);
            List<Pemasukan> loadedList = wordHandler.loadPemasukan();
            assertFalse(loadedList.isEmpty(), "Daftar pemasukan tidak boleh kosong setelah disimpan.");
            Pemasukan loaded = loadedList.get(0);
            assertEquals(pemasukan.getId(), loaded.getId(), "ID pemasukan harus sama.");
            assertEquals(pemasukan.getJumlah(), loaded.getJumlah(), 0.001, "Jumlah pemasukan harus sama.");
            assertEquals(pemasukan.getDeskripsi(), loaded.getDeskripsi(), "Deskripsi pemasukan harus sama.");
            assertEquals(pemasukan.getTanggal(), loaded.getTanggal(), "Tanggal pemasukan harus sama.");
        } catch (IOException e) {
            fail("IOException terjadi saat menyimpan atau memuat pemasukan: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test Save and Load Pengeluaran tanpa Bukti")
    public void testSaveAndLoadPengeluaranWithoutBukti() {
        Pengeluaran pengeluaran = new Pengeluaran();
        pengeluaran.setId(1L);
        pengeluaran.setJumlah(200.00);
        pengeluaran.setDeskripsi("Beli buku");
        pengeluaran.setTanggal("2024-12-02");
        pengeluaran.setBukti(""); // Tanpa bukti

        try {
            wordHandler.savePengeluaran(pengeluaran);
            List<Pengeluaran> loadedList = wordHandler.loadPengeluaran();
            assertFalse(loadedList.isEmpty(), "Daftar pengeluaran tidak boleh kosong setelah disimpan.");
            Pengeluaran loaded = loadedList.get(0);
            assertEquals(pengeluaran.getId(), loaded.getId(), "ID pengeluaran harus sama.");
            assertEquals(pengeluaran.getJumlah(), loaded.getJumlah(), 0.001, "Jumlah pengeluaran harus sama.");
            assertEquals(pengeluaran.getDeskripsi(), loaded.getDeskripsi(), "Deskripsi pengeluaran harus sama.");
            assertEquals(pengeluaran.getTanggal(), loaded.getTanggal(), "Tanggal pengeluaran harus sama.");
            assertEquals(pengeluaran.getBukti(), loaded.getBukti(), "Bukti pengeluaran harus sama.");
        } catch (IOException e) {
            fail("IOException terjadi saat menyimpan atau memuat pengeluaran: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test Save and Load Pengeluaran dengan Bukti")
    public void testSaveAndLoadPengeluaranWithBukti() {
        Pengeluaran pengeluaran = new Pengeluaran();
        pengeluaran.setId(2L);
        pengeluaran.setJumlah(150.00);
        pengeluaran.setDeskripsi("Beli alat tulis");
        pengeluaran.setTanggal("2024-12-03");
        pengeluaran.setBukti("gbw.jpg");

        // Buat file bukti sementara
        Path buktiPath = buktiDirPath.resolve("gbw.jpg");
        try {
            Files.createFile(buktiPath);
            Files.write(buktiPath, new byte[]{0, 1, 2, 3, 4});
        } catch (IOException e) {
            fail("Gagal membuat file bukti sementara: " + e.getMessage());
        }

        try {
            wordHandler.savePengeluaran(pengeluaran);
            List<Pengeluaran> loadedList = wordHandler.loadPengeluaran();
            assertFalse(loadedList.isEmpty(), "Daftar pengeluaran tidak boleh kosong setelah disimpan.");
            // Cari pengeluaran dengan ID 2L
            Pengeluaran loaded = loadedList.stream().filter(p -> p.getId() == 2L).findFirst().orElse(null);
            assertNotNull(loaded, "Pengeluaran dengan ID 2L harus ada.");
            assertEquals(pengeluaran.getId(), loaded.getId(), "ID pengeluaran harus sama.");
            assertEquals(pengeluaran.getJumlah(), loaded.getJumlah(), 0.001, "Jumlah pengeluaran harus sama.");
            assertEquals(pengeluaran.getDeskripsi(), loaded.getDeskripsi(), "Deskripsi pengeluaran harus sama.");
            assertEquals(pengeluaran.getTanggal(), loaded.getTanggal(), "Tanggal pengeluaran harus sama.");
            assertEquals(pengeluaran.getBukti(), loaded.getBukti(), "Bukti pengeluaran harus sama.");
        } catch (IOException e) {
            fail("IOException terjadi saat menyimpan atau memuat pengeluaran: " + e.getMessage());
        } finally {
            // Hapus file bukti sementara
            try {
                Files.deleteIfExists(buktiPath);
            } catch (IOException e) {
                // Abaikan
            }
        }
    }

    @Test
    @DisplayName("Test Save and Load Pemasukan List")
    public void testSaveAndLoadPemasukanList() {
        List<Pemasukan> pemasukanList = new ArrayList<>();
        Pemasukan p1 = new Pemasukan();
        p1.setId(1L);
        p1.setJumlah(1000.00);
        p1.setDeskripsi("Gaji");
        p1.setTanggal("2024-12-01");
        pemasukanList.add(p1);

        Pemasukan p2 = new Pemasukan();
        p2.setId(2L);
        p2.setJumlah(250.50);
        p2.setDeskripsi("Freelance");
        p2.setTanggal("2024-12-15");
        pemasukanList.add(p2);

        try {
            wordHandler.savePemasukanList(pemasukanList);
            List<Pemasukan> loadedList = wordHandler.loadPemasukan();
            assertEquals(pemasukanList.size(), loadedList.size(), "Jumlah pemasukan yang dimuat harus sama dengan yang disimpan.");
            for (int i = 0; i < pemasukanList.size(); i++) {
                Pemasukan original = pemasukanList.get(i);
                Pemasukan loaded = loadedList.get(i);
                assertEquals(original.getId(), loaded.getId(), "ID pemasukan harus sama.");
                assertEquals(original.getJumlah(), loaded.getJumlah(), 0.001, "Jumlah pemasukan harus sama.");
                assertEquals(original.getDeskripsi(), loaded.getDeskripsi(), "Deskripsi pemasukan harus sama.");
                assertEquals(original.getTanggal(), loaded.getTanggal(), "Tanggal pemasukan harus sama.");
            }
        } catch (IOException e) {
            fail("IOException terjadi saat menyimpan atau memuat daftar pemasukan: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test Save and Load Pengeluaran List")
    public void testSaveAndLoadPengeluaranList() {
        List<Pengeluaran> pengeluaranList = new ArrayList<>();
        Pengeluaran p1 = new Pengeluaran();
        p1.setId(1L);
        p1.setJumlah(300.00);
        p1.setDeskripsi("Beli makanan");
        p1.setTanggal("2024-12-05");
        p1.setBukti(""); // Tanpa bukti
        pengeluaranList.add(p1);

        Pengeluaran p2 = new Pengeluaran();
        p2.setId(2L);
        p2.setJumlah(450.75);
        p2.setDeskripsi("Beli elektronik");
        p2.setTanggal("2024-12-10");
        p2.setBukti("gbw.jpg");
        pengeluaranList.add(p2);

        // Buat file bukti sementara untuk p2
        Path buktiPath = buktiDirPath.resolve("gbw.jpg");
        try {
            Files.createFile(buktiPath);
            // (Optional) Tulis beberapa data ke file bukti jika diperlukan
            Files.write(buktiPath, new byte[]{10, 20, 30, 40, 50});
        } catch (IOException e) {
            fail("Gagal membuat file bukti sementara: " + e.getMessage());
        }

        try {
            wordHandler.savePengeluaranList(pengeluaranList);
            List<Pengeluaran> loadedList = wordHandler.loadPengeluaran();
            assertEquals(pengeluaranList.size(), loadedList.size(), "Jumlah pengeluaran yang dimuat harus sama dengan yang disimpan.");
            for (int i = 0; i < pengeluaranList.size(); i++) {
                Pengeluaran original = pengeluaranList.get(i);
                Pengeluaran loaded = loadedList.get(i);
                assertEquals(original.getId(), loaded.getId(), "ID pengeluaran harus sama.");
                assertEquals(original.getJumlah(), loaded.getJumlah(), 0.001, "Jumlah pengeluaran harus sama.");
                assertEquals(original.getDeskripsi(), loaded.getDeskripsi(), "Deskripsi pengeluaran harus sama.");
                assertEquals(original.getTanggal(), loaded.getTanggal(), "Tanggal pengeluaran harus sama.");
                assertEquals(original.getBukti(), loaded.getBukti(), "Bukti pengeluaran harus sama.");
            }
        } catch (IOException e) {
            fail("IOException terjadi saat menyimpan atau memuat daftar pengeluaran: " + e.getMessage());
        } finally {
            try {
                Files.deleteIfExists(buktiPath);
            } catch (IOException e) {
                // Abaikan
            }
        }
    }

    @Test
    @DisplayName("Test Load Total Uang ketika file tidak ada")
    public void testLoadTotalUangFileNotExist() {
        // Pastikan file tidak ada
        File file = totalUangPath.toFile();
        if (file.exists()) {
            assertTrue(file.delete(), "Gagal menghapus file TotalUang.docx sebelum tes.");
        }

        double total = wordHandler.loadTotalUang();
        assertEquals(0.0, total, 0.001, "Total uang harus 0.0 ketika file tidak ada.");
    }

    @Test
    @DisplayName("Test Load Pemasukan ketika file tidak ada")
    public void testLoadPemasukanFileNotExist() {
        // Pastikan file tidak ada
        File file = pemasukanPath.toFile();
        if (file.exists()) {
            assertTrue(file.delete(), "Gagal menghapus file RiwayatPemasukan.docx sebelum tes.");
        }

        List<Pemasukan> list = wordHandler.loadPemasukan();
        assertTrue(list.isEmpty(), "Daftar pemasukan harus kosong ketika file tidak ada.");
    }

    @Test
    @DisplayName("Test Load Pengeluaran ketika file tidak ada")
    public void testLoadPengeluaranFileNotExist() {
        // Pastikan file tidak ada
        File file = pengeluaranPath.toFile();
        if (file.exists()) {
            assertTrue(file.delete(), "Gagal menghapus file RiwayatPengeluaran.docx sebelum tes.");
        }

        List<Pengeluaran> list = wordHandler.loadPengeluaran();
        assertTrue(list.isEmpty(), "Daftar pengeluaran harus kosong ketika file tidak ada.");
    }

    // Tambahkan lebih banyak tes sesuai kebutuhan, seperti menguji penanganan gambar yang rusak, format yang tidak didukung, dll.
}
