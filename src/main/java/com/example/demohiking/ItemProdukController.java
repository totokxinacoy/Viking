package com.example.demohiking;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;


public class ItemProdukController {
        @FXML private Label lblNama;
        @FXML private Label lblHarga;
        @FXML private Button btnUpdate;

        private Produk produk;
        private HomeController homeController;

        public void setData(Produk produk) {
            this.produk = produk;
            lblNama.setText(produk.getId());
            lblHarga.setText(produk.getNama());
        }


        @FXML
        private void handleUpdateButtonClick() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateProduk.fxml"));
                Parent root = loader.load();

                UpdateProdukController controller = loader.getController();
                controller.setProduk(produk); // kirim data ke popup

                Stage stage = new Stage();
                stage.setTitle("Update Produk");
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @FXML
        private void handleSelectItem() {
            if (homeController != null && produk != null) {
                homeController.setDetailProduk(produk); // Kirim data ke form
            }
        }

    }


