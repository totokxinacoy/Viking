package com.example.demohiking.Controller;

import com.example.demohiking.ADT.detailPaket;
import com.example.demohiking.ADT.Produk;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    private Button btnTambah;

    @FXML
    private Button btnKurang;

    private int jumlah = 0;

    private Produk produk;
    private detailPaket item;
    private HomeKasirController homeKasirController;

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
    private void handleTambahProduk(ActionEvent event) {
        if (produk.getStok() > jumlah) {
            jumlah++;
            updateDetailPaket();
        } else {
            showAlert("Stok tidak mencukupi!");
        }
    }

    @FXML
    private void handleKurangProduk(ActionEvent event) {
        if (jumlah > 0) {
            jumlah--;
            updateDetailPaket();
        } else {
            showAlert("Jumlah tidak bisa kurang dari 0.");
        }
    }

    private void updateDetailPaket() {
        if (item == null) {
            item = new detailPaket();
            item.setProduk(produk);
        }
        item.setJumlah(jumlah);

        if (homeKasirController != null) {
            homeKasirController.updateDetailProduk(item);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Peringatan");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}