package controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TentangKamiController implements Initializable {

    @FXML
    private Label navHome;
    
    @FXML
    private Label navProgram;
    
    @FXML
    private Label navHubungi;
    
    @FXML
    private Label navTentang;
    
    @FXML
    private Button btnMasuk;
    
    @FXML
    private ImageView logoImageView;
    
    @FXML
    private ImageView logoMainImageView;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupHoverEffects();
        setupLogoClickable();
    }
    
    private void setupLogoClickable() {
        if (logoImageView != null) {
            logoImageView.setCursor(Cursor.HAND);
            logoImageView.setOnMouseClicked(e -> navigateToHome(e));
        }
        if (logoMainImageView != null) {
            logoMainImageView.setCursor(Cursor.HAND);
            logoMainImageView.setOnMouseClicked(e -> navigateToHome(e));
        }
    }
    
    private void setupHoverEffects() {
        // Setup navigation labels dengan click handler
        if (navHome != null) {
            navHome.setOnMouseClicked(e -> navigateToHome(e));
            setupLabelHover(navHome, Color.WHITE, Color.web("#FFA726"));
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
            setupLabelHover(navTentang, Color.ORANGE, Color.web("#FFA726"));
        }
        
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
    
    @FXML
    private void navigateToHome(MouseEvent event) {
        navigateToPage("src/main/java/view/LandingPage.fxml", event);
    }
    
    @FXML
    private void navigateToProgram(MouseEvent event) {
        navigateToPage("src/main/java/view/ProgramPage.fxml", event);
    }
    
    @FXML
    private void navigateToHubungi(MouseEvent event) {
        navigateToPage("src/main/java/view/HubungiKami.fxml", event);
    }
    
    @FXML
    private void navigateToTentang(MouseEvent event) {
        // Already on tentang kami page - scroll to top
        if (navTentang != null && navTentang.getScene() != null) {
            ScrollPane scrollPane = (ScrollPane) navTentang.getScene().getRoot();
            if (scrollPane != null) {
                scrollPane.setVvalue(0);
            }
        }
    }
    
    @FXML
    private void btnMasuk(ActionEvent event) {
        handleLogin(event);
    }
    
    private void navigateToPage(String fxmlPath, MouseEvent event) {
        try {
            URL url = new File(fxmlPath).toURI().toURL();
            Parent root = FXMLLoader.load(url);
            Scene scene = new Scene(root);
            
            Stage stage;
            if (event != null && event.getSource() instanceof Node) {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            } else if (navHome != null && navHome.getScene() != null) {
                stage = (Stage) navHome.getScene().getWindow();
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

