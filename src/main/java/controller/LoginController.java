/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import dao.HomeDAO;

/**
 * Controller untuk halaman login.
 */
public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;
    
    @FXML
    private Button registerButton;
    
    private UserDAO userDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userDAO = new UserDAO();
        
        // Test koneksi database saat inisialisasi
        if (!HomeDAO.testConnection()) {
            showAlert(AlertType.ERROR, "Database Error", 
                "Tidak dapat terhubung ke database. Pastikan:\n" +
                "1. MySQL sudah berjalan\n" +
                "2. Database 'edu-sun' sudah dibuat\n" +
                "3. Konfigurasi koneksi di HomeDAO.java sudah benar");
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Username dan password tidak boleh kosong");
            return;
        }

        try {
            // Mencari user di database
            User user = userDAO.login(username, password);
            
            if (user != null) {
                showAlert(AlertType.INFORMATION, "Login Berhasil", 
                    "Selamat datang, " + user.getNama() + "!\n" +
                    "Role: " + user.getRole());
                
                // TODO: Redirect ke halaman sesuai role
                // Contoh: jika admin -> ke halaman admin
                //         jika donatur -> ke halaman donatur
                //         jika relawan -> ke halaman relawan
                
            } else {
                showAlert(AlertType.ERROR, "Login Gagal", 
                    "Username atau password salah, atau akun tidak aktif!");
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", 
                "Terjadi kesalahan saat login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            URL url = new File("src/main/java/view/register.fxml").toURI().toURL();
            Parent root = FXMLLoader.load(url);
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", 
                "Tidak dapat membuka halaman register: " + e.getMessage());
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
