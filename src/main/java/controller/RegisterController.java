package controller;

import dao.UserDAO;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;

/**
 * Controller untuk halaman register.
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
    private ComboBox<String> roleComboBox;

    @FXML
    private Button registerButton;

    @FXML
    private Button backToLoginButton;

    private UserDAO userDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userDAO = new UserDAO();
        
        // Mengisi ComboBox dengan role yang tersedia
        roleComboBox.getItems().addAll("donatur", "relawan");
        roleComboBox.setValue("donatur"); // Set default value
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String nama = namaField.getText();
        String username = usernameField.getText();
        String email = emailField.getText();
        String telp = telpField.getText();
        String password = passwordField.getText();
        String role = roleComboBox.getValue();

        // Validasi input
        if (nama == null || nama.trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Nama lengkap tidak boleh kosong");
            return;
        }

        if (username == null || username.trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Username tidak boleh kosong");
            return;
        }

        if (email == null || email.trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Email tidak boleh kosong");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showAlert(AlertType.WARNING, "Validasi", "Format email tidak valid");
            return;
        }

        if (telp == null || telp.trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "No. telepon tidak boleh kosong");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Password tidak boleh kosong");
            return;
        }

        if (password.length() < 6) {
            showAlert(AlertType.WARNING, "Validasi", "Password minimal 6 karakter");
            return;
        }

        if (role == null || role.trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Role harus dipilih");
            return;
        }

        try {
            // Cek apakah username sudah ada
            if (userDAO.findByUsername(username) != null) {
                showAlert(AlertType.ERROR, "Registrasi Gagal", 
                    "Username sudah digunakan. Silakan gunakan username lain.");
                return;
            }

            // Buat user baru
            User newUser = new User();
            newUser.setNama(nama.trim());
            newUser.setUsername(username.trim());
            newUser.setEmail(email.trim());
            newUser.setTelp(telp.trim());
            newUser.setPassword(password);
            newUser.setRole(role);
            newUser.setStatus(false); // Default status non-aktif, perlu aktivasi admin

            // Simpan ke database
            if (userDAO.save(newUser)) {
                showAlert(AlertType.INFORMATION, "Registrasi Berhasil", 
                    "Akun berhasil dibuat!\n" +
                    "Akun Anda akan diaktifkan oleh admin.\n" +
                    "Silakan login setelah akun diaktifkan.");
                
                // Kembali ke halaman login
                handleBackToLogin(event);
            } else {
                showAlert(AlertType.ERROR, "Registrasi Gagal", 
                    "Terjadi kesalahan saat menyimpan data. Silakan coba lagi.");
            }
        } catch (Exception e) {
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
