package com.example.demohiking.Controller;

import com.example.demohiking.ADT.detailPeminjaman;
import com.example.demohiking.ADT.Produk;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ItemDetailPeminjamanProdukController {

    @FXML
    private HBox Item1;

    @FXML
    private Label lblIDProduk;

    @FXML
    private Label lblNamaProduk;

    @FXML
    private Label lblJumlah;

    @FXML
    private Label lblTotalHarga;

    private Produk produk;
    private detailPeminjaman item;
    private int jumlah;

    private HomeKasirController homeKasirController;

    public void setHomeController(HomeKasirController controller) {
        this.homeKasirController = controller;
    }

    public void setData(detailPeminjaman item, int nomorUrut) {
        this.item = item;
        this.jumlah = item.getJumlah();
        this.produk = item.getProduk();

        lblIDProduk.setText(String.valueOf(nomorUrut));
        lblNamaProduk.setText(produk.getNama());
        lblJumlah.setText("x" + jumlah);

        double total = jumlah * produk.getHarga();
        lblTotalHarga.setText(String.format("Total : Rp. %,.0f", total));

//        Item1.setPrefWidth(800);
//        Item1.setMaxWidth(600);
//        HBox.setMargin(lblTotalHarga, new Insets(0, 0, 0, 5));
    }
}