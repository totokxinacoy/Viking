package com.example.demohiking.Controller;

import com.example.demohiking.Session;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

public class LoadingController {

    @FXML
    private ImageView landingImage;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label loadingText;

    @FXML
    private Label progressLabel;

    @FXML
    private ImageView hikerIcon;

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            playSoundEffect();
            startLoadingAnimation();
        });
    }

    private void playSoundEffect() {
        URL soundURL = getClass().getResource("/sounds/cinematicTunel.wav");
        if (soundURL != null) {
            AudioClip clip = new AudioClip(soundURL.toExternalForm());
            clip.play();
        }
    }


    private void startLoadingAnimation() {
        progressBar.setProgress(0);
        hikerIcon.setLayoutX(760);

        FadeTransition fade = new FadeTransition(Duration.seconds(1), loadingText);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.setCycleCount(Animation.INDEFINITE);
        fade.setAutoReverse(true);
        fade.play();

        Timeline timeline = new Timeline();
        int duration = 3;
        double startX = 760;
        double endX = startX + 360;

        for (int i = 0; i <= 100; i++) {
            double progress = i / 100.0;
            KeyFrame frame = new KeyFrame(Duration.seconds(progress * duration),
                    new KeyValue(progressBar.progressProperty(), progress),
                    new KeyValue(progressLabel.textProperty(), i + "%"),
                    new KeyValue(hikerIcon.layoutXProperty(), startX + (endX - startX) * progress));
            timeline.getKeyFrames().add(frame);
        }

        timeline.setOnFinished(event -> {
            try {
                goToHomePage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        timeline.play();
    }

    private void goToHomePage() throws IOException {
        String jabatan = Session.getRole();
        String fxml = switch (jabatan.toLowerCase()) {
            case "manager" -> "homeManager.fxml";
            case "kasir" -> "homeKasir.fxml";
            default -> throw new IllegalStateException("Jabatan tidak dikenali: " + jabatan);
        };

        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        Stage stage = (Stage) landingImage.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Halaman Utama");
        stage.show();
    }
}