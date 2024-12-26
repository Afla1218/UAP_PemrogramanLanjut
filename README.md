# Pengelola Keuangan Pribadi

![Logo](path_to_logo_image)

## Deskripsi

**Pengelola Keuangan Pribadi** adalah aplikasi desktop berbasis Java yang dirancang untuk membantu pengguna dalam mengelola pemasukan dan pengeluaran mereka secara efisien. Dengan antarmuka pengguna yang intuitif dan fitur lengkap, aplikasi ini memungkinkan pengguna untuk mencatat transaksi keuangan, memantau saldo, serta melampirkan bukti pengeluaran dalam bentuk gambar.

## Fitur Utama

- **Kelola Pemasukan:**
  - Tambah, edit, dan hapus data pemasukan.
  - Deskripsi pemasukan untuk keterangan lebih jelas.
  - Otomatis menghitung total uang berdasarkan pemasukan dan pengeluaran.

- **Kelola Pengeluaran:**
  - Tambah, edit, dan hapus data pengeluaran.
  - Deskripsi pengeluaran untuk keterangan lebih jelas.
  - Lampirkan bukti pengeluaran berupa gambar (JPG, PNG, JPEG, BMP).
  - Validasi pengeluaran agar tidak melebihi total uang yang tersedia.

- **Antarmuka Pengguna yang Modern:**
  - Menggunakan Nimbus Look and Feel untuk tampilan yang lebih profesional.
  - Navigasi mudah antar panel (Home, Pemasukan, Pengeluaran).

- **Manajemen Data:**
  - Penyimpanan data sementara dalam aplikasi.
  - Fitur ekspor data menggunakan Apache POI (fitur pengembangan).

- **Keamanan dan Validasi:**
  - Validasi input untuk memastikan data yang dimasukkan benar.
  - Konfirmasi sebelum menghapus data untuk menghindari kesalahan.

## Teknologi yang Digunakan

- **Bahasa Pemrograman:** Java
- **GUI Framework:** Swing
- **Library Eksternal:** Apache POI (untuk ekspor data)
- **Build Tool:** Maven/Gradle (opsional, sesuai kebutuhan)

## Instalasi

### Prasyarat

- **Java Development Kit (JDK) 8** atau yang lebih baru.
- **Apache Maven** atau **Gradle** (jika menggunakan build tool).
- **Apache POI Library** harus ditambahkan ke classpath.

### Langkah-langkah

1. **Clone Repository:**

   ```bash
   git clone (https://github.com/Afla1218/UAP_PemrogramanLanjut.git)
