package com.example.demohiking.Controller;

import com.example.demohiking.ADT.detailPaket;
import com.example.demohiking.ADT.Produk;
import com.example.demohiking.ADT.detailPaket;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

public class ItemTransactProdukController {

    @FXML
    private Label lblIDProduk;

    @FXML
    private Label lblNamaProduk;

    @FXML
    private Label lblKategori;

    @FXML
    private Label lblHarga;

    @FXML
    private Label lblStok;

    @FXML
    private TextField txtJumlah;

    @FXML
    private Button btnTambah;

    private Produk produk;
    private HomeKasirController homeKasirController; // reference ke parent controller

    public void setHomeController(HomeKasirController controller) {
        this.homeKasirController = controller;
    }

    public void setData(Produk produk) {
        this.produk = produk;

        lblIDProduk.setText(produk.getId() != null ? produk.getId() : "-");
        lblNamaProduk.setText(produk.getNama() != null ? produk.getNama() : "-");
        lblKategori.setText(produk.getKategori() != null ? produk.getKategori() : "-");
        lblHarga.setText(String.format("Rp. %,.0f", produk.getHarga()));
        lblStok.setText(String.valueOf(produk.getStok()));
    }

    @FXML
    private void handleTambahKeKeranjang(ActionEvent event) {
        try {
            int jumlah = Integer.parseInt(txtJumlah.getText());
            if (jumlah <= 0) return;

            detailPaket item = new detailPaket(null, produk.getId(), jumlah);
            homeKasirController.tambahKeKeranjang(item);

        } catch (NumberFormatException e) {
            System.out.println("Jumlah tidak valid");
        }
    }
}