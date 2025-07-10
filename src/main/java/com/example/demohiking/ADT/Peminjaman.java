package com.example.demohiking.ADT;

import java.time.LocalDate;

public class Peminjaman {
    private String idPeminjaman;
    private LocalDate tanggalPeminjaman;
    private LocalDate tanggalPengembalian;
    private Karyawan karyawan;
    private Customer customer;

    public Peminjaman(){}

    public Peminjaman(String idPeminjaman, LocalDate tanggalPeminjaman, LocalDate tanggalPengembalian, Karyawan karyawan, Customer customer) {
        this.idPeminjaman = idPeminjaman;
        this.tanggalPeminjaman = tanggalPeminjaman;
        this.tanggalPengembalian = tanggalPengembalian;
        this.karyawan = karyawan;
        this.customer = customer;
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
}
