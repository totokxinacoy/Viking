package com.example.demohiking.Controller;

import com.example.demohiking.ADT.Jabatan;
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

public class ItemJabatanController {
    @FXML private Label lblNama;
    @FXML private Label lblID;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;

    private Jabatan jabatan;
    private HomeManagerController homeManagerController;

    public void setHomeController(HomeManagerController controller) {
        this.homeManagerController = controller;
    }

    public void setData(Jabatan jabatan) {
        this.jabatan = jabatan;

        if (jabatan != null) {
            lblID.setText(jabatan.getId() != null ? jabatan.getId() : "-");
            lblNama.setText(jabatan.getNama() != null ? jabatan.getNama() : "-");
        } else {
            lblID.setText("-");
            lblNama.setText("-");
        }
    }

    @FXML
    private void handleUpdateButtonClick() {
        if (UpdateJabatanController.isWindowOpen()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Info");
            alert.setHeaderText(null);
            alert.setContentText("Form update sudah dibuka. Klik OK untuk membukanya kembali.");
            alert.showAndWait();

            UpdateJabatanController.bringToFront();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateJabatan.fxml"));
            Parent root = loader.load();

            UpdateJabatanController controller = loader.getController();
            controller.setJabatan(jabatan);
            controller.setHomeController(homeManagerController);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Update Jabatan");
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            UpdateJabatanController.setWindow(stage);
            stage.setOnCloseRequest(event -> UpdateJabatanController.clearWindow());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteButtonClick() {
        if (jabatan == null) {
            showAlert(Alert.AlertType.ERROR, "Kesalahan", "Jabatan tidak ditemukan.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText(null);
        confirm.setContentText("Apakah Anda yakin ingin menghapus Jabatan ini?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    DBConnect db = new DBConnect();
                    Connection conn = db.getConnection();

                    String query = "UPDATE Jabatan SET status = 'Non Aktif' WHERE ID_Jabatan = ?";
                    PreparedStatement pstat = conn.prepareStatement(query);
                    pstat.setString(1, jabatan.getId());

                    int rows = pstat.executeUpdate();
                    pstat.close();
                    conn.close();

                    if (rows > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Jabatan telah dihapus.");
                        if (homeManagerController != null) {
                            homeManagerController.RefreshDataJabatan();
                        }
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Gagal", "Jabatan tidak ditemukan di database.");
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
        if (homeManagerController != null && jabatan != null) {
            homeManagerController.setDetailJabatan(jabatan);
        }
    }
}