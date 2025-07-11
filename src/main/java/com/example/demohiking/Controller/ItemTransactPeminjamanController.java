package com.example.demohiking.Controller;

import com.example.demohiking.ADT.Peminjaman;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.format.DateTimeFormatter;

public class ItemTransactPeminjamanController {
    @FXML
    private Label lblID;
    @FXML
    private Label lblNama;
    @FXML
    private Label lblTglPeminjaman;
    @FXML
    private Label lblTglPengembalian;

    private Peminjaman peminjaman;

    private HomeKasirController homeKasirController;

    public void setHomeController(HomeKasirController controller) {
        this.homeKasirController = controller;
    }

    public void setData(Peminjaman peminjaman) {
        this.peminjaman = peminjaman;

        lblID.setText(peminjaman.getIdPeminjaman());
        lblNama.setText(peminjaman.getIdCustomer());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String tanggalPinjamFormatted = peminjaman.getTanggalPeminjaman().format(formatter);
        String tanggalKembaliFormatted = peminjaman.getTanggalPengembalian().format(formatter);
        lblTglPeminjaman.setText(tanggalPinjamFormatted);
        lblTglPengembalian.setText(tanggalKembaliFormatted);
    }

}
