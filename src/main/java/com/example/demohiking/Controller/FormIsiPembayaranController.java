package com.example.demohiking.Controller;

import com.example.demohiking.ADT.detailPeminjaman;
import com.example.demohiking.Connection.DBConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    private List<detailPeminjaman> itemDalamPembayaran;
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
                    continue; // Skip jika tidak punya nama produk/paket
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

}
