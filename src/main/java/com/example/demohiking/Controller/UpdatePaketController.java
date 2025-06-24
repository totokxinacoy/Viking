package com.example.demohiking.Controller;

import com.example.demohiking.ADT.Paket;
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
import java.sql.*;

public class UpdatePaketController {

    @FXML
    private ImageView imgPaket;
    @FXML
    private TextField txtNama, txtHarga, txtJumlah;
    @FXML
    private TextArea txtDeskripsi;

    private File selectedImageFile;
    private Paket paket;

    private static Stage updateStage = null;
    private HomeKasirController homeKasirController;

    // Data awal
    private String initialNama, initialDeskripsi;
    private double initialHarga;
    private int initialJumlah;
    private Image initialImage;

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

    public void setHomeController(HomeKasirController controller) {
        this.homeKasirController = controller;
    }

    public void setPaket(Paket paket) {
        this.paket = paket;

        try (Connection conn = new DBConnect().getConnection()) {
            String query = "SELECT * FROM Paket WHERE ID_Paket = ?";
            PreparedStatement pstat = conn.prepareStatement(query);
            pstat.setString(1, paket.getId());

            ResultSet rs = pstat.executeQuery();
            if (rs.next()) {
                String nama = rs.getString("Nama_Paket");
                String deskripsi = rs.getString("Deskripsi");
                double harga = rs.getDouble("Harga");
                int jumlah = rs.getInt("Jumlah");

                txtNama.setText(nama);
                txtDeskripsi.setText(deskripsi);
                txtHarga.setText(String.valueOf(harga));
                txtJumlah.setText(String.valueOf(jumlah));

                InputStream is = rs.getBinaryStream("Image");
                if (is != null) {
                    Image img = new Image(is);
                    imgPaket.setImage(img);
                }

                initialNama = nama;
                initialDeskripsi = deskripsi;
                initialHarga = harga;
                initialJumlah = jumlah;
                initialImage = imgPaket.getImage();

            } else {
                showAlert(Alert.AlertType.WARNING, "Tidak Ditemukan", "Data paket tidak ditemukan.");
            }

            rs.close();
            pstat.close();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Gambar Paket");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Gambar", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            Image image = new Image(file.toURI().toString());
            imgPaket.setImage(image);
        }
    }

    @FXML
    private void handleUpdate() {
        String nama = txtNama.getText().trim();
        String deskripsi = txtDeskripsi.getText().trim();
        String hargaStr = txtHarga.getText().trim();
        String jumlahStr = txtJumlah.getText().trim();

        if (nama.isEmpty() || deskripsi.isEmpty() || hargaStr.isEmpty() || jumlahStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Semua field wajib diisi.");
            return;
        }

        try {
            double harga = Double.parseDouble(hargaStr);
            int jumlah = Integer.parseInt(jumlahStr);

            boolean adaPerubahan = !nama.equals(initialNama)
                    || !deskripsi.equals(initialDeskripsi)
                    || harga != initialHarga
                    || jumlah != initialJumlah
                    || selectedImageFile != null;

            if (!adaPerubahan) {
                showAlert(Alert.AlertType.INFORMATION, "Info", "Belum ada perubahan data.");
                return;
            }

            try (Connection conn = new DBConnect().getConnection()) {
                String query;
                PreparedStatement pstat;

                if (selectedImageFile != null) {
                    query = "UPDATE Paket SET Nama_Paket=?, Deskripsi=?, Harga=?, Jumlah=?, Image=? WHERE ID_Paket=?";
                    pstat = conn.prepareStatement(query);
                    pstat.setString(1, nama);
                    pstat.setString(2, deskripsi);
                    pstat.setDouble(3, harga);
                    pstat.setInt(4, jumlah);
                    InputStream imageStream = new FileInputStream(selectedImageFile);
                    pstat.setBinaryStream(5, imageStream, (int) selectedImageFile.length());
                    pstat.setString(6, paket.getId());
                } else {
                    query = "UPDATE Paket SET Nama_Paket=?, Deskripsi=?, Harga=?, Jumlah=? WHERE ID_Paket=?";
                    pstat = conn.prepareStatement(query);
                    pstat.setString(1, nama);
                    pstat.setString(2, deskripsi);
                    pstat.setDouble(3, harga);
                    pstat.setInt(4, jumlah);
                    pstat.setString(5, paket.getId());
                }

                int rows = pstat.executeUpdate();
                pstat.close();

                if (rows > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Paket berhasil diperbarui!");
                    if (homeKasirController != null) homeKasirController.RefreshData();
                    if (updateStage != null) {
                        updateStage.close();
                        clearWindow();
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Gagal", "Paket tidak ditemukan di database.");
                }

            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Format Salah", "Harga dan jumlah harus berupa angka.");
        } catch (SQLException | FileNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    protected void handleCancel() {
        if (updateStage != null) {
            updateStage.close();
            clearWindow();
        }
    }

    public void RefreshData() {
        txtNama.setText("");
        txtDeskripsi.setText("");
        txtHarga.setText("");
        txtJumlah.setText("");
        imgPaket.setImage(null);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}