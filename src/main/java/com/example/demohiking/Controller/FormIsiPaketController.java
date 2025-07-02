package com.example.demohiking.Controller;

import com.example.demohiking.ADT.detailPaket;
import com.example.demohiking.Connection.DBConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.List;

public class FormIsiPaketController {

    @FXML private TextField txtAutoID;
    @FXML private TextField txtNamaPaket;
    @FXML private TextField txtHarga;
    @FXML private TextField txtDiskon;
    @FXML private TextField txtJumlahPaket;
    @FXML private TextArea txtDeskripsi;
    @FXML private ListView<String> lvProdukPaket;

    private List<detailPaket> produkDalamPaket;
    private HomeKasirController homeKasirController;

    public void setHomeController(HomeKasirController controller) {
        this.homeKasirController = controller;
    }

    public void setProdukDalamPaket(List<detailPaket> produk) {
        this.produkDalamPaket = produk;
        tampilkanProdukDalamListView();
        generatePaketID();
        hitungHargaAwal();
        txtDiskon.textProperty().addListener((obs, oldVal, newVal) -> updateHargaSetelahDiskon());
    }

    private void tampilkanProdukDalamListView() {
        ObservableList<String> items = FXCollections.observableArrayList();
        for (detailPaket dp : produkDalamPaket) {
            items.add(dp.getProduk().getNama() + " x" + dp.getJumlah());
        }
        lvProdukPaket.setItems(items);
    }

    private void generatePaketID() {
        String id = "PKT001";
        String query = "SELECT MAX(ID_Paket) as max_id FROM Paket";

        try (Connection conn = new DBConnect().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                String maxID = rs.getString("max_id");

                if (maxID != null && maxID.startsWith("PKT")) {
                    String numberPart = maxID.substring(3);
                    if (numberPart.matches("\\d+")) {
                        int nextID = Integer.parseInt(numberPart) + 1;
                        id = String.format("PKT%03d", nextID);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        txtAutoID.setText(id);
    }

    private double hitungTotalHargaProduk() {
        double total = 0.0;
        for (detailPaket dp : produkDalamPaket) {
            total += dp.getProduk().getHarga() * dp.getJumlah();
        }
        return total;
    }

    private void hitungHargaAwal() {
        double hargaAwal = hitungTotalHargaProduk();
        txtHarga.setText(String.format("Rp. %,.0f", hargaAwal));
    }

    private void updateHargaSetelahDiskon() {
        double diskon = 0.0;
        double hargaAwal = hitungTotalHargaProduk();

        try {
            String input = txtDiskon.getText().replace("%", "").replace(",", ".").trim();
            if (!input.isEmpty()) {
                diskon = Double.parseDouble(input);
            }

            if (diskon < 0 || diskon > 100) {
                showAlert("Diskon harus antara 0% - 100%");
                txtDiskon.setText("0");
                txtHarga.setText(String.format("Rp. %,.0f", hargaAwal));
                return;
            }

        } catch (NumberFormatException e) {
            txtHarga.setText(String.format("Rp. %,.0f", hargaAwal));
            return;
        }

        double hargaSetelahDiskon = hargaAwal * (1 - (diskon / 100.0));
        txtHarga.setText(String.format("Rp. %,.0f", hargaSetelahDiskon));
    }

    @FXML
    private void handleSimpanKeDatabase(ActionEvent event) {
        String idPaket = txtAutoID.getText().trim();
        String nama = txtNamaPaket.getText().trim();
        String deskripsi = txtDeskripsi.getText().trim();
        String jumlahStr = txtJumlahPaket.getText().trim();
        String diskonStr = txtDiskon.getText().replace("%", "").replace(",", ".").trim();
        String hargaStr = txtHarga.getText().trim();

        if (nama.isEmpty() || deskripsi.isEmpty() || jumlahStr.isEmpty() || diskonStr.isEmpty()) {
            showAlert("Semua field wajib diisi.");
            return;
        }

        if (hargaStr.isEmpty() || parseNominal(hargaStr) <= 0) {
            showAlert("Harga paket tidak valid.");
            return;
        }

        int jumlahPaket;
        double diskon, harga;

        try {
            jumlahPaket = Integer.parseInt(jumlahStr);
            if (jumlahPaket <= 0) {
                showAlert("Jumlah paket harus lebih dari 0.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Jumlah harus berupa angka.");
            return;
        }

        try {
            diskon = Double.parseDouble(diskonStr);
            if (diskon < 0 || diskon > 100) {
                showAlert("Diskon harus antara 0% - 100%");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Diskon harus berupa angka.");
            return;
        }

        harga = parseNominal(hargaStr);

        if (produkDalamPaket == null || produkDalamPaket.isEmpty()) {
            showAlert("Minimal pilih 1 produk untuk membuat paket.");
            return;
        }

        try (Connection conn = new DBConnect().getConnection()) {
            conn.setAutoCommit(false);

            String insertPaket = "INSERT INTO Paket (ID_Paket, Nama_Paket, Harga, Diskon, Deskripsi, Jumlah) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement paketStmt = conn.prepareStatement(insertPaket);
            paketStmt.setString(1, idPaket);
            paketStmt.setString(2, nama);
            paketStmt.setDouble(3, harga);
            paketStmt.setDouble(4, diskon/100.0);
            paketStmt.setString(5, deskripsi);
            paketStmt.setInt(6, jumlahPaket);
            paketStmt.executeUpdate();

            String updateStokPaket = "UPDATE Paket SET Stok = ? WHERE ID_Paket = ?";
            PreparedStatement updateStokStmt = conn.prepareStatement(updateStokPaket);
            updateStokStmt.setInt(1, jumlahPaket);
            updateStokStmt.setString(2, idPaket);
            updateStokStmt.executeUpdate();

            String insertDetail = "INSERT INTO detail_paket (ID_Paket, ID_Produk, Jumlah) VALUES (?, ?, ?)";
            PreparedStatement detailStmt = conn.prepareStatement(insertDetail);
            for (detailPaket item : produkDalamPaket) {
                detailStmt.setString(1, idPaket);
                detailStmt.setString(2, item.getProduk().getId());
                detailStmt.setInt(3, item.getJumlah());
                detailStmt.addBatch();
            }
            detailStmt.executeBatch();

            String updateStok = "UPDATE Produk SET Stok = Stok - ? WHERE ID_Produk = ?";
            PreparedStatement stokStmt = conn.prepareStatement(updateStok);
            for (detailPaket item : produkDalamPaket) {
                int totalPengurangan = item.getJumlah() * jumlahPaket;
                stokStmt.setInt(1, totalPengurangan);
                stokStmt.setString(2, item.getProduk().getId());
                stokStmt.addBatch();
            }
            stokStmt.executeBatch();

            conn.commit();

            if (homeKasirController != null) {
                homeKasirController.clearKeranjang();
                homeKasirController.refreshKeranjangView();
                homeKasirController.refreshProdukList();
                homeKasirController.refreshPaket();
                homeKasirController.showHomePaketPanel();
            }

            showAlert("✅ Paket berhasil disimpan ke database!");
            ((Stage) txtAutoID.getScene().getWindow()).close();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Gagal menyimpan paket: " + e.getMessage());
        }
    }

    @FXML
    private void handleBatal(ActionEvent event) {
        ((Stage) txtAutoID.getScene().getWindow()).close();
    }

    private void showAlert(String pesan) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    private double parseNominal(String text) {
        String cleaned = text.replace("Rp", "").replace(".", "").replace(",", "").trim();
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}