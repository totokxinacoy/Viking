package com.example.demohiking.Controller;

import com.example.demohiking.Session;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

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

    private Clip loadingClip;

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            playWavSound();
            startLoadingAnimation();
        });
    }

    private void playWavSound() {
        try {
            InputStream audioSrc = getClass().getResourceAsStream("/sounds/cinematicTunel.wav");
            if (audioSrc == null) {
                System.err.println("Audio file not found.");
                return;
            }

            InputStream bufferedIn = new java.io.BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            loadingClip = AudioSystem.getClip();
            loadingClip.open(audioStream);
            loadingClip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopWavSound() {
        if (loadingClip != null && loadingClip.isRunning()) {
            loadingClip.stop();
            loadingClip.close();
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
        int duration = 5;
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
            stopWavSound();
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