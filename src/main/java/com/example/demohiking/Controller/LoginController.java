package com.example.demohiking.Controller;

import com.example.demohiking.Connection.DBConnect;
import com.example.demohiking.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnBatal;

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnLogin.setOnAction(event -> handleLogin());
        btnBatal.setOnAction(event -> handleCancel());
    }

    @FXML
    private void handleLogin() {
        String inputNama = txtUsername.getText();
        String password = txtPassword.getText();

        String query = """
        SELECT k.ID_Karyawan, k.Nama_Karyawan, j.Nama_Jabatan
        FROM Karyawan k
        JOIN Jabatan j ON k.ID_Jabatan = j.ID_Jabatan
        WHERE LOWER(k.Nama_Karyawan) LIKE ? AND k.Password = ?
    """;

        try {
            DBConnect connection = new DBConnect();
            Connection conn = connection.getConnection();

            PreparedStatement statement = conn.prepareStatement(query,
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            statement.setString(1, inputNama.toLowerCase() + "%");
            statement.setString(2, password);

            ResultSet result = statement.executeQuery();

            // Hitung jumlah kemungkinan sama
            result.last();
            int rowCount = result.getRow();
            result.beforeFirst();

            if (rowCount == 0) {
                showAlert(Alert.AlertType.ERROR, "Login Gagal", "Nama atau password salah.");
                txtUsername.clear();
                txtPassword.clear();
            } else if (rowCount > 1) {
                showAlert(Alert.AlertType.WARNING, "Nama Tidak Spesifik",
                        "Ditemukan lebih dari satu karyawan dengan nama tersebut.\nSilakan masukkan nama lengkap.");
                txtUsername.clear();
                txtPassword.clear();
            } else {
                if (result.next()) {
                    String id = result.getString("ID_Karyawan");
                    String namaLengkap = result.getString("Nama_Karyawan");
                    String namaJabatan = result.getString("Nama_Jabatan");

                    Session.setSession(id, namaLengkap, namaJabatan);

                    showAlert(Alert.AlertType.INFORMATION, "Login Berhasil",
                            "Halo, " + namaLengkap + "\nJabatan: " + namaJabatan);

                    Parent root = FXMLLoader.load(getClass().getResource("loading.fxml"));
                    Stage stage = (Stage) btnLogin.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Loading...");
                    stage.show();
                }
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Kesalahan", "Terjadi kesalahan saat login.");
        }
    }




    @FXML
    private void handleCancel() {
        Stage stage = (Stage) txtUsername.getScene().getWindow();
        stage.close();
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}