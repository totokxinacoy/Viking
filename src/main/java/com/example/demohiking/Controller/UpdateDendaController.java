package com.example.demohiking.Controller;

import com.example.demohiking.ADT.Denda;
import com.example.demohiking.Connection.DBConnect;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateDendaController {
    @FXML
    private ImageView imgDenda;

    @FXML
    private TextField txtNominal;
    @FXML
    private ComboBox<String> cmbJenisDenda;
    @FXML
    private TextArea txtDeskripsi;

    private Denda denda;

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

    private String initialJenis, initialDeskripsi;
    private double initialNominal;
    private HomeKasirController homeKasirController;

    public void setHomeController(HomeKasirController controller) {
        this.homeKasirController = controller;
    }

    public void setDenda(Denda denda) {
        this.denda = denda;

        try {
            DBConnect db = new DBConnect();
            Connection conn = db.getConnection();

            String query = "SELECT * FROM Denda WHERE ID_Denda = ?";
            PreparedStatement pstat = conn.prepareStatement(query);
            pstat.setString(1, denda.getId());

            ResultSet rs = pstat.executeQuery();
            if (rs.next()) {
                String jenis = rs.getString("Jenis_Denda");
                String deskripsi = rs.getString("Deskripsi");
                double nominal = rs.getDouble("Nominal");

                cmbJenisDenda.getItems().setAll("Kehilangan", "Kerusakan", "Terlambat");
                cmbJenisDenda.setValue(jenis);
                txtDeskripsi.setText(deskripsi);
                txtNominal.setText(String.valueOf(nominal));

                // Simpan nilai awal
                initialJenis = jenis;
                initialDeskripsi = deskripsi;
                initialNominal = nominal;
            } else {
                showAlert(Alert.AlertType.WARNING, "Tidak Ditemukan", "Data denda tidak ditemukan.");
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
        String jenis = cmbJenisDenda.getValue();
        String deskripsi = txtDeskripsi.getText().trim();
        String nominalStr = txtNominal.getText().trim();

        if (jenis == null || deskripsi.isEmpty() || nominalStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Semua field wajib diisi.");
            return;
        }

        double nominal;
        try {
            nominal = Double.parseDouble(nominalStr);
            if (nominal < 0) {
                showAlert(Alert.AlertType.WARNING, "Validasi", "Nominal tidak boleh negatif.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Nominal harus berupa angka.");
            return;
        }

        boolean adaPerubahan = !jenis.equals(initialJenis)
                || !deskripsi.equals(initialDeskripsi)
                || nominal != initialNominal;

        if (!adaPerubahan) {
            showAlert(Alert.AlertType.INFORMATION, "Info", "Belum ada perubahan data.");
            return;
        }

        try {
            DBConnect db = new DBConnect();
            Connection conn = db.getConnection();

            String query;
            PreparedStatement pstat;

                query = "UPDATE Denda SET Jenis_Denda=?, Deskripsi=?, Nominal=? WHERE ID_Denda=?";
                pstat = conn.prepareStatement(query);
                pstat.setString(1, jenis);
                pstat.setString(2, deskripsi);
                pstat.setDouble(3, nominal);
                pstat.setString(4, denda.getId());

            int rows = pstat.executeUpdate();
            pstat.close();
            conn.close();

            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data denda berhasil diperbarui!");
                if (homeKasirController != null) {
                    homeKasirController.RefreshDataDenda();
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
