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

import java.util.Optional;


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
        lblStok.setText(String.valueOf(produk.getStok()));

        btnTambah.setVisible(true);
        btnKurang.setDisable(true);
    }

    public void setData(detailPeminjaman item, int nomorUrut) {
        this.item = item;
        this.jumlah = item.getJumlah();
        this.produk = item.getProduk();

        // Tampilkan hanya informasi penting untuk keranjang
        lblIDProduk.setText(String.valueOf(nomorUrut));
        lblNamaProduk.setText(produk.getNama() != null ? produk.getNama() : "-");
        lblKategori.setText("x" + jumlah);

        // Sembunyikan elemen yang tidak relevan
        lblHargaSatuan.setVisible(false);
        lblStok.setVisible(false);
        btnTambah.setVisible(false);
        btnKurang.setVisible(false);
    }

    @FXML
    private void handleTambahProduk(ActionEvent event) {
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

        updateLabelHargaTotal();
        btnKurang.setDisable(false);

        if (homeKasirController != null) {
            homeKasirController.updateKeranjang(item);
        }
    }

    @FXML
    private void handleKurangProduk(ActionEvent event) {
        if (produk == null || jumlah <= 0) {
            showAlert("Produk belum ditambahkan ke keranjang.");
            btnKurang.setDisable(true);
            return;
        }

        // Jika jumlah tinggal 1, konfirmasi penghapusan
        if (jumlah == 1) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Konfirmasi");
            alert.setHeaderText("Jumlah tinggal 1");
            alert.setContentText("Ingin menghapus item ini dari keranjang?");
            ButtonType hapus = new ButtonType("Hapus", ButtonBar.ButtonData.OK_DONE);
            ButtonType batal = new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(hapus, batal);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == hapus) {
                produk.setStok(produk.getStok() + 1);
                lblStok.setText(String.valueOf(produk.getStok()));

                jumlah = 0;
                if (item != null) item.setJumlah(jumlah);

                if (homeKasirController != null && item != null) {
                    homeKasirController.hapusItemDariKeranjang(item);
                }

                btnKurang.setDisable(true);
                showAlert("Item dihapus dari keranjang.");
            } else {
                showAlert("Penghapusan dibatalkan.");
            }
        } else {
            // Kurangi jumlah dan kembalikan stok
            jumlah--;
            produk.setStok(produk.getStok() + 1);
            lblStok.setText(String.valueOf(produk.getStok()));

            if (item != null) {
                item.setJumlah(jumlah);
            }

            updateLabelHargaTotal();

            if (homeKasirController != null) {
                homeKasirController.updateKeranjang(item);
            }
        }
    }

    private void updateLabelHargaTotal() {
        if (produk != null && isKeranjangView){double total = jumlah * produk.getHarga();
            lblHargaSatuan.setText(String.format("Total : Rp. %,.0f", total));}
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
