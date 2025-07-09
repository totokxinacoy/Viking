package com.example.demohiking.Controller;

import com.example.demohiking.ADT.Produk;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;


public class ItemPeminjamanProdukController {
    @FXML
    private HBox Item1;

    @FXML
    private Label lblIDProduk;

    @FXML
    private Label lblNamaProduk;

    @FXML
    private Label lblKategori;

    @FXML
    private Label lblTotalHarga;

    @FXML
    private Label lblHargaSatuan;

    @FXML
    private Label lblStok;

    @FXML
    private Button btnTambah;

    @FXML
    private Button btnKurang;

    private int jumlah = 0;
    private boolean isKeranjangView = false;
    private Produk produk;

    private HomeKasirController homeKasirController;

    public void setHomeController(HomeKasirController controller) {
        this.homeKasirController = controller;
    }

    public void setData(Produk produk) {
        this.produk = produk;
        this.jumlah = 0;

        lblIDProduk.setText(produk.getId() != null ? produk.getId() : "-");
        lblNamaProduk.setText(produk.getNama() != null ? produk.getNama() : "-");
        lblKategori.setText(produk.getKategori() != null ? produk.getKategori() : "-");
        lblHargaSatuan.setText(String.format("Rp. %,.0f", produk.getHarga()));
        lblHargaSatuan.setVisible(true);
        lblTotalHarga.setVisible(false);
        lblStok.setText(String.valueOf(produk.getStok()));

        btnTambah.setVisible(true);
        btnKurang.setDisable(true);
    }
}
