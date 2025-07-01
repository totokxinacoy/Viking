package com.example.demohiking.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class CekCustomerController {
    @FXML
    private TextField txtCekCustomer;
    @FXML
    private Button btnCek;
    @FXML
    private Button btnBatal;

    private HomeKasirController homeKasirController;

    public void setHomeController(HomeKasirController controller) {
        this.homeKasirController = controller;
    }


}
