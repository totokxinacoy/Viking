package com.example.demohiking.Controller;

import com.example.demohiking.ADT.Jabatan;
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

public class HomeManagerController implements Initializable {
    // IMAGE ALL
    @FXML
    private ImageView imgKaryawan;
    private boolean isImageSelected = false;
    private File selectedImageFile;

    // ITEM ALL
    @FXML
    private VBox pnItemsKaryawan = null;
    @FXML
    private VBox pnItemsJabatan = null;
    @FXML
    private VBox pnItemsLaporan = null;

    // BUTTON MENU ALL
    @FXML
    private Button btnKaryawan;
    @FXML
    private Button btnJabatan;
    @FXML
    private Button btnLaporan;
    @FXML
    private Button btnLogout;

    // PANEL ALL
    @FXML
    private Pane pnlKaryawan;
    @FXML
    private Pane pnlJabatan;
    @FXML
    private Pane pnlLaporan;

    // KARYAWAN ITEMS
    @FXML
    private Button btnSearchKaryawan;
    @FXML
    private TextField txtSearchKaryawan;
    @FXML
    private TextField txtIDKaryawan;
    @FXML
    private TextField txtNamaKaryawan;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private ComboBox<String> cmbJabatan;
    @FXML
    private TextField txtEmailKaryawan;
    @FXML
    private TextArea txtAlamatKaryawan;

    // JABATAN ITEMS
    @FXML
    private Button btnSearchJabatan;
    @FXML
    private TextField txtSearchJabatan;
    @FXML
    private TextField txtIDJabatan;
    @FXML
    private TextField txtNamaJabatan;

    /* --- JABATAN METHOD --- */
    private String generateJabatanID() {
        String id = "JAB001";
        String query = "SELECT MAX(ID_Jabatan) as max_id FROM Jabatan";

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
                        id = String.format("JAB%03d", nextID);
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

    public List<Jabatan> getDataJabatan() {
        List<Jabatan> list = new ArrayList<>();
        DBConnect connection = new DBConnect();

        try (
                Connection conn = connection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT ID_Jabatan, Nama_Jabatan FROM Jabatan WHERE status = 'Aktif'");
        ) {
            while (rs.next()) {
                list.add(new Jabatan(
                        rs.getString("ID_Jabatan"),
                        rs.getString("Nama_Jabatan")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private void loadJabatanItems() {
        List<Jabatan> dataJabatan = getDataJabatan();
        loadJabatanItems(dataJabatan);
    }

    private void loadJabatanItems(List<Jabatan> dataJabatan) {
        pnItemsJabatan.getChildren().clear();

        for (int i = 0; i < dataJabatan.size(); i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemJabatan.fxml"));
                Node node = loader.load();

                ItemJabatanController controller = loader.getController();
                controller.setData(dataJabatan.get(i));
                controller.setHomeController(this); // <-- tetap penting

                node.setOnMouseEntered(event -> {
                    node.setStyle("-fx-background-color : #051036; -fx-background-radius : 15");
                });
                node.setOnMouseExited(event -> {
                    node.setStyle("-fx-background-color : #0D2857; -fx-background-radius : 15");
                });

                pnItemsJabatan.getChildren().add(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setDetailJabatan(Jabatan jabatan) {
        txtIDJabatan.setText(jabatan.getId());
        txtNamaJabatan.setText(jabatan.getNama());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inisalisasi Component Produk
        loadJabatanItems();
        txtIDJabatan.setEditable(false);
        txtIDJabatan.setText(generateJabatanID());
        txtSearchJabatan.textProperty().addListener((observable, oldValue, newValue) -> {
            btnSearchJabatan.setDisable(newValue.trim().isEmpty());
        });
        btnSearchJabatan.setDisable(true);

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
        if (actionEvent.getSource() == btnKaryawan) {
            pnlKaryawan.setStyle("-fx-background-color : #ffffff");
            pnlKaryawan.toFront();
        }
        if (actionEvent.getSource() == btnJabatan) {
            pnlJabatan.setStyle("-fx-background-color : #ffffff");
            pnlJabatan.toFront();
        }
        if (actionEvent.getSource() == btnLaporan) {
            pnlLaporan.setStyle("-fx-background-color : #ffffff");
            pnlLaporan.toFront();
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

    /* --- JABATAN CRUD --- */
    @FXML
    protected void onAddJabatan() {
        String idJabatan = txtIDJabatan.getText().trim();
        String nama = txtNamaJabatan.getText().trim();
        String status = "Aktif";

        if (idJabatan.isEmpty() || nama.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi Input", "ID dan Nama Jabatan harus diisi!");
            return;
        }

        String query = "INSERT INTO Jabatan (ID_Jabatan, Nama_Jabatan, Status) VALUES (?, ?, ?)";

        try {
            DBConnect connect = new DBConnect();
            Connection conn = connect.getConnection();
            PreparedStatement pstat = conn.prepareStatement(query);

            pstat.setString(1, idJabatan);
            pstat.setString(2, nama);
            pstat.setString(3, status);

            pstat.executeUpdate();
            pstat.close();
            conn.close();

            RefreshDataJabatan();
            JOptionPane.showMessageDialog(null, "Data jabatan berhasil ditambahkan!");
        } catch (SQLException ex) {
            System.out.println("Terjadi error saat insert data jabatan: " + ex.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error", ex.getMessage());
        }
    }

    @FXML
    protected void onClearJabatan() {
        RefreshDataJabatan();
    }

    @FXML
    private void handleSearchJabatan(ActionEvent event) {
        String keyword = txtSearchJabatan.getText().trim();

        String query = "SELECT * FROM Jabatan WHERE (ID_Jabatan = ? OR LOWER(Nama_Jabatan) LIKE ?) AND Status = 'Aktif'";
        try {
            DBConnect connect = new DBConnect();
            Connection conn = connect.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, keyword);
            pstmt.setString(2, "%" + keyword.toLowerCase() + "%");

            ResultSet rs = pstmt.executeQuery();
            List<Jabatan> foundList = new ArrayList<>();

            while (rs.next()) {
                Jabatan jabatan = new Jabatan(
                        rs.getString("ID_Jabatan"),
                        rs.getString("Nama_Jabatan")
                );
                foundList.add(jabatan);
            }

            if (!foundList.isEmpty()) {
                setDetailJabatan(foundList.get(0)); // tampilkan detail pertama
                loadJabatanItems(foundList);        // tampilkan hasil pencarian
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Pencarian Jabatan");
                alert.setHeaderText(null);
                alert.setContentText("Jabatan tidak ditemukan.");
                alert.showAndWait();
                txtSearchJabatan.clear();
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void RefreshDataJabatan() {
        txtIDJabatan.setText(generateJabatanID());
        txtNamaJabatan.setText("");
        txtSearchJabatan.setText("");
        loadJabatanItems();
    }
}
