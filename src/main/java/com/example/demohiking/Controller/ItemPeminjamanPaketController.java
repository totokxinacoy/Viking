package com.example.demohiking.Controller;

import com.example.demohiking.ADT.Paket;
import com.example.demohiking.ADT.Produk;
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

public class ItemPeminjamanPaketController {
    @FXML
    private HBox Item1;

    @FXML
    private Label lblID;

    @FXML
    private Label lblNama;

    @FXML
    private Label lblStok;

    @FXML
    private Label lblTotalHarga;

    @FXML
    private Label lblHarga;

    @FXML
    private Button btnTambah;

    @FXML
    private Button btnKurang;

    private int jumlah = 0;
    private boolean isKeranjangView = false;
    private Paket paket;
    private Produk produk;
    private detailPeminjaman item;
    private HomeKasirController homeKasirController;

    public void setHomeController(HomeKasirController controller) {
        this.homeKasirController = controller;
    }

    public void setData(Paket paket) {
        this.paket = paket;
        this.jumlah = 0;

        lblID.setText(paket.getId() != null ? paket.getId() : "-");
        lblNama.setText(paket.getNama() != null ? paket.getNama() : "-");
        lblStok.setText(String.valueOf(paket.getStok()));
        lblHarga.setText(String.format("Rp. %,.0f", paket.getHarga()));
        lblHarga.setVisible(true);
        lblTotalHarga.setVisible(false);

        btnTambah.setVisible(true);
        btnKurang.setDisable(true);
    }

    public void setData(detailPeminjaman item, int nomorUrut) {
        this.item = item;
        this.jumlah = item.getJumlah();
        this.paket = item.getPaket();

        lblID.setText(String.valueOf(nomorUrut));
        lblNama.setText("[PAKET] " + paket.getNama());
        lblStok.setText("x" + jumlah);

        double total = jumlah * paket.getHarga();
        lblTotalHarga.setText(String.format("Total : Rp. %,.0f", total));
        lblTotalHarga.setVisible(true);
        lblHarga.setVisible(false);

        btnTambah.setManaged(false);
        btnTambah.setVisible(false);
        btnKurang.setManaged(false);
        btnKurang.setVisible(false);

        Item1.setPrefWidth(800);
        Item1.setMaxWidth(600);
        HBox.setMargin(lblTotalHarga, new Insets(0, 0, 0, 5));
    }


    @FXML
    private void handleTambahPaket(ActionEvent event) {
        // Cegah penambahan jika form isi paket sedang aktif
        if (homeKasirController != null && homeKasirController.isFormIsiPaketTerbuka()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Form Paket Sedang Aktif");
            alert.setHeaderText("Tidak bisa menambah paket saat mengisi paket lain.");
            alert.setContentText("Apakah Anda ingin menutup form paket agar bisa menambahkan paket?");
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

        // Validasi stok paket (jika paket punya stok total)
        if (paket.getStok() <= 1) {
            showAlert("Stok paket terlalu sedikit untuk ditambahkan ke keranjang (minimum 1).");
            return;
        }

        if (paket.getStok() < 1) {
            showAlert("Stok tidak mencukupi!");
            return;
        }

        // Kurangi stok
        paket.setStok(paket.getStok() - 1);
        lblStok.setText(String.valueOf(paket.getStok()));

        // Buat atau update item peminjaman
        if (item == null) {
            item = new detailPeminjaman(paket, 1);
            jumlah = 1;
        } else {
            jumlah++;
            item.setJumlah(jumlah);
        }

        lblStok.setText("x" + jumlah);
        updateLabelHargaTotalPaket();
        btnKurang.setDisable(false);

        if (homeKasirController != null) {
            homeKasirController.updateKeranjang(item);
        }
    }

    @FXML
    private void handleKurangPaket() {
        if (paket == null || jumlah <= 1) {
            showMinimalAlert();
            return;
        }

        jumlah--;
        item.setJumlah(jumlah);

        lblStok.setText("x" + jumlah);
        double total = jumlah * paket.getHarga();
        lblTotalHarga.setText(String.format("Total : Rp. %,.0f", total));
    }

    private void updateLabelHargaTotalPaket() {
        double total = jumlah * paket.getHarga();
        lblTotalHarga.setText(String.format("Total : Rp. %,.0f", total));
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
