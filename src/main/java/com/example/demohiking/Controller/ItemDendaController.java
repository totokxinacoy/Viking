package com.example.demohiking.Controller;

import com.example.demohiking.ADT.Denda;
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

public class ItemDendaController {
    @FXML private Label lblJenis;
    @FXML private Label lblID;
    @FXML private Label lblNominal;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;

    private Denda denda;
    private HomeKasirController homeKasirController;

    public void setHomeController(HomeKasirController controller) {
        this.homeKasirController = controller;
    }

    public void setData(Denda denda) {
        this.denda = denda;

        if (denda != null) {
            lblID.setText(denda.getId() != null ? denda.getId() : "-");
            lblJenis.setText(denda.getJenis() != null ? denda.getJenis() : "-");
            lblNominal.setText(String.valueOf(denda.getNominal()));
        } else {
            lblID.setText("-");
            lblJenis.setText("-");
            lblNominal.setText("0");
        }
    }

    @FXML
    private void handleUpdateButtonClick() {
        if (UpdateDendaController.isWindowOpen()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Info");
            alert.setHeaderText(null);
            alert.setContentText("Form update sudah dibuka. Klik OK untuk membukanya kembali.");
            alert.showAndWait();

            // Setelah user menekan OK, tampilkan jendela yang sedang dibuka
            UpdateDendaController.bringToFront();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateDenda.fxml"));
            Parent root = loader.load();

            UpdateDendaController controller = loader.getController();
            controller.setDenda(denda);
            controller.setHomeController(homeKasirController);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Update Denda");
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            // Simpan referensi stage
            UpdateDendaController.setWindow(stage);

            stage.setOnCloseRequest(event -> UpdateDendaController.clearWindow());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteButtonClick() {
        if (denda == null) {
            showAlert(Alert.AlertType.ERROR, "Kesalahan", "Denda tidak ditemukan.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText(null);
        confirm.setContentText("Apakah Anda yakin ingin menghapus Denda ini?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    DBConnect db = new DBConnect();
                    Connection conn = db.getConnection();

                    String query = "UPDATE Denda SET status = 'Non Aktif' WHERE ID_Denda = ?";
                    PreparedStatement pstat = conn.prepareStatement(query);
                    pstat.setString(1, denda.getId());

                    int rows = pstat.executeUpdate();
                    pstat.close();
                    conn.close();

                    if (rows > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Denda telah dihapus.");

                        // Langsung panggil refresh tanpa menunggu konfirmasi tambahan
                        if (homeKasirController != null) {
                            homeKasirController.RefreshDataDenda();
                        }
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Gagal", "Denda tidak ditemukan di database.");
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
        if (homeKasirController != null && denda != null) {
            homeKasirController.setDetailDenda(denda); // Kirim data ke form
        }
    }
}
