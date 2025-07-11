package com.example.demohiking.ADT;

import java.time.LocalDate;

public class Pengembalian {
    private String idPengembalian;
    private Peminjaman peminjaman;
    private Denda denda;

    public Pengembalian(String idPengembalian, Peminjaman peminjaman, Denda denda) {
        this.idPengembalian = idPengembalian;
        this.peminjaman = peminjaman;
        this.denda = denda;
    }

    public String getIdPengembalian() {
        return idPengembalian;
    }

    public void setIdPengembalian(String idPengembalian) {
        this.idPengembalian = idPengembalian;
    }

    public Peminjaman getPeminjaman() {
        return peminjaman;
    }

    public void setPeminjaman(Peminjaman peminjaman) {
        this.peminjaman = peminjaman;
    }

    public Denda getDenda() {
        return denda;
    }

    public void setDenda(Denda denda) {
        this.denda = denda;
    }
}
