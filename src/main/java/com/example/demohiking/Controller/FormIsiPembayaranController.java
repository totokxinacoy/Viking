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

    public void setHomeController(HomeKasirController controller) {
        this.homeKasirController = controller;
    }

    public void setItemDalamPembayaran(List<detailPeminjaman> item) {
        this.itemDalamPembayaran = item;
        tampilkanItemDalamListView();
        generatePembayaranID();
    }

    private void tampilkanItemDalamListView() {
        ObservableList<String> items = FXCollections.observableArrayList();

        for (detailPeminjaman dp : itemDalamPembayaran) {
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
    private void generatePembayaranID() {
        String id = "PMB001";
        String query = "SELECT MAX(id_pembayaran) as max_id FROM Transaksi_Peminjaman";

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
}
