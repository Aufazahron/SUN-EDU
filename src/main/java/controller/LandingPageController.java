/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import dao.ProgramDAO;
import java.io.File;
import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.animation.ScaleTransition;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Insets;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Program;

/**
 * FXML Controller class
 *
 * @author aufaz
 */
public class LandingPageController implements Initializable {

    @FXML
    private ImageView programImageView;
    
    @FXML
    private Label programNameLabel;
    
    @FXML
    private Label programDescLabel;
    
    @FXML
    private Button btnNext;
    
    @FXML
    private Button btnPrev;
    
    @FXML
    private VBox carouselContainer;
    
    @FXML
    private Button btnMasuk;
    
    @FXML
    private Button btnDonasi;
    
    @FXML
    private Button btnProgram;
    
    @FXML
    private Button btnDaftarRelawan;
    
    @FXML
    private Label navDashboard;
    
    @FXML
    private Label navProgram;
    
    @FXML
    private Label navHubungi;
    
    @FXML
    private Label navTentang;
    
    @FXML
    private ImageView logoImageView;
    
    @FXML
    private ImageView logoMainImageView;
    
    private List<Program> programs;
    private int currentIndex = 0;
    private ProgramDAO programDAO;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        programDAO = new ProgramDAO();
        loadPrograms();
        updateCarousel();
        setupHoverEffects();
        setupLogoClickable();
    }
    
    private void setupLogoClickable() {
        // Setup logo untuk kembali ke home (scroll to top jika sudah di home)
        if (logoImageView != null) {
            logoImageView.setCursor(Cursor.HAND);
            logoImageView.setOnMouseClicked(e -> {
                if (navDashboard != null && navDashboard.getScene() != null) {
                    ScrollPane scrollPane = (ScrollPane) navDashboard.getScene().getRoot();
                    if (scrollPane != null) {
                        scrollPane.setVvalue(0);
                    }
                }
            });
        }
        if (logoMainImageView != null) {
            logoMainImageView.setCursor(Cursor.HAND);
            logoMainImageView.setOnMouseClicked(e -> {
                if (navDashboard != null && navDashboard.getScene() != null) {
                    ScrollPane scrollPane = (ScrollPane) navDashboard.getScene().getRoot();
                    if (scrollPane != null) {
                        scrollPane.setVvalue(0);
                    }
                }
            });
        }
    }
    
    private void setupHoverEffects() {
        // Setup hover untuk tombol Masuk
        if (btnMasuk != null) {
            btnMasuk.setCursor(Cursor.HAND);
            btnMasuk.setOnMouseEntered(e -> {
                btnMasuk.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2); -fx-border-color: #FFA726; -fx-border-radius: 5; -fx-text-fill: #FFA726;");
                applyScaleAnimation(btnMasuk, 1.05);
            });
            btnMasuk.setOnMouseExited(e -> {
                btnMasuk.setStyle("-fx-background-color: transparent; -fx-border-color: white; -fx-border-radius: 5; -fx-text-fill: white;");
                applyScaleAnimation(btnMasuk, 1.0);
            });
        }
        
        // Setup hover untuk tombol Donasi
        if (btnDonasi != null) {
            btnDonasi.setCursor(Cursor.HAND);
            btnDonasi.setOnMouseEntered(e -> {
                btnDonasi.setStyle("-fx-background-color: #1a3dfc; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 15 40;");
                applyScaleAnimation(btnDonasi, 1.05);
            });
            btnDonasi.setOnMouseExited(e -> {
                btnDonasi.setStyle("-fx-background-color: #020EFC; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 15 40;");
                applyScaleAnimation(btnDonasi, 1.0);
            });
        }
        
        // Setup hover untuk tombol Program
        if (btnProgram != null) {
            btnProgram.setCursor(Cursor.HAND);
            btnProgram.setOnMouseEntered(e -> {
                btnProgram.setStyle("-fx-background-color: #1a3dfc; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 15 40;");
                applyScaleAnimation(btnProgram, 1.05);
            });
            btnProgram.setOnMouseExited(e -> {
                btnProgram.setStyle("-fx-background-color: #020efc; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 15 40;");
                applyScaleAnimation(btnProgram, 1.0);
            });
        }
        
        // Setup hover untuk tombol Carousel Next
        if (btnNext != null) {
            btnNext.setCursor(Cursor.HAND);
            btnNext.setOnMouseEntered(e -> {
                if (!btnNext.isDisabled()) {
                    btnNext.setStyle("-fx-background-radius: 100; -fx-background-color: rgba(255,255,255,0.4); -fx-border-color: #FFA726; -fx-border-radius: 100; -fx-border-width: 2; -fx-text-fill: #FFA726;");
                    applyScaleAnimation(btnNext, 1.1);
                }
            });
            btnNext.setOnMouseExited(e -> {
                btnNext.setStyle("-fx-background-radius: 100; -fx-background-color: rgba(255,255,255,0.2); -fx-border-color: white; -fx-border-radius: 100; -fx-border-width: 2; -fx-text-fill: WHITE;");
                applyScaleAnimation(btnNext, 1.0);
            });
        }
        
        // Setup hover untuk tombol Carousel Prev
        if (btnPrev != null) {
            btnPrev.setCursor(Cursor.HAND);
            btnPrev.setOnMouseEntered(e -> {
                if (!btnPrev.isDisabled()) {
                    btnPrev.setStyle("-fx-background-radius: 100; -fx-background-color: rgba(255,255,255,0.4); -fx-border-color: #FFA726; -fx-border-radius: 100; -fx-border-width: 2; -fx-text-fill: #FFA726;");
                    applyScaleAnimation(btnPrev, 1.1);
                }
            });
            btnPrev.setOnMouseExited(e -> {
                btnPrev.setStyle("-fx-background-radius: 100; -fx-background-color: rgba(255,255,255,0.2); -fx-border-color: white; -fx-border-radius: 100; -fx-border-width: 2; -fx-text-fill: WHITE;");
                applyScaleAnimation(btnPrev, 1.0);
            });
        }
        
        // Setup hover untuk tombol Daftar Relawan
        if (btnDaftarRelawan != null) {
            btnDaftarRelawan.setCursor(Cursor.HAND);
            btnDaftarRelawan.setOnMouseEntered(e -> {
                btnDaftarRelawan.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 15 30;");
                applyScaleAnimation(btnDaftarRelawan, 1.05);
            });
            btnDaftarRelawan.setOnMouseExited(e -> {
                btnDaftarRelawan.setStyle("-fx-background-color: #1565C0; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 15 30;");
                applyScaleAnimation(btnDaftarRelawan, 1.0);
            });
        }
        
        // Setup hover untuk Navigation Labels dengan click handler
        if (navDashboard != null) {
            navDashboard.setOnMouseClicked(e -> navigateToHome(e));
            setupLabelHover(navDashboard, Color.ORANGE, Color.web("#FFA726"));
        }
        if (navProgram != null) {
            navProgram.setOnMouseClicked(e -> navigateToProgram(e));
            setupLabelHover(navProgram, Color.WHITE, Color.web("#FFA726"));
        }
        if (navHubungi != null) {
            navHubungi.setOnMouseClicked(e -> navigateToHubungi(e));
            setupLabelHover(navHubungi, Color.WHITE, Color.web("#FFA726"));
        }
        if (navTentang != null) {
            navTentang.setOnMouseClicked(e -> navigateToTentang(e));
            setupLabelHover(navTentang, Color.WHITE, Color.web("#FFA726"));
        }
    }
    
    private void setupLabelHover(Label label, Color defaultColor, Color hoverColor) {
        if (label != null) {
            label.setCursor(Cursor.HAND);
            label.setOnMouseEntered(e -> {
                label.setTextFill(hoverColor);
                applyScaleAnimation(label, 1.05);
            });
            label.setOnMouseExited(e -> {
                label.setTextFill(defaultColor);
                applyScaleAnimation(label, 1.0);
            });
        }
    }
    
    private void applyScaleAnimation(Node node, double scale) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), node);
        scaleTransition.setToX(scale);
        scaleTransition.setToY(scale);
        scaleTransition.play();
    }
    
    private void loadPrograms() {
        programs = programDAO.getAllPrograms();
        if (programs == null || programs.isEmpty()) {
            // Jika tidak ada program, tampilkan pesan default
            programNameLabel.setText("Belum ada program tersedia");
            programDescLabel.setText("Program akan ditampilkan di sini");
            btnNext.setDisable(true);
            btnPrev.setDisable(true);
        } else {
            btnNext.setDisable(programs.size() <= 1);
            btnPrev.setDisable(programs.size() <= 1);
        }
    }
    
    private void loadProgramImage(String coverPath) {
        if (coverPath == null || coverPath.isEmpty()) {
            // Load default image jika tidak ada cover
            try {
                Image defaultImage = new Image(getClass().getResourceAsStream("/assets/proker.jpeg"));
                programImageView.setImage(defaultImage);
            } catch (Exception e) {
                System.err.println("Gagal memuat gambar default");
            }
            return;
        }
        
        try {
            // Coba load dari assets/donasi/cover terlebih dahulu sebagai resource
            String resourceCoverPath = "/assets/donasi/cover/" + coverPath;
            Image image = new Image(getClass().getResourceAsStream(resourceCoverPath));
            if (image != null && !image.isError()) {
                programImageView.setImage(image);
                return;
            }
        } catch (Exception e) {
            // Lanjut ke metode lain
        }
        
        // Jika tidak ditemukan sebagai resource, coba folder assets di source
        try {
            File file = new File("src/main/java/assets/donasi/cover/" + coverPath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                programImageView.setImage(image);
                return;
            }
            
            // Coba path alternatif
            File altFile = new File("src/main/java/assets/" + coverPath);
            if (altFile.exists()) {
                Image image = new Image(altFile.toURI().toString());
                programImageView.setImage(image);
                return;
            }
        } catch (Exception ex) {
            System.err.println("Gagal memuat gambar: " + ex.getMessage());
        }
        
        // Jika semua gagal, load gambar default
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/assets/proker.jpeg"));
            programImageView.setImage(defaultImage);
        } catch (Exception e) {
            System.err.println("Gagal memuat gambar default");
        }
    }
    
    private void updateCarousel() {
        if (programs == null || programs.isEmpty()) {
            return;
        }
        
        if (currentIndex < 0) {
            currentIndex = programs.size() - 1;
        } else if (currentIndex >= programs.size()) {
            currentIndex = 0;
        }
        
        Program currentProgram = programs.get(currentIndex);
        
        // Update nama program
        programNameLabel.setText(currentProgram.getNama());
        
        // Update deskripsi (potong jika terlalu panjang)
        String deskripsi = currentProgram.getDeskripsi();
        if (deskripsi != null && deskripsi.length() > 100) {
            deskripsi = deskripsi.substring(0, 97) + "...";
        }
        programDescLabel.setText(deskripsi != null ? deskripsi : "Tidak ada deskripsi");
        
        // Update gambar
        loadProgramImage(currentProgram.getCover());
        
        // Update tombol navigasi
        btnNext.setDisable(programs.size() <= 1);
        btnPrev.setDisable(programs.size() <= 1);
    }
    
    @FXML
    private void nextProgram(ActionEvent event) {
        if (programs != null && !programs.isEmpty()) {
            currentIndex = (currentIndex + 1) % programs.size();
            updateCarousel();
        }
    }
    
    @FXML
    private void prevProgram(ActionEvent event) {
        if (programs != null && !programs.isEmpty()) {
            currentIndex = (currentIndex - 1 + programs.size()) % programs.size();
            updateCarousel();
        }
    }    

    @FXML
    private void btnMasuk(ActionEvent event) {
        handleLogin(event);
    }

    @FXML
    private void btnDonasi(ActionEvent event) {
        handleLogin(event);
    }
    
    @FXML
    private void handleDaftarRelawan(ActionEvent event) {
        try {
            URL url = new File("src/main/java/view/register.fxml").toURI().toURL();
            Parent root = FXMLLoader.load(url);
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading register page: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void navigateToHome(javafx.scene.input.MouseEvent event) {
        // Already on home page - scroll to top
        if (navDashboard != null && navDashboard.getScene() != null) {
            ScrollPane scrollPane = (ScrollPane) navDashboard.getScene().getRoot();
            if (scrollPane != null) {
                scrollPane.setVvalue(0);
            }
        }
    }
    
    @FXML
    private void navigateToProgram(javafx.scene.input.MouseEvent event) {
        navigateToPage("src/main/java/view/ProgramPage.fxml", event);
    }
    
    @FXML
    private void navigateToProgram(ActionEvent event) {
        navigateToPage("src/main/java/view/ProgramPage.fxml", event);
    }
    
    @FXML
    private void navigateToHubungi(javafx.scene.input.MouseEvent event) {
        navigateToPage("src/main/java/view/HubungiKami.fxml", event);
    }
    
    @FXML
    private void navigateToTentang(javafx.scene.input.MouseEvent event) {
        navigateToPage("src/main/java/view/TentangKami.fxml", event);
    }
    
    private void navigateToPage(String fxmlPath, Object eventSource) {
        try {
            URL url = new File(fxmlPath).toURI().toURL();
            Parent root = FXMLLoader.load(url);
            Scene scene = new Scene(root);
            
            Stage stage;
            if (eventSource instanceof Node) {
                stage = (Stage) ((Node) eventSource).getScene().getWindow();
            } else if (navDashboard != null && navDashboard.getScene() != null) {
                stage = (Stage) navDashboard.getScene().getWindow();
            } else {
                stage = new Stage();
            }
            
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error navigating to page: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleLogin(ActionEvent event) {
        try {
            URL url = new File("src/main/java/view/login.fxml").toURI().toURL();
            Parent root = FXMLLoader.load(url);
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading login page: " + e.getMessage());
        }
    }
}
