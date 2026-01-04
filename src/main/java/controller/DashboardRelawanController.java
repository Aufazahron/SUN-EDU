package controller;

import dao.AnakDAO;
import dao.DetailAnakDAO;
import dao.LaporanMonitoringDAO;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.File;
import model.Anak;
import model.LaporanMonitoring;
import util.SessionManager;

/**
 * FXML Controller class untuk Dashboard Relawan
 *
 * @author aufaz
 */
public class DashboardRelawanController implements Initializable {

    @FXML
    private Button logoutButton;
    
    @FXML
    private Text welcomeText;
    
    @FXML
    private Text anakDampinganCountText;
    
    @FXML
    private Text laporanTerkirimCountText;
    
    @FXML
    private ScrollPane laporanScrollPane;
    
    @FXML
    private ScrollPane anakScrollPane;
    
    @FXML
    private Button btnLihatSemuaLaporan;
    
    private AnakDAO anakDAO;
    private DetailAnakDAO detailAnakDAO;
    private LaporanMonitoringDAO laporanDAO;
    private int currentRelawanId;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        anakDAO = new AnakDAO();
        detailAnakDAO = new DetailAnakDAO();
        laporanDAO = new LaporanMonitoringDAO();
        
        // Ambil ID relawan dari session
        if (SessionManager.getCurrentUser() != null) {
            currentRelawanId = SessionManager.getCurrentUser().getId();
        }
        
        // Load user data dari session
        loadUserData();
        
        // Load statistik
        loadStatistics();
        
        // Load laporan terbaru
        loadLaporanTerbaru();
        
        // Load anak dampingan
        loadAnakDampingan();
    }
    
    private void loadUserData() {
        if (SessionManager.getCurrentUser() != null) {
            var user = SessionManager.getCurrentUser();
            if (welcomeText != null) {
                welcomeText.setText("Selamat Datang, " + user.getNama() + "!");
            }
            System.out.println("Relawan logged in: " + user.getNama());
        }
    }
    
    private void loadStatistics() {
        // Hitung jumlah anak dampingan
        List<Anak> anakList = anakDAO.getAnakByRelawan(currentRelawanId);
        int countAnak = anakList.size();
        if (anakDampinganCountText != null) {
            anakDampinganCountText.setText(String.valueOf(countAnak));
        }
        
        // Hitung jumlah laporan terkirim
        int countLaporan = laporanDAO.getJumlahLaporanByUser(currentRelawanId);
        if (laporanTerkirimCountText != null) {
            laporanTerkirimCountText.setText(String.valueOf(countLaporan));
        }
    }
    
    private void loadLaporanTerbaru() {
        if (laporanScrollPane == null) {
            return;
        }
        
        // Buat container VBox baru untuk laporan
        VBox laporanContainer = new VBox(8);
        laporanContainer.setPrefWidth(430);
        laporanContainer.setStyle("-fx-padding: 8;");
        
        // Ambil laporan terbaru untuk relawan ini
        List<LaporanMonitoring> laporanList = laporanDAO.getLaporanByUser(currentRelawanId);
        
        if (laporanList.isEmpty()) {
            Label emptyLabel = new Label("Belum ada laporan monitoring");
            emptyLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 14; -fx-padding: 20;");
            laporanContainer.getChildren().add(emptyLabel);
        } else {
            // Tampilkan maksimal 5 laporan terbaru
            int maxDisplay = Math.min(laporanList.size(), 5);
            for (int i = 0; i < maxDisplay; i++) {
                LaporanMonitoring laporan = laporanList.get(i);
                VBox card = createLaporanCard(laporan);
                laporanContainer.getChildren().add(card);
            }
        }
        
        // Set content ScrollPane
        AnchorPane scrollContent = new AnchorPane();
        scrollContent.getChildren().add(laporanContainer);
        laporanScrollPane.setContent(scrollContent);
    }
    
    private VBox createLaporanCard(LaporanMonitoring laporan) {
        AnchorPane card = new AnchorPane();
        card.setPrefHeight(73.0);
        card.setPrefWidth(436.0);
        card.setStyle("-fx-background-color: WHITE;");
        
        // Nama Laporan Monitoring
        Text namaLabel = new Text();
        String namaLaporan = laporan.getNama();
        if (namaLaporan == null || namaLaporan.isEmpty()) {
            // Fallback ke nama anak + tanggal jika nama laporan kosong
            Anak anak = anakDAO.getAnakById(laporan.getIdAnak());
            String namaAnak = anak != null ? anak.getNama() : "-";
            if (laporan.getTanggalMonitoring() != null) {
                namaLaporan = namaAnak + " - " + laporan.getTanggalMonitoring().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } else {
                namaLaporan = namaAnak;
            }
        }
        namaLabel.setText(namaLaporan);
        namaLabel.setLayoutX(7.0);
        namaLabel.setLayoutY(33.0);
        namaLabel.setStyle("-fx-fill: #020e4c;");
        namaLabel.setFont(Font.font("Segoe UI Bold", 24.0));
        
        // Tanggal Monitoring
        Text tanggalLabel = new Text();
        if (laporan.getTanggalMonitoring() != null) {
            tanggalLabel.setText(laporan.getTanggalMonitoring().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } else {
            tanggalLabel.setText("-");
        }
        tanggalLabel.setLayoutX(11.0);
        tanggalLabel.setLayoutY(60.0);
        tanggalLabel.setStyle("-fx-fill: #020e4c;");
        tanggalLabel.setFont(Font.font("Segoe UI", 12.0));
        
        // Button Detail
        Button detailButton = new Button("Detail Laporan");
        detailButton.setLayoutX(302.0);
        detailButton.setLayoutY(38.0);
        detailButton.setPrefHeight(30.0);
        detailButton.setPrefWidth(122.0);
        detailButton.setStyle("-fx-background-color: ffa500; -fx-background-radius: 15;");
        detailButton.setOnAction(e -> {
            // Navigate to detail atau list monitoring
            handleLihatSemuaLaporan(e);
        });
        
        card.getChildren().addAll(namaLabel, tanggalLabel, detailButton);
        
        // Wrap dalam VBox untuk konsistensi
        VBox wrapper = new VBox();
        wrapper.getChildren().add(card);
        wrapper.setSpacing(8);
        
        return wrapper;
    }
    
    private void loadAnakDampingan() {
        if (anakScrollPane == null) {
            return;
        }
        
        // Buat container VBox baru untuk anak
        VBox anakContainer = new VBox(8);
        anakContainer.setPrefWidth(430);
        anakContainer.setStyle("-fx-padding: 8;");
        
        // Ambil anak dampingan untuk relawan ini
        List<Anak> anakList = anakDAO.getAnakByRelawan(currentRelawanId);
        
        if (anakList.isEmpty()) {
            Label emptyLabel = new Label("Belum ada anak dampingan");
            emptyLabel.setStyle("-fx-text-fill: #333; -fx-font-size: 14; -fx-padding: 20;");
            anakContainer.getChildren().add(emptyLabel);
        } else {
            // Tampilkan maksimal 5 anak dampingan terbaru
            int maxDisplay = Math.min(anakList.size(), 5);
            for (int i = 0; i < maxDisplay; i++) {
                Anak anak = anakList.get(i);
                VBox card = createAnakCard(anak);
                anakContainer.getChildren().add(card);
            }
        }
        
        // Set content ScrollPane
        AnchorPane scrollContent = new AnchorPane();
        scrollContent.getChildren().add(anakContainer);
        anakScrollPane.setContent(scrollContent);
    }
    
    private VBox createAnakCard(Anak anak) {
        AnchorPane card = new AnchorPane();
        card.setPrefHeight(73.0);
        card.setPrefWidth(436.0);
        card.setStyle("-fx-background-color: WHITE;");
        card.setCursor(javafx.scene.Cursor.HAND);
        
        // Foto/avatar - ambil dari DetailAnak
        javafx.scene.image.ImageView fotoImageView = new javafx.scene.image.ImageView();
        fotoImageView.setFitHeight(48.0);
        fotoImageView.setFitWidth(52.0);
        fotoImageView.setLayoutX(9.0);
        fotoImageView.setLayoutY(19.0);
        fotoImageView.setPreserveRatio(true);
        
        // Rectangle placeholder jika foto tidak ada
        javafx.scene.shape.Rectangle avatarRect = new javafx.scene.shape.Rectangle();
        avatarRect.setArcHeight(5.0);
        avatarRect.setArcWidth(5.0);
        avatarRect.setFill(javafx.scene.paint.Color.DODGERBLUE);
        avatarRect.setHeight(48.0);
        avatarRect.setWidth(52.0);
        avatarRect.setLayoutX(9.0);
        avatarRect.setLayoutY(19.0);
        
        // Coba load foto dari DetailAnak
        boolean fotoLoaded = false;
        try {
            model.DetailAnak detailAnak = detailAnakDAO.getDetailByAnakId(anak.getId());
            if (detailAnak != null && detailAnak.getFoto() != null && !detailAnak.getFoto().isEmpty()) {
                File fotoFile = new File("src/main/java/assets/detail_anak/" + detailAnak.getFoto());
                if (!fotoFile.exists()) {
                    String projectRoot = System.getProperty("user.dir");
                    fotoFile = new File(projectRoot, "src/main/java/assets/detail_anak/" + detailAnak.getFoto());
                }
                
                if (fotoFile.exists()) {
                    javafx.scene.image.Image fotoImage = new javafx.scene.image.Image(fotoFile.toURI().toString());
                    fotoImageView.setImage(fotoImage);
                    fotoLoaded = true;
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading foto anak: " + e.getMessage());
        }
        
        // Nama Anak
        Text namaLabel = new Text(anak.getNama());
        namaLabel.setLayoutX(70.0);
        namaLabel.setLayoutY(37.0);
        namaLabel.setStyle("-fx-fill: #020e4c;");
        namaLabel.setFont(Font.font("Segoe UI Bold", 24.0));
        namaLabel.setWrappingWidth(220.0);
        
        // Umur (jika ada tanggal lahir)
        String umurText = "- Tahun";
        if (anak.getTanggalLahir() != null) {
            java.time.LocalDate sekarang = java.time.LocalDate.now();
            int umur = sekarang.getYear() - anak.getTanggalLahir().getYear();
            if (sekarang.getDayOfYear() < anak.getTanggalLahir().getDayOfYear()) {
                umur--;
            }
            umurText = umur + " Tahun";
        }
        Text umurLabel = new Text(umurText);
        umurLabel.setLayoutX(73.0);
        umurLabel.setLayoutY(57.0);
        umurLabel.setStyle("-fx-fill: #020e4c;");
        umurLabel.setFont(Font.font("Segoe UI", 12.0));
        
        // Button Detail (optional, bisa dihapus jika tidak perlu)
        Button detailButton = new Button("Detail");
        detailButton.setLayoutX(302.0);
        detailButton.setLayoutY(38.0);
        detailButton.setPrefHeight(30.0);
        detailButton.setPrefWidth(122.0);
        detailButton.setStyle("-fx-background-color: ffa500; -fx-background-radius: 15;");
        detailButton.setOnAction(e -> {
            handleNavigateToAnak(e);
        });
        
        if (fotoLoaded) {
            card.getChildren().addAll(fotoImageView, namaLabel, umurLabel, detailButton);
        } else {
            card.getChildren().addAll(avatarRect, namaLabel, umurLabel, detailButton);
        }
        
        // Click handler untuk navigasi ke detail
        card.setOnMouseClicked(e -> {
            handleNavigateToAnak(new ActionEvent());
        });
        
        // Wrap dalam VBox untuk konsistensi
        VBox wrapper = new VBox();
        wrapper.getChildren().add(card);
        wrapper.setSpacing(8);
        
        return wrapper;
    }
    
    @FXML
    private void handleLihatSemuaLaporan(ActionEvent event) {
        navigateToPage("src/main/java/view/ListMonitoringRelawan.fxml", event);
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Clear session
            SessionManager.clearSession();
            
            // Navigate kembali ke halaman login
            URL url = new File("src/main/java/view/login.fxml").toURI().toURL();
            Parent root = FXMLLoader.load(url);
            Scene scene = new Scene(root);
            
            // Get current stage from event source
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error saat logout: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleNavigateToAnak(ActionEvent event) {
        navigateToPage("src/main/java/view/AnakView.fxml", event);
    }
    
    @FXML
    private void handleNavigateToMonitoring(ActionEvent event) {
        navigateToPage("src/main/java/view/ListMonitoringRelawan.fxml", event);
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
            System.err.println("Error navigating to page: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
