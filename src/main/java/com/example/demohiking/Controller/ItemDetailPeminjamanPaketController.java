package com.example.demohiking.Controller;

import com.example.demohiking.ADT.Paket;
import com.example.demohiking.ADT.detailPeminjaman;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ItemDetailPeminjamanPaketController {

    @FXML
    private HBox Item1;

    @FXML
    private Label lblIDPaket;

    @FXML
    private Label lblNamaPaket;

    @FXML
    private Label lblJumlah;

    @FXML
    private Label lblTotalHarga;

    private Paket paket;
    private detailPeminjaman item;
    private int jumlah;

    private HomeKasirController homeKasirController;

    public void setHomeController(HomeKasirController controller) {
        this.homeKasirController = controller;
    }

    public void setData(detailPeminjaman item, int nomorUrut) {
        this.item = item;
        this.jumlah = item.getJumlah();
        this.paket = item.getPaket();

        lblIDPaket.setText(String.valueOf(nomorUrut));
        lblNamaPaket.setText(paket.getNama());
        lblJumlah.setText("x" + jumlah);

        double total = jumlah * paket.getHarga();
        lblTotalHarga.setText(String.format("Total : Rp. %,.0f", total));

//        Item1.setPrefWidth(800);
//        Item1.setMaxWidth(600);
//        HBox.setMargin(lblTotalHarga, new Insets(0, 0, 0, 5));
    }
}