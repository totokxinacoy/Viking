package com.example.demohiking.Controller;

import com.example.demohiking.ADT.detailPaket;
import com.example.demohiking.ADT.detailPeminjaman;
import com.example.demohiking.Connection.DBConnect;
import com.example.demohiking.Session;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class FormIsiPeminjamanController {

    @FXML private TextField txtIDPeminjaman;
    @FXML private TextField txtIDKaryawan;
    @FXML private TextField txtIDCustomer;
    @FXML private DatePicker txtTglPeminjaman;
    @FXML private DatePicker txtTglPengembalian;
    @FXML private ListView<String> listPeminjaman;

    private List<detailPeminjaman> itemDalamPeminjaman;
    private HomeKasirController homeKasirController;
    private Connection conn;

    public void setHomeController(HomeKasirController controller) {
        this.homeKasirController = controller;
    }

    public void setItemDalamPeminjaman(List<detailPeminjaman> item) {
        this.itemDalamPeminjaman = item;
        tampilkanItemDalamListView();
        generatePeminjamanID();
    }

    private void tampilkanItemDalamListView() {
        ObservableList<String> items = FXCollections.observableArrayList();

        for (detailPeminjaman dp : itemDalamPeminjaman) {
            String namaItem = null;

            if (dp.getProduk() != null) {
                namaItem = dp.getProduk().getNama() + " x" + dp.getJumlah();
            } else if (dp.getPaket() != null) {
                namaItem = dp.getPaket().getNama() + " x" + dp.getJumlah();
            } else {
                System.err.println("Warning: detailPeminjaman tanpa produk atau paket ditemukan dan dilewati.");
                continue;
            }

            items.add(namaItem);
        }

        listPeminjaman.setItems(items);
    }
    private void generatePeminjamanID() {
        String id = "TRS001";
        String query = "SELECT MAX(id_peminjaman) as max_id FROM Transaksi_Peminjaman";

        try (Connection conn = new DBConnect().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                String maxID = rs.getString("max_id");

                if (maxID != null && maxID.startsWith("TRS")) {
                    String numberPart = maxID.substring(3);
                    if (numberPart.matches("\\d+")) {
                        int nextID = Integer.parseInt(numberPart) + 1;
                        id = String.format("TRS%03d", nextID);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        txtIDPeminjaman.setText(id);
    }


    @FXML
    public void initialize() {
        try {
            conn = new DBConnect().getConnection();
        } catch (Exception e) {
            showAlert("Database Error", e.getMessage());
        }

        loadPeminjamanList();

        if (Session.isLoggedIn()) {
            txtIDKaryawan.setText(Session.getId());
            txtIDKaryawan.setEditable(false);
        } else {
            showAlert("Sesi Tidak Aktif", "Silakan login terlebih dahulu.");
        }
    }

    public void setSelectedCustomerId(String customerId) {
        txtIDCustomer.setText(customerId);
        txtIDCustomer.setEditable(false); // opsional
    }

    @FXML
    private void handlePinjam() {
        String idPeminjaman = txtIDPeminjaman.getText();
        String idKaryawan = txtIDKaryawan.getText();
        String idCustomer = txtIDCustomer.getText();
        LocalDate tglPinjam = txtTglPeminjaman.getValue();
        LocalDate tglKembali = txtTglPengembalian.getValue();

        if (idPeminjaman.isEmpty() || idKaryawan.isEmpty() || idCustomer.isEmpty() || tglPinjam == null || tglKembali == null) {
            showAlert("Validasi", "Semua field harus diisi.");
            return;
        }

        try {
            conn.setAutoCommit(false);

// 1. Insert ke Transaksi_Peminjaman
            String insertPeminjaman = "INSERT INTO Transaksi_Peminjaman (id_peminjaman, id_customer, id_karyawan, tanggal_peminjaman , tanggal_pengembalian, status) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertPeminjaman)) {
                ps.setString(1, idPeminjaman);
                ps.setString(2, idCustomer);
                ps.setString(3, idKaryawan);
                ps.setDate(4, Date.valueOf(tglPinjam));
                ps.setDate(5, Date.valueOf(tglKembali));
                ps.setString(6, "Dipinjam");
                ps.executeUpdate();
            }

// 2. Insert ke detail_peminjaman dan kurangi stok
            String insertDetail = "INSERT INTO Detail_Peminjaman (id_peminjaman, id_item, jumlah, tipe) VALUES (?, ?, ?, ?)";
            String updateStokProduk = "UPDATE produk SET stok = stok - ? WHERE id_produk = ?";
            String updateStokPaket = "UPDATE paket SET stok = stok - ? WHERE id_paket = ?";

            for (detailPeminjaman dp : itemDalamPeminjaman) {
                if (dp.getProduk() != null) {
                    String idProduk = dp.getProduk().getId();
                    int jumlah = dp.getJumlah();

                    // Insert detail
                    try (PreparedStatement ps = conn.prepareStatement(insertDetail)) {
                        ps.setString(1, idPeminjaman);
                        ps.setString(2, idProduk);
                        ps.setInt(3, jumlah);
                        ps.setString(4, "produk");
                        ps.executeUpdate();
                    }

                    // Kurangi stok produk
                    try (PreparedStatement ps = conn.prepareStatement(updateStokProduk)) {
                        ps.setInt(1, jumlah);
                        ps.setString(2, idProduk);
                        ps.executeUpdate();
                    }

                } else if (dp.getPaket() != null) {
                    String idPaket = dp.getPaket().getId();
                    int jumlah = dp.getJumlah();

                    // Insert detail
                    try (PreparedStatement ps = conn.prepareStatement(insertDetail)) {
                        ps.setString(1, idPeminjaman);
                        ps.setString(2, idPaket);
                        ps.setInt(3, jumlah);
                        ps.setString(4, "paket");
                        ps.executeUpdate();
                    }

                    // Kurangi stok paket
                    try (PreparedStatement ps = conn.prepareStatement(updateStokPaket)) {
                        ps.setInt(1, jumlah);
                        ps.setString(2, idPaket);
                        ps.executeUpdate();
                    }

                } else {
                    // Validasi: item tidak valid
                    throw new SQLException("Item peminjaman tidak memiliki produk atau paket.");
                }
            }

            conn.commit();
            showAlert("Sukses", "Transaksi peminjaman berhasil disimpan.");
            loadPeminjamanList();
            if (homeKasirController != null) {
                homeKasirController.clearKeranjang();
                homeKasirController.refreshKeranjangTransaksiView();
                homeKasirController.refreshProdukList();
                homeKasirController.refreshPaket();
            }
            clearForm();

        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            showAlert("Gagal", "Terjadi kesalahan: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        clearForm();
    }

    private void clearForm() {
        txtIDPeminjaman.clear();
        txtIDKaryawan.clear();
        txtIDCustomer.clear();
        txtTglPeminjaman.setValue(null);
        txtTglPengembalian.setValue(null);
    }

    private void loadPeminjamanList() {
        ObservableList<String> data = FXCollections.observableArrayList();
        String query = "SELECT * FROM Transaksi_Peminjaman";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                data.add(rs.getString("id_peminjaman") + " - " + rs.getString("id_customer"));
            }
            listPeminjaman.setItems(data);
        } catch (SQLException e) {
            showAlert("Error", "Gagal memuat data: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}