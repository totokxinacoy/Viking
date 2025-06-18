package com.example.demohiking;

import com.example.demohiking.Connection.DBConnect;
import com.example.demohiking.ADT.Produk;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class ItemProdukController {
        @FXML private Label lblNama;
        @FXML private Label lblHarga;
        @FXML private Button btnUpdate;
        @FXML private Button btnDelete;

        private Produk produk;
        private HomeController homeController;

        public void setData(Produk produk) {
            this.produk = produk;
            lblNama.setText(produk.getId());
            lblHarga.setText(produk.getNama());
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

            Stage stage = new Stage();
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

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText(null);
        confirm.setContentText("Apakah Anda yakin ingin menghapus produk ini?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    DBConnect db = new DBConnect();
                    Connection conn = db.getConnection();

                    String query = "UPDATE Produk SET status = 'Non Aktif' WHERE ID_Produk = ?";
                    PreparedStatement pstat = conn.prepareStatement(query);
                    pstat.setString(1, produk.getId());

                    int rows = pstat.executeUpdate();
                    pstat.close();
                    conn.close();

                    if (rows > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Produk telah dihapus.");
                        if (homeController != null) {
                            homeController.RefreshData(); // refresh tampilan produk
                        }
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Gagal", "Produk tidak ditemukan di database.");
                    }

                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
                }
            }
        });
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
            if (homeController != null && produk != null) {
                homeController.setDetailProduk(produk); // Kirim data ke form
            }
        }

    }


