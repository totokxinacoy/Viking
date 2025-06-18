package com.example.demohiking;

public class Produk {
    private String id;
    private String nama;
    private String kategori;
    private String deskripsi;
    private double harga;
    private int stok;

    public Produk(String id, String nama, String kategori, String deskripsi, double harga, int stok) {
        this.id = id;
        this.nama = nama;
        this.kategori = kategori;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.stok = stok;
    }

    public Produk(String id, String nama) {
        this.id = id;
        this.nama = nama;
        this.kategori = null;
        this.deskripsi = null;
        this.harga = 0;
        this.stok = 0;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }
}