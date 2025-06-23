package com.example.demohiking.Controller;

import com.example.demohiking.ADT.Karyawan;
import com.example.demohiking.Connection.DBConnect;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

public class UpdateKaryawanController {
    @FXML private ImageView imgKaryawan;
    @FXML private TextField txtNama, txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cmbJabatan;
    @FXML private TextArea txtAlamat;

    private File selectedImageFile;
    private Karyawan karyawan;

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

    private String initialNama, initialEmail, initialAlamat, initialIDJabatan;
    private Image initialImage;

    private HomeManagerController homeManagerController;

    public void setHomeController(HomeManagerController controller) {
        this.homeManagerController = controller;
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    public void setKaryawan(Karyawan karyawan) {
        this.karyawan = karyawan;

        try {
            DBConnect db = new DBConnect();
            Connection conn = db.getConnection();

            String query = "SELECT * FROM Karyawan WHERE NPK = ?";
            PreparedStatement pstat = conn.prepareStatement(query);
            pstat.setString(1, karyawan.getId());

            ResultSet rs = pstat.executeQuery();
            if (rs.next()) {
                String nama = rs.getString("Nama_Karyawan");
                String email = rs.getString("Email");
                String alamat = rs.getString("Alamat");
                String idJabatan = rs.getString("ID_Jabatan");

                txtNama.setText(nama);
                txtEmail.setText(email);
                txtAlamat.setText(alamat);
                cmbJabatan.setValue(idJabatan);

                InputStream is = rs.getBinaryStream("Image");
                if (is != null) {
                    Image image = new Image(is);
                    imgKaryawan.setImage(image);
                }

                initialNama = nama;
                initialEmail = email;
                initialAlamat = alamat;
                initialIDJabatan = idJabatan;
                initialImage = imgKaryawan.getImage();
            } else {
                showAlert(Alert.AlertType.WARNING, "Tidak Ditemukan", "Data karyawan tidak ditemukan.");
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
        fileChooser.setTitle("Pilih Gambar Karyawan");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Gambar", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            Image image = new Image(file.toURI().toString());
            imgKaryawan.setImage(image);
        }
    }

    private void loadNamaJabatanToComboBox() {
        cmbJabatan.getItems().clear();

        String query = "SELECT Nama_Jabatan FROM Jabatan WHERE status = 'Aktif'";
        try {
            DBConnect connect = new DBConnect();
            Connection conn = connect.getConnection();
            PreparedStatement pstat = conn.prepareStatement(query);
            ResultSet rs = pstat.executeQuery();

            while (rs.next()) {
                cmbJabatan.getItems().add(rs.getString("Nama_Jabatan"));
            }

            rs.close();
            pstat.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        loadNamaJabatanToComboBox();
    }

    @FXML
    private void handleUpdate() {
        String nama = txtNama.getText().trim();
        String email = txtEmail.getText().trim();
        String alamat = txtAlamat.getText().trim();
        String idJabatan = cmbJabatan.getValue();

        if (nama.isEmpty() || email.isEmpty() || alamat.isEmpty() || idJabatan == null) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Semua field wajib diisi.");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.WARNING, "Validasi Email", "Format email tidak valid.");
            return;
        }

        boolean adaPerubahan = !nama.equals(initialNama)
                || !email.equals(initialEmail)
                || !alamat.equals(initialAlamat)
                || !idJabatan.equals(initialIDJabatan)
                || selectedImageFile != null;

        if (!adaPerubahan && txtPassword.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Info", "Belum ada perubahan data.");
            return;
        }

        try {
            DBConnect db = new DBConnect();
            Connection conn = db.getConnection();

            String query;
            PreparedStatement pstat;

            boolean updatePassword = !txtPassword.getText().trim().isEmpty();

            if (selectedImageFile != null && updatePassword) {
                query = "UPDATE Karyawan SET Nama_Karyawan=?, Email=?, Alamat=?, ID_Jabatan=?, Password=?, Image=? WHERE NPK=?";
                pstat = conn.prepareStatement(query);
                pstat.setString(1, nama);
                pstat.setString(2, email);
                pstat.setString(3, alamat);
                pstat.setString(4, idJabatan);
                pstat.setString(5, txtPassword.getText().trim());
                InputStream imageStream = new FileInputStream(selectedImageFile);
                pstat.setBinaryStream(6, imageStream, (int) selectedImageFile.length());
                pstat.setString(7, karyawan.getId());
            } else if (selectedImageFile != null) {
                query = "UPDATE Karyawan SET Nama_Karyawan=?, Email=?, Alamat=?, ID_Jabatan=?, Image=? WHERE NPK=?";
                pstat = conn.prepareStatement(query);
                pstat.setString(1, nama);
                pstat.setString(2, email);
                pstat.setString(3, alamat);
                pstat.setString(4, idJabatan);
                InputStream imageStream = new FileInputStream(selectedImageFile);
                pstat.setBinaryStream(5, imageStream, (int) selectedImageFile.length());
                pstat.setString(6, karyawan.getId());
            } else if (updatePassword) {
                query = "UPDATE Karyawan SET Nama_Karyawan=?, Email=?, Alamat=?, ID_Jabatan=?, Password=? WHERE NPK=?";
                pstat = conn.prepareStatement(query);
                pstat.setString(1, nama);
                pstat.setString(2, email);
                pstat.setString(3, alamat);
                pstat.setString(4, idJabatan);
                pstat.setString(5, txtPassword.getText().trim());
                pstat.setString(6, karyawan.getId());
            } else {
                query = "UPDATE Karyawan SET Nama_Karyawan=?, Email=?, Alamat=?, ID_Jabatan=? WHERE NPK=?";
                pstat = conn.prepareStatement(query);
                pstat.setString(1, nama);
                pstat.setString(2, email);
                pstat.setString(3, alamat);
                pstat.setString(4, idJabatan);
                pstat.setString(5, karyawan.getId());
            }

            int rows = pstat.executeUpdate();
            pstat.close();
            conn.close();

            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data karyawan berhasil diperbarui!");
                if (homeManagerController != null) {
                    homeManagerController.RefreshDataKaryawan();
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