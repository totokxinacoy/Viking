package com.example.demohiking.Controller;

import com.example.demohiking.ADT.Karyawan;
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
import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemKaryawanController {
    @FXML private Label lblID;
    @FXML private Label lblNama;
    @FXML private Label lblJabatan;
    @FXML private Label lblEmail;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;

    private Karyawan karyawan;
    private HomeManagerController homeManagerController;

    public void setHomeController(HomeManagerController controller) {
        this.homeManagerController = controller;
    }

    public void setData(Karyawan karyawan) {
        this.karyawan = karyawan;
        lblID.setText(karyawan.getId());
        lblNama.setText(karyawan.getNama());
        lblEmail.setText(karyawan.getEmail());

        // Ambil Nama Jabatan berdasarkan ID_Jabatan
        String namaJabatan = "-";
        try {
            DBConnect db = new DBConnect();
            Connection conn = db.getConnection();

            String query = "SELECT Nama_Jabatan FROM Jabatan WHERE ID_Jabatan = ?";
            PreparedStatement pstat = conn.prepareStatement(query);
            pstat.setString(1, karyawan.getId_jabatan());

            ResultSet rs = pstat.executeQuery();
            if (rs.next()) {
                namaJabatan = rs.getString("Nama_Jabatan");
            }

            rs.close();
            pstat.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        lblJabatan.setText(namaJabatan);
    }

    @FXML
    private void handleUpdateButtonClick() {
        if (UpdateKaryawanController.isWindowOpen()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Info");
            alert.setHeaderText(null);
            alert.setContentText("Form update sudah dibuka. Klik OK untuk membukanya kembali.");
            alert.showAndWait();

            UpdateKaryawanController.bringToFront();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateKaryawan.fxml"));
            Parent root = loader.load();

            UpdateKaryawanController controller = loader.getController();
            controller.setKaryawan(karyawan);
            controller.setHomeController(homeManagerController);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Update Karyawan");
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            UpdateKaryawanController.setWindow(stage);
            stage.setOnCloseRequest(event -> UpdateKaryawanController.clearWindow());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteButtonClick() {
        if (karyawan == null) {
            showAlert(Alert.AlertType.ERROR, "Kesalahan", "Karyawan tidak ditemukan.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText(null);
        confirm.setContentText("Apakah Anda yakin ingin menghapus Karyawan ini?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    DBConnect db = new DBConnect();
                    Connection conn = db.getConnection();

                    String query = "UPDATE Karyawan SET status = 'Non Aktif' WHERE ID_Jabatan = ?";
                    PreparedStatement pstat = conn.prepareStatement(query);
                    pstat.setString(1, karyawan.getId());

                    int rows = pstat.executeUpdate();
                    pstat.close();
                    conn.close();

                    if (rows > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Karyawan telah dihapus.");
                        if (homeManagerController != null) {
                            homeManagerController.RefreshDataKaryawan();
                        }
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Gagal", "Karyawan tidak ditemukan di database.");
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
        if (homeManagerController != null && karyawan != null) {
            homeManagerController.setDetailKaryawan(karyawan);
        }
    }
}