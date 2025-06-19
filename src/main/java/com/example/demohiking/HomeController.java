package com.example.demohiking;

import com.example.demohiking.ADT.Customer;
import com.example.demohiking.Connection.DBConnect;
import com.example.demohiking.ADT.Produk;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    @FXML
    private ImageView imgProduk;

    @FXML
    private ImageView imgCustomer;

    private File selectedImageFile;

    @FXML
    private RadioButton rdLaki;
    @FXML
    private RadioButton rdPerempuan;
    @FXML
    private ToggleGroup genderGroup;

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
    private Button btnLogout;

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
    private ComboBox<String> cmbKategori;
    @FXML
    private TextField txtHarga;
    @FXML
    private TextField txtStock;
    @FXML
    private TextArea txtDeskripsi;

    //CUSTOMER ITEMS
    @FXML
    private TextField txtIDCustomer;
    @FXML
    private TextField txtNamaCustomer;
    @FXML
    private ComboBox<String> cmbJenisKelamin;
    @FXML
    private TextField txtNoTelephone;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextArea txtAlamat;

    /* --- PRODUK METHOD --- */
    private String generateProdukID() {
        String id = "PRD001";
        String query = "SELECT MAX(ID_Produk) as max_id FROM Produk";

        try {
            DBConnect connect = new DBConnect();
            Connection conn = connect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                String maxID = rs.getString("max_id");

                // Validasi agar tidak error saat null atau format tidak sesuai
                if (maxID != null && maxID.length() >= 4) {
                    String numberPart = maxID.substring(3);
                    if (!numberPart.isEmpty() && numberPart.matches("\\d+")) {
                        int nextID = Integer.parseInt(numberPart) + 1;
                        id = String.format("PRD%03d", nextID);
                    }
                }
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }

    public List<Produk> getDataProduk() {
        List<Produk> list = new ArrayList<>();
        DBConnect connection = new DBConnect();

        try (
                Connection conn = connection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT ID_Produk, Nama_Produk FROM produk WHERE status = 'Aktif'");
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
        pnItemsProduk.getChildren().clear();
        List<Produk> dataProduk = getDataProduk();

        for (int i = 0; i < dataProduk.size(); i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemProduk.fxml"));
                Node node = loader.load();

                ItemProdukController controller = loader.getController();
                controller.setData(dataProduk.get(i));
                controller.setHomeController(this); // <-- penting!

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

    public void setDetailProduk(Produk produk) {
        txtIDProduk.setText(produk.getId());
        txtNama.setText(produk.getNama());
        cmbKategori.setValue(produk.getKategori());
        txtDeskripsi.setText(produk.getDeskripsi());
        txtHarga.setText(String.valueOf((int) produk.getHarga()));
        txtStock.setText(String.valueOf(produk.getStok()));
    }


    /* --- CUSTOMER METHOD --- */
    private String generateCustomerID() {
        String id = "CST001";
        String query = "SELECT MAX(ID_Customer) as max_id FROM Customer";

        try {
            DBConnect connect = new DBConnect();
            Connection conn = connect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
                String maxID = rs.getString("max_id");

                // Validasi agar tidak error saat null atau format tidak sesuai
                if (maxID != null && maxID.length() >= 4) {
                    String numberPart = maxID.substring(3);
                    if (!numberPart.isEmpty() && numberPart.matches("\\d+")) {
                        int nextID = Integer.parseInt(numberPart) + 1;
                        id = String.format("CST%03d", nextID);
                    }
                }
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }

    public List<Customer> getDataCustomer() {
        List<Customer> list = new ArrayList<>();
        DBConnect connection = new DBConnect();

        try (
                Connection conn = connection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT ID_Customer, Nama_Customer FROM Customer WHERE status = 'Aktif'");
        ) {
            while (rs.next()) {
                list.add(new Customer(
                        rs.getString("ID_Customer"),
                        rs.getString("Nama_Customer")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private void loadCustomerItems() {
        pnItemsCustomer.getChildren().clear();
        List<Customer> dataCustomer = getDataCustomer();

        for (int i = 0; i < dataCustomer.size(); i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemCustomer.fxml"));
                Node node = loader.load();

                ItemCustomerController controller = loader.getController();
                controller.setData(dataCustomer.get(i));
                controller.setHomeController(this); // <-- penting!

                final int j = i;
                node.setOnMouseEntered(event -> {
                    node.setStyle("-fx-background-color : #051036; -fx-background-radius : 15");
                });
                node.setOnMouseExited(event -> {
                    node.setStyle("-fx-background-color : #0D2857; -fx-background-radius : 15");
                });

                pnItemsCustomer.getChildren().add(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setDetailCustomer(Customer customer) {
        txtIDCustomer.setText(customer.getId());
        txtNamaCustomer.setText(customer.getNama());
        cmbJenisKelamin.setValue(customer.getJeniskelamin());
        txtAlamat.setText(customer.getAlamat());
        txtNoTelephone.setText(customer.getNomortelephone());
        txtEmail.setText(customer.getEmail());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inisalisasi Component Produk
        cmbKategori.getItems().addAll("Tas", "Sepatu", "Aksessoris", "Pakaian", "Tenda");
        loadProdukItems();
        txtIDProduk.setEditable(false);
        txtIDProduk.setText(generateProdukID());

        // Inisalisasi Component Customer
        loadCustomerItems();
        txtIDCustomer.setEditable(false);
        txtIDCustomer.setText(generateCustomerID());
        genderGroup = new ToggleGroup();
        rdLaki.setToggleGroup(genderGroup);
        rdPerempuan.setToggleGroup(genderGroup);

        // Tunda pengecekan session sampai setelah UI tampil
        Platform.runLater(this::cekSession);
    }

    private void cekSession() {
        if (!Session.isLoggedIn()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
                Parent root = loader.load();

                Stage loginStage = new Stage();
                loginStage.setScene(new Scene(root));
                loginStage.initStyle(StageStyle.UNDECORATED);
                loginStage.setTitle("Login");
                loginStage.show();

                Stage currentStage = (Stage) btnLogout.getScene().getWindow();
                currentStage.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    // GENERAL METHOD
    @FXML
    private void handleLogout() {
        Session.clearSession();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();

            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.initStyle(StageStyle.UNDECORATED);
            loginStage.setTitle("Login Kembali");
            loginStage.show();

            // Tutup Home
            Stage currentStage = (Stage) btnLogout.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    /* --- PRODUK CRUD --- */
    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Gambar Produk");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Gambar", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            Image image = new Image(file.toURI().toString());
            imgProduk.setImage(image);
        }
    }


    @FXML
    protected void onAddProduk() {
        String idProduk = txtIDProduk.getText().trim();
        String nama = txtNama.getText().trim();
        String kategori = cmbKategori.getValue();
        String desk = txtDeskripsi.getText().trim();
        String hargaStr = txtHarga.getText().trim();
        String stockStr = txtStock.getText().trim();
        String status = "Aktif";

        if (idProduk.isEmpty() || nama.isEmpty() || kategori == null || kategori.isEmpty()
                || desk.isEmpty() || hargaStr.isEmpty() || stockStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi Input", "Semua field harus diisi!");
            return;
        }

        // Validasi gambar
        if (selectedImageFile == null) {
            showAlert(Alert.AlertType.WARNING, "Validasi Gambar", "Silakan pilih gambar produk terlebih dahulu.");
            return;
        }

        // Validasi angka
        int harga, stock;
        try {
            harga = Integer.parseInt(hargaStr);
            stock = Integer.parseInt(stockStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Harga dan Stok harus berupa angka.");
            return;
        }

        // Insert Produk
        String query = "INSERT INTO Produk VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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
            InputStream imageStream = new FileInputStream(selectedImageFile);
            pstat.setBinaryStream(8, imageStream, (int) selectedImageFile.length());

            pstat.executeUpdate();
            pstat.close();
            conn.close();

            RefreshData();
            JOptionPane.showMessageDialog(null, "Insert data produk berhasil!");
        } catch (SQLException ex) {
            System.out.println("Terjadi error saat insert data produk: " + ex.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    protected void onClearProduk() {
        RefreshData();
    }


    public void RefreshData() {
        txtIDProduk.setText(generateProdukID());
        txtNama.setText("");
        cmbKategori.setValue("");
        txtHarga.setText("");
        txtStock.setText("");
        txtDeskripsi.setText("");
        imgProduk.setImage(null);
        loadProdukItems();
    }

    /* --- CUSTOMER CRUD --- */
    @FXML
    private void handleChooseImageCustomer() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Gambar Customer");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Gambar", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            Image image = new Image(file.toURI().toString());
            imgCustomer.setImage(image);
        }
    }


    @FXML
    protected void onAddCustomer() {
        String idCustomer = txtIDCustomer.getText().trim();
        String nama = txtNamaCustomer.getText().trim();
        String noTelp = txtNoTelephone.getText().trim();
        String email = txtEmail.getText().trim();
        String alamat = txtAlamat.getText().trim();
        String status = "Aktif";

        // Validasi pilihan jenis kelamin dari radio button
        String jenisKelamin = "";
        if (rdLaki.isSelected()) {
            jenisKelamin = "Laki-laki";
        } else if (rdPerempuan.isSelected()) {
            jenisKelamin = "Perempuan";
        }

        // Validasi input kosong
        if (idCustomer.isEmpty() || nama.isEmpty() || jenisKelamin.isEmpty()
                || noTelp.isEmpty() || email.isEmpty() || alamat.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi Input", "Semua field harus diisi!");
            return;
        }

        // Validasi gambar
        if (selectedImageFile == null) {
            showAlert(Alert.AlertType.WARNING, "Validasi Gambar", "Silakan pilih foto customer terlebih dahulu.");
            return;
        }

        // Validasi nomor telepon
        if (!noTelp.matches("\\d{10,15}")) {
            showAlert(Alert.AlertType.WARNING, "Validasi Nomor", "Nomor telepon harus terdiri dari 10–15 digit angka.");
            return;
        }

        // Validasi email
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
            showAlert(Alert.AlertType.WARNING, "Validasi Email", "Format email tidak valid (contoh: user@example.com).");
            return;
        }

        // Simpan ke database
        String query = "INSERT INTO Customer VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            DBConnect connect = new DBConnect();
            Connection conn = connect.getConnection();
            PreparedStatement pstat = conn.prepareStatement(query);

            pstat.setString(1, idCustomer);
            pstat.setString(2, nama);
            pstat.setString(3, jenisKelamin);
            pstat.setString(4, noTelp);
            pstat.setString(5, email);
            pstat.setString(6, alamat);
            pstat.setString(7, status);

            InputStream fotoStream = new FileInputStream(selectedImageFile);
            pstat.setBinaryStream(8, fotoStream, (int) selectedImageFile.length());

            pstat.executeUpdate();
            pstat.close();
            conn.close();

            RefreshDataCustomer();
            JOptionPane.showMessageDialog(null, "Data customer berhasil ditambahkan!");

        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage());
        } catch (FileNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "File Foto Tidak Ditemukan", e.getMessage());
        }
    }

    @FXML
    protected void onClearCustomer() {
        RefreshDataCustomer();
    }


    public void RefreshDataCustomer() {
        txtIDCustomer.setText(generateCustomerID());
        txtNamaCustomer.setText("");
        cmbJenisKelamin.setValue("");
        txtNoTelephone.setText("");
        txtEmail.setText("");
        txtAlamat.setText("");
        imgProduk.setImage(null);
        loadCustomerItems();
    }
}
