package com.example.demohiking.Controller;

import com.example.demohiking.ADT.Customer;
import com.example.demohiking.ADT.Denda;
import com.example.demohiking.ADT.Produk;
import com.example.demohiking.Connection.DBConnect;
import com.example.demohiking.Session;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    // IMAGE ALL
    @FXML
    private ImageView imgProduk;
    @FXML
    private ImageView imgCustomer;
    @FXML
    private ImageView imgDenda;
    private boolean isImageSelected = false;
    private File selectedImageFile;

    // ITEM ALL
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

    // BUTTON MENU ALL
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

    // PANEL ALL
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

    // PRODUK ITEMS
    @FXML
    private Button btnSearchProduk;
    @FXML
    private TextField txtSearchProduk;
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

    // CUSTOMER ITEMS
    @FXML
    private Button btnSearchCustomer;
    @FXML
    private TextField txtSearchCustomer;
    @FXML
    private TextField txtIDCustomer;
    @FXML
    private TextField txtNamaCustomer;
//    @FXML
//    private ComboBox<String> cmbJenisKelamin;
    @FXML
    private TextField txtNoTelephone;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextArea txtAlamat;
    @FXML
    private RadioButton rdLaki;
    @FXML
    private RadioButton rdPerempuan;
    @FXML
    private ToggleGroup genderGroup;

    // DENDA ITEMS
    @FXML
    private Button btnSearchDenda;
    @FXML
    private TextField txtSearchDenda;
    @FXML
    private TextField txtIDDenda;
    @FXML
    private ComboBox<String> cmbJenisDenda;
    @FXML
    private TextArea txtDeskripsiDenda;
    @FXML
    private TextField txtNominal;



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
        List<Produk> dataProduk = getDataProduk();
        loadProdukItems(dataProduk);
    }

    private void loadProdukItems(List<Produk> dataProduk) {
        pnItemsProduk.getChildren().clear();

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


    /* --- DENDA METHOD --- */
    private String generateDendaID() {
        String id = "DND001";
        String query = "SELECT MAX(ID_Denda) as max_id FROM Denda";

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
                        id = String.format("DND%03d", nextID);
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

    public List<Denda> getDataDenda() {
        List<Denda> list = new ArrayList<>();
        DBConnect connection = new DBConnect();

        try (
                Connection conn = connection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT ID_Denda, Jenis_Denda FROM Denda WHERE status = 'Aktif'");
        ) {
            while (rs.next()) {
                list.add(new Denda(
                        rs.getString("ID_Denda"),
                        rs.getString("Jenis_Denda")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private void loadDendaItems() {
        List<Denda> dataDenda = getDataDenda();
        loadDendaItems(dataDenda);
    }

    private void loadDendaItems(List<Denda> dataDenda) {
        pnItemsDenda.getChildren().clear();

        for (int i = 0; i < dataDenda.size(); i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemDenda.fxml"));
                Node node = loader.load();

                ItemDendaController controller = loader.getController();
                controller.setData(dataDenda.get(i));
                controller.setHomeController(this);

                final int j = i;
                node.setOnMouseEntered(event -> {
                    node.setStyle("-fx-background-color : #051036; -fx-background-radius : 15");
                });
                node.setOnMouseExited(event -> {
                    node.setStyle("-fx-background-color : #0D2857; -fx-background-radius : 15");
                });

                pnItemsDenda.getChildren().add(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setDetailDenda(Denda denda) {
        txtIDDenda.setText(denda.getId());
        cmbJenisDenda.setValue(denda.getJenis());
        txtDeskripsiDenda.setText(denda.getDeskripsi());
        txtNominal.setText(String.valueOf((int) denda.getNominal()));
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
        List<Customer> dataCustomer = getDataCustomer();
        loadCustomerItems(dataCustomer);
    }

    private void loadCustomerItems(List<Customer> dataCustomer) {
        pnItemsCustomer.getChildren().clear();

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

        // Pilih radio button berdasarkan jenis kelamin
        if ("Laki-laki".equalsIgnoreCase(customer.getJeniskelamin())) {
            rdLaki.setSelected(true);
        } else if ("Perempuan".equalsIgnoreCase(customer.getJeniskelamin())) {
            rdPerempuan.setSelected(true);
        }

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
        txtSearchProduk.textProperty().addListener((observable, oldValue, newValue) -> {
            btnSearchProduk.setDisable(newValue.trim().isEmpty());
        });
        btnSearchProduk.setDisable(true);

        // Inisalisasi Component Customer
        loadCustomerItems();
        txtIDCustomer.setEditable(false);
        txtIDCustomer.setText(generateCustomerID());
        genderGroup = new ToggleGroup();
        rdLaki.setToggleGroup(genderGroup);
        rdPerempuan.setToggleGroup(genderGroup);
        txtSearchCustomer.textProperty().addListener((observable, oldValue, newValue) -> {
            btnSearchCustomer.setDisable(newValue.trim().isEmpty());
        });
        btnSearchCustomer.setDisable(true);

        // Inisialisasi ComboBox jenis denda
        cmbJenisDenda.getItems().addAll("Kehilangan", "Kerusakan", "Terlambat");
        loadDendaItems();
        txtIDDenda.setEditable(false);
        txtIDDenda.setText(generateDendaID());        btnSearchDenda.setDisable(true); // tombol nonaktif awalnya
        txtSearchDenda.textProperty().addListener((observable, oldValue, newValue) -> {
            btnSearchDenda.setDisable(newValue.trim().isEmpty());
        });
        txtSearchDenda.setDisable(false);

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
        } else {
            System.out.println("Login sebagai: " + Session.getNama() + " (" + Session.getRole() + ")");
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
        if (isImageSelected) {
            showAlert(Alert.AlertType.INFORMATION, "Gambar Sudah Dipilih", "Gambar hanya bisa dipilih satu kali dalam sesi ini.");
            return;
        }

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
            isImageSelected = true;
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

    @FXML
    private void handleSearchProduk(ActionEvent event) {
        String keyword = txtSearchProduk.getText().trim();

        String query = "SELECT * FROM Produk WHERE (ID_Produk = ? OR LOWER(Nama_Produk) LIKE ?) AND status = 'Aktif'";
        try {
            DBConnect connect = new DBConnect();
            Connection conn = connect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, keyword);
            pstmt.setString(2, "%" + keyword.toLowerCase() + "%");

            ResultSet rs = pstmt.executeQuery();
            List<Produk> foundList = new ArrayList<>();

            while (rs.next()) {
                Produk produk = new Produk(
                        rs.getString("ID_Produk"),
                        rs.getString("Nama_Produk")
                );
                foundList.add(produk);
            }

            if (!foundList.isEmpty()) {
                setDetailProduk(foundList.get(0)); // tampilkan detail pertama
                loadProdukItems(foundList);        // tampilkan hasil pencarian
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Pencarian Produk");
                alert.setHeaderText(null);
                alert.setContentText("Produk tidak ditemukan.");
                alert.showAndWait();
                txtSearchProduk.clear();
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void RefreshData() {
        txtIDProduk.setText(generateProdukID());
        txtNama.setText("");
        cmbKategori.setValue("");
        txtHarga.setText("");
        txtStock.setText("");
        txtDeskripsi.setText("");
        isImageSelected = false;
        selectedImageFile = null;
        imgProduk.setImage(null);
        loadProdukItems();
    }


    /* --- CUSTOMER CRUD --- */
    @FXML
    private void handleChooseImageCustomer() {
        if (isImageSelected) {
            showAlert(Alert.AlertType.INFORMATION, "Gambar Sudah Dipilih", "Gambar hanya bisa dipilih satu kali dalam sesi ini.");
            return;
        }

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
            isImageSelected = true;
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

    @FXML
    private void handleSearchCustomer(ActionEvent event) {
        String keyword = txtSearchCustomer.getText().trim();

        String query = "SELECT * FROM Customer WHERE (ID_Customer = ? OR LOWER(Nama_Customer) LIKE ?) AND status = 'Aktif'";
        try {
            DBConnect connect = new DBConnect();
            Connection conn = connect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, keyword);
            pstmt.setString(2, "%" + keyword.toLowerCase() + "%");

            ResultSet rs = pstmt.executeQuery();
            List<Customer> foundList = new ArrayList<>();

            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getString("ID_Customer"),
                        rs.getString("Nama_Customer")
                );
                foundList.add(customer);
            }

            if (!foundList.isEmpty()) {
                setDetailCustomer(foundList.get(0)); // tampilkan detail pertama
                loadCustomerItems(foundList);        // tampilkan hasil pencarian
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Pencarian Customer");
                alert.setHeaderText(null);
                alert.setContentText("Customer tidak ditemukan.");
                alert.showAndWait();
                txtSearchCustomer.clear();
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void RefreshDataCustomer() {
        txtIDCustomer.setText(generateCustomerID());
        txtNamaCustomer.setText("");
        genderGroup.selectToggle(null);
        txtNoTelephone.setText("");
        txtEmail.setText("");
        txtAlamat.setText("");
        isImageSelected = false;
        selectedImageFile = null;
        imgCustomer.setImage(null);
        loadCustomerItems();
    }


    /* --- DENDA CRUD --- */
    @FXML
    private void handleChooseImageDenda() {
        if (isImageSelected) {
            showAlert(Alert.AlertType.INFORMATION, "Gambar Sudah Dipilih", "Gambar hanya bisa dipilih satu kali dalam sesi ini.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Gambar Denda");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Gambar", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            Image image = new Image(file.toURI().toString());
            imgDenda.setImage(image);
            isImageSelected = true; // lock sampai form direset
        }
    }

    @FXML
    protected void onAddDenda() {
        String idDenda = txtIDDenda.getText().trim();
        String jenisDenda = cmbJenisDenda.getValue();
        String deskripsi = txtDeskripsiDenda.getText().trim();
        String nominalStr = txtNominal.getText().trim();

        if (idDenda.isEmpty() || jenisDenda == null || deskripsi.isEmpty() || nominalStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi Input", "Semua field harus diisi!");
            return;
        }

        if (selectedImageFile == null) {
            showAlert(Alert.AlertType.WARNING, "Validasi Gambar", "Silakan pilih gambar terlebih dahulu.");
            return;
        }

        double nominal;
        try {
            nominal = Double.parseDouble(nominalStr);
            if (nominal < 0) {
                showAlert(Alert.AlertType.WARNING, "Validasi Nominal", "Nominal tidak boleh negatif.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validasi Nominal", "Nominal harus berupa angka.");
            return;
        }

        String query = "INSERT INTO Denda (ID_Denda, Jenis_Denda, Deskripsi, Nominal, Image) VALUES (?, ?, ?, ?, ?)";

        try {
            DBConnect connect = new DBConnect();
            Connection conn = connect.getConnection();
            PreparedStatement pstat = conn.prepareStatement(query);

            pstat.setString(1, idDenda);
            pstat.setString(2, jenisDenda);
            pstat.setString(3, deskripsi);
            pstat.setDouble(4, nominal);

            InputStream imgStream = new FileInputStream(selectedImageFile);
            pstat.setBinaryStream(5, imgStream, (int) selectedImageFile.length());

            pstat.executeUpdate();
            pstat.close();
            conn.close();

            RefreshDataDenda();
            JOptionPane.showMessageDialog(null, "Data denda berhasil ditambahkan!");

        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage());
        } catch (FileNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "File Gambar Tidak Ditemukan", e.getMessage());
        }
    }

    @FXML
    private void handleSearchDenda(ActionEvent event) {
        String keyword = txtSearchDenda.getText().trim().toLowerCase();

        String query = "SELECT * FROM Denda WHERE LOWER(ID_Denda) = ? OR LOWER(Jenis_Denda) LIKE ?";
        try {
            DBConnect connect = new DBConnect();
            Connection conn = connect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, keyword);
            pstmt.setString(2, "%" + keyword + "%");

            ResultSet rs = pstmt.executeQuery();
            List<Denda> foundList = new ArrayList<>();

            while (rs.next()) {
                Denda denda = new Denda(
                        rs.getString("ID_Denda"),
                        rs.getString("Jenis_Denda")
                );
                foundList.add(denda);
            }

            if (!foundList.isEmpty()) {
                setDetailDenda(foundList.get(0)); // tampilkan detail pertama
                loadDendaItems(foundList);        // tampilkan hasil pencarian dalam list/tabel
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Pencarian Denda");
                alert.setHeaderText(null);
                alert.setContentText("Denda tidak ditemukan.");
                alert.showAndWait();
                txtSearchDenda.clear();
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    protected void onClearDenda() {
        RefreshDataDenda();
    }

    public void RefreshDataDenda() {
        txtIDDenda.setText(generateDendaID());
        cmbJenisDenda.getSelectionModel().clearSelection();
        txtDeskripsiDenda.clear();
        txtNominal.clear();
        isImageSelected = false;
        imgDenda.setImage(null);
        selectedImageFile = null;
        loadDendaItems();
    }
}
