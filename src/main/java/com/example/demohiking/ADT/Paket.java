package com.example.demohiking.ADT;

public class Paket {
    private String id;
    private String nama;
    private String deskripsi;
    private double harga;
    private double diskon;

    public Paket(String id, String nama, double harga, double diskon) {
        this.id = id;
        this.nama = nama;
        this.deskripsi = null;
        this.harga = harga;
        this.diskon = diskon;
    }

    public Paket(String id, String nama, String deskripsi, double harga, double diskon) {
        this.id = id;
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.diskon = diskon;
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

}
