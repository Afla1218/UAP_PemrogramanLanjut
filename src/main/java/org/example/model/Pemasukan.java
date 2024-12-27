package org.example.model;

public class Pemasukan {
    private long id;
    private double jumlah;
    private String deskripsi;
    private String tanggal;

    // Constructors
    public Pemasukan() {}

    public Pemasukan(long id, double jumlah, String deskripsi, String tanggal) {
        this.id = id;
        this.jumlah = jumlah;
        this.deskripsi = deskripsi;
        this.tanggal = tanggal;
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
}
