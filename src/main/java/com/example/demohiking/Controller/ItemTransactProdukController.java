package com.example.demohiking.Controller;

import com.example.demohiking.ADT.detailPaket;
import com.example.demohiking.ADT.Produk;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class ItemTransactProdukController {


    @FXML
    private HBox Item1;

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

        btnTambah.setVisible(true);
    }

    public void setData(detailPaket item, int nomorUrut) {
        this.produk = item.getProduk();
        this.jumlah = item.getJumlah();

        lblIDProduk.setText(String.valueOf(nomorUrut)); // pakai angka urut
        lblNamaProduk.setText(produk.getNama());
        lblKategori.setText("x" + jumlah);

        lblHarga.setText("");
        lblStok.setText("");
        lblHarga.setManaged(false);
        lblHarga.setVisible(false);
        lblStok.setManaged(false);
        lblStok.setVisible(false);
        btnTambah.setManaged(false);
        btnTambah.setVisible(false);

        Item1.setPrefWidth(300);
        Item1.setMaxWidth(600);

        btnTambah.setOnAction(event -> {
            if (produk.getStok() > jumlah) {
                jumlah++;
                item.setJumlah(jumlah);
                lblKategori.setText("x" + jumlah);

                if (homeKasirController != null) {
                    homeKasirController.updateDetailProduk(item);
                }
            } else {
                showAlert("Stok tidak mencukupi!");
            }
        });
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