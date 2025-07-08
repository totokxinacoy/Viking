package com.example.demohiking.Controller;

import com.example.demohiking.ADT.Produk;
import com.example.demohiking.Connection.DBConnect;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.sql.*;

public class UpdateProdukController {

    @FXML private ImageView imgProduk;
    @FXML private TextField txtNama, txtHarga, txtStok;
    @FXML private Label lblTotalJumlah;
    @FXML private ComboBox<String> cmbKategori;
    @FXML private TextArea txtDeskripsi;

    private File selectedImageFile;
    private Produk produk;
    private HomeKasirController homeKasirController;

    private String initialNama, initialKategori, initialDeskripsi;
    private double initialHarga;
    private int initialStok, initialJumlah;
    private Image initialImage;

    private static Stage updateStage = null;
    public static boolean isWindowOpen() { return updateStage != null; }
    public static void setWindow(Stage stage) { updateStage = stage; }
    public static void clearWindow() { updateStage = null; }
    public static void bringToFront() {
        if (updateStage != null) {
            updateStage.toFront();
            updateStage.requestFocus();
        }
    }

    public void setHomeController(HomeKasirController controller) {
        this.homeKasirController = controller;
    }

    public void setProduk(Produk produk) {
        this.produk = produk;

        try (Connection conn = new DBConnect().getConnection()) {
            String query = "SELECT * FROM Produk WHERE ID_Produk = ?";
            PreparedStatement pstat = conn.prepareStatement(query);
            pstat.setString(1, produk.getId());

            ResultSet rs = pstat.executeQuery();
            if (rs.next()) {
                String nama = rs.getString("Nama_Produk");
                String kategori = rs.getString("Kategori");
                String deskripsi = rs.getString("Deskripsi");
                double harga = rs.getDouble("Harga");
                int stok = rs.getInt("Stok");
                int jumlah = rs.getInt("Jumlah");

                txtNama.setText(nama);
                cmbKategori.getItems().setAll("Tas", "Sepatu", "Aksessoris", "Pakaian", "Tenda");
                cmbKategori.setValue(kategori);
                txtDeskripsi.setText(deskripsi);
                txtHarga.setText(String.valueOf(harga));
                lblTotalJumlah.setText(String.valueOf(jumlah));

                InputStream is = rs.getBinaryStream("Image");
                if (is != null) {
                    Image image = new Image(is);
                    imgProduk.setImage(image);
                }

                initialNama = nama;
                initialKategori = kategori;
                initialDeskripsi = deskripsi;
                initialHarga = harga;
                initialStok = stok;
                initialJumlah = jumlah;
                initialImage = imgProduk.getImage();
            } else {
                showAlert(Alert.AlertType.WARNING, "Tidak Ditemukan", "Produk tidak ditemukan.");
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
        fileChooser.setTitle("Pilih Gambar Produk");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Gambar", "*.png", "*.jpg", "*.jpeg"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            Image image = new Image(file.toURI().toString());
            imgProduk.setImage(image);
        }
    }

    @FXML
    private void handleUpdate() {
        String nama = txtNama.getText().trim();
        String kategori = cmbKategori.getValue();
        String deskripsi = txtDeskripsi.getText().trim();
        String hargaStr = txtHarga.getText().trim();
        String jumlahStr = txtStok.getText().trim();

        // Validasi input
        if (nama.isEmpty() || kategori == null || kategori.isEmpty()
                || deskripsi.isEmpty() || hargaStr.isEmpty() || jumlahStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Semua field wajib diisi.");
            return;
        }

        try {
            double harga = Double.parseDouble(hargaStr);
            int jumlahInput = Integer.parseInt(jumlahStr);
            int perubahanJumlah = jumlahInput - initialJumlah;
            int stokBaru = initialStok + perubahanJumlah;

            // Cek apakah stok mencukupi jika jumlah dikurangi
            if (perubahanJumlah < 0 && stokBaru < 0) {
                showAlert(Alert.AlertType.WARNING, "Stok Tidak Cukup",
                        "Stok tidak mencukupi untuk mengurangi jumlah sebanyak " + Math.abs(perubahanJumlah));
                return;
            }

            // Cek ada perubahan data
            boolean adaPerubahan = !nama.equals(initialNama)
                    || !kategori.equals(initialKategori)
                    || !deskripsi.equals(initialDeskripsi)
                    || harga != initialHarga
                    || jumlahInput != initialJumlah
                    || selectedImageFile != null;

            if (!adaPerubahan) {
                showAlert(Alert.AlertType.INFORMATION, "Info", "Tidak ada perubahan data.");
                return;
            }

            // Proses update ke database
            try (Connection conn = new DBConnect().getConnection()) {
                String query;
                PreparedStatement pstat;

                if (selectedImageFile != null) {
                    query = "UPDATE Produk SET Nama_Produk=?, Kategori=?, Deskripsi=?, Harga=?, Stok=?, Jumlah=?, Image=? WHERE ID_Produk=?";
                    pstat = conn.prepareStatement(query);
                    pstat.setString(1, nama);
                    pstat.setString(2, kategori);
                    pstat.setString(3, deskripsi);
                    pstat.setDouble(4, harga);
                    pstat.setInt(5, stokBaru);
                    pstat.setInt(6, jumlahInput);
                    InputStream imageStream = new FileInputStream(selectedImageFile);
                    pstat.setBinaryStream(7, imageStream, (int) selectedImageFile.length());
                    pstat.setString(8, produk.getId());
                } else {
                    query = "UPDATE Produk SET Nama_Produk=?, Kategori=?, Deskripsi=?, Harga=?, Stok=?, Jumlah=? WHERE ID_Produk=?";
                    pstat = conn.prepareStatement(query);
                    pstat.setString(1, nama);
                    pstat.setString(2, kategori);
                    pstat.setString(3, deskripsi);
                    pstat.setDouble(4, harga);
                    pstat.setInt(5, stokBaru);
                    pstat.setInt(6, jumlahInput);
                    pstat.setString(7, produk.getId());
                }

                int rows = pstat.executeUpdate();
                pstat.close();

                if (rows > 0) {
                    lblTotalJumlah.setText(String.valueOf(jumlahInput));
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Produk berhasil diperbarui!");
                    if (homeKasirController != null) {
                        homeKasirController.RefreshData();
                    }
                    if (updateStage != null) {
                        updateStage.close();
                        clearWindow();
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Gagal", "Produk tidak ditemukan.");
                }

            } catch (SQLException | FileNotFoundException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Format Salah", "Harga dan jumlah harus berupa angka.");
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
        cmbKategori.setValue("");
        txtHarga.setText("");
        txtStok.setText("");
        txtDeskripsi.setText("");
        imgProduk.setImage(null);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}