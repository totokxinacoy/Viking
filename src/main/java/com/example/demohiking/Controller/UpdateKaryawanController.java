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
import java.util.HashMap;
import java.util.Map;

public class UpdateKaryawanController {
    @FXML private ImageView imgKaryawan;
    @FXML private TextField txtNama, txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cmbJabatan;
    @FXML private TextArea txtAlamat;
    @FXML private TextField txtPasswordVisible;
    @FXML private CheckBox chkShowPassword;
    private Map<String, String> jabatanMap = new HashMap<>();

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

    @FXML
    private void togglePasswordVisibility() {
        boolean show = chkShowPassword.isSelected();
        if (show) {
            txtPasswordVisible.setText(txtPassword.getText());
            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setManaged(true);
            txtPassword.setVisible(false);
            txtPassword.setManaged(false);
        } else {
            txtPassword.setText(txtPasswordVisible.getText());
            txtPassword.setVisible(true);
            txtPassword.setManaged(true);
            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);
        }
    }

    public void setKaryawan(Karyawan karyawan) {
        this.karyawan = karyawan;

        try {
            DBConnect db = new DBConnect();
            Connection conn = db.getConnection();

            String query = "SELECT K.*, J.Nama_Jabatan FROM Karyawan K " +
                    "JOIN Jabatan J ON K.ID_Jabatan = J.ID_Jabatan " +
                    "WHERE K.ID_Karyawan = ?";

            PreparedStatement pstat = conn.prepareStatement(query);
            pstat.setString(1, karyawan.getId());

            ResultSet rs = pstat.executeQuery();
            if (rs.next()) {
                String nama = rs.getString("Nama_Karyawan");
                String email = rs.getString("Email");
                String alamat = rs.getString("Alamat");
                String idJabatan = rs.getString("ID_Jabatan");
                String namaJabatan = rs.getString("Nama_Jabatan");
                String password = rs.getString("Password");

                txtNama.setText(nama);
                txtEmail.setText(email);
                txtAlamat.setText(alamat);
                txtPassword.setText(password);
                cmbJabatan.setValue(namaJabatan);
                initialIDJabatan = idJabatan;

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
        jabatanMap.clear();

        String query = "SELECT ID_Jabatan, Nama_Jabatan FROM Jabatan WHERE Status = 'Aktif'";
        try {
            DBConnect connect = new DBConnect();
            Connection conn = connect.getConnection();
            PreparedStatement pstat = conn.prepareStatement(query);
            ResultSet rs = pstat.executeQuery();

            while (rs.next()) {
                String id = rs.getString("ID_Jabatan");
                String nama = rs.getString("Nama_Jabatan");
                cmbJabatan.getItems().add(nama);
                jabatanMap.put(nama, id);
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

        // Sinkronisasi real-time antar field
        txtPassword.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!chkShowPassword.isSelected()) txtPasswordVisible.setText(newVal);
        });

        txtPasswordVisible.textProperty().addListener((obs, oldVal, newVal) -> {
            if (chkShowPassword.isSelected()) txtPassword.setText(newVal);
        });
    }

    @FXML
    private void handleUpdate() {
        String nama = txtNama.getText().trim();
        String email = txtEmail.getText().trim();
        String alamat = txtAlamat.getText().trim();
        String jabatanDipilih = jabatanMap.get(cmbJabatan.getValue());
        String password = chkShowPassword.isSelected()
                ? txtPasswordVisible.getText().trim()
                : txtPassword.getText().trim();

        if (nama.isEmpty() || email.isEmpty() || alamat.isEmpty() || jabatanDipilih == null) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Semua field wajib diisi.");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.WARNING, "Validasi Email", "Format email tidak valid.");
            return;
        }

        boolean updatePassword = !password.isEmpty();
        boolean updateImage = selectedImageFile != null;

        if (!updatePassword && !updateImage &&
                nama.equals(initialNama) && email.equals(initialEmail) &&
                alamat.equals(initialAlamat) && jabatanDipilih.equals(initialIDJabatan)) {
            showAlert(Alert.AlertType.INFORMATION, "Info", "Belum ada perubahan data.");
            return;
        }

        try {
            DBConnect db = new DBConnect();
            Connection conn = db.getConnection();

            StringBuilder sb = new StringBuilder("UPDATE Karyawan SET ");
            if (!nama.equals(initialNama)) sb.append("Nama_Karyawan=?, ");
            if (!email.equals(initialEmail)) sb.append("Email=?, ");
            if (!alamat.equals(initialAlamat)) sb.append("Alamat=?, ");
            if (!jabatanDipilih.equals(initialIDJabatan)) sb.append("ID_Jabatan=?, ");
            if (updatePassword) sb.append("Password=?, ");
            if (updateImage) sb.append("Image=?, ");
            sb.setLength(sb.length() - 2); // buang koma terakhir
            sb.append(" WHERE ID_Karyawan=?");

            PreparedStatement pstat = conn.prepareStatement(sb.toString());

            int index = 1;
            if (!nama.equals(initialNama)) pstat.setString(index++, nama);
            if (!email.equals(initialEmail)) pstat.setString(index++, email);
            if (!alamat.equals(initialAlamat)) pstat.setString(index++, alamat);
            if (!jabatanDipilih.equals(initialIDJabatan)) pstat.setString(index++, jabatanDipilih);
            if (updatePassword) pstat.setString(index++, password);
            if (updateImage) {
                InputStream is = new FileInputStream(selectedImageFile);
                pstat.setBinaryStream(index++, is, (int) selectedImageFile.length());
            }
            pstat.setString(index, karyawan.getId());

            int rows = pstat.executeUpdate();
            pstat.close();
            conn.close();

            if (rows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data karyawan berhasil diperbarui!");
                if (homeManagerController != null) homeManagerController.RefreshDataKaryawan();
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