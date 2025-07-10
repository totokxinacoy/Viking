package com.example.demohiking.ADT;

public class detailPeminjaman {
    private String idPeminjaman;
    private String tipe;
    private int jumlah;
    private Produk produk;
    private Paket paket;

    public detailPeminjaman(Produk produk, int jumlah) {
        this.tipe = "produk";
        this.produk = produk;
        this.jumlah = jumlah;
    }

    public detailPeminjaman(Paket paket, int jumlah) {
        this.paket = paket;
        this.tipe = "paket";
        this.jumlah = jumlah;
    }

    public String getTipe() { return tipe; }
    public int getJumlah() { return jumlah; }
    public Produk getProduk() { return produk; }
    public Paket getPaket() { return paket; }

    public void setIdPeminjaman(String idPeminjaman) {
        this.idPeminjaman = idPeminjaman;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public void setProduk(Produk produk) {
        this.produk = produk;
    }

    public void setPaket(Paket paket) {
        this.paket = paket;
    }
}