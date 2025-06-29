package com.example.demohiking.Controller;

import com.example.demohiking.ADT.detailPaket;
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

import java.util.Optional;

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
        lblHargaSatuan.setText(String.format("Rp. %,.0f", produk.getHarga()));
        lblHargaSatuan.setVisible(true);
        lblTotalHarga.setVisible(false);
        lblStok.setText(String.valueOf(produk.getStok()));

        btnTambah.setVisible(true);
    }

    public void setData(detailPaket item, int nomorUrut) {
        this.produk = item.getProduk();
        this.jumlah = item.getJumlah();
        this.item = item;

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
                    homeKasirController.tutupFormIsiPaket(); //
                } else if (response == tidak) {
                    homeKasirController.bringFormIsiPaketToFront();
                }

            });

            return;
        }

        if (produk.getStok() < 1) {
            showAlert("Stok tidak mencukupi!");
            return;
        }

        produk.setStok(produk.getStok() - 1);
        lblStok.setText(String.valueOf(produk.getStok()));

        jumlah = (item == null) ? 1 : jumlah + 1;
        updateDetailPaket();
        updateLabelHargaTotal();
    }

    @FXML
    private void handleKurangProduk(ActionEvent event) {
        if (homeKasirController != null && homeKasirController.isFormIsiPaketTerbuka()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Form Paket Sedang Aktif");
            alert.setHeaderText("Tidak bisa mengurangi produk saat mengisi paket.");
            alert.setContentText("Apakah Anda ingin menutup form paket agar bisa mengurangi produk?");
            ButtonType ya = new ButtonType("Ya", ButtonBar.ButtonData.YES);
            ButtonType tidak = new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(ya, tidak);

            alert.showAndWait().ifPresent(response -> {
                if (response == ya) {
                    homeKasirController.tutupFormIsiPaket(); //
                } else if (response == tidak) {
                    homeKasirController.bringFormIsiPaketToFront();
                }

            });

            return;
        }
        if (jumlah > 1) {
            jumlah--;
            produk.setStok(produk.getStok() + 1);
            lblStok.setText(String.valueOf(produk.getStok()));

            if (item != null) {
                item.setJumlah(jumlah);
            }

            if (homeKasirController != null) {
                homeKasirController.updateDetailProduk(item);
            }

            updateLabelHargaTotal();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Konfirmasi");
            alert.setHeaderText("Jumlah tinggal 1");
            alert.setContentText("Ingin menghapus item ini dari keranjang?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Jika user pilih YES
                produk.setStok(produk.getStok() + 1);
                lblStok.setText(String.valueOf(produk.getStok()));

                jumlah = 0;
                if (item != null) {
                    item.setJumlah(jumlah);
                }

                if (homeKasirController != null && item != null) {
                    homeKasirController.hapusItemDariKeranjang(item);
                }

                showAlert("Item dihapus dari keranjang.");
            } else {
                showAlert("Penghapusan dibatalkan.");
            }
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

    private void updateLabelHargaTotal() {
        if (produk != null && isKeranjangView) {
            double total = jumlah * produk.getHarga();
            lblTotalHarga.setText(String.format("Rp %, .0f", total));
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