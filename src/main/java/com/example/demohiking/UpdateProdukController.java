package com.example.demohiking;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateProdukController {
    @FXML
    private TextField txtNama, txtKategori, txtHarga, txtStock;
    @FXML
    private TextArea txtDeskripsi;

    private Produk produk;

    public void setProduk(Produk produk) {
        this.produk = produk;
        txtNama.setText(produk.getNama());
        txtKategori.setText(produk.getKategori());
        txtHarga.setText(String.valueOf((int) produk.getHarga()));
        txtStock.setText(String.valueOf(produk.getStok()));
        txtDeskripsi.setText(produk.getDeskripsi());
    }

    @FXML
    private void handleSimpan() {
        String nama = txtNama.getText().trim();
        String kategori = txtKategori.getText().trim();
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

            int rowsAffected = pstat.executeUpdate();
            pstat.close();
            conn.close();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data produk berhasil diperbarui!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Data produk tidak ditemukan.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error Input", "Harga dan stok harus berupa angka.");
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