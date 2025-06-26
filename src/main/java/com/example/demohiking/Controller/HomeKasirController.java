package com.example.demohiking.Controller;

import com.example.demohiking.ADT.*;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.*;


public class HomeKasirController implements Initializable {
    // IMAGE ALL
    @FXML
    private ImageView imgProduk;
    @FXML
    private ImageView imgCustomer;
    @FXML
    private ImageView imgDenda;
    private boolean isImageSelected = false;
    private File selectedImageFile;

    // VBOX ALL
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
    private VBox pnCartProduk = null;

    // HBOX ALL
    @FXML
    private HBox papanPaket = null;
    @FXML
    private HBox papanProduk = null;

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
    @FXML
    private Button btnAktifDenda;
    @FXML
    private ComboBox<String> cmbFilterStatusDenda;

    // PAKET ITEMS
    @FXML
    private Button btnItemPaket;
    @FXML
    private Button btnItemProduk;





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
                ResultSet rs = stmt.executeQuery("SELECT ID_Produk, Nama_Produk, Kategori, Harga, Stok FROM produk WHERE status = 'Aktif'");
        ) {
            while (rs.next()) {
                list.add(new Produk(
                        rs.getString("ID_Produk"),
                        rs.getString("Nama_Produk"),
                        rs.getString("Kategori"),
                        rs.getDouble("Harga"),
                        rs.getInt("Stok")
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

    private void loadProdukItemsTransact(List<Produk> dataProduk) {
        papanProduk.setVisible(true);
        papanPaket.setVisible(false);
        pnItemsPaket.getChildren().clear();


        for (int i = 0; i < dataProduk.size(); i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemTransactProduk.fxml"));
                Node node = loader.load();

                ItemTransactProdukController controller = loader.getController();
                controller.setData(dataProduk.get(i));
                controller.setHomeController(this); // <-- penting!

                final int j = i;
                node.setOnMouseEntered(event -> {
                    node.setStyle("-fx-background-color : #051036; -fx-background-radius : 15");
                });
                node.setOnMouseExited(event -> {
                    node.setStyle("-fx-background-color : #0D2857; -fx-background-radius : 15");
                });

                pnItemsPaket.getChildren().add(node);
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
                ResultSet rs = stmt.executeQuery("SELECT ID_Denda, Jenis_Denda, Nominal FROM Denda WHERE status = 'Aktif'");
        ) {
            while (rs.next()) {
                list.add(new Denda(
                        rs.getString("ID_Denda"),
                        rs.getString("Jenis_Denda"),
                        rs.getDouble("Nominal")
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

    /* --- PAKET METHOD --- */
    @FXML
    private void handleBtnItemProduk(MouseEvent event) {
        List<Produk> dataProduk = getDataProduk();
        loadProdukItemsTransact(dataProduk);
    }

    @FXML
    private void handleBtnItemPaket(MouseEvent event) {
        List<Paket> dataPaket = getDataPaket(); // ganti dengan data asli
        loadItemPaket(dataPaket);
    }

    private void loadItemPaket(List<Paket> dataPaket) {
        pnItemsPaket.getChildren().clear();
        papanPaket.setVisible(true);
        papanProduk.setVisible(false);
        pnItemsPaket.getChildren().clear();

        for (Paket paket : dataPaket) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemPaket.fxml"));
                Node node = loader.load();

                ItemPaketController controller = loader.getController();
                controller.setData(paket);
                controller.setHomeController(this); // opsional, kalau diperlukan

                // Hover effect (opsional)
                node.setOnMouseEntered(e -> node.setStyle("-fx-background-color: #051036; -fx-background-radius: 15"));
                node.setOnMouseExited(e -> node.setStyle("-fx-background-color: #0D2857; -fx-background-radius: 15"));

                pnItemsPaket.getChildren().add(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Paket> getDataPaket() {
        List<Paket> daftarPaket = new ArrayList<>();
        DBConnect db = new DBConnect();

        String query = "SELECT ID_Paket, Nama_Paket, Harga, Diskon FROM Paket";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("ID_Paket");
                String nama = rs.getString("Nama_Paket");
                double harga = rs.getDouble("Harga");
                double diskon = rs.getDouble("Diskon");

                int jumlah = 1;

                Paket paket = new Paket(id, nama, harga, diskon, jumlah);
                daftarPaket.add(paket);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return daftarPaket;
    }

//    public void setDetailPaket(Paket paket) {
//        if (paket == null) return;
//
//        lblNamaPaket.setText(paket.getNama());
//        lblHargaPaket.setText(String.format("Rp%,.0f", paket.getHarga()));
//        lblDiskon.setText(String.format("%.0f%%", paket.getDiskon() * 100));
//        lblJumlahPaket.setText(String.valueOf(paket.getJumlahPaket()));
//
//        // Tampilkan isi detail paket
//        vbDetailPaket.getChildren().clear();
//        if (paket.getIsiPaket() != null && !paket.getIsiPaket().isEmpty()) {
//            for (detailPaket detail : paket.getIsiPaket()) {
//                Label lbl = new Label("- " + detail.getIdProduk() + " x" + detail.getJumlah());
//                lbl.setStyle("-fx-text-fill: white; -fx-font-size: 13;");
//                vbDetailPaket.getChildren().add(lbl);
//            }
//        } else {
//            Label kosong = new Label("Tidak ada isi paket");
//            kosong.setStyle("-fx-text-fill: gray;");
//            vbDetailPaket.getChildren().add(kosong);
//        }
//
//         (Opsional) Simpan ke keranjang jika ingin langsung ditransaksikan
//         tambahKeKeranjangPaket(paket);
//    }

    private List<detailPaket> keranjang = new ArrayList<>();

    public void updateDetailProduk(detailPaket newItem) {
        boolean found = false;
        for (detailPaket item : keranjang) {
            if (item.getProduk().getId().equals(newItem.getProduk().getId())) {
                item.setJumlah(newItem.getJumlah());
                found = true;
                break;
            }
        }

        if (!found) {
            keranjang.add(newItem);
        }

        refreshKeranjangView();
    }

    public void refreshKeranjangView() {
        pnCartProduk.getChildren().clear();

        try {
            for (detailPaket item : keranjang) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemTransactProduk.fxml"));
                Node node = loader.load();


                ItemTransactProdukController controller = loader.getController();
                controller.setData(item.getProduk());
//                controller.setJumlah(item.getJumlah());
                pnCartProduk.getChildren().add(node);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        // Inisialisasi Component Denda
        cmbJenisDenda.getItems().addAll("Kehilangan", "Kerusakan", "Terlambat");
        loadDendaItems();
        txtIDDenda.setEditable(false);
        txtIDDenda.setText(generateDendaID());        btnSearchDenda.setDisable(true); // tombol nonaktif awalnya
        txtSearchDenda.textProperty().addListener((observable, oldValue, newValue) -> {
            btnSearchDenda.setDisable(newValue.trim().isEmpty());
        });
        txtSearchDenda.setDisable(false);

        // Inisialisasi Component Paket
        Platform.runLater(() -> {
            papanPaket.setVisible(false);
            papanProduk.setVisible(false);
            pnItemsPaket.getChildren().clear();
        });

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

    @FXML
    private void ScaleUpPaket(MouseEvent event) {
        btnItemPaket.setScaleX(0.95);
        btnItemPaket.setScaleY(0.95);
    }

    @FXML
    private void ScaleDownPaket(MouseEvent event) {
        btnItemPaket.setScaleX(1.0);
        btnItemPaket.setScaleY(1.0);
    }

    @FXML
    private void ScaleUpProduk(MouseEvent event) {
        btnItemProduk.setScaleX(0.95);
        btnItemProduk.setScaleY(0.95);
    }

    @FXML
    private void ScaleDownProduk(MouseEvent event) {
        btnItemProduk.setScaleX(1.0);
        btnItemProduk.setScaleY(1.0);
    }

    @FXML
    private void ScaleUpDenda(MouseEvent event) {
        btnAktifDenda.setScaleX(0.95);
        btnAktifDenda.setScaleY(0.95);
    }

    @FXML
    private void ScaleDownDenda(MouseEvent event) {
        btnAktifDenda.setScaleX(1.0);
        btnAktifDenda.setScaleY(1.0);
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
                        rs.getString("Nama_Produk"),
                        rs.getString("Kategori"),
                        rs.getDouble("Harga"),
                        rs.getInt("Stok")
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
        List<Denda> foundList = new ArrayList<>();
        DBConnect connect = new DBConnect();
        try (

                Connection conn = connect.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)
        ) {
            pstmt.setString(1, keyword);
            pstmt.setString(2, "%" + keyword + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Denda denda = new Denda(
                            rs.getString("ID_Denda"),
                            rs.getString("Jenis_Denda"),
                            rs.getDouble("Nominal")
                    );
                    foundList.add(denda);
                }
            }

            if (!foundList.isEmpty()) {
                setDetailDenda(foundList.get(0));
                loadDendaItems(foundList);
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Pencarian Denda", "Denda tidak ditemukan.");
                txtSearchDenda.clear();
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Kesalahan Database", "Gagal memuat data: " + e.getMessage());
        }
    }

    private Denda getDendaById(String id) {
        String query = "SELECT * FROM Denda WHERE ID_Denda = ?";
        try (
                Connection conn = new DBConnect().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)
        ) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Denda(
                        rs.getString("ID_Denda"),
                        rs.getString("Jenis_Denda"),
                        rs.getDouble("Nominal")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @FXML
    private void handleAktifkanDenda(MouseEvent event) {
        String id = txtSearchDenda.getText().trim();

        if (id.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Aktivasi Denda", "ID Denda tidak boleh kosong.");
            return;
        }

        String checkQuery = "SELECT Status FROM Denda WHERE ID_Denda = ?";
        String updateQuery = "UPDATE Denda SET Status = 'Aktif' WHERE ID_Denda = ?";

        DBConnect connect = new DBConnect();
        Connection conn = null;
        PreparedStatement pstmtCheck = null;
        PreparedStatement pstmtUpdate = null;

        try {
            conn = connect.getConnection();

            pstmtCheck = conn.prepareStatement(checkQuery);
            pstmtCheck.setString(1, id);
            ResultSet rs = pstmtCheck.executeQuery();

            if (rs.next()) {
                String status = rs.getString("Status");
                if ("Aktif".equalsIgnoreCase(status)) {
                    showAlert(Alert.AlertType.INFORMATION, "Status Sudah Aktif", "Denda sudah aktif. Tidak dapat diaktifkan ulang.");
                    return;
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "ID Tidak Ditemukan", "ID Denda tidak ditemukan.");
                return;
            }

            // Update jika lolos validasi
            pstmtUpdate = conn.prepareStatement(updateQuery);
            pstmtUpdate.setString(1, id);

            int rowsAffected = pstmtUpdate.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Aktivasi Berhasil", "Denda berhasil diaktifkan kembali.");
                loadDendaItems();
            } else {
                showAlert(Alert.AlertType.WARNING, "Aktivasi Gagal", "Gagal mengaktifkan denda.");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Kesalahan Database", "Terjadi kesalahan: " + e.getMessage());
        } finally {
            try {
                if (pstmtCheck != null) pstmtCheck.close();
                if (pstmtUpdate != null) pstmtUpdate.close();
                if (conn != null && !conn.isClosed()) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
