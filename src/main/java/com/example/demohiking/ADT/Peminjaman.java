package com.example.demohiking.ADT;

import java.time.LocalDate;

public class Peminjaman {
    private String idPeminjaman;
    private String idCustomer;
    private String idKaryawan;
    private LocalDate tanggalPeminjaman;
    private LocalDate tanggalPengembalian;
    private Karyawan karyawan;
    private Customer customer;

    public Peminjaman(){}

    public Peminjaman(String idPeminjaman, String idCustomer, String idKaryawan, LocalDate tanggalPeminjaman, LocalDate tanggalPengembalian) {
        this.idPeminjaman = idPeminjaman;
        this.idCustomer = idCustomer;
        this.idKaryawan = idKaryawan;
        this.tanggalPeminjaman = tanggalPeminjaman;
        this.tanggalPengembalian = tanggalPengembalian;
    }

    public Peminjaman(String idPeminjaman, String idCustomer, LocalDate tanggalPeminjaman, LocalDate tanggalPengembalian) {
        this.idPeminjaman = idPeminjaman;
        this.idCustomer = idCustomer;
        this.idKaryawan = null;
        this.tanggalPeminjaman = tanggalPeminjaman;
        this.tanggalPengembalian = tanggalPengembalian;
    }

    public String getIdPeminjaman() {
        return idPeminjaman;
    }

    public void setIdPeminjaman(String idPeminjaman) {
        this.idPeminjaman = idPeminjaman;
    }

    public LocalDate getTanggalPeminjaman() {
        return tanggalPeminjaman;
    }

    public void setTanggalPeminjaman(LocalDate tanggalPeminjaman) {
        this.tanggalPeminjaman = tanggalPeminjaman;
    }

    public LocalDate getTanggalPengembalian() {
        return tanggalPengembalian;
    }

    public void setTanggalPengembalian(LocalDate tanggalPengembalian) {
        this.tanggalPengembalian = tanggalPengembalian;
    }

    public Karyawan getKaryawan() {
        return karyawan;
    }

    public void setKaryawan(Karyawan karyawan) {
        this.karyawan = karyawan;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(String idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getIdKaryawan() {
        return idKaryawan;
    }

    public void setIdKaryawan(String idKaryawan) {
        this.idKaryawan = idKaryawan;
    }
}
