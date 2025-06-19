package com.example.demohiking;


import com.example.demohiking.ADT.Customer;
import com.example.demohiking.Connection.DBConnect;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ItemCustomerController {
    @FXML private Label lblNama;
    @FXML private Label lblHarga;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;

    private Customer customer;
    private HomeController homeController;

    public void setHomeController(HomeController controller) {
        this.homeController = controller;
    }

    public void setData(Customer customer) {
        this.customer = customer;
        lblNama.setText(customer.getId());
        lblHarga.setText(customer.getNama());
    }

    @FXML
    private void handleUpdateButtonClick() {
        if (UpdateCustomerController.isWindowOpen()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Info");
            alert.setHeaderText(null);
            alert.setContentText("Form update sudah dibuka. Klik OK untuk membukanya kembali.");
            alert.showAndWait();

            // Setelah user menekan OK, tampilkan jendela yang sedang dibuka
            UpdateCustomerController.bringToFront();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateCustomer.fxml"));
            Parent root = loader.load();

            UpdateCustomerController controller = loader.getController();
            controller.setCustomer(customer);
            controller.setHomeController(homeController);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Update Customer");
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            // Simpan referensi stage
            UpdateCustomerController.setWindow(stage);

            stage.setOnCloseRequest(event -> UpdateCustomerController.clearWindow());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteButtonClick() {
        if (customer == null) {
            showAlert(Alert.AlertType.ERROR, "Kesalahan", "Customer tidak ditemukan.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText(null);
        confirm.setContentText("Apakah Anda yakin ingin menghapus Customer ini?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    DBConnect db = new DBConnect();
                    Connection conn = db.getConnection();

                    String query = "UPDATE Customer SET status = 'Non Aktif' WHERE ID_Customer = ?";
                    PreparedStatement pstat = conn.prepareStatement(query);
                    pstat.setString(1, customer.getId());

                    int rows = pstat.executeUpdate();
                    pstat.close();
                    conn.close();

                    if (rows > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Berhasil", "Customer telah dihapus.");

                        // Langsung panggil refresh tanpa menunggu konfirmasi tambahan
                        if (homeController != null) {
                            homeController.RefreshDataCustomer();
                        }
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Gagal", "Customer tidak ditemukan di database.");
                    }

                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
                }
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleSelectItem() {
        if (homeController != null && customer != null) {
            homeController.setDetailCustomer(customer); // Kirim data ke form
        }
    }

}
