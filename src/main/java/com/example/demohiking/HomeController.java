package com.example.demohiking;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private VBox pnItems = null;

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
    private Pane pnlProduk;

    @FXML
    private Pane pnlPaket;

    @FXML
    private Pane pnlDenda;

    @FXML
    private Pane pnlCustomer;

    @FXML
    private Pane pnlTransaksi;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Node[] nodes = new Node[15];
        for (int i = 0; i < nodes.length; i++) {
            try {

                final int j = i;
                nodes[i] = FXMLLoader.load(getClass().getResource("Item.fxml"));

                //give the items some effect

                nodes[i].setOnMouseEntered(event -> {
                    nodes[j].setStyle("-fx-background-color : #051036; -fx-background-radius : 15");
                });
                nodes[i].setOnMouseExited(event -> {
                    nodes[j].setStyle("-fx-background-color : #0D2857; -fx-background-radius : 15");
                });
                pnItems.getChildren().add(nodes[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
}
