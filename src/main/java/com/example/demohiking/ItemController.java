package com.example.demohiking;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;


    public class ItemController  {
        @FXML private Label lblNama;
        @FXML private Label lblHarga;

        public void setData(Produk produk) {
            lblNama.setText(produk.getNama());
            lblHarga.setText(produk.getHarga());
        }
    }

