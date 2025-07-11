package com.example.demohiking.ADT;

import java.time.LocalDate;

public class Pembayaran {
    private String idPembayaran;
    private String idPeminjaman;
    private String idKaryawan;
    private String idCustomer;
    private String metodePembayaran;
    private LocalDate tglPembayaran;
    private double totalDenda;
    private double totalHarga;
    private double uangBayar;
    private double uangKembalian;

    public Pembayaran(String idPembayaran, String idPeminjaman, String idKaryawan, String idCustomer, String metodePembayaran, LocalDate tglPembayaran, double totalDenda, double totalHarga, double uangBayar, double uangKembalian) {
        this.idPembayaran = idPembayaran;
        this.idPeminjaman = idPeminjaman;
        this.idKaryawan = idKaryawan;
        this.idCustomer = idCustomer;
        this.metodePembayaran = metodePembayaran;
        this.tglPembayaran = tglPembayaran;
        this.totalDenda = totalDenda;
        this.totalHarga = totalHarga;
        this.uangBayar = uangBayar;
        this.uangKembalian = uangKembalian;
    }

    public String getIdPembayaran() {
        return idPembayaran;
    }

    public void setIdPembayaran(String idPembayaran) {
        this.idPembayaran = idPembayaran;
    }

    public String getIdPeminjaman() {
        return idPeminjaman;
    }

    public void setIdPeminjaman(String idPeminjaman) {
        this.idPeminjaman = idPeminjaman;
    }

    public String getIdKaryawan() {
        return idKaryawan;
    }

    public void setIdKaryawan(String idKaryawan) {
        this.idKaryawan = idKaryawan;
    }

    public String getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(String idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getMetodePembayaran() {
        return metodePembayaran;
    }

    public void setMetodePembayaran(String metodePembayaran) {
        this.metodePembayaran = metodePembayaran;
    }

    public LocalDate getTglPembayaran() {
        return tglPembayaran;
    }

    public void setTglPembayaran(LocalDate tglPembayaran) {
        this.tglPembayaran = tglPembayaran;
    }

    public double getTotalDenda() {
        return totalDenda;
    }

    public void setTotalDenda(double totalDenda) {
        this.totalDenda = totalDenda;
    }

    public double getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(double totalHarga) {
        this.totalHarga = totalHarga;
    }

    public double getUangBayar() {
        return uangBayar;
    }

    public void setUangBayar(double uangBayar) {
        this.uangBayar = uangBayar;
    }

    public double getUangKembalian() {
        return uangKembalian;
    }

    public void setUangKembalian(double uangKembalian) {
        this.uangKembalian = uangKembalian;
    }
}
