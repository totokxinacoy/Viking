package com.example.demohiking;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateProdukController {
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
                txtNama.setText(rs.getString("Nama_Produk"));
                cmbKategori.getItems().setAll("Tas", "Sepatu", "Aksessoris", "Pakaian", "Tenda");
                cmbKategori.setValue(rs.getString("Kategori"));
                txtHarga.setText(String.valueOf(rs.getDouble("Harga")));
                txtStock.setText(String.valueOf(rs.getInt("Stok")));
                txtDeskripsi.setText(rs.getString("Deskripsi"));
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
    private void handleSimpan() {
        String nama = txtNama.getText().trim();
        String kategori = cmbKategori.getValue();
        String deskripsi = txtDeskripsi.getText().trim();
        String hargaStr = txtHarga.getText().trim();
        String stokStr = txtStock.getText().trim();

        if (nama.isEmpty() || kategori.isEmpty() || deskripsi.isEmpty() || hargaStr.isEmpty() || stokStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Semua field wajib diisi.");
            return;
        }

        try {
            double harga = Double.parseDouble(hargaStr);
            int stok = Integer.parseInt(stokStr);

            DBConnect db = new DBConnect();
            Connection conn = db.getConnection();

            String query = "UPDATE Produk SET Nama_Produk=?, Kategori=?, Deskripsi=?, Harga=?, Stok=? WHERE ID_Produk=?";
            PreparedStatement pstat = conn.prepareStatement(query);
            pstat.setString(1, nama);
            pstat.setString(2, kategori);
            pstat.setString(3, deskripsi);
            pstat.setDouble(4, harga);
            pstat.setInt(5, stok);
            pstat.setString(6, produk.getId());

            int rows = pstat.executeUpdate();
            pstat.close();
            conn.close();

            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Produk berhasil diperbarui!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Data tidak ditemukan.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Format Salah", "Harga dan stok harus angka.");
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