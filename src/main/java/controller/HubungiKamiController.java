package controller;

import dao.PesanDAO;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Pesan;

/**
 * Controller untuk halaman Hubungi Kami
 */
public class HubungiKamiController implements Initializable {
    
    @FXML
    private Label navDashboard;
    
    @FXML
    private Label navProgram;
    
    @FXML
    private Label navHubungi;
    
    @FXML
    private Label navTentang;
    
    @FXML
    private Button btnMasuk;
    
    @FXML
    private Button btnDaftarRelawan;
    
    @FXML
    private ImageView logoMainImageView;
    
    @FXML
    private TextField fieldNama;
    
    @FXML
    private TextField fieldEmail;
    
    @FXML
    private TextField fieldSubjek;
    
    @FXML
    private TextArea fieldPesan;
    
    @FXML
    private Button btnKirimPesan;
    
    private PesanDAO pesanDAO;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pesanDAO = new PesanDAO();
        setupHoverEffects();
        setupLogoClickable();
    }
    
    private void setupLogoClickable() {
        if (logoMainImageView != null) {
            logoMainImageView.setCursor(Cursor.HAND);
            logoMainImageView.setOnMouseClicked(e -> navigateToHome(e));
        }
    }
    
    private void setupHoverEffects() {
        if (navDashboard != null) {
            navDashboard.setOnMouseClicked(e -> navigateToHome(e));
            setupLabelHover(navDashboard, javafx.scene.paint.Color.WHITE, javafx.scene.paint.Color.web("#FFA726"));
        }
        if (navProgram != null) {
            navProgram.setOnMouseClicked(e -> navigateToProgram(e));
            setupLabelHover(navProgram, javafx.scene.paint.Color.WHITE, javafx.scene.paint.Color.web("#FFA726"));
        }
        if (navHubungi != null) {
            navHubungi.setOnMouseClicked(e -> navigateToHubungi(e));
            setupLabelHover(navHubungi, javafx.scene.paint.Color.ORANGE, javafx.scene.paint.Color.web("#FFA726"));
        }
        if (navTentang != null) {
            navTentang.setOnMouseClicked(e -> navigateToTentang(e));
            setupLabelHover(navTentang, javafx.scene.paint.Color.WHITE, javafx.scene.paint.Color.web("#FFA726"));
        }
        
        if (btnMasuk != null) {
            btnMasuk.setCursor(Cursor.HAND);
        }
    }
    
    private void setupLabelHover(Label label, javafx.scene.paint.Color defaultColor, javafx.scene.paint.Color hoverColor) {
        if (label != null) {
            label.setCursor(Cursor.HAND);
            label.setOnMouseEntered(e -> {
                label.setTextFill(hoverColor);
            });
            label.setOnMouseExited(e -> {
                label.setTextFill(defaultColor);
            });
        }
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
        // Already on hubungi page - do nothing
    }
    
    @FXML
    private void navigateToTentang(MouseEvent event) {
        navigateToPage("src/main/java/view/TentangKami.fxml", event);
    }
    
    @FXML
    private void btnMasuk(ActionEvent event) {
        handleLogin(event);
    }
    
    @FXML
    private void handleDaftarRelawan(ActionEvent event) {
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
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleKirimPesan(ActionEvent event) {
        // Validasi form
        if (fieldNama.getText() == null || fieldNama.getText().trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Peringatan", "Nama harus diisi!");
            return;
        }
        
        if (fieldEmail.getText() == null || fieldEmail.getText().trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Peringatan", "Email harus diisi!");
            return;
        }
        
        if (fieldSubjek.getText() == null || fieldSubjek.getText().trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Peringatan", "Subjek harus diisi!");
            return;
        }
        
        if (fieldPesan.getText() == null || fieldPesan.getText().trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Peringatan", "Pesan harus diisi!");
            return;
        }
        
        // Validasi email format sederhana
        String email = fieldEmail.getText().trim();
        if (!email.contains("@") || !email.contains(".")) {
            showAlert(AlertType.WARNING, "Peringatan", "Format email tidak valid!");
            return;
        }
        
        // Buat object Pesan
        Pesan pesan = new Pesan();
        pesan.setNama(fieldNama.getText().trim());
        pesan.setEmail(fieldEmail.getText().trim());
        pesan.setSubjek(fieldSubjek.getText().trim());
        pesan.setPesan(fieldPesan.getText().trim());
        pesan.setStatus("baru");
        
        // Simpan ke database
        if (pesanDAO.save(pesan)) {
            showAlert(AlertType.INFORMATION, "Berhasil", 
                "Pesan Anda berhasil dikirim! Kami akan segera menghubungi Anda.");
            
            // Clear form
            fieldNama.clear();
            fieldEmail.clear();
            fieldSubjek.clear();
            fieldPesan.clear();
        } else {
            showAlert(AlertType.ERROR, "Gagal", 
                "Gagal mengirim pesan. Silakan coba lagi.");
        }
    }
    
    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
