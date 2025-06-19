package com.example.demohiking;

import com.example.demohiking.ADT.Customer;
import com.example.demohiking.Connection.DBConnect;
import com.example.demohiking.ADT.Produk;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateCustomerController {
    @FXML
    private ImageView imgCustomer;

    private File selectedImageFile;
    @FXML
    private TextField txtNama, txtNoTelephone, txtEmail;
    @FXML
    private ComboBox<String> cmbJenisKelamin;
    @FXML
    private TextArea txtAlamat;

    private Customer customer;

    // Static stage untuk kontrol satu jendela saja
    private static Stage updateStage = null;

    public static boolean isWindowOpen() {
        return updateStage != null;
    }

    public static void setWindow(Stage stage) {
        updateStage = stage;
    }

    public static void clearWindow() {
        updateStage = null;
    }

    public static void bringToFront() {
        if (updateStage != null) {
            updateStage.toFront();
            updateStage.requestFocus();
        }
    }

    private String initialNama, initialJenisKelamin, initialAlamat, initialEmail, initialNoTelephone;
    private Image initialImage;

    private HomeController homeController;

    public void setHomeController(HomeController controller) {
        this.homeController = controller;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;

        try {
            DBConnect db = new DBConnect();
            Connection conn = db.getConnection();

            String query = "SELECT * FROM Customer WHERE ID_Customer = ?";
            PreparedStatement pstat = conn.prepareStatement(query);
            pstat.setString(1, customer.getId());

            ResultSet rs = pstat.executeQuery();
            if (rs.next()) {
                String nama = rs.getString("Nama_Customer");
                String jenisKelamin = rs.getString("Jenis_Kelamin");
                String  telpon = rs.getString("Nomor_Telephone");
                String email = rs.getString("Email");
                String alamat = rs.getString("Alamat");


                txtNama.setText(nama);
                cmbJenisKelamin.getItems().setAll("Laki-Laki", "Perempuan");
                cmbJenisKelamin.setValue(jenisKelamin);
                txtNoTelephone.setText(String.valueOf(telpon));
                txtEmail.setText(email);
                txtAlamat.setText(alamat);

                InputStream is = rs.getBinaryStream("Image");
                if (is != null) {
                    Image image = new Image(is);
                    imgCustomer.setImage(image);
                }

                // Simpan data awal
                initialNama = nama;
                initialJenisKelamin = jenisKelamin;
                initialNoTelephone = telpon;
                initialEmail = email;
                initialAlamat= alamat;
                initialImage = imgCustomer.getImage();
            } else {
                showAlert(Alert.AlertType.WARNING, "Tidak Ditemukan", "Customer tidak ditemukan.");
            }

            rs.close();
            pstat.close();
            conn.close();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    private void handleChooseImage() {
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
    private void handleUpdate() {
        String nama = txtNama.getText().trim();
        String jenisKelamin = cmbJenisKelamin.getValue();
        String telpon = txtNoTelephone.getText().trim();
        String email = txtEmail.getText().trim();
        String alamat = txtAlamat.getText().trim();

        if (nama.isEmpty() || jenisKelamin.isEmpty() || telpon.isEmpty()
                || email.isEmpty() || alamat.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Semua field wajib diisi.");
            return;
        }

        try {


            boolean adaPerubahan = !nama.equals(initialNama)
                    || !jenisKelamin.equals(initialJenisKelamin)
                    || !telpon.equals(initialNoTelephone)
                    || !email.equals(initialEmail)
                    || !alamat.equals(initialAlamat)
                    || selectedImageFile != null;

            if (!adaPerubahan) {
                showAlert(Alert.AlertType.INFORMATION, "Info", "Belum ada perubahan data.");
                return;
            }

            DBConnect db = new DBConnect();
            Connection conn = db.getConnection();

            String query;
            PreparedStatement pstat;

            if (selectedImageFile != null) {
                query = "UPDATE Customer SET Nama_Customer=?, Jenis_Kelamin=?, Nomor_Telephone=?, Email=?, Alamat=?, Image=? WHERE ID_Customer=?";
                pstat = conn.prepareStatement(query);
                pstat.setString(1, nama);
                pstat.setString(2, jenisKelamin);
                pstat.setString(3, telpon);
                pstat.setString(4, email);
                pstat.setString(5, alamat);
                InputStream imageStream = new FileInputStream(selectedImageFile);
                pstat.setBinaryStream(6, imageStream, (int) selectedImageFile.length());
                pstat.setString(7, customer.getId());
            } else {
                query = "UPDATE Customer SET Nama_Customer=?, Jenis_Kelamin=?, Nomor_Telephone=?, Email=?, Alamat=? WHERE ID_Customer=?";
                pstat = conn.prepareStatement(query);
                pstat.setString(1, nama);
                pstat.setString(2, jenisKelamin);
                pstat.setString(3, telpon);
                pstat.setString(4, email);
                pstat.setString(5, alamat);
                pstat.setString(6, customer.getId());
            }

            int rows = pstat.executeUpdate();
            pstat.close();
            conn.close();

            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Customer berhasil diperbarui!");

                if (homeController != null) {
                    homeController.RefreshDataCustomer();
                }

                if (updateStage != null) {
                    updateStage.close();
                    clearWindow();
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Data tidak ditemukan.");
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        } catch (FileNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "File Gambar Tidak Ditemukan", e.getMessage());
        }
    }

    @FXML
    protected void handleCancel() {
        if (updateStage != null) {
            updateStage.close();
            clearWindow();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
