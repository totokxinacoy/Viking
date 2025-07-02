package com.example.demohiking.Controller;

import com.example.demohiking.ADT.Paket;
import com.example.demohiking.Connection.DBConnect;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ItemPaketController {

    @FXML private Label lblNama;
    @FXML private Label lblID;
    @FXML private Label lblJumlah;
    @FXML private Label lblHarga;
    @FXML private Label lblDiskon;
    @FXML private Label lblStok;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;

    private Paket paket;
    private HomeKasirController homeKasirController;

    public void setHomeController(HomeKasirController controller) {
        this.homeKasirController = controller;
    }

    public void setData(Paket paket) {
        this.paket = paket;

        if (paket != null) {
            lblID.setText(paket.getId() != null ? paket.getId() : "-");
            lblNama.setText(paket.getNama() != null ? paket.getNama() : "-");
            lblHarga.setText(paket.getHarga() > 0 ? String.format("Rp. %,.0f", paket.getHarga()) : "Rp. 0");
            lblDiskon.setText(String.format("%.0f%%", paket.getDiskon() * 100));
            lblJumlah.setText(String.format("%,d", paket.getJumlahPaket()));
            lblStok.setText(String.format("%,d", paket.getJumlahPaket()));
        } else {
            lblID.setText("-");
            lblNama.setText("-");
            lblHarga.setText("Rp. 0");
            lblDiskon.setText("0%");
            lblJumlah.setText("0");
            lblStok.setText("0");
        }
    }

    @FXML
    private void handleUpdateButtonClick() {
        if (paket == null) {
            showAlert(Alert.AlertType.WARNING, "Update Gagal", "Data paket tidak tersedia.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdatePaket.fxml"));
            Parent root = loader.load();

            UpdatePaketController controller = loader.getController();
            controller.setPaket(paket);
            controller.setHomeController(homeKasirController);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Update Paket");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Gagal Membuka Form", e.getMessage());
        }
    }

    @FXML
    private void handleDeleteButtonClick() {
        if (paket == null) {
            showAlert(Alert.AlertType.ERROR, "Kesalahan", "Paket tidak ditemukan.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText(null);
        confirm.setContentText("Apakah Anda yakin ingin menghapus paket ini?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                hapusPaket();
            }
        });
    }

    @FXML
    private void hapusPaket() {
        String query = "UPDATE Paket SET status = 'Non Aktif' WHERE ID_Paket = ?";
        try (
                Connection conn = new DBConnect().getConnection();
                PreparedStatement pstat = conn.prepareStatement(query)
        ) {
            pstat.setString(1, paket.getId());

            int rows = pstat.executeUpdate();
            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Paket telah dihapus.");
                if (homeKasirController != null) {
                    homeKasirController.refreshPaket();
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Gagal", "Paket tidak ditemukan di database.");
            }

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
}