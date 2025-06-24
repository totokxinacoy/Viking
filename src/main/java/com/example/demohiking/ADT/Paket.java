package com.example.demohiking.ADT;

import java.util.List;

public class Paket {
    private String id;
    private String nama;
    private String deskripsi;
    private double harga;
    private double diskon;
    private int jumlahPaket;
    private List<detailPaket> isiPaket; // Tambahan: daftar isi paket

    // Constructor tanpa deskripsi
    public Paket(String id, String nama, double harga, double diskon, int jumlahPaket) {
        this.id = id;
        this.nama = nama;
        this.deskripsi = null;
        this.harga = harga;
        this.diskon = diskon;
        this.jumlahPaket = jumlahPaket;
    }

    // Constructor lengkap tanpa isiPaket
    public Paket(String id, String nama, String deskripsi, double harga, double diskon, int jumlahPaket) {
        this.id = id;
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.diskon = diskon;
        this.jumlahPaket = jumlahPaket;
    }

    // Constructor lengkap + isi paket
    public Paket(String id, String nama, String deskripsi, double harga, double diskon, int jumlahPaket, List<detailPaket> isiPaket) {
        this.id = id;
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.diskon = diskon;
        this.jumlahPaket = jumlahPaket;
        this.isiPaket = isiPaket;
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

    public double getDiskon() {
        return diskon;
    }

    public void setDiskon(double diskon) {
        this.diskon = diskon;
    }

    public int getJumlahPaket() {
        return jumlahPaket;
    }

    public void setJumlahPaket(int jumlahPaket) {
        this.jumlahPaket = jumlahPaket;
    }

    public List<detailPaket> getIsiPaket() {
        return isiPaket;
    }

    public void setIsiPaket(List<detailPaket> isiPaket) {
        this.isiPaket = isiPaket;
    }
}

