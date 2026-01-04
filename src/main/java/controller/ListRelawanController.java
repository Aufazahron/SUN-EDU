/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import dao.AnakDAO;
import dao.UserDAO;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.User;
import util.SessionManager;

/**
 * FXML Controller class untuk halaman List Relawan
 *
 * @author aufaz
 */
public class ListRelawanController implements Initializable {

    @FXML
    private VBox relawanListContainer;
    
    @FXML
    private ScrollPane scrollPane;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private Button pendingButton;
    
    @FXML
    private Button activeButton;
    
    @FXML
    private Label emptyLabel;
    
    private UserDAO userDAO;
    private AnakDAO anakDAO;
    private List<User> allRelawan = new ArrayList<>();
    private boolean showingPending = true;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userDAO = new UserDAO();
        anakDAO = new AnakDAO();
        loadRelawanData();
        renderRelawanList();
    }
    
    private void loadRelawanData() {
        if (showingPending) {
            allRelawan = userDAO.getPendingRelawan();
        } else {
            allRelawan = userDAO.getActiveRelawan();
        }
    }
    
    private void renderRelawanList() {
        relawanListContainer.getChildren().clear();
        
        String keyword = searchField != null ? searchField.getText() : "";
        List<User> filtered = filterRelawan(keyword);
        
        if (filtered.isEmpty()) {
            emptyLabel.setVisible(true);
            emptyLabel.setText(showingPending 
                ? "Tidak ada relawan yang perlu disetujui" 
                : "Tidak ada relawan aktif");
        } else {
            emptyLabel.setVisible(false);
            for (User relawan : filtered) {
                VBox card = createRelawanCard(relawan);
                relawanListContainer.getChildren().add(card);
            }
        }
    }
    
    private List<User> filterRelawan(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return new ArrayList<>(allRelawan);
        }
        
        List<User> filtered = new ArrayList<>();
        String lower = keyword.toLowerCase();
        for (User relawan : allRelawan) {
            if (relawan.getNama().toLowerCase().contains(lower) ||
                relawan.getUsername().toLowerCase().contains(lower) ||
                relawan.getEmail().toLowerCase().contains(lower)) {
                filtered.add(relawan);
            }
        }
        return filtered;
    }
    
    private VBox createRelawanCard(User relawan) {
        VBox card = new VBox();
        card.setPrefWidth(850);
        // Tinggi card disesuaikan: 120 untuk pending, 140 untuk aktif (karena ada info jumlah anak)
        card.setPrefHeight(showingPending ? 120 : 140);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setSpacing(10);
        card.setPadding(new Insets(20));
        
        HBox contentBox = new HBox(20);
        contentBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        // Info Section
        VBox infoBox = new VBox(8);
        infoBox.setPrefWidth(500);
        
        Label nameLabel = new Label(relawan.getNama());
        nameLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #020E4C;");
        
        Label usernameLabel = new Label("@" + relawan.getUsername());
        usernameLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");
        
        Label emailLabel = new Label("📧 " + relawan.getEmail());
        emailLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");
        
        Label telpLabel = new Label("📱 " + relawan.getTelp());
        telpLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");
        
        // Informasi jumlah anak (hanya untuk relawan aktif)
        int jumlahAnak = 0;
        if (!showingPending) {
            jumlahAnak = anakDAO.getJumlahAnakByRelawan(relawan.getId());
        }
        Label jumlahAnakLabel = new Label("👶 Memegang " + jumlahAnak + " anak");
        jumlahAnakLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #1976D2; -fx-font-weight: bold;");
        
        infoBox.getChildren().addAll(nameLabel, usernameLabel, emailLabel, telpLabel);
        if (!showingPending) {
            infoBox.getChildren().add(jumlahAnakLabel);
        }
        
        // Status Badge
        Label statusBadge = new Label(showingPending ? "PENDING" : "AKTIF");
        statusBadge.setStyle(showingPending 
            ? "-fx-background-color: #FFA500; -fx-text-fill: white; -fx-padding: 5 15; -fx-background-radius: 10; -fx-font-size: 12; -fx-font-weight: bold;"
            : "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 5 15; -fx-background-radius: 10; -fx-font-size: 12; -fx-font-weight: bold;");
        
        // Action Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        
        if (showingPending) {
            Button approveButton = new Button("Setujui");
            approveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 8 20; -fx-font-size: 14; -fx-font-weight: bold;");
            approveButton.setOnAction(e -> handleApprove(relawan));
            
            Button rejectButton = new Button("Tolak");
            rejectButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 8 20; -fx-font-size: 14; -fx-font-weight: bold;");
            rejectButton.setOnAction(e -> handleReject(relawan));
            
            buttonBox.getChildren().addAll(rejectButton, approveButton);
        } else {
            // Untuk relawan aktif, tambahkan tombol lihat detail anak
            // jumlahAnak sudah didefinisikan di atas
            if (jumlahAnak > 0) {
                Button lihatAnakButton = new Button("Lihat Anak (" + jumlahAnak + ")");
                lihatAnakButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 8 20; -fx-font-size: 14; -fx-font-weight: bold;");
                lihatAnakButton.setOnAction(e -> handleLihatAnak(relawan));
                buttonBox.getChildren().add(lihatAnakButton);
            } else {
                Label noAnakLabel = new Label("Belum ada anak");
                noAnakLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 14;");
                buttonBox.getChildren().add(noAnakLabel);
            }
        }
        
        contentBox.getChildren().addAll(infoBox, statusBadge, buttonBox);
        HBox.setHgrow(infoBox, javafx.scene.layout.Priority.ALWAYS);
        
        card.getChildren().add(contentBox);
        
        return card;
    }
    
    private void handleApprove(User relawan) {
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Apakah Anda yakin ingin menyetujui relawan " + relawan.getNama() + "?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                if (userDAO.approveRelawan(relawan.getId())) {
                    showAlert(AlertType.INFORMATION, "Berhasil", 
                        "Relawan " + relawan.getNama() + " berhasil disetujui!");
                    loadRelawanData();
                    renderRelawanList();
                } else {
                    showAlert(AlertType.ERROR, "Gagal", 
                        "Gagal menyetujui relawan. Silakan coba lagi.");
                }
            }
        });
    }
    
    private void handleReject(User relawan) {
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Apakah Anda yakin ingin menolak relawan " + relawan.getNama() + "?\nData relawan akan dihapus.");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                if (userDAO.rejectRelawan(relawan.getId())) {
                    showAlert(AlertType.INFORMATION, "Berhasil", 
                        "Relawan " + relawan.getNama() + " telah ditolak.");
                    loadRelawanData();
                    renderRelawanList();
                } else {
                    showAlert(AlertType.ERROR, "Gagal", 
                        "Gagal menolak relawan. Silakan coba lagi.");
                }
            }
        });
    }
    
    @FXML
    private void showPendingRelawan() {
        showingPending = true;
        pendingButton.setStyle("-fx-background-color: #020e4c; -fx-background-radius: 10; -fx-text-fill: white;");
        activeButton.setStyle("-fx-background-color: #d9d9d9; -fx-background-radius: 10; -fx-text-fill: #020e4c;");
        loadRelawanData();
        renderRelawanList();
    }
    
    @FXML
    private void showActiveRelawan() {
        showingPending = false;
        activeButton.setStyle("-fx-background-color: #020e4c; -fx-background-radius: 10; -fx-text-fill: white;");
        pendingButton.setStyle("-fx-background-color: #d9d9d9; -fx-background-radius: 10; -fx-text-fill: #020e4c;");
        loadRelawanData();
        renderRelawanList();
    }
    
    @FXML
    private void onSearchKeyReleased() {
        renderRelawanList();
    }
    
    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        navigateToPage("src/main/java/view/DashboardAdmin.fxml", event);
    }
    
    @FXML
    private void handleAnakMenu(ActionEvent event) {
        navigateToPage("src/main/java/view/AnakAdminView.fxml", event);
    }
    
    @FXML
    private void handleProgramKerjaMenu(ActionEvent event) {
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
            SessionManager.clearSession();
            
            URL url = new File("src/main/java/view/login.fxml").toURI().toURL();
            Parent root = FXMLLoader.load(url);
            Scene scene = new Scene(root);
            
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", 
                "Error saat logout: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleLihatAnak(User relawan) {
        List<String> namaAnakList = anakDAO.getNamaAnakByRelawan(relawan.getId());
        
        // Buat dialog untuk menampilkan list anak
        Alert dialog = new Alert(AlertType.INFORMATION);
        dialog.setTitle("Daftar Anak - " + relawan.getNama());
        dialog.setHeaderText("Anak yang dipegang oleh " + relawan.getNama() + " (" + namaAnakList.size() + " anak)");
        
        if (namaAnakList.isEmpty()) {
            dialog.setContentText("Belum ada anak yang dipegang oleh relawan ini.");
        } else {
            // Buat VBox untuk menampilkan list nama anak
            VBox contentBox = new VBox(10);
            contentBox.setPadding(new Insets(10));
            
            Label titleLabel = new Label("Daftar Nama Anak:");
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
            contentBox.getChildren().add(titleLabel);
            
            // Tampilkan setiap nama anak
            for (int i = 0; i < namaAnakList.size(); i++) {
                Label anakLabel = new Label((i + 1) + ". " + namaAnakList.get(i));
                anakLabel.setStyle("-fx-font-size: 13; -fx-padding: 5 0;");
                contentBox.getChildren().add(anakLabel);
            }
            
            ScrollPane scrollPane = new ScrollPane(contentBox);
            scrollPane.setPrefHeight(300);
            scrollPane.setPrefWidth(400);
            scrollPane.setFitToWidth(true);
            
            dialog.getDialogPane().setContent(scrollPane);
        }
        
        dialog.showAndWait();
    }
    
    private void navigateToPage(String fxmlPath, ActionEvent event) {
        try {
            URL url = new File(fxmlPath).toURI().toURL();
            Parent root = FXMLLoader.load(url);
            Scene scene = new Scene(root);
            
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", 
                "Tidak dapat membuka halaman: " + e.getMessage());
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

