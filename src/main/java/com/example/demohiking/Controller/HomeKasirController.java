package com.example.demohiking.Controller;

import com.example.demohiking.ADT.*;
import com.example.demohiking.Connection.DBConnect;
import com.example.demohiking.Session;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private ImageView imgCekCustomer;
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
    private VBox pnItemsCekCustomer = null;
    @FXML
    private VBox pnItemsPaket = null;
    @FXML
    private VBox pnItemsHomePaket = null;
    @FXML
    private VBox pnCartProduk = null;
    @FXML
    private VBox pnCartPeminjaman = null;
    @FXML
    private VBox pnPeminjamanProduk = null;
    @FXML
    private VBox pnPeminjamanPaket = null;

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
    private Button btnNewPaket;
    @FXML
    private Button btnDenda;
    @FXML
    private Button btnCustomer;
    @FXML
    private Button btnTransaksi;
    @FXML
    private Button btnPeminjaman;
    @FXML
    private Button btnPembayaran;
    @FXML
    private Button btnLogout;

    // PANEL ALL
    @FXML
    private Pane pnlProduk;
    @FXML
    private Pane pnlPaket;
    @FXML
    private Pane pnlHomePaket;
    @FXML
    private Pane pnlDenda;
    @FXML
    private Pane pnlCustomer;
    @FXML
    private Pane pnlCekCustomer;
    @FXML
    private Pane pnlTransaksi;
    @FXML
    private Pane pnlPeminjaman;
    @FXML
    private Pane pnlTransaksiPembayaran;

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
    private TextField txtStok;
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
    private ComboBox<String> cmbFilterStatusDenda;

    // PAKET ITEMS
    @FXML
    private Button btnBackPaket;
    @FXML
    private Button btnAddPaket;
    @FXML
    private TextField lblNamaPaket;
    @FXML
    private TextField lblIDPaket;
    @FXML
    private TextField lblHargaPaket;
    @FXML
    private TextField lblDiskonPaket;
    @FXML
    private TextField lblJumlahPaket;
    @FXML
    private TextField lblStokPaket;
    @FXML
    private Button btnClearDetailPaket;



    /* --- TRANSAKSI PEMINJAMAN ITEMS --- */

    // CEK CUSTOMER ITEMS
    @FXML
    private Button btnSearchCekCustomer;
    @FXML
    private TextField txtSearchCekCustomer;
    @FXML
    private Button btnCekCustomer;
    @FXML
    private TextField txtIDCekCustomer;
    @FXML
    private TextField txtNamaCekCustomer;
    //    @FXML
//    private ComboBox<String> cmbJenisKelamin;
    @FXML
    private TextField txtCekNoTelephone;
    @FXML
    private TextField txtCekEmail;
    @FXML
    private TextArea txtCekAlamat;
//    @FXML
//    private RadioButton rdLaki;
//    @FXML
//    private RadioButton rdPerempuan;
//    @FXML
//    private ToggleGroup genderGroup;




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
                ResultSet rs = stmt.executeQuery("SELECT ID_Produk, Nama_Produk, Kategori, Harga, Stok, Jumlah FROM produk WHERE status = 'Aktif'");
        ) {
            while (rs.next()) {
                list.add(new Produk(
                        rs.getString("ID_Produk"),
                        rs.getString("Nama_Produk"),
                        rs.getString("Kategori"),
                        rs.getDouble("Harga"),
                        rs.getInt("Stok"),
                        rs.getInt("Jumlah")
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

    private void loadProdukItemsTransact(){
        List<Produk> dataProduk = getDataProduk();
        loadProdukItemsTransact(dataProduk);
    }

    private void loadProdukItemsTransact(List<Produk> dataProduk) {
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

    private void loadItemPeminjamanProduk(){
        List<Produk> dataProduk = getDataProduk();
        loadItemPeminjamanProduk(dataProduk);
    }

    private void loadItemPeminjamanProduk(List<Produk> dataProduk) {
        pnPeminjamanProduk.getChildren().clear();


        for (int i = 0; i < dataProduk.size(); i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemPeminjamanProduk.fxml"));
                Node node = loader.load();

                ItemPeminjamanProdukController controller = loader.getController();
                controller.setData(dataProduk.get(i));
                controller.setHomeController(this); // <-- penting!

                final int j = i;
                node.setOnMouseEntered(event -> {
                    node.setStyle("-fx-background-color : #051036; -fx-background-radius : 15");
                });
                node.setOnMouseExited(event -> {
                    node.setStyle("-fx-background-color : #0D2857; -fx-background-radius : 15");
                });

                pnPeminjamanProduk.getChildren().add(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void refreshProdukList() {
        List<Produk> dataProduk = getDataProduk();
        loadProdukItems(dataProduk);
        loadProdukItemsTransact(dataProduk);
        loadItemPeminjamanProduk(dataProduk);
    }

    public void setDetailProduk(Produk produk) {
        txtIDProduk.setText(produk.getId());
        txtNama.setText(produk.getNama());
        cmbKategori.setValue(produk.getKategori());
        txtDeskripsi.setText(produk.getDeskripsi());
        txtHarga.setText(String.valueOf((int) produk.getHarga()));
        txtStok.setText(String.valueOf(produk.getStok()));
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
                ResultSet rs = stmt.executeQuery("SELECT ID_Customer, Nama_Customer, Nomor_Telephone, Email, Alamat, Image  FROM Customer WHERE status = 'Aktif'");
        ) {
            while (rs.next()) {
                list.add(new Customer(
                        rs.getString("ID_Customer"),
                        rs.getString("Nama_Customer"),
                        rs.getString("Nomor_Telephone"),
                        rs.getString("Email"),
                        rs.getString("Alamat"),
                        rs.getBytes("Image")
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

    public void refreshAllCustomerViews() {
        List<Customer> dataCustomer = getDataCustomer();
        loadCustomerItems(dataCustomer);
        loadCekCustomerItems(dataCustomer);
    }


    private void loadCekCustomerItems() {
        List<Customer> dataCustomer = getDataCustomer();
        loadCekCustomerItems(dataCustomer);
    }

    private void loadCekCustomerItems(List<Customer> dataCustomer) {
        pnItemsCekCustomer.getChildren().clear();

        for (int i = 0; i < dataCustomer.size(); i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemCustomer.fxml"));
                Node node = loader.load();

                ItemCustomerController controller = loader.getController();
                controller.setData2(dataCustomer.get(i));
                controller.setHomeController(this); // <-- penting!

                final Customer selectedCustomer = dataCustomer.get(i);

                node.setOnMouseEntered(event -> {
                    node.setStyle("-fx-background-color : #051036; -fx-background-radius : 15");
                });
                node.setOnMouseExited(event -> {
                    node.setStyle("-fx-background-color : #0D2857; -fx-background-radius : 15");
                });
                node.setOnMouseClicked(e -> showDetailCustomer(selectedCustomer));

                pnItemsCekCustomer.getChildren().add(node);
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
    // NEW PAKET
    protected void loadItemPaket() {
        List<Paket> dataPaket = getDataPaket();
        loadItemPaket(dataPaket);
        loadItemPeminjamanPaket();
    }

    private void loadItemPaket(List<Paket> dataPaket) {
        pnItemsHomePaket.getChildren().clear();

        for (Paket paket : dataPaket) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemPaket.fxml"));
                Node node = loader.load();

                ItemPaketController controller = loader.getController();
                controller.setData(paket);
                controller.setHomeController(this);

                node.setOnMouseEntered(e -> node.setStyle("-fx-background-color: #051036; -fx-background-radius: 15"));
                node.setOnMouseExited(e -> node.setStyle("-fx-background-color: #0D2857; -fx-background-radius: 15"));
                node.setOnMouseClicked(e -> showDetailPaket(paket));


                pnItemsHomePaket.getChildren().add(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void loadItemPeminjamanPaket() {
        List<Paket> dataPaket = getDataPaket();
        loadItemPeminjamanPaket(dataPaket);
    }

    private void loadItemPeminjamanPaket(List<Paket> dataPaket) {
        pnPeminjamanPaket.getChildren().clear();

        for (Paket paket : dataPaket) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemPeminjamanPaket.fxml"));
                Node node = loader.load();

                ItemPeminjamanPaketController controller = loader.getController();
                controller.setData(paket);
                controller.setHomeController(this);

                node.setOnMouseEntered(e -> node.setStyle("-fx-background-color: #051036; -fx-background-radius: 15"));
                node.setOnMouseExited(e -> node.setStyle("-fx-background-color: #0D2857; -fx-background-radius: 15"));
                node.setOnMouseClicked(e -> showDetailPaket(paket));


                pnPeminjamanPaket.getChildren().add(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public List<Paket> getDataPaket() {
        List<Paket> list = new ArrayList<>();
        DBConnect db = new DBConnect();

        String query = "SELECT ID_Paket, Nama_Paket, Jumlah, Harga, Diskon, Stok FROM Paket WHERE status = 'Aktif'";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Paket(
                        rs.getString("ID_Paket"),
                        rs.getString("Nama_Paket"),
                        rs.getDouble("Harga"),
                        rs.getDouble("Diskon"),
                        rs.getInt("Jumlah"),
                        rs.getInt("Stok")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
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

    public void hapusItemDariKeranjang(detailPaket item) {
        keranjang.removeIf(k -> k.getProduk().getId().equals(item.getProduk().getId()));
        refreshKeranjangView();
    }

    public void refreshKeranjangView() {
        pnCartProduk.getChildren().clear();

        try {
            int index = 1;
            for (detailPaket item : keranjang) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemTransactProduk.fxml"));
                Node node = loader.load();
                ItemTransactProdukController controller = loader.getController();
                controller.setData(item, index);
                pnCartProduk.getChildren().add(node);
                index++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showHomePaketPanel() {
        pnlHomePaket.toFront();
    }

    /* --- TRANSAKSI PEMINJAMAN METHOD --- */
    private List<detailPeminjaman> keranjangTransaksi = new ArrayList<>();
    public void updateKeranjang(detailPeminjaman newItem) {
        boolean found = false;

        for (detailPeminjaman items : keranjangTransaksi) {
            if (items.getTipe().equals(newItem.getTipe())) {
                if ("produk".equals(items.getTipe()) &&
                        items.getProduk() != null &&
                        newItem.getProduk() != null &&
                        items.getProduk().getId().equals(newItem.getProduk().getId())) {
                    items.setJumlah(newItem.getJumlah());
                    found = true;
                    break;
                } else if ("paket".equals(items.getTipe()) &&
                        items.getPaket() != null &&
                        newItem.getPaket() != null &&
                        items.getPaket().getId().equals(newItem.getPaket().getId())) {
                    items.setJumlah(newItem.getJumlah());
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            keranjangTransaksi.add(newItem);
        }

        refreshKeranjangTransaksiView();
    }

    public void hapusItemDariKeranjang(detailPeminjaman items) {
        keranjangTransaksi.removeIf(k -> {
            if ("produk".equals(k.getTipe()) && k.getProduk().getId().equals(items.getProduk().getId())) {
                return true;
            } else if ("paket".equals(k.getTipe()) && k.getPaket().getId().equals(items.getPaket().getId())) {
                return true;
            }
            return false;
        });

        refreshKeranjangTransaksiView();
    }

//    public void refreshKeranjangTransaksiView() {
//        pnCartPeminjaman.getChildren().clear();
//
//        try {
//            int index = 1;
//            for (detailPeminjaman item : keranjangTransaksi) {
//                FXMLLoader loader;
//                if ("produk".equals(item.getTipe())) {
//                    loader = new FXMLLoader(getClass().getResource("ItemTransactProduk.fxml"));
//                } else {
//                    loader = new FXMLLoader(getClass().getResource("ItemTransactPaket.fxml"));
//                }
//
//                Node node = loader.load();
//                Object controller = loader.getController();
//
//                if (controller instanceof ItemPeminjamanProdukController && "produk".equals(item.getTipe())) {
//                    ((ItemPeminjamanProdukController) controller).setData(item, index);
//                } else if (controller instanceof ItemPeminjamanPaketController && "paket".equals(item.getTipe())) {
//                    ((ItemPeminjamanPaketController) controller).setData(item, index);
//                }
//
//                pnCartPeminjaman.getChildren().add(node);
//                index++;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
public void refreshKeranjangTransaksiView() {
    pnCartPeminjaman.getChildren().clear();

    try {
        int index = 1;
        for (detailPeminjaman item : keranjangTransaksi) {
            FXMLLoader loader;

            if ("produk".equalsIgnoreCase(item.getTipe())) {
                loader = new FXMLLoader(getClass().getResource("ItemDetailPeminjamanProduk.fxml"));
            } else if ("paket".equalsIgnoreCase(item.getTipe())) {
                loader = new FXMLLoader(getClass().getResource("ItemPeminjamanPaket.fxml"));
            } else {
                System.err.println("Tipe item tidak dikenal: " + item.getTipe());
                continue;
            }

            Node node = loader.load();

            if ("produk".equalsIgnoreCase(item.getTipe())) {
                ItemDetailPeminjamanProdukController controller = loader.getController();
                controller.setHomeController(this);
                controller.setData(item, index);
            } else {
                ItemPeminjamanPaketController controller = loader.getController();
                controller.setHomeController(this);
                controller.setData(item, index);
            }

            pnCartPeminjaman.getChildren().add(node);
            index++;
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
        loadProdukItemsTransact();
        loadItemPaket();

        // Inisialisasi Component Transaksi Peminjaman
        loadCekCustomerItems();
        txtSearchCekCustomer.textProperty().addListener((observable, oldValue, newValue) -> {
            btnSearchCekCustomer.setDisable(newValue.trim().isEmpty());
        });
        btnSearchCekCustomer.setDisable(true);
        txtNamaCekCustomer.setOnAction(event -> handleCekCustomer(null));
        txtNamaCekCustomer.setOnAction(event -> {
            String nama = txtNamaCekCustomer.getText().trim();
            if (!nama.isEmpty()) {
                isCustomerExist(nama);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validasi");
                alert.setHeaderText(null);
                alert.setContentText("Silakan masukkan nama customer terlebih dahulu.");
                alert.showAndWait();
            }
        });

        loadItemPeminjamanProduk();
        loadItemPeminjamanPaket();


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
            pnlHomePaket.setStyle("-fx-background-color : #ffffff");
            pnlHomePaket.toFront();
        }
        if (actionEvent.getSource() == btnNewPaket) {
            pnlPaket.setStyle("-fx-background-color : #ffffff");
            pnlPaket.toFront();
        }
        if (actionEvent.getSource() == btnBackPaket) {
            pnlHomePaket.setStyle("-fx-background-color : #ffffff");
            pnlHomePaket.toFront();
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
        if(actionEvent.getSource()==btnPeminjaman)
        {
            pnlCekCustomer.setStyle("-fx-background-color : #ffffff");
            pnlCekCustomer.toFront();
        }
        if(actionEvent.getSource()==btnPembayaran)
        {
            pnlTransaksiPembayaran.setStyle("-fx-background-color : #ffffff");
            pnlTransaksiPembayaran.toFront();
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
    private void ScaleUpHomePaket(MouseEvent event) {
        btnNewPaket.setScaleX(0.95);
        btnNewPaket.setScaleY(0.95);
    }

    @FXML
    private void ScaleDownHomePaket(MouseEvent event) {
        btnNewPaket.setScaleX(1.0);
        btnNewPaket.setScaleY(1.0);
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
        String stockStr = txtStok.getText().trim();
        String status = "Aktif";

        if (idProduk.isEmpty() || nama.isEmpty() || kategori == null || kategori.isEmpty()
                || desk.isEmpty() || hargaStr.isEmpty() || stockStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi Input", "Semua field harus diisi!");
            return;
        }

        if (selectedImageFile == null) {
            showAlert(Alert.AlertType.WARNING, "Validasi Gambar", "Silakan pilih gambar produk terlebih dahulu.");
            return;
        }

        int harga, stock;
        try {
            harga = Integer.parseInt(hargaStr);
            stock = Integer.parseInt(stockStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Harga dan Stok harus berupa angka.");
            return;
        }

        DBConnect connect = new DBConnect();
        try (Connection conn = connect.getConnection()) {

            String cekQuery = "SELECT ID_Produk FROM Produk WHERE Nama_Produk = ?";
            PreparedStatement cekStmt = conn.prepareStatement(cekQuery);
            cekStmt.setString(1, nama);
            ResultSet rs = cekStmt.executeQuery();

            if (rs.next()) {
                String existingID = rs.getString("ID_Produk");
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Produk Sudah Ada");
                confirm.setHeaderText(null);
                confirm.setContentText("Produk dengan nama ini sudah terdaftar.\nIngin mengupdate data produk?");
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        updateProduk(existingID, nama, kategori, desk, harga, stock, status);
                    }
                });
                return;
            }

            String insertQuery = "INSERT INTO Produk (ID_Produk, Nama_Produk, Kategori, Deskripsi, Harga, Stok, Jumlah, Status, Image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstat = conn.prepareStatement(insertQuery);
            pstat.setString(1, idProduk);
            pstat.setString(2, nama);
            pstat.setString(3, kategori);
            pstat.setString(4, desk);
            pstat.setInt(5, harga);
            pstat.setInt(6, stock);
            pstat.setInt(7, stock);
            pstat.setString(8, status);
            InputStream imageStream = new FileInputStream(selectedImageFile);
            pstat.setBinaryStream(9, imageStream, (int) selectedImageFile.length());

            pstat.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Insert data produk berhasil!");
            RefreshData();

            pstat.close();
            conn.close();

        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateProduk(String idProduk, String nama, String kategori, String desk,
                              int harga, int stock, String status) {

        String updateQuery = "UPDATE Produk SET Kategori = ?, Deskripsi = ?, Harga = ?, Stok = ?, Jumlah = ?, Status = ?, Image = ? WHERE ID_Produk = ?";

        try (
                Connection conn = new DBConnect().getConnection();
                PreparedStatement pstmt = conn.prepareStatement(updateQuery)
        ) {
            pstmt.setString(1, kategori);
            pstmt.setString(2, desk);
            pstmt.setInt(3, harga);
            pstmt.setInt(4, stock);
            pstmt.setInt(5, stock);
            pstmt.setString(6, status);
            InputStream imageStream = new FileInputStream(selectedImageFile);
            pstmt.setBinaryStream(7, imageStream, (int) selectedImageFile.length());
            pstmt.setString(8, idProduk);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data produk berhasil diperbarui.");
                RefreshData();
                refreshProdukList();
            } else {
                showAlert(Alert.AlertType.WARNING, "Gagal", "Update produk gagal.");
            }

        } catch (SQLException | FileNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
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
                        rs.getInt("Stok"),
                        rs.getInt("Jumlah")
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
        txtStok.setText("");
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
                        rs.getString("Nama_Customer"),
                        rs.getString("Nomor_Telephone"),
                        rs.getString("Email")
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
    protected void onAddDenda() {
        String idDenda = txtIDDenda.getText().trim();
        String jenisDenda = cmbJenisDenda.getValue();
        String deskripsi = txtDeskripsiDenda.getText().trim();
        String nominalStr = txtNominal.getText().trim();

        if (idDenda.isEmpty() || jenisDenda == null || deskripsi.isEmpty() || nominalStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi Input", "Semua field harus diisi!");
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

        String query = "INSERT INTO Denda (ID_Denda, Jenis_Denda, Deskripsi, Nominal) VALUES (?, ?, ?, ?)";

        try {
            DBConnect connect = new DBConnect();
            Connection conn = connect.getConnection();
            PreparedStatement pstat = conn.prepareStatement(query);

            pstat.setString(1, idDenda);
            pstat.setString(2, jenisDenda);
            pstat.setString(3, deskripsi);
            pstat.setDouble(4, nominal);

            pstat.executeUpdate();
            pstat.close();
            conn.close();

            RefreshDataDenda();
            JOptionPane.showMessageDialog(null, "Data denda berhasil ditambahkan!");

        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage());
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

    @FXML
    protected void onClearDenda() {
        RefreshDataDenda();
    }

    public void RefreshDataDenda() {
        txtIDDenda.setText(generateDendaID());
        cmbJenisDenda.getSelectionModel().clearSelection();
        txtDeskripsiDenda.clear();
        txtNominal.clear();
        loadDendaItems();
    }

    /* --- PAKET CRUD --- */
    private Stage stageFormIsiPaket;
    private boolean isFormIsiPaketTerbuka = false;

    public boolean isFormIsiPaketTerbuka() {
        return isFormIsiPaketTerbuka;
    }

    public void setFormIsiPaketTerbuka(boolean status) {
        this.isFormIsiPaketTerbuka = status;
    }

    public void tutupFormIsiPaket() {
        if (stageFormIsiPaket != null) {
            stageFormIsiPaket.close();
            stageFormIsiPaket = null;
            setFormIsiPaketTerbuka(false);
        }
    }

    public void bringFormIsiPaketToFront() {
        if (stageFormIsiPaket != null) {
            javafx.application.Platform.runLater(() -> {
                stageFormIsiPaket.setIconified(false);
                stageFormIsiPaket.toFront();
                stageFormIsiPaket.requestFocus();
            });
        }
    }

    @FXML
    private void handleSimpanPaket(ActionEvent event) {
        if (isFormIsiPaketTerbuka) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Form Sudah Dibuka");
            alert.setHeaderText("Form Isi Paket sedang aktif");
            alert.setContentText("Klik OK untuk membukanya kembali.");
            alert.showAndWait();

            if (stageFormIsiPaket != null) {
                javafx.application.Platform.runLater(() -> {
                    stageFormIsiPaket.setIconified(false);
                    stageFormIsiPaket.toFront();
                    stageFormIsiPaket.requestFocus();
                });
            }
            return;
        }

        if (keranjang.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Keranjang Kosong", "Silakan tambahkan produk terlebih dahulu.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FormIsiPaket.fxml"));
            Parent root = loader.load();

            FormIsiPaketController controller = loader.getController();
            controller.setProdukDalamPaket(new ArrayList<>(keranjang));
            controller.setHomeController(this);

            Stage stage = new Stage();
            stage.setTitle("Isi Data Paket");
            stage.setScene(new Scene(root));

            setFormIsiPaketTerbuka(true);
            stage.setOnHiding(e -> {
                setFormIsiPaketTerbuka(false);
                stageFormIsiPaket = null; // clear reference
            });

            stageFormIsiPaket = stage; // simpan referensinya
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            setFormIsiPaketTerbuka(false);
            showAlert(Alert.AlertType.ERROR, "Gagal Membuka Form", "Terjadi kesalahan:\n" + e.getMessage());
        }
    }

    public void clearKeranjang() {
        keranjang.clear();
    }




    /* --- TRANSAKSI PEMINJAMAN --- */

    // CEK CUSTOMER
    @FXML
    private void handleSearchCekCustomer(ActionEvent event) {
        String keyword = txtSearchCekCustomer.getText().trim();

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
                        rs.getString("Nama_Customer"),
                        rs.getString("Nomor_Telephone"),
                        rs.getString("Email")
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
                txtSearchCekCustomer.clear();
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCekCustomer(ActionEvent event) {
        String nama = txtNamaCekCustomer.getText().trim();

        if (!nama.isEmpty()) {
            isCustomerExist(nama);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validasi");
            alert.setHeaderText(null);
            alert.setContentText("Silakan masukkan nama customer terlebih dahulu.");
            alert.showAndWait();
        }
    }

    @FXML
    private void isCustomerExist(String namaCustomer) {
        String query = "SELECT TOP 1 * FROM Customer WHERE LOWER(Nama_Customer) = LOWER(?) AND status = 'Aktif'";

        try  {
            DBConnect connect = new DBConnect();
            Connection conn = connect.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, namaCustomer);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String id = rs.getString("ID_Customer");
                String nama = rs.getString("Nama_Customer");
                String jenisKelamin = rs.getString("Jenis_Kelamin");
                String  telpon = rs.getString("Nomor_Telephone");
                String email = rs.getString("Email");
                String alamat = rs.getString("Alamat");

                txtIDCekCustomer.setText(id);
                txtNamaCekCustomer.setText(nama);
//                cmbJenisKelamin.getItems().setAll("Laki-Laki", "Perempuan");
//                cmbJenisKelamin.setValue(jenisKelamin);
                txtCekNoTelephone.setText(String.valueOf(telpon));
                txtCekEmail.setText(email);
                txtCekAlamat.setText(alamat);

                InputStream is = rs.getBinaryStream("Image");
                if (is != null) {
                    Image image = new Image(is);
                    imgCekCustomer.setImage(image);
                }


            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Customer Tidak Ditemukan");
                alert.setHeaderText("Customer tidak ditemukan");
                alert.setContentText("Apakah Anda ingin menambahkan customer baru?");

                ButtonType btnYes = new ButtonType("Ya", ButtonBar.ButtonData.YES);
                ButtonType btnNo = new ButtonType("Tidak", ButtonBar.ButtonData.NO);
                alert.getButtonTypes().setAll(btnYes, btnNo);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == btnYes) {
                    pnlCustomer.toFront();
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengecek customer: " + e.getMessage());
        }
    }

    public void refreshPaket() {
        loadItemPaket();
    }

    public void showDetailPaket(Paket paket) {
        if (paket == null) return;
//        System.out.println("Paket Dipilih: " + paket.getNama()); // Debug

        lblIDPaket.setText(paket.getId());
        lblNamaPaket.setText(paket.getNama());
        lblHargaPaket.setText(String.format("Rp. %,.0f", paket.getHarga()));
        lblDiskonPaket.setText(String.format("%.0f%%", paket.getDiskon() * 100));
        lblJumlahPaket.setText(String.valueOf(paket.getJumlahPaket()));
        lblStokPaket.setText(String.valueOf(paket.getStok()));
    }

    public void showDetailCustomer(Customer customer) {
        if (customer == null) return;

        txtIDCekCustomer.setText(customer.getId());
        txtNamaCekCustomer.setText(customer.getNama());
        txtCekNoTelephone.setText(customer.getNomortelephone());
        txtCekEmail.setText(customer.getEmail());
        txtCekAlamat.setText(customer.getAlamat() != null ? customer.getAlamat() : "");

        byte[] bytes = customer.getImageBytes();
        if (bytes != null && bytes.length > 0) {
            InputStream is = new ByteArrayInputStream(bytes);
            Image image = new Image(is);
            imgCekCustomer.setImage(image);
        } else {
            imgCekCustomer.setImage(null);
        }
    }


    @FXML
    public void clearDetailCustomer() {
        txtIDCekCustomer.clear();
        txtNamaCekCustomer.clear();
        txtCekNoTelephone.clear();
        txtCekEmail.clear();
        txtCekAlamat.clear();
        imgCekCustomer.setImage(null);
    }

    public void handleClearDetail(){
        lblIDPaket.setText("");
        lblNamaPaket.setText("");
        lblHargaPaket.setText("");
        lblDiskonPaket.setText("");
        lblJumlahPaket.setText("");
        lblStokPaket.setText("");
    }

    private String selectedCustomerId;

    @FXML
    private void handleNextButton(ActionEvent event) {
        if (txtIDCekCustomer.getText() == null || txtIDCekCustomer.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Peringatan");
            alert.setHeaderText("Customer belum dipilih");
            alert.setContentText("Silakan pilih customer terlebih dahulu.");
            alert.showAndWait();
            return;
        }

        // Simpan ID customer yang dipilih
        selectedCustomerId = txtIDCekCustomer.getText();

        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setTitle("Informasi");
        infoAlert.setHeaderText("Customer Dipilih");
        infoAlert.setContentText("Customer ID yang dipilih: " + selectedCustomerId);
        infoAlert.showAndWait();

        pnlPeminjaman.toFront();
        pnlCekCustomer.setVisible(false);
    }
}
