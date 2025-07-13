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


    public void setInformasiPeminjaman(String idPeminjaman, String namaCustomer, String namaKaryawan) {
        generatePembayaranID();
        txtIDPeminjaman.setText(idPeminjaman);
        txtNamaCustomer.setText(namaCustomer);
        txtNamaKaryawan.setText(namaKaryawan);
    }

//    private void tampilkanDetailPeminjaman(String idPeminjaman) {
//        ObservableList<String> items = FXCollections.observableArrayList();
//
//        String query = "SELECT dp.jumlah, pr.nama_produk, pk.nama_paket " +
//                "FROM Detail_Peminjaman dp " +
//                "LEFT JOIN Produk pr ON dp.id_produk = pr.id_produk " +
//                "LEFT JOIN Paket pk ON dp.id_paket = pk.id_paket " +
//                "WHERE dp.id_peminjaman = '" + idPeminjaman + "'";
//
//        try (Connection conn = new DBConnect().getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//
//            while (rs.next()) {
//                int jumlah = rs.getInt("jumlah");
//                String namaProduk = rs.getString("nama_produk");
//                String namaPaket = rs.getString("nama_paket");
//
//                String namaItem;
//                if (namaProduk != null) {
//                    namaItem = namaProduk + " x" + jumlah;
//                } else if (namaPaket != null) {
//                    namaItem = namaPaket + " x" + jumlah;
//                } else {
//                    continue; // skip jika null semua
//                }
//
//                items.add(namaItem);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        listPeminjaman.setItems(items);
//    }

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

}
