package org.example.model;

public class Pengeluaran {
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
