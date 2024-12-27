```markdown
# Pengelola Keuangan Pribadi

**Pengelola Keuangan Pribadi** adalah aplikasi desktop berbasis Java yang dirancang untuk membantu pengguna mengelola pemasukan dan pengeluaran mereka secara efisien. Aplikasi ini menggunakan antarmuka pengguna grafis (GUI) dengan Java Swing dan menyimpan data ke dalam dokumen Word menggunakan Apache POI. Selain itu, aplikasi ini mendukung penyisipan bukti pengeluaran berupa gambar.

## 📋 Fitur

- **Manajemen Pemasukan**
  - Tambah pemasukan dengan jumlah, deskripsi, dan tanggal.
  - Lihat riwayat pemasukan dalam tabel.
  - Hapus pemasukan dengan double-click pada entri tabel.

- **Manajemen Pengeluaran**
  - Tambah pengeluaran dengan jumlah, deskripsi, tanggal, dan bukti berupa gambar.
  - Lihat riwayat pengeluaran dalam tabel, termasuk tampilan gambar bukti.
  - Hapus pengeluaran dengan double-click pada entri tabel.

- **Total Uang**
  - Menampilkan total uang yang tersedia.
  - Update otomatis saat menambah atau menghapus pemasukan dan pengeluaran.

- **Penyimpanan Data**
  - Data pemasukan dan pengeluaran disimpan dalam dokumen Word (`.docx`).
  - Gambar bukti pengeluaran disimpan di folder terpisah (`bukti_pengeluaran`).

## 🛠️ Teknologi yang Digunakan

- **Bahasa Pemrograman**: Java
- **GUI Framework**: Java Swing
- **Library**: Apache POI (untuk operasi baca/tulis dokumen Word)
- **Build Tool**: Maven (disarankan)

## 📂 Struktur Proyek

```
PengelolaKeuangan/
├── src/
│   └── org/
│       └── example/
│           ├── data/
│           │   └── WordHandler.java
│           ├── model/
│           │   ├── Pemasukan.java
│           │   └── Pengeluaran.java
│           ├── ui/
│           │   ├── HomePanel.java
│           │   ├── PemasukanPanel.java
│           │   └── PengeluaranPanel.java
│           └── PengelolaKeuangan.java
├── lib/
│   └── poi-ooxml-5.2.3.jar
├── README.md
└── bukti_pengeluaran/
```

## 🚀 Instalasi

### Prasyarat

- **Java Development Kit (JDK)**: Pastikan Anda telah menginstal JDK 8 atau lebih tinggi. [Unduh JDK](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- **Apache Maven**: Untuk mengelola dependensi. [Unduh Maven](https://maven.apache.org/download.cgi)
- **Apache POI Library**: Untuk operasi baca/tulis dokumen Word.

### Langkah-langkah

1. **Clone Repository**

   ```bash
   git clone https://github.com/username/PengelolaKeuangan.git
   cd PengelolaKeuangan
   ```

2. **Tambahkan Library Apache POI**

   Jika menggunakan Maven, pastikan `pom.xml` Anda memiliki dependensi berikut:

   ```xml
   <dependencies>
       <!-- Apache POI -->
       <dependency>
           <groupId>org.apache.poi</groupId>
           <artifactId>poi-ooxml</artifactId>
           <version>5.2.3</version>
       </dependency>
   </dependencies>
   ```

   Jika tidak menggunakan Maven, unduh JAR Apache POI dan tambahkan ke classpath Anda:

   ```bash
   mkdir lib
   # Unduh poi-ooxml-5.2.3.jar dari https://poi.apache.org/download.html dan letakkan di folder lib/
   ```

3. **Kompilasi Proyek**

   Jika menggunakan Maven:

   ```bash
   mvn compile
   ```

   Jika menggunakan command line biasa:

   ```bash
   javac -cp ".;lib/*" src/org/example/model/*.java src/org/example/data/*.java src/org/example/ui/*.java src/org/example/PengelolaKeuangan.java
   ```

4. **Jalankan Aplikasi**

   Jika menggunakan Maven:

   ```bash
   mvn exec:java -Dexec.mainClass="org.example.PengelolaKeuangan"
   ```

   Jika menggunakan command line biasa:

   ```bash
   java -cp ".;lib/*;src" org.example.PengelolaKeuangan
   ```

## 🖥️ Penggunaan

1. **Menambahkan Pemasukan**
   - Klik tombol **Pemasukan** pada halaman utama.
   - Isi jumlah uang dan deskripsi.
   - Klik **Simpan** untuk menambahkan pemasukan.

2. **Menambahkan Pengeluaran**
   - Klik tombol **Pengeluaran** pada halaman utama.
   - Isi jumlah uang, deskripsi, dan pilih gambar sebagai bukti pengeluaran.
   - Klik **Simpan** untuk menambahkan pengeluaran.

3. **Melihat Riwayat**
   - Pada masing-masing panel **Pemasukan** dan **Pengeluaran**, Anda dapat melihat riwayat entri dalam tabel.
   - Double-click pada entri untuk menghapusnya.

4. **Navigasi**
   - Klik tombol **Kembali** pada masing-masing panel untuk kembali ke halaman utama.

## 🤝 Kontribusi

Kontribusi sangat diterima! Silakan ikuti langkah-langkah berikut untuk berkontribusi:

1. **Fork Repository**
2. **Buat Branch Baru**

   ```bash
   git checkout -b fitur-baru
   ```

3. **Commit Perubahan Anda**

   ```bash
   git commit -m "Tambah fitur baru"
   ```

4. **Push ke Branch Anda**

   ```bash
   git push origin fitur-baru
   ```

5. **Buat Pull Request**

## 📄 Lisensi

Distributed under the MIT License. See `LICENSE` for more information.

## ✍️ Penulis

- **Nama**: Tabuarasa
- **Email**: tabuarasa@example.com
- **GitHub**: [@tabuarasa](https://github.com/tabuarasa)

## 📷 Screenshot

### Halaman Utama

![Home](screenshots/home.png)

### Panel Pemasukan

![Pemasukan](screenshots/pemasukan.png)

### Panel Pengeluaran

![Pengeluaran](screenshots/pengeluaran.png)

## 📝 Catatan

- Pastikan folder `bukti_pengeluaran` ada di direktori kerja aplikasi untuk menyimpan gambar bukti pengeluaran.
- Aplikasi ini menggunakan dokumen Word untuk menyimpan data, jadi pastikan Apache POI library terintegrasi dengan baik.
- Untuk mengoptimalkan ukuran gambar dan mencegah dokumen Word menjadi terlalu besar, disarankan untuk menggunakan gambar dengan resolusi rendah.

## 📚 Referensi

- [Apache POI Documentation](https://poi.apache.org/)
- [Java Swing Tutorial](https://docs.oracle.com/javase/tutorial/uiswing/)
```
