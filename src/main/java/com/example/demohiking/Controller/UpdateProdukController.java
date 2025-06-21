package com.example.demohiking.Controller;

import com.example.demohiking.ADT.Produk;
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

public class UpdateProdukController {
    @FXML
    private ImageView imgProduk;

    private File selectedImageFile;
    @FXML
    private TextField txtNama, txtKategori, txtHarga, txtStock;
    @FXML
    private ComboBox<String> cmbKategori;
    @FXML
    private TextArea txtDeskripsi;

    private Produk produk;

    // Static stage untuk kontrol satu jendela saja
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

    private String initialNama, initialKategori, initialDeskripsi;
    private double initialHarga;
    private int initialStok;
    private Image initialImage;

    private HomeController homeController;

    public void setHomeController(HomeController controller) {
        this.homeController = controller;
    }

    public void setProduk(Produk produk) {
        this.produk = produk;

        try {
            DBConnect db = new DBConnect();
            Connection conn = db.getConnection();

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

                txtNama.setText(nama);
                cmbKategori.getItems().setAll("Tas", "Sepatu", "Aksessoris", "Pakaian", "Tenda");
                cmbKategori.setValue(kategori);
                txtDeskripsi.setText(deskripsi);
                txtHarga.setText(String.valueOf(harga));
                txtStock.setText(String.valueOf(stok));

                InputStream is = rs.getBinaryStream("Image");
                if (is != null) {
                    Image image = new Image(is);
                    imgProduk.setImage(image);
                }

                // Simpan data awal
                initialNama = nama;
                initialKategori = kategori;
                initialDeskripsi = deskripsi;
                initialHarga = harga;
                initialStok = stok;
                initialImage = imgProduk.getImage();
            } else {
                showAlert(Alert.AlertType.WARNING, "Tidak Ditemukan", "Produk tidak ditemukan.");
            }

            rs.close();
            pstat.close();
            conn.close();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Gambar Produk");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Gambar", "*.png", "*.jpg", "*.jpeg")
        );

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
        String stokStr = txtStock.getText().trim();

        if (nama.isEmpty() || kategori.isEmpty() || deskripsi.isEmpty()
                || hargaStr.isEmpty() || stokStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Semua field wajib diisi.");
            return;
        }

        try {
            double harga = Double.parseDouble(hargaStr);
            int stok = Integer.parseInt(stokStr);

            boolean adaPerubahan = !nama.equals(initialNama)
                    || !kategori.equals(initialKategori)
                    || !deskripsi.equals(initialDeskripsi)
                    || harga != initialHarga
                    || stok != initialStok
                    || selectedImageFile != null;

            if (!adaPerubahan) {
                showAlert(Alert.AlertType.INFORMATION, "Info", "Belum ada perubahan data.");
                return;
            }

            DBConnect db = new DBConnect();
            Connection conn = db.getConnection();

            String query;
            PreparedStatement pstat;

            if (selectedImageFile != null) {
                query = "UPDATE Produk SET Nama_Produk=?, Kategori=?, Deskripsi=?, Harga=?, Stok=?, Image=? WHERE ID_Produk=?";
                pstat = conn.prepareStatement(query);
                pstat.setString(1, nama);
                pstat.setString(2, kategori);
                pstat.setString(3, deskripsi);
                pstat.setDouble(4, harga);
                pstat.setInt(5, stok);
                InputStream imageStream = new FileInputStream(selectedImageFile);
                pstat.setBinaryStream(6, imageStream, (int) selectedImageFile.length());
                pstat.setString(7, produk.getId());
            } else {
                query = "UPDATE Produk SET Nama_Produk=?, Kategori=?, Deskripsi=?, Harga=?, Stok=? WHERE ID_Produk=?";
                pstat = conn.prepareStatement(query);
                pstat.setString(1, nama);
                pstat.setString(2, kategori);
                pstat.setString(3, deskripsi);
                pstat.setDouble(4, harga);
                pstat.setInt(5, stok);
                pstat.setString(6, produk.getId());
            }

            int rows = pstat.executeUpdate();
            pstat.close();
            conn.close();

            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Produk berhasil diperbarui!");

                if (homeController != null) {
                    homeController.RefreshData();
                }

                if (updateStage != null) {
                    updateStage.close();
                    clearWindow();
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Data tidak ditemukan.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Format Salah", "Harga dan stok harus angka.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        } catch (FileNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "File Gambar Tidak Ditemukan", e.getMessage());
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
        txtStock.setText("");
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