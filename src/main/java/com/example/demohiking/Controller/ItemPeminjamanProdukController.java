package com.example.demohiking.Controller;

import com.example.demohiking.ADT.Paket;
import com.example.demohiking.ADT.Produk;
import com.example.demohiking.ADT.detailPaket;
import com.example.demohiking.ADT.detailPeminjaman;
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
    private detailPeminjaman item;

    private HomeKasirController homeKasirController;

    public void setHomeController(HomeKasirController controller) {
        this.homeKasirController = controller;
    }

    public void setData(Produk produk) {
        this.produk = produk;
        this.jumlah = 0;
        this.item = null;

        lblIDProduk.setText(produk.getId() != null ? produk.getId() : "-");
        lblNamaProduk.setText(produk.getNama() != null ? produk.getNama() : "-");
        lblKategori.setText(produk.getKategori() != null ? produk.getKategori() : "-");
        lblHargaSatuan.setText(String.format("Rp. %,.0f", produk.getHarga()));
        lblHargaSatuan.setVisible(true);
        lblTotalHarga.setVisible(false);
        lblStok.setText(String.valueOf(produk.getStok()));

        btnTambah.setVisible(true);
        btnKurang.setManaged(false);
        btnKurang.setVisible(true);
    }

    public void setData(detailPeminjaman item, int nomorUrut) {
        this.item = item;
        this.jumlah = item.getJumlah();
        this.produk = item.getProduk();

        lblIDProduk.setText(String.valueOf(nomorUrut));
        lblNamaProduk.setText(produk.getNama());
        lblKategori.setText("x" + jumlah);

        double total = jumlah * produk.getHarga();
        lblHargaSatuan.setText(String.format("Total : Rp. %,.0f", total));
        lblHargaSatuan.setVisible(true);
        lblTotalHarga.setVisible(false);

        lblStok.setText("");
        lblStok.setManaged(false);
        lblStok.setVisible(false);
        btnTambah.setManaged(false);
        btnTambah.setVisible(false);
        btnKurang.setManaged(false);
        btnKurang.setVisible(false);

        Item1.setPrefWidth(800);
        Item1.setMaxWidth(600);
        HBox.setMargin(lblTotalHarga, new Insets(0, 0, 0, 5));
    }

    @FXML
    private void handleTambahProduk(ActionEvent event) {
        // Cegah penambahan jika form isi paket sedang aktif
        if (homeKasirController != null && homeKasirController.isFormIsiPaketTerbuka()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Form Paket Sedang Aktif");
            alert.setHeaderText("Tidak bisa menambah produk saat mengisi paket.");
            alert.setContentText("Apakah Anda ingin menutup form paket agar bisa menambahkan produk?");
            ButtonType ya = new ButtonType("Ya", ButtonBar.ButtonData.YES);
            ButtonType tidak = new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(ya, tidak);

            alert.showAndWait().ifPresent(response -> {
                if (response == ya) {
                    homeKasirController.tutupFormIsiPaket();
                } else if (response == tidak) {
                    homeKasirController.bringFormIsiPaketToFront();
                }
            });
            return;
        }

        // Validasi stok
        if (produk.getStok() <= 1) {
            showAlert("Stok produk terlalu sedikit untuk ditambahkan ke keranjang (minimum 1).");
            return;
        }

        if (produk.getStok() < 1) {
            showAlert("Stok tidak mencukupi!");
            return;
        }

        produk.setStok(produk.getStok() - 1);
        lblStok.setText(String.valueOf(produk.getStok()));

        if (item == null) {
            item = new detailPeminjaman(produk, 1);
            jumlah = 1;
        } else {
            jumlah++;
            item.setJumlah(jumlah);
        }

        lblKategori.setText("x" + jumlah);
        updateLabelHargaTotal();
        btnKurang.setDisable(false);

        if (homeKasirController != null) {
            homeKasirController.updateKeranjang(item);
        }
    }

    @FXML
    private void handleKurangProduk() {
        if (produk == null || jumlah <= 1) {
            showMinimalAlert();
            return;
        }

        jumlah--;
        item.setJumlah(jumlah);

        lblKategori.setText("x" + jumlah);
        double total = jumlah * produk.getHarga();
        lblHargaSatuan.setText(String.format("Total : Rp. %,.0f", total));
    }

    private void updateLabelHargaTotal() {
        double total = jumlah * produk.getHarga();
        lblHargaSatuan.setText(String.format("Total : Rp. %,.0f", total));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showMinimalAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText("Jumlah minimal adalah 1.");
        alert.showAndWait();
    }
}
