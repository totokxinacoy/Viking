package com.example.demohiking.ADT;

public class Produk {
    private String id;
    private String nama;
    private String kategori;
    private String deskripsi;
    private double harga;
    private int stok;

    public Produk(String id, String nama, String kategori, double harga, int stok) {
        this.id = id;
        this.nama = nama;
        this.kategori = kategori;
        this.deskripsi = null;
        this.harga = harga;
        this.stok = stok;
    }
    public Produk(String id, String nama, String kategori, String deskripsi, double harga, int stok) {
        this.id = id;
        this.nama = nama;
        this.kategori = kategori;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.stok = stok;
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