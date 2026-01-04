package controller;

import dao.UserDAO;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.User;

/**
 * Controller untuk halaman register dengan fitur interaktif.
 */
public class RegisterController implements Initializable {

    @FXML
    private TextField namaField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField telpField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registerButton;

    @FXML
    private Button backToLoginButton;

    @FXML
    private StackPane modalOverlay;

    @FXML
    private Pane roleModal;

    @FXML
    private StackPane previewOverlay;

    @FXML
    private Pane previewModal;

    @FXML
    private VBox previewContainer;

    @FXML
    private Button editButton;

    @FXML
    private Button submitButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressLabel;

    @FXML
    private Label headerLabel;

    @FXML
    private VBox formContainer;

    @FXML
    private VBox buttonsContainer;

    @FXML
    private Pane registerCard;

    @FXML
    private VBox donaturCard;

    @FXML
    private VBox relawanCard;

    private UserDAO userDAO;
    private String selectedRole = ""; // "donatur" or "relawan"

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userDAO = new UserDAO();
        
        // Setup modal overlay
        setupModals();
        
        // Setup form interactions
        setupFormInteractions();
        
        // Setup button hover effects
        setupButtonEffects();
        
        // Setup role card hover effects
        setupRoleCardEffects();
        
        // Show role selection modal on load
        showRoleModal();
    }
    
    private void setupRoleCardEffects() {
        // Setup Donatur card
        if (donaturCard != null) {
            donaturCard.setCursor(Cursor.HAND);
            donaturCard.setOnMouseEntered(e -> {
                donaturCard.setStyle("-fx-background-color: #e0f0ff; -fx-background-radius: 15; -fx-padding: 20; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(2,14,252,0.3), 15, 0, 0, 5);");
                applyScaleAnimation(donaturCard, 1.05);
            });
            donaturCard.setOnMouseExited(e -> {
                donaturCard.setStyle("-fx-background-color: #f0f7ff; -fx-background-radius: 15; -fx-padding: 20; -fx-cursor: hand;");
                applyScaleAnimation(donaturCard, 1.0);
            });
        }
        
        // Setup Relawan card
        if (relawanCard != null) {
            relawanCard.setCursor(Cursor.HAND);
            relawanCard.setOnMouseEntered(e -> {
                relawanCard.setStyle("-fx-background-color: #ffe6cc; -fx-background-radius: 15; -fx-padding: 20; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(255,167,38,0.3), 15, 0, 0, 5);");
                applyScaleAnimation(relawanCard, 1.05);
            });
            relawanCard.setOnMouseExited(e -> {
                relawanCard.setStyle("-fx-background-color: #fff7e6; -fx-background-radius: 15; -fx-padding: 20; -fx-cursor: hand;");
                applyScaleAnimation(relawanCard, 1.0);
            });
        }
    }

    private void setupModals() {
        // Hide preview modal initially, but show role modal
        previewOverlay.setVisible(false);
        
        // Hide form initially
        if (registerCard != null) {
            registerCard.setVisible(false);
        }
        
        // Ensure roleModal is visible and managed initially
        if (roleModal != null) {
            roleModal.setVisible(true);
            roleModal.setManaged(true);
        }
        
        // Don't allow closing role modal by clicking outside (user must choose)
        // modalOverlay.setOnMouseClicked(e -> {
        //     if (e.getTarget() == modalOverlay) {
        //         hideRoleModal();
        //     }
        // });
        
        previewOverlay.setOnMouseClicked(e -> {
            if (e.getTarget() == previewOverlay) {
                hidePreviewModal();
            }
        });
    }

    private void setupFormInteractions() {
        // Add Enter key navigation
        namaField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                usernameField.requestFocus();
            }
        });
        
        usernameField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                emailField.requestFocus();
            }
        });
        
        emailField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                telpField.requestFocus();
            }
        });
        
        telpField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                passwordField.requestFocus();
            }
        });
        
        passwordField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                registerButton.fire();
            }
        });
        
        // Add focus effects
        addFocusEffects(namaField);
        addFocusEffects(usernameField);
        addFocusEffects(emailField);
        addFocusEffects(telpField);
        addFocusEffects(passwordField);
    }

    private void addFocusEffects(TextField field) {
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-padding: 8; -fx-border-color: #020EFC; -fx-border-width: 2; -fx-border-radius: 10; -fx-font-size: 14px;");
            } else {
                field.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10; -fx-padding: 8; -fx-border-color: #e0e0e0; -fx-border-radius: 10; -fx-font-size: 14px;");
            }
        });
    }

    private void addFocusEffects(PasswordField field) {
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-padding: 8; -fx-border-color: #020EFC; -fx-border-width: 2; -fx-border-radius: 10; -fx-font-size: 14px;");
            } else {
                field.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10; -fx-padding: 8; -fx-border-color: #e0e0e0; -fx-border-radius: 10; -fx-font-size: 14px;");
            }
        });
    }

    private void setupButtonEffects() {
        // Register button
        registerButton.setOnMouseEntered(e -> {
            registerButton.setStyle("-fx-background-color: #1a3dfc; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;");
            applyScaleAnimation(registerButton, 1.05);
        });
        registerButton.setOnMouseExited(e -> {
            registerButton.setStyle("-fx-background-color: #020EFC; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-size: 16px; -fx-font-weight: bold; -fx-cursor: hand;");
            applyScaleAnimation(registerButton, 1.0);
        });
        
        // Back button
        backToLoginButton.setOnMouseEntered(e -> {
            backToLoginButton.setStyle("-fx-background-color: #f5f5f5; -fx-text-fill: #1a237e; -fx-border-color: #020EFC; -fx-border-radius: 10; -fx-font-size: 14px; -fx-cursor: hand;");
            applyScaleAnimation(backToLoginButton, 1.05);
        });
        backToLoginButton.setOnMouseExited(e -> {
            backToLoginButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #666666; -fx-border-color: #e0e0e0; -fx-border-radius: 10; -fx-font-size: 14px; -fx-cursor: hand;");
            applyScaleAnimation(backToLoginButton, 1.0);
        });
        
        // Submit button
        submitButton.setOnMouseEntered(e -> {
            submitButton.setStyle("-fx-background-color: #1a3dfc; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
            applyScaleAnimation(submitButton, 1.05);
        });
        submitButton.setOnMouseExited(e -> {
            submitButton.setStyle("-fx-background-color: #020EFC; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
            applyScaleAnimation(submitButton, 1.0);
        });
    }

    private void applyScaleAnimation(Node node, double scale) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), node);
        scaleTransition.setToX(scale);
        scaleTransition.setToY(scale);
        scaleTransition.play();
    }

    @FXML
    private void selectDonatur(MouseEvent event) {
        selectedRole = "donatur";
        hideRoleModal();
        showRegisterForm();
        updateHeaderLabel();
    }

    @FXML
    private void selectRelawan(MouseEvent event) {
        selectedRole = "relawan";
        hideRoleModal();
        showRegisterForm();
        updateHeaderLabel();
    }
    
    private void updateHeaderLabel() {
        if (headerLabel != null) {
            if (selectedRole.equals("donatur")) {
                headerLabel.setText("💰 Daftar Donatur");
            } else if (selectedRole.equals("relawan")) {
                headerLabel.setText("🤝 Daftar Relawan");
            }
        }
    }
    
    private void showRegisterForm() {
        // Show register card
        if (registerCard != null) {
            registerCard.setVisible(true);
            updateProgress(1.0, "Langkah 2 dari 2: Isi Data Anda");
            animateCardIn();
        }
    }

    private void showRoleModal() {
        // Modal already visible in FXML, just animate it
        if (modalOverlay != null && modalOverlay.isVisible()) {
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), modalOverlay);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
            
            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), roleModal);
            scaleIn.setFromX(0.8);
            scaleIn.setFromY(0.8);
            scaleIn.setToX(1.0);
            scaleIn.setToY(1.0);
            scaleIn.play();
        }
    }

    private void hideRoleModal() {
        // Hide modal overlay
        if (modalOverlay != null) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), modalOverlay);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> modalOverlay.setVisible(false));
            fadeOut.play();
        }
        
        // Hide role modal (now it's separate from overlay)
        if (roleModal != null) {
            FadeTransition fadeOutModal = new FadeTransition(Duration.millis(300), roleModal);
            fadeOutModal.setFromValue(1.0);
            fadeOutModal.setToValue(0.0);
            fadeOutModal.setOnFinished(e -> {
                roleModal.setVisible(false);
                roleModal.setManaged(false);
            });
            fadeOutModal.play();
        }
    }

    private void animateCardIn() {
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(400), registerCard);
        scaleIn.setFromX(0.9);
        scaleIn.setFromY(0.9);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);
        scaleIn.play();
    }

    private void updateProgress(double progress, String text) {
        progressBar.setProgress(progress);
        progressLabel.setText(text);
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        // Validasi tipe akun
        if (selectedRole.isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Pilih tipe akun terlebih dahulu");
            showRoleModal();
            return;
        }

        String nama = namaField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String telp = telpField.getText().trim();
        String password = passwordField.getText();

        // Validasi input
        if (nama.isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Nama lengkap tidak boleh kosong");
            namaField.requestFocus();
            return;
        }

        if (username.isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Username tidak boleh kosong");
            usernameField.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Email tidak boleh kosong");
            emailField.requestFocus();
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showAlert(AlertType.WARNING, "Validasi", "Format email tidak valid");
            emailField.requestFocus();
            return;
        }

        if (telp.isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "No. telepon tidak boleh kosong");
            telpField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Password tidak boleh kosong");
            passwordField.requestFocus();
            return;
        }

        if (password.length() < 6) {
            showAlert(AlertType.WARNING, "Validasi", "Password minimal 6 karakter");
            passwordField.requestFocus();
            return;
        }

        // Show preview modal
        showPreviewModal(nama, username, email, telp, selectedRole);
    }

    private void showPreviewModal(String nama, String username, String email, String telp, String role) {
        previewContainer.getChildren().clear();
        
        // Create preview items
        addPreviewItem("👤", "Nama Lengkap", nama);
        addPreviewItem("🔑", "Username", username);
        addPreviewItem("📧", "Email", email);
        addPreviewItem("📱", "No. Telepon", telp);
        addPreviewItem("🎯", "Tipe Akun", role.equals("donatur") ? "Donatur 💰" : "Relawan 🤝");
        
        previewOverlay.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), previewOverlay);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
        
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), previewModal);
        scaleIn.setFromX(0.8);
        scaleIn.setFromY(0.8);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);
        scaleIn.play();
    }

    private void addPreviewItem(String emoji, String label, String value) {
        HBox item = new HBox(15);
        item.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        item.setPadding(new Insets(12, 15, 12, 15));
        item.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10;");
        
        Label emojiLabel = new Label(emoji);
        emojiLabel.setStyle("-fx-font-size: 24px;");
        
        VBox textBox = new VBox(3);
        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
        Label valueText = new Label(value);
        valueText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1a237e;");
        
        textBox.getChildren().addAll(labelText, valueText);
        item.getChildren().addAll(emojiLabel, textBox);
        
        previewContainer.getChildren().add(item);
    }

    private void hidePreviewModal() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), previewOverlay);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> previewOverlay.setVisible(false));
        fadeOut.play();
    }

    @FXML
    private void editData(ActionEvent event) {
        hidePreviewModal();
    }

    @FXML
    private void confirmSubmit(ActionEvent event) {
        String nama = namaField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String telp = telpField.getText().trim();
        String password = passwordField.getText();

        try {
            // Cek apakah username sudah ada
            if (userDAO.findByUsername(username) != null) {
                hidePreviewModal();
                showAlert(AlertType.ERROR, "Registrasi Gagal", 
                    "Username sudah digunakan. Silakan gunakan username lain.");
                usernameField.requestFocus();
                return;
            }

            // Buat user baru
            User newUser = new User();
            newUser.setNama(nama);
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setTelp(telp);
            newUser.setPassword(password);
            newUser.setRole(selectedRole);
            newUser.setStatus(false); // Default status non-aktif, perlu aktivasi admin

            // Simpan ke database
            if (userDAO.save(newUser)) {
                hidePreviewModal();
                
                // Success animation
                ScaleTransition successAnim = new ScaleTransition(Duration.millis(300), registerCard);
                successAnim.setFromX(1.0);
                successAnim.setFromY(1.0);
                successAnim.setToX(1.1);
                successAnim.setToY(1.1);
                successAnim.setAutoReverse(true);
                successAnim.setCycleCount(2);
                successAnim.play();
                
                successAnim.setOnFinished(e -> {
                    showAlert(AlertType.INFORMATION, "🎉 Registrasi Berhasil!", 
                        "Akun berhasil dibuat!\n" +
                        "Akun Anda akan diaktifkan oleh admin.\n" +
                        "Silakan login setelah akun diaktifkan.");
                    
                    // Kembali ke halaman login setelah delay
                    PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
                    delay.setOnFinished(ev -> handleBackToLogin(event));
                    delay.play();
                });
            } else {
                hidePreviewModal();
                showAlert(AlertType.ERROR, "Registrasi Gagal", 
                    "Terjadi kesalahan saat menyimpan data. Silakan coba lagi.");
            }
        } catch (Exception e) {
            hidePreviewModal();
            showAlert(AlertType.ERROR, "Error", 
                "Terjadi kesalahan saat registrasi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToLogin(ActionEvent event) {
        try {
            URL url = new File("src/main/java/view/login.fxml").toURI().toURL();
            Parent root = FXMLLoader.load(url);
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", 
                "Tidak dapat membuka halaman login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}
