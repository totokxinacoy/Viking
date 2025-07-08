package com.example.demohiking.Controller;

import com.example.demohiking.ADT.Produk;
import com.example.demohiking.Connection.DBConnect;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ItemProdukController {
        @FXML private Label lblNama;
        @FXML private Label lblID;
        @FXML private Label lblStok;
        @FXML private Label lblKategori;
        @FXML private Label lblHarga;
        @FXML private Label lblJumlah;
        @FXML private Button btnUpdate;
        @FXML private Button btnDelete;

        private Produk produk;
        private HomeKasirController homeKasirController;

    public void setHomeController(HomeKasirController controller) {
        this.homeKasirController = controller;
    }

    public void setData(Produk produk) {
        this.produk = produk;

        if (produk != null) {
            lblID.setText(produk.getId() != null ? produk.getId() : "-");
            lblNama.setText(produk.getNama() != null ? produk.getNama() : "-");
            lblKategori.setText(produk.getKategori() != null ? produk.getKategori() : "-");
            lblHarga.setText(String.format("Rp. %,.0f", produk.getHarga()));
            lblStok.setText(String.valueOf(produk.getStok()));
            lblJumlah.setText(String.valueOf(produk.getJumlah()));
        } else {
            lblID.setText("-");
            lblNama.setText("-");
            lblKategori.setText("-");
            lblHarga.setText("0");
            lblStok.setText("0");
            lblJumlah.setText("0");
        }
    }

    @FXML
    private void handleUpdateButtonClick() {
        if (UpdateProdukController.isWindowOpen()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Info");
            alert.setHeaderText(null);
            alert.setContentText("Form update sudah dibuka. Klik OK untuk membukanya kembali.");
            alert.showAndWait();

            // Setelah user menekan OK, tampilkan jendela yang sedang dibuka
            UpdateProdukController.bringToFront();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateProduk.fxml"));
            Parent root = loader.load();

            UpdateProdukController controller = loader.getController();
            controller.setProduk(produk);
            controller.setHomeController(homeKasirController);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Update Produk");
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            // Simpan referensi stage
            UpdateProdukController.setWindow(stage);

            stage.setOnCloseRequest(event -> UpdateProdukController.clearWindow());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteButtonClick() {
        if (produk == null) {
            showAlert(Alert.AlertType.ERROR, "Kesalahan", "Produk tidak ditemukan.");
            return;
        }

        try (Connection conn = new DBConnect().getConnection()) {

            // Cek apakah produk masih dipakai di transaksi yang belum selesai
//            String transaksiCheck = "SELECT COUNT(*) AS jumlah FROM detail_transaksi dt " +
//                    "JOIN Transaksi t ON dt.ID_Transaksi = t.ID_Transaksi " +
//                    "WHERE dt.ID_Produk = ? AND t.Status = 'Non Aktif'";
//            try (PreparedStatement stmt1 = conn.prepareStatement(transaksiCheck)) {
//                stmt1.setString(1, produk.getId());
//                ResultSet rs1 = stmt1.executeQuery();
//                if (rs1.next() && rs1.getInt("jumlah") > 0) {
//                    showAlert(Alert.AlertType.WARNING, "Tidak Dapat Menghapus",
//                            "Produk ini sedang digunakan dalam transaksi yang belum selesai.");
//                    return;
//                }
//            }

            // Cek apakah produk masih dipakai di paket yang aktif
            String paketCheck = "SELECT COUNT(*) AS jumlah FROM detail_paket dp " +
                    "JOIN Paket p ON dp.ID_Paket = p.ID_Paket " +
                    "WHERE dp.ID_Produk = ? AND p.Status = 'Aktif'";
            try (PreparedStatement stmt2 = conn.prepareStatement(paketCheck)) {
                stmt2.setString(1, produk.getId());
                ResultSet rs2 = stmt2.executeQuery();
                if (rs2.next() && rs2.getInt("jumlah") > 0) {
                    showAlert(Alert.AlertType.WARNING, "Tidak Dapat Menghapus",
                            "Produk ini masih digunakan dalam paket yang aktif.");
                    return;
                }
            }

            // Konfirmasi sebelum nonaktifkan
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Konfirmasi Hapus");
            confirm.setHeaderText(null);
            confirm.setContentText("Apakah Anda yakin ingin menghapus produk ini?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try (PreparedStatement pstat = conn.prepareStatement(
                            "UPDATE Produk SET status = 'Non Aktif' WHERE ID_Produk = ?")) {
                        pstat.setString(1, produk.getId());
                        int rows = pstat.executeUpdate();

                        if (rows > 0) {
                            showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Produk berhasil dinonaktifkan.");
                            if (homeKasirController != null) {
                                homeKasirController.refreshProdukList();
                            }
                        } else {
                            showAlert(Alert.AlertType.WARNING, "Gagal", "Produk tidak ditemukan di database.");
                        }
                    } catch (SQLException e) {
                        showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
                    }
                }
            });

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
        @FXML
        private void handleSelectItem() {
            if (homeKasirController != null && produk != null) {
                homeKasirController.setDetailProduk(produk); // Kirim data ke form
            }
        }

    private void RefreshData() {
        if (homeKasirController != null) {
            homeKasirController.refreshProdukList();
        }
    }

    public void refreshStok() {
        if (produk != null && lblStok.isVisible()) {
            lblStok.setText(String.valueOf(produk.getStok()));
        }
    }
    }


