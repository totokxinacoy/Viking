package com.example.demohiking.Controller;

import com.example.demohiking.ADT.Paket;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class ItemPeminjamanPaketController {
    @FXML
    private HBox Item1;

    @FXML
    private Label lblID;

    @FXML
    private Label lblNama;

    @FXML
    private Label lblStok;

    @FXML
    private Label lblTotalHarga;

    @FXML
    private Label lblHarga;

    @FXML
    private Button btnTambah;

    @FXML
    private Button btnKurang;

    private int jumlah = 0;
    private boolean isKeranjangView = false;
    private Paket paket;

    private HomeKasirController homeKasirController;

    public void setHomeController(HomeKasirController controller) {
        this.homeKasirController = controller;
    }

    public void setData(Paket paket) {
        this.paket = paket;
        this.jumlah = 0;

        lblID.setText(paket.getId() != null ? paket.getId() : "-");
        lblNama.setText(paket.getNama() != null ? paket.getNama() : "-");
        lblStok.setText(String.valueOf(paket.getStok()));
        lblHarga.setText(String.format("Rp. %,.0f", paket.getHarga()));
        lblHarga.setVisible(true);
        lblTotalHarga.setVisible(false);

        btnTambah.setVisible(true);
        btnKurang.setDisable(true);
    }
}
