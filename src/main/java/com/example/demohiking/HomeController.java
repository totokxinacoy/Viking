package com.example.demohiking;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private VBox pnItemsProduk = null;

    @FXML
    private VBox pnItemsDenda = null;

    @FXML
    private VBox pnItemsCustomer = null;

    @FXML
    private VBox pnItemsPaket = null;

    @FXML
    private VBox pnItemsTransaksi = null;

    @FXML
    private Button btnProduk;

    @FXML
    private Button btnPaket;

    @FXML
    private Button btnDenda;

    @FXML
    private Button btnCustomer;

    @FXML
    private Button btnTransaksi;

    @FXML
    private Pane pnlProduk;

    @FXML
    private Pane pnlPaket;

    @FXML
    private Pane pnlDenda;

    @FXML
    private Pane pnlCustomer;

    @FXML
    private Pane pnlTransaksi;

    //PRODUK ITEMS
    @FXML
    private TextField txtIDProduk;
    @FXML
    private TextField txtNama;
    @FXML
    private TextField cmbKategori;
    @FXML
    private TextField txtHarga;
    @FXML
    private TextField txtStock;
    @FXML
    private TextArea txtDeskripsi;

    public List<Produk> getDataProduk() {
        List<Produk> list = new ArrayList<>();
        DBConnect connection = new DBConnect();

        try (
                Connection conn = connection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT ID_Produk, Nama_Produk FROM produk")
        ) {
            while (rs.next()) {
                list.add(new Produk(
                        rs.getString("ID_Produk"),
                        rs.getString("Nama_Produk")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private void loadProdukItems() {
        List<Produk> dataProduk = getDataProduk();
        for (int i = 0; i < dataProduk.size(); i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemProduk.fxml"));
                Node node = loader.load();

                // Ambil controller dan isi data
                ItemController controller = loader.getController();
                controller.setData(dataProduk.get(i));

                final int j = i;
                node.setOnMouseEntered(event -> {
                    node.setStyle("-fx-background-color : #051036; -fx-background-radius : 15");
                });
                node.setOnMouseExited(event -> {
                    node.setStyle("-fx-background-color : #0D2857; -fx-background-radius : 15");
                });

                pnItemsProduk.getChildren().add(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loadProdukItems();

    }




    public void handleClicks(ActionEvent actionEvent) {
        if (actionEvent.getSource() == btnProduk) {
            pnlProduk.setStyle("-fx-background-color : #ffffff");
            pnlProduk.toFront();
        }
        if (actionEvent.getSource() == btnPaket) {
            pnlPaket.setStyle("-fx-background-color : #ffffff");
            pnlPaket.toFront();
        }
        if (actionEvent.getSource() == btnDenda) {
            pnlDenda.setStyle("-fx-background-color : #ffffff");
            pnlDenda.toFront();
        }
        if(actionEvent.getSource()==btnCustomer)
        {
            pnlCustomer.setStyle("-fx-background-color : #ffffff");
            pnlCustomer.toFront();
        }
        if(actionEvent.getSource()==btnTransaksi)
        {
            pnlTransaksi.setStyle("-fx-background-color : #ffffff");
            pnlTransaksi.toFront();
        }
    }

    @FXML
    protected void onAddProduk() {
        String idProduk = txtIDProduk.getText();
        String nama = txtNama.getText();
//        String kategori = (String) cmbKategori.getValue();
        String kategori = cmbKategori.getText();
        String desk = txtDeskripsi.getText();
        String status = "Aktif";

        int harga, stock;
        try {
            harga = Integer.parseInt(txtHarga.getText());
            stock = Integer.parseInt(txtStock.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Harga dan Stok harus berupa angka.");
            return;
        }

        String query = "INSERT INTO Produk VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            DBConnect connect = new DBConnect();
            Connection conn = connect.getConnection();
            PreparedStatement pstat = conn.prepareStatement(query);

            pstat.setString(1, idProduk);
            pstat.setString(2, nama);
            pstat.setString(3, kategori);
            pstat.setString(4, desk);
            pstat.setInt(5, harga);
            pstat.setInt(6, stock);
            pstat.setString(7, status);

            pstat.executeUpdate();
            pstat.close();
            conn.close();

            RefreshData();
            JOptionPane.showMessageDialog(null, "Insert data produk berhasil!");
        } catch (SQLException ex) {
            System.out.println("Terjadi error saat insert data produk: " + ex.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage());
        }
    }

    @FXML
    protected void onClearProduk() {
        RefreshData();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void RefreshData(){
        txtIDProduk.setText("");
        txtNama.setText("");
        cmbKategori.setText("");
        txtHarga.setText("");
        txtStock.setText("");
        txtDeskripsi.setText("");
    }
}
