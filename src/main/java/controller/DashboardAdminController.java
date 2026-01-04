/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import dao.AktivitasDAO;
import dao.AnakDAO;
import dao.LaporanMonitoringDAO;
import dao.ProgramDAO;
import dao.UserDAO;
import java.io.File;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Aktivitas;
import model.User;
import util.SessionManager;

/**
 * FXML Controller class untuk Dashboard Admin
 *
 * @author aufaz
 */
public class DashboardAdminController implements Initializable {

    @FXML
    private Button logoutButton;
    
    @FXML
    private Label relawanPendingCountLabel;
    
    @FXML
    private Label anakPendingCountLabel;
    
    @FXML
    private Label laporanPendingCountLabel;
    
    @FXML
    private ScrollPane relawanPendingScrollPane;
    
    @FXML
    private VBox relawanPendingContainer;
    
    @FXML
    private ScrollPane aktivitasScrollPane;
    
    @FXML
    private VBox aktivitasContainer;
    
    @FXML
    private Label emptyAktivitasLabel;
    
    private UserDAO userDAO;
    private AnakDAO anakDAO;
    private LaporanMonitoringDAO laporanDAO;
    private ProgramDAO programDAO;
    private AktivitasDAO aktivitasDAO;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userDAO = new UserDAO();
        anakDAO = new AnakDAO();
        laporanDAO = new LaporanMonitoringDAO();
        programDAO = new ProgramDAO();
        aktivitasDAO = new AktivitasDAO();
        
        // Load user data dari session
        loadUserData();
        
        // Load statistik
        loadStatistics();
        
        // Load relawan pending list
        loadRelawanPendingList();
        
        // Load aktivitas terbaru
        loadAktivitasTerbaru();
    }
    
    private void loadUserData() {
        if (SessionManager.getCurrentUser() != null) {
            var user = SessionManager.getCurrentUser();
            System.out.println("Admin logged in: " + user.getNama());
        }
    }
    
    private void loadStatistics() {
        // Hitung jumlah relawan pending
        List<User> pendingRelawan = userDAO.getPendingRelawan();
        int countPendingRelawan = pendingRelawan.size();
        
        // Update label jika ada
        if (relawanPendingCountLabel != null) {
            relawanPendingCountLabel.setText(String.valueOf(countPendingRelawan));
        }
        
        // Hitung jumlah anak (semua anak)
        int countAnak = anakDAO.getAllAnak().size();
        if (anakPendingCountLabel != null) {
            anakPendingCountLabel.setText(String.valueOf(countAnak));
        }
        
        // Hitung jumlah laporan (semua laporan)
        int countLaporan = laporanDAO.getAllLaporan().size();
        if (laporanPendingCountLabel != null) {
            laporanPendingCountLabel.setText(String.valueOf(countLaporan));
        }
    }
    
    private void loadRelawanPendingList() {
        if (relawanPendingContainer == null) {
            return;
        }
        
        relawanPendingContainer.getChildren().clear();
        
        List<User> pendingRelawan = userDAO.getPendingRelawan();
        
        if (pendingRelawan.isEmpty()) {
            Label emptyLabel = new Label("Tidak ada relawan yang perlu disetujui");
            emptyLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 14;");
            relawanPendingContainer.getChildren().add(emptyLabel);
            return;
        }
        
        // Tampilkan maksimal 5 relawan pending terbaru
        int maxDisplay = Math.min(pendingRelawan.size(), 5);
        for (int i = 0; i < maxDisplay; i++) {
            User relawan = pendingRelawan.get(i);
            HBox card = createRelawanPendingCard(relawan);
            relawanPendingContainer.getChildren().add(card);
        }
        
        // Jika ada lebih dari 5, tambahkan tombol "Lihat Semua"
        if (pendingRelawan.size() > 5) {
            Button btnLihatSemua = new Button("Lihat Semua (" + pendingRelawan.size() + ")");
            btnLihatSemua.setStyle("-fx-background-color: #020E4C; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 8 20;");
            btnLihatSemua.setOnAction(e -> handleRelawanMenu(e));
            relawanPendingContainer.getChildren().add(btnLihatSemua);
        }
    }
    
    private HBox createRelawanPendingCard(User relawan) {
        HBox card = new HBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setPrefWidth(400);
        
        VBox infoBox = new VBox(5);
        infoBox.setPrefWidth(250);
        
        Label nameLabel = new Label(relawan.getNama());
        nameLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #020E4C;");
        
        Label emailLabel = new Label("📧 " + relawan.getEmail());
        emailLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666;");
        
        Label usernameLabel = new Label("@" + relawan.getUsername());
        usernameLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666;");
        
        infoBox.getChildren().addAll(nameLabel, emailLabel, usernameLabel);
        
        Button btnApprove = new Button("Approve");
        btnApprove.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 15;");
        btnApprove.setOnAction(e -> handleQuickApprove(relawan));
        
        card.getChildren().addAll(infoBox, btnApprove);
        HBox.setHgrow(infoBox, javafx.scene.layout.Priority.ALWAYS);
        
        return card;
    }
    
    private void handleQuickApprove(User relawan) {
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Approve relawan " + relawan.getNama() + "?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                if (userDAO.approveRelawan(relawan.getId())) {
                    showAlert(AlertType.INFORMATION, "Berhasil", 
                        "Relawan " + relawan.getNama() + " berhasil disetujui!");
                    // Refresh data
                    loadStatistics();
                    loadRelawanPendingList();
                } else {
                    showAlert(AlertType.ERROR, "Gagal", 
                        "Gagal menyetujui relawan. Silakan coba lagi.");
                }
            }
        });
    }
    
    @FXML
    private void handleRelawanMenu(ActionEvent event) {
        navigateToPage("src/main/java/view/ListRelawan.fxml", event);
    }
    
    @FXML
    private void handleAnakMenu(ActionEvent event) {
        // Navigate ke halaman manajemen anak untuk admin
        navigateToPage("src/main/java/view/AnakAdminView.fxml", event);
    }
    
    @FXML
    private void handleProgramKerjaMenu(ActionEvent event) {
        // Navigate ke halaman manajemen program kerja untuk admin
        navigateToPage("src/main/java/view/ProgramAdminView.fxml", event);
    }
    
    @FXML
    private void handleLaporanMenu(ActionEvent event) {
        navigateToPage("src/main/java/view/ListMonitoringAdmin.fxml", event);
    }
    
    @FXML
    private void handleLaporanProgramMenu(ActionEvent event) {
        navigateToPage("src/main/java/view/LaporanProgramAdminView.fxml", event);
    }
    
    @FXML
    private void handleFaqMenu(ActionEvent event) {
        navigateToPage("src/main/java/view/FaqAdminView.fxml", event);
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Clear session
            SessionManager.clearSession();
            
            // Navigate kembali ke halaman login
            navigateToPage("src/main/java/view/login.fxml", event);
        } catch (Exception e) {
            System.err.println("Error saat logout: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void navigateToPage(String fxmlPath, ActionEvent event) {
        try {
            URL url = new File(fxmlPath).toURI().toURL();
            Parent root = FXMLLoader.load(url);
            Scene scene = new Scene(root);
            
            Stage stage;
            if (event != null && event.getSource() instanceof javafx.scene.Node) {
                stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            } else if (aktivitasContainer != null && aktivitasContainer.getScene() != null) {
                stage = (Stage) aktivitasContainer.getScene().getWindow();
            } else if (relawanPendingContainer != null && relawanPendingContainer.getScene() != null) {
                stage = (Stage) relawanPendingContainer.getScene().getWindow();
            } else {
                stage = new Stage();
            }
            
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error navigating to page: " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Tidak dapat membuka halaman: " + e.getMessage());
        }
    }
    
    private void loadAktivitasTerbaru() {
        if (aktivitasContainer == null) {
            return;
        }
        
        aktivitasContainer.getChildren().clear();
        
        // Ambil 10 aktivitas terbaru
        List<Aktivitas> aktivitasList = aktivitasDAO.getAktivitasTerbaru(10);
        
        if (aktivitasList.isEmpty()) {
            if (emptyAktivitasLabel != null) {
                emptyAktivitasLabel.setVisible(true);
            }
            return;
        }
        
        if (emptyAktivitasLabel != null) {
            emptyAktivitasLabel.setVisible(false);
        }
        
        // Tampilkan aktivitas
        for (Aktivitas aktivitas : aktivitasList) {
            VBox card = createAktivitasCard(aktivitas);
            aktivitasContainer.getChildren().add(card);
        }
    }
    
    private VBox createAktivitasCard(Aktivitas aktivitas) {
        VBox card = new VBox(8);
        card.setPrefWidth(430);
        card.setStyle("-fx-background-color: " + aktivitas.getColor() + "; -fx-background-radius: 10; -fx-padding: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        // Icon
        Label iconLabel = new Label(aktivitas.getIcon());
        iconLabel.setStyle("-fx-font-size: 24px;");
        
        // Judul dan waktu
        VBox infoBox = new VBox(3);
        Label judulLabel = new Label(aktivitas.getJudul());
        judulLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #020E4C;");
        
        String waktuText = "";
        if (aktivitas.getWaktu() != null) {
            waktuText = aktivitas.getWaktu().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", new Locale("id")));
        }
        Label waktuLabel = new Label(waktuText);
        waktuLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        
        infoBox.getChildren().addAll(judulLabel, waktuLabel);
        
        headerBox.getChildren().addAll(iconLabel, infoBox);
        HBox.setHgrow(infoBox, javafx.scene.layout.Priority.ALWAYS);
        
        // Deskripsi
        Label deskripsiLabel = new Label(aktivitas.getDeskripsi());
        deskripsiLabel.setWrapText(true);
        deskripsiLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #333;");
        
        card.getChildren().addAll(headerBox, deskripsiLabel);
        
        // Tambahkan click handler untuk navigasi
        card.setOnMouseClicked(e -> handleAktivitasClick(aktivitas));
        card.setStyle(card.getStyle() + " -fx-cursor: hand;");
        
        return card;
    }
    
    private void handleAktivitasClick(Aktivitas aktivitas) {
        switch (aktivitas.getTipe()) {
            case "anak_baru":
                navigateToPage("src/main/java/view/AnakAdminView.fxml", null);
                break;
            case "laporan_baru":
                navigateToPage("src/main/java/view/ListMonitoringAdmin.fxml", null);
                break;
            case "donasi_baru":
                // Bisa navigasi ke halaman program atau donasi
                navigateToPage("src/main/java/view/ProgramAdminView.fxml", null);
                break;
            case "pesan_baru":
                navigateToPage("src/main/java/view/FaqAdminView.fxml", null);
                break;
            case "relawan_baru":
                navigateToPage("src/main/java/view/ListRelawan.fxml", null);
                break;
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
