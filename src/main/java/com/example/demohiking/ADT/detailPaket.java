package com.example.demohiking.ADT;

public class detailPaket {
    private String idPaket;
    private String idProduk;
    private int jumlah;

    public detailPaket(String idPaket, String idProduk, int jumlah) {
        this.idPaket = idPaket;
        this.idProduk = idProduk;
        this.jumlah = jumlah;
    }

    public String getIdPaket() {
        return idPaket;
    }

    public void setIdPaket(String idPaket) {
        this.idPaket = idPaket;
    }

    public String getIdProduk() {
        return idProduk;
    }

    public void setIdProduk(String idProduk) {
        this.idProduk = idProduk;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }
}