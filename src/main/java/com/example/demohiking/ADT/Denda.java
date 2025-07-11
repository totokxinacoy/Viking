package com.example.demohiking.ADT;

public class Denda {
    private String id;
    private String jenis;
    private String deskripsi;
    private double nominal;

    public Denda(String id, String jenis, Double nominal) {
        this.id = id;
        this.jenis = jenis;
        this.deskripsi = null;
        this.nominal = nominal;
    }

    public Denda(String id, String jenis, String deskripsi, double nominal) {
        this.id = id;
        this.jenis = jenis;
        this.deskripsi = deskripsi;
        this.nominal = nominal;
    }

    // Constructor untuk transaksi pengembalian
    public Denda(String id, String jenis) {
        this.id = id;
        this.jenis = jenis;
        this.deskripsi = null;
        this.nominal = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public double getNominal() {
        return nominal;
    }

    public void setNominal(double nominal) {
        this.nominal = nominal;
    }

    @Override
    public String toString() {
        return jenis;
    }

}
