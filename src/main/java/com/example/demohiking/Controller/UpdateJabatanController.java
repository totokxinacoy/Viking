package com.example.demohiking.Controller;

import com.example.demohiking.ADT.Jabatan;
import com.example.demohiking.Connection.DBConnect;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateJabatanController {
    @FXML
    private TextField txtNama;

    private Jabatan jabatan;

    private static Stage updateStage = null;

    public static boolean isWindowOpen() {
        return updateStage != null;
    }

    public static void setWindow(Stage stage) {
        updateStage = stage;
    }

    public static void clearWindow() {
        updateStage = null;
    }

    public static void bringToFront() {
        if (updateStage != null) {
            updateStage.toFront();
            updateStage.requestFocus();
        }
    }

    private String initialNama;
    private HomeManagerController homeManagerController;

    public void setHomeController(HomeManagerController controller) {
        this.homeManagerController = controller;
    }

    public void setJabatan(Jabatan jabatan) {
        this.jabatan = jabatan;

        try {
            DBConnect db = new DBConnect();
            Connection conn = db.getConnection();

            String query = "SELECT * FROM Jabatan WHERE ID_Jabatan = ?";
            PreparedStatement pstat = conn.prepareStatement(query);
            pstat.setString(1, jabatan.getId());

            ResultSet rs = pstat.executeQuery();
            if (rs.next()) {
                String nama = rs.getString("Nama_Jabatan");
                txtNama.setText(nama);
                initialNama = nama;
            } else {
                showAlert(Alert.AlertType.WARNING, "Tidak Ditemukan", "Data jabatan tidak ditemukan.");
            }

            rs.close();
            pstat.close();
            conn.close();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        String nama = txtNama.getText().trim();

        if (nama.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Nama jabatan wajib diisi.");
            return;
        }

        if (nama.equals(initialNama)) {
            showAlert(Alert.AlertType.INFORMATION, "Info", "Belum ada perubahan data.");
            return;
        }

        try {
            DBConnect db = new DBConnect();
            Connection conn = db.getConnection();

            String query = "UPDATE Jabatan SET Nama_Jabatan=? WHERE ID_Jabatan=?";
            PreparedStatement pstat = conn.prepareStatement(query);
            pstat.setString(1, nama);
            pstat.setString(2, jabatan.getId());

            int rows = pstat.executeUpdate();
            pstat.close();
            conn.close();

            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Nama jabatan berhasil diperbarui!");
                if (homeManagerController != null) {
                    homeManagerController.RefreshDataJabatan();
                }
                if (updateStage != null) {
                    updateStage.close();
                    clearWindow();
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Data tidak ditemukan.");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    protected void handleCancel() {
        if (updateStage != null) {
            updateStage.close();
            clearWindow();
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