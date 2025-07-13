package com.example.demohiking.Controller;

import com.example.demohiking.ADT.detailPeminjaman;
import com.example.demohiking.Connection.DBConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class FormIsiPembayaranController {
    @FXML
    private TextField txtIDPembayaran;
    @FXML
    private TextField txtIDPeminjaman;
    @FXML
    private TextField txtNamaKaryawan;
    @FXML
    private TextField txtNamaCustomer;
    @FXML
    private DatePicker txtTglPembayaran;
    @FXML
    private TextField txtTotalDenda;
    @FXML
    private TextField txtTotalHarga;
    @FXML
    private ComboBox<String> cmbMetode;
    @FXML
    private TextField txtBayar;
    @FXML
    private TextField txtKembalian;
    @FXML
    private Button btnSubmit;
    @FXML
    private Button btnCancel;
    @FXML
    private ListView<String> listPeminjaman;

    private HomeKasirController homeKasirController;
    private double jumlahYangHarusDibayar = 0;

    public void setHomeController(HomeKasirController controller) {
        this.homeKasirController = controller;
    }


    public void setInformasiPeminjaman(String idPeminjaman, String namaCustomer, String namaKaryawan, String nominalDenda, String totalHarga) {
        generatePembayaranID();
        txtIDPeminjaman.setText(idPeminjaman);
        txtNamaCustomer.setText(namaCustomer);
        txtNamaKaryawan.setText(namaKaryawan);

        double harga = 0;
        double denda = 0;
        try {
            harga = Double.parseDouble(totalHarga);
        } catch (NumberFormatException e) {
            harga = 0;
        }

        try {
            denda = Double.parseDouble(nominalDenda);
        } catch (NumberFormatException e) {
            denda = 0;
        }

        jumlahYangHarusDibayar = harga + denda;

        txtTotalDenda.setText(formatRupiah(denda));
        txtTotalHarga.setText(formatRupiah(jumlahYangHarusDibayar));
        txtBayar.setText(String.valueOf(jumlahYangHarusDibayar));
        cmbMetode.getItems().setAll("Tunai", "Non-Tunai");
        setupTanggalPembayaran();
        tampilkanDetailPeminjaman(idPeminjaman);
        hitungKembalian();
    }

    public void setIdPeminjamanLangsung(String idPeminjaman) {
        txtIDPeminjaman.setText(idPeminjaman);
        tampilkanDetailPeminjaman(idPeminjaman);
    }

    @FXML
    public void initialize() {
        txtBayar.textProperty().addListener((observable, oldValue, newValue) -> hitungKembalian());
    }

    private void hitungKembalian() {
        try {
            double totalBayar = Double.parseDouble(txtBayar.getText());
            double kembalian = totalBayar - jumlahYangHarusDibayar;
            txtKembalian.setText(formatRupiah(kembalian < 0 ? 0 : kembalian));
        } catch (NumberFormatException e) {
            txtKembalian.setText(formatRupiah(0));
        }
    }

    private void setupTanggalPembayaran() {
        txtTglPembayaran.setValue(LocalDate.now());

        txtTglPembayaran.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(true);
            }
        });
        txtTglPembayaran.setEditable(false);
    }

    private void tampilkanDetailPeminjaman(String idPeminjaman) {
        ObservableList<String> items = FXCollections.observableArrayList();

        String query = "SELECT dp.jumlah, pr.nama_produk, pk.nama_paket " +
                "FROM Detail_Peminjaman dp " +
                "LEFT JOIN Produk pr ON dp.id_item = pr.ID_Produk " +
                "LEFT JOIN Paket pk ON dp.id_item = pk.ID_Paket " +
                "WHERE dp.id_peminjaman = '" + idPeminjaman + "'";

        try (Connection conn = new DBConnect().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int jumlah = rs.getInt("jumlah");
                String namaProduk = rs.getString("nama_produk");
                String namaPaket = rs.getString("nama_paket");

                String namaItem;
                if (namaProduk != null && !namaProduk.isEmpty()) {
                    namaItem = namaProduk + " x" + jumlah;
                } else if (namaPaket != null && !namaPaket.isEmpty()) {
                    namaItem = namaPaket + " x" + jumlah;
                } else {
                    continue;
                }

                items.add(namaItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        listPeminjaman.setItems(items);
    }

    private void generatePembayaranID() {
        String id = "PMB001";
        String query = "SELECT MAX(id_pembayaran) as max_id FROM Transaksi_Pembayaran";

        try (Connection conn = new DBConnect().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                String maxID = rs.getString("max_id");

                if (maxID != null && maxID.startsWith("PMB")) {
                    String numberPart = maxID.substring(3);
                    if (numberPart.matches("\\d+")) {
                        int nextID = Integer.parseInt(numberPart) + 1;
                        id = String.format("PMB%03d", nextID);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        txtIDPembayaran.setText(id);
    }

    private String formatRupiah(double nilai) {
        java.text.NumberFormat formatter = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("in", "ID"));
        return formatter.format(nilai);
    }

    @FXML
    private void handleInsertPembayaran() {
        String idPembayaran = txtIDPembayaran.getText().trim();
        String idPeminjaman = txtIDPeminjaman.getText().trim();
        String metode = cmbMetode.getValue();
        LocalDate tanggalPembayaran = txtTglPembayaran.getValue();

        if (idPembayaran.isEmpty() || idPeminjaman.isEmpty() || metode == null || tanggalPembayaran == null) {
            showAlert("Validasi", "Pastikan semua data sudah terisi dengan benar.");
            return;
        }

        double uangBayar;
        try {
            uangBayar = Double.parseDouble(txtBayar.getText());
            if (uangBayar < jumlahYangHarusDibayar) {
                showAlert("Validasi", "Jumlah yang dibayar kurang dari total tagihan.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Validasi", "Format nominal bayar tidak valid.");
            return;
        }

        double uangKembalian = uangBayar - jumlahYangHarusDibayar;
        double totalDenda = parseRupiah(txtTotalDenda.getText());

        String idKaryawan = null;
        String idCustomer = null;

        String infoQuery = "SELECT id_karyawan, id_customer FROM Transaksi_Peminjaman WHERE id_peminjaman = ?";
        try (Connection conn = new DBConnect().getConnection();
             PreparedStatement psInfo = conn.prepareStatement(infoQuery)) {

            psInfo.setString(1, idPeminjaman);
            try (ResultSet rs = psInfo.executeQuery()) {
                if (rs.next()) {
                    idKaryawan = rs.getString("id_karyawan");
                    idCustomer = rs.getString("id_customer");
                } else {
                    showAlert("Data Tidak Ditemukan", "ID Peminjaman tidak valid.");
                    return;
                }
            }

            String updateStatusPeminjaman = "UPDATE Transaksi_Peminjaman SET status = ? WHERE id_peminjaman = ?";
            String insertQuery = "INSERT INTO Transaksi_Pembayaran " +
                    "(id_pembayaran, id_peminjaman, id_karyawan, id_customer, metode_pembayaran, tanggal_pembayaran, total_denda, total_harga, uang_bayar, uang_kembalian) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement psInsert = conn.prepareStatement(insertQuery)) {
                psInsert.setString(1, idPembayaran);
                psInsert.setString(2, idPeminjaman);
                psInsert.setString(3, idKaryawan);
                psInsert.setString(4, idCustomer);
                psInsert.setString(5, metode);
                psInsert.setDate(6, java.sql.Date.valueOf(tanggalPembayaran));
                psInsert.setDouble(7, totalDenda);
                psInsert.setDouble(8, jumlahYangHarusDibayar);
                psInsert.setDouble(9, uangBayar);
                psInsert.setDouble(10, uangKembalian);

                psInsert.executeUpdate();

                try (PreparedStatement psUpdate = conn.prepareStatement(updateStatusPeminjaman)) {
                    psUpdate.setString(1, "Non-Aktif");
                    psUpdate.setString(2, idPeminjaman);
                    psUpdate.executeUpdate();
                }

                showAlert("Sukses", "Transaksi pembayaran berhasil disimpan.");
                if (homeKasirController != null) {
                    homeKasirController.showHomeTransactPanel();
                }
                Stage stage = (Stage) btnSubmit.getScene().getWindow();
                stage.close();
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Gagal menyimpan transaksi pembayaran: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private double parseRupiah(String teks) {
        try {
            String angka = teks.replaceAll("[^\\d]", "");
            return Double.parseDouble(angka);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @FXML
    private void handleBatalPembayaran() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}