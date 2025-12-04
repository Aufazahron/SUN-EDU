/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import dao.DonasiDAO;
import dao.ProgramDAO;
import java.io.File;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Program;
import util.SessionManager;

/**
 * FXML Controller class
 *
 * @author aufaz
 */
public class DashboardDonaturController implements Initializable {

    @FXML
    private HBox programContainer;
    
    @FXML
    private Label namaLabel;
    
    @FXML
    private Label totalDonasiLabel;
    
    @FXML
    private Label jumlahProgramLabel;
    
    private ProgramDAO programDAO;
    private DonasiDAO donasiDAO;
    private NumberFormat currencyFormat;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        programDAO = new ProgramDAO();
        donasiDAO = new DonasiDAO();
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        
        // Load user data
        loadUserData();
        
        // Load programs
        loadPrograms();
    }
    
    private void loadUserData() {
        if (SessionManager.getCurrentUser() != null) {
            var user = SessionManager.getCurrentUser();
            if (namaLabel != null) {
                namaLabel.setText(user.getNama());
            }
            
            // Load total donasi
            long totalDonasi = donasiDAO.getTotalDonasiByUser(user.getId());
            int jumlahProgram = donasiDAO.getJumlahProgramDonasi(user.getId());
            
            if (totalDonasiLabel != null) {
                totalDonasiLabel.setText(currencyFormat.format(totalDonasi));
            }
            
            if (jumlahProgramLabel != null) {
                jumlahProgramLabel.setText("untuk " + jumlahProgram + " Program Kerja");
            }
        }
    }
    
    private void loadPrograms() {
        List<Program> programs = programDAO.getAllPrograms();
        
        for (Program program : programs) {
            VBox card = createProgramCard(program);
            programContainer.getChildren().add(card);
        }
    }
    
    private VBox createProgramCard(Program program) {
        VBox card = new VBox();
        card.setPrefWidth(300);
        card.setPrefHeight(400);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setSpacing(10);
        card.setPadding(new Insets(0, 0, 15, 0));
        card.setCursor(javafx.scene.Cursor.HAND);
        
        // Hover effect dengan animasi
        card.setOnMouseEntered((MouseEvent e) -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), card);
            scaleTransition.setToX(1.05);
            scaleTransition.setToY(1.05);
            scaleTransition.play();
            
            DropShadow shadow = new DropShadow();
            shadow.setColor(Color.rgb(0, 0, 0, 0.3));
            shadow.setRadius(20);
            shadow.setOffsetX(0);
            shadow.setOffsetY(5);
            card.setEffect(shadow);
        });
        
        card.setOnMouseExited((MouseEvent e) -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), card);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
            
            DropShadow shadow = new DropShadow();
            shadow.setColor(Color.rgb(0, 0, 0, 0.1));
            shadow.setRadius(10);
            shadow.setOffsetX(0);
            shadow.setOffsetY(2);
            card.setEffect(shadow);
        });
        
        // Click handler untuk membuka modal
        card.setOnMouseClicked((MouseEvent e) -> {
            openProgramDetailModal(program);
        });
        
        // Image Container
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefHeight(180);
        imageContainer.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 16 16 0 0;");
        
        ImageView imageView = new ImageView();
        imageView.setFitWidth(300);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(false);
        
        // Clip untuk rounded corners
        Rectangle clip = new Rectangle(300, 180);
        clip.setArcWidth(16);
        clip.setArcHeight(16);
        imageView.setClip(clip);
        
        try {
            // Coba load dari assets/donasi/cover terlebih dahulu
            String imagePath = "../assets/donasi/cover/" + program.getCover();
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            imageView.setImage(image);
        } catch (Exception e) {
            // Jika tidak ditemukan, coba path lama
            try {
                String imagePath = "../assets/" + program.getCover();
                Image image = new Image(getClass().getResourceAsStream(imagePath));
                imageView.setImage(image);
            } catch (Exception ex) {
                // Jika gambar tidak ditemukan, gunakan placeholder
                System.err.println("Gambar tidak ditemukan: " + program.getCover());
            }
        }
        
        imageContainer.getChildren().add(imageView);
        
        // Content Container
        VBox contentBox = new VBox();
        contentBox.setSpacing(8);
        contentBox.setPadding(new Insets(15));
        
        // Program Name
        Label nameLabel = new Label(program.getNama());
        nameLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #020E4C;");
        nameLabel.setWrapText(true);
        
        // Description
        Label descLabel = new Label(program.getDeskripsi());
        descLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");
        descLabel.setWrapText(true);
        descLabel.setMaxHeight(40);
        
        // Location
        Label locationLabel = new Label("📍 " + program.getTempat());
        locationLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #888;");
        locationLabel.setWrapText(true);
        
        // Donation Info
        VBox donationBox = new VBox(5);
        donationBox.setPadding(new Insets(10, 0, 0, 0));
        
        // Progress Bar Container
        VBox progressContainer = new VBox(5);
        
        // Progress Info
        HBox progressInfo = new HBox();
        progressInfo.setSpacing(5);
        
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        long terkumpul = program.getDonasiTerkumpul();
        long target = program.getTargetDonasi() > 0 ? program.getTargetDonasi() : 1;
        
        Label progressLabel = new Label("Terkumpul: " + currencyFormat.format(terkumpul));
        progressLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #020E4C;");
        
        Label targetLabel = new Label(" / " + currencyFormat.format(target));
        targetLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #888;");
        
        progressInfo.getChildren().addAll(progressLabel, targetLabel);
        
        // Progress Bar
        javafx.scene.control.ProgressBar progressBar = new javafx.scene.control.ProgressBar();
        progressBar.setPrefWidth(270);
        progressBar.setPrefHeight(8);
        double progress = (double) terkumpul / target;
        progressBar.setProgress(Math.min(progress, 1.0));
        progressBar.setStyle("-fx-accent: #FFA500;");
        
        // Donatur Count
        Label donaturLabel = new Label(program.getJumlahDonatur() + " Donatur");
        donaturLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666;");
        
        progressContainer.getChildren().addAll(progressInfo, progressBar, donaturLabel);
        donationBox.getChildren().add(progressContainer);
        
        contentBox.getChildren().addAll(nameLabel, descLabel, locationLabel, donationBox);
        
        card.getChildren().addAll(imageContainer, contentBox);
        
        return card;
    }
    
    private void openProgramDetailModal(Program program) {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = new File("src/main/java/view/ProgramDetailModal.fxml").toURI().toURL();
            loader.setLocation(url);
            Parent root = loader.load();
            
            ProgramDetailModalController controller = loader.getController();
            controller.setProgram(program);
            
            Stage modalStage = new Stage();
            modalStage.setTitle("Detail Program");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(programContainer.getScene().getWindow());
            
            Scene scene = new Scene(root);
            modalStage.setScene(scene);
            
            // Animasi fade in
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
            
            modalStage.showAndWait();
            
            // Refresh data setelah modal ditutup
            refreshData();
        } catch (Exception e) {
            System.err.println("Error membuka modal: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void refreshData() {
        // Refresh user data
        loadUserData();
        
        // Refresh program list
        programContainer.getChildren().clear();
        loadPrograms();
    }
}
