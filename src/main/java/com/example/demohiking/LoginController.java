package com.example.demohiking;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import java.net.URL;
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

    private void handleLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        String query = "SELECT * FROM Users WHERE username = ? AND password = ?";

        try {
            DBConnect connection = new DBConnect();
            Connection conn = connection.getConnection();

            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                // Login berhasil
                showAlert(Alert.AlertType.INFORMATION, "Login Berhasil", "Selamat datang, " + username + "!");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) btnLogin.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Halaman Utama");
                stage.show();
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Gagal", "Username atau password salah.");
                txtUsername.clear();
                txtPassword.clear();
            }

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Kesalahan", "Terjadi kesalahan saat login.");
        }
    }




    private void handleCancel() {
        txtUsername.clear();
        txtPassword.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}