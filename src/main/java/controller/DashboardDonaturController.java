/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import dao.DonasiDAO;
import dao.LaporanProgramDAO;
import dao.ProgramDAO;
import java.io.File;
import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.LaporanProgram;
import model.Program;
import util.SessionManager;

/**
 * FXML Controller class
 *
 * @author aufaz
 */
public class DashboardDonaturController implements Initializable {

    @FXML
    private FlowPane programListContainer;

    @FXML
    private VBox homeView;

    @FXML
    private VBox programListView;
    
    @FXML
    private VBox hasilProgramView;

    @FXML
    private Button homeMenuButton;

    @FXML
    private Button programMenuButton;
    
    @FXML
    private Button hasilProgramMenuButton;

    @FXML
    private Button supportedButton;

    @FXML
    private Button availableButton;

    @FXML
    private TextField searchField;

    @FXML
    private Label programListTitle;

    @FXML
    private BarChart<String, Number> donasiChart;

    @FXML
    private PieChart programPieChart;
    
    @FXML
    private Label namaLabel;
    
    @FXML
    private Label totalDonasiLabel;
    
    @FXML
    private Label jumlahProgramLabel;
    
    @FXML
    private javafx.scene.control.ComboBox<Integer> tahunComboBox;
    
    private ProgramDAO programDAO;
    private DonasiDAO donasiDAO;
    private LaporanProgramDAO laporanProgramDAO;
    private NumberFormat currencyFormat;
    private List<Program> allPrograms = new ArrayList<>();
    private List<Program> supportedPrograms = new ArrayList<>();
    private boolean showingSupported = false;
    
    @FXML
    private FlowPane hasilProgramContainer;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        programDAO = new ProgramDAO();
        donasiDAO = new DonasiDAO();
        laporanProgramDAO = new LaporanProgramDAO();
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        
        // Setup tahun combo box
        setupTahunComboBox();
        
        // Load user data
        loadUserData();
        
        // Load programs
        loadProgramData();
        buildCharts();
        showHomeView();
        filterAndRenderPrograms();
        loadHasilPrograms();
    }
    
    private void setupTahunComboBox() {
        if (tahunComboBox == null) {
            return;
        }
        
        // Tambahkan tahun saat ini dan 4 tahun sebelumnya
        int tahunSaatIni = java.time.Year.now().getValue();
        for (int i = 0; i < 5; i++) {
            tahunComboBox.getItems().add(tahunSaatIni - i);
        }
        
        // Set tahun saat ini sebagai default
        tahunComboBox.setValue(tahunSaatIni);
        
        // Handler ketika tahun berubah
        tahunComboBox.setOnAction(e -> buildCharts());
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
    
    private void loadProgramData() {
        allPrograms = programDAO.getAllPrograms();

        if (SessionManager.getCurrentUser() != null) {
            supportedPrograms = programDAO.getProgramsDonatedByUser(SessionManager.getCurrentUser().getId());
        } else {
            supportedPrograms = new ArrayList<>();
        }
    }
    
    private void filterAndRenderPrograms() {
        List<Program> source = showingSupported ? supportedPrograms : allPrograms;
        String keyword = searchField != null ? searchField.getText() : "";

        // Filter out programs that have reports (already completed) only for available programs
        List<Program> programsToFilter = new ArrayList<>();
        if (!showingSupported) {
            // For available programs, exclude those with reports
            List<Program> programsWithLaporan = programDAO.getProgramsWithLaporan();
            for (Program program : source) {
                boolean hasLaporan = programsWithLaporan.stream()
                    .anyMatch(p -> p.getId() == program.getId());
                if (!hasLaporan) {
                    programsToFilter.add(program);
                }
            }
        } else {
            // For supported programs, show all (don't filter out programs with reports)
            programsToFilter.addAll(source);
        }

        // Apply search filter
        List<Program> filtered = new ArrayList<>();
        if (keyword == null || keyword.isBlank()) {
            filtered.addAll(programsToFilter);
        } else {
            String lower = keyword.toLowerCase();
            for (Program program : programsToFilter) {
                if (program.getNama().toLowerCase().contains(lower) ||
                        program.getDeskripsi().toLowerCase().contains(lower) ||
                        program.getTempat().toLowerCase().contains(lower)) {
                    filtered.add(program);
                }
            }
        }

        renderPrograms(filtered);
    }

    private void renderPrograms(List<Program> programs) {
        if (programListContainer == null) {
            return;
        }

        programListContainer.getChildren().clear();
        for (Program program : programs) {
            VBox card = createProgramCard(program);
            programListContainer.getChildren().add(card);
        }

        if (programListTitle != null) {
            programListTitle.setText(showingSupported ? "Program yang Didukung" : "Program Kerja Tersedia");
        }
    }

    private void buildCharts() {
        if (donasiChart != null) {
            donasiChart.getData().clear();
            var series = new javafx.scene.chart.XYChart.Series<String, Number>();
            series.setName("Total Donasi");
            
            // Ambil data donasi per bulan untuk user yang sedang login
            if (SessionManager.getCurrentUser() != null) {
                int userId = SessionManager.getCurrentUser().getId();
                int tahun = tahunComboBox != null && tahunComboBox.getValue() != null 
                    ? tahunComboBox.getValue() 
                    : java.time.Year.now().getValue();
                
                java.util.Map<Integer, Long> donasiPerBulan = donasiDAO.getDonasiPerBulanByUser(userId, tahun);
                
                // Nama bulan dalam bahasa Indonesia
                String[] namaBulan = {
                    "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                    "Juli", "Agustus", "September", "Oktober", "November", "Desember"
                };
                
                // Tambahkan data untuk setiap bulan
                for (int bulan = 1; bulan <= 12; bulan++) {
                    long total = donasiPerBulan.getOrDefault(bulan, 0L);
                    series.getData().add(new javafx.scene.chart.XYChart.Data<>(namaBulan[bulan - 1], total));
                }
            }
            
            donasiChart.getData().add(series);
        }

        if (programPieChart != null) {
            programPieChart.getData().clear();
            
            // Hilangkan label lines (panah/notes)
            programPieChart.setLabelLineLength(0);
            
            // Ambil data alokasi kategori untuk user yang sedang login
            if (SessionManager.getCurrentUser() != null) {
                int userId = SessionManager.getCurrentUser().getId();
                int tahun = tahunComboBox != null && tahunComboBox.getValue() != null 
                    ? tahunComboBox.getValue() 
                    : java.time.Year.now().getValue();
                
                java.util.Map<String, Long> donasiPerKategori = donasiDAO.getDonasiPerKategoriByUser(userId, tahun);
                
                // Hitung total donasi
                long totalDonasi = donasiPerKategori.values().stream().mapToLong(Long::longValue).sum();
                
                // Jika tidak ada data, biarkan pie chart kosong
                if (totalDonasi > 0) {
                    // Inisialisasi semua kategori dengan 0
                    String[] kategoriList = {"Pendidikan", "Pengembangan Skill", "Pendampingan"};
                    
                    for (String kategori : kategoriList) {
                        long nominal = donasiPerKategori.getOrDefault(kategori, 0L);
                        
                        // Hanya tambahkan kategori yang memiliki donasi
                        if (nominal > 0) {
                            double persentase = (double) nominal / totalDonasi * 100;
                            
                            // Format label dengan kategori, persentase, dan dana
                            String label = String.format("%s (%.1f%%)", kategori, persentase);
                            PieChart.Data data = new PieChart.Data(label, nominal);
                            programPieChart.getData().add(data);
                            
                            // Tidak menambahkan tooltip (dihapus sesuai permintaan)
                        }
                    }
                }
                // Jika totalDonasi == 0, pie chart tetap kosong (tidak menambahkan data)
            }
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
            // Coba load dari assets/donasi/cover terlebih dahulu (resources)
            String resourceCoverPath = "/assets/donasi/cover/" + program.getCover();
            Image image = new Image(getClass().getResourceAsStream(resourceCoverPath));
            imageView.setImage(image);
        } catch (Exception e) {
            // Jika tidak ditemukan sebagai resource, coba folder assets di source
            try {
                File file = new File("src/main/java/assets/" + program.getCover());
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    imageView.setImage(image);
                } else {
                    System.err.println("Gambar tidak ditemukan: " + file.getPath());
                }
            } catch (Exception ex) {
                System.err.println("Gagal memuat gambar: " + ex.getMessage());
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
        progressLabel.setMouseTransparent(true);
        
        Label targetLabel = new Label(" / " + currencyFormat.format(target));
        targetLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #888;");
        targetLabel.setMouseTransparent(true);
        
        progressInfo.getChildren().addAll(progressLabel, targetLabel);
        
        // Progress Bar
        javafx.scene.control.ProgressBar progressBar = new javafx.scene.control.ProgressBar();
        progressBar.setPrefWidth(270);
        progressBar.setPrefHeight(8);
        double progress = (double) terkumpul / target;
        progressBar.setProgress(Math.min(progress, 1.0));
        progressBar.setStyle("-fx-accent: #FFA500;");
        progressBar.setMouseTransparent(true);
        
        // Donatur Count
        Label donaturLabel = new Label(program.getJumlahDonatur() + " Donatur");
        donaturLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666;");
        donaturLabel.setMouseTransparent(true);
        
        progressContainer.getChildren().addAll(progressInfo, progressBar, donaturLabel);
        donationBox.getChildren().add(progressContainer);
        
        // Set mouse transparent untuk label agar click event bisa sampai ke card
        nameLabel.setMouseTransparent(true);
        descLabel.setMouseTransparent(true);
        locationLabel.setMouseTransparent(true);
        
        contentBox.getChildren().addAll(nameLabel, descLabel, locationLabel, donationBox);
        
        card.getChildren().addAll(imageContainer, contentBox);
        
        // Click handler untuk membuka modal - set di akhir setelah semua child ditambahkan
        card.setOnMouseClicked((MouseEvent e) -> {
            openProgramDetailModal(program);
        });
        
        // Pastikan semua child elements tidak menghalangi click event
        card.setPickOnBounds(true);
        imageContainer.setMouseTransparent(false);
        contentBox.setMouseTransparent(false);
        
        return card;
    }
    
    @FXML
    private void showHomeView() {
        if (homeView != null && programListView != null && hasilProgramView != null) {
            homeView.setVisible(true);
            homeView.setManaged(true);
            programListView.setVisible(false);
            programListView.setManaged(false);
            hasilProgramView.setVisible(false);
            hasilProgramView.setManaged(false);
        }

        if (homeMenuButton != null && programMenuButton != null && hasilProgramMenuButton != null) {
            homeMenuButton.setStyle("-fx-background-color: white; -fx-text-fill: #020E4C; -fx-background-radius: 25;");
            programMenuButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-background-radius: 25;");
            hasilProgramMenuButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-background-radius: 25;");
        }
    }

    @FXML
    private void showProgramListView() {
        if (homeView != null && programListView != null && hasilProgramView != null) {
            homeView.setVisible(false);
            homeView.setManaged(false);
            programListView.setVisible(true);
            programListView.setManaged(true);
            hasilProgramView.setVisible(false);
            hasilProgramView.setManaged(false);
        }

        if (homeMenuButton != null && programMenuButton != null && hasilProgramMenuButton != null) {
            programMenuButton.setStyle("-fx-background-color: white; -fx-text-fill: #020E4C; -fx-background-radius: 25;");
            homeMenuButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-background-radius: 25;");
            hasilProgramMenuButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-background-radius: 25;");
        }

        filterAndRenderPrograms();
    }
    
    @FXML
    private void showHasilProgramView() {
        if (homeView != null && programListView != null && hasilProgramView != null) {
            homeView.setVisible(false);
            homeView.setManaged(false);
            programListView.setVisible(false);
            programListView.setManaged(false);
            hasilProgramView.setVisible(true);
            hasilProgramView.setManaged(true);
        }

        if (homeMenuButton != null && programMenuButton != null && hasilProgramMenuButton != null) {
            hasilProgramMenuButton.setStyle("-fx-background-color: white; -fx-text-fill: #020E4C; -fx-background-radius: 25;");
            homeMenuButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-background-radius: 25;");
            programMenuButton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-background-radius: 25;");
        }
        
        loadHasilPrograms();
    }

    @FXML
    private void showSupportedPrograms() {
        showingSupported = true;
        if (supportedButton != null && availableButton != null) {
            supportedButton.setStyle("-fx-background-color: #020E4C; -fx-text-fill: white; -fx-background-radius: 10;");
            availableButton.setStyle("-fx-background-color: #d9d9d9; -fx-text-fill: #020E4C; -fx-background-radius: 10;");
        }
        filterAndRenderPrograms();
    }

    @FXML
    private void showAvailablePrograms() {
        showingSupported = false;
        if (supportedButton != null && availableButton != null) {
            availableButton.setStyle("-fx-background-color: #020E4C; -fx-text-fill: white; -fx-background-radius: 10;");
            supportedButton.setStyle("-fx-background-color: #d9d9d9; -fx-text-fill: #020E4C; -fx-background-radius: 10;");
        }
        filterAndRenderPrograms();
    }

    @FXML
    private void onSearchKeyReleased() {
        filterAndRenderPrograms();
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
            var ownerNode = homeView != null && homeView.getScene() != null
                    ? homeView
                    : programListContainer;
            if (ownerNode != null && ownerNode.getScene() != null) {
                modalStage.initOwner(ownerNode.getScene().getWindow());
            }
            
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
        loadProgramData();
        buildCharts();
        filterAndRenderPrograms();
    }
    
    private void loadHasilPrograms() {
        if (hasilProgramContainer == null) {
            return;
        }
        
        List<Program> programsWithLaporan = programDAO.getProgramsWithLaporan();
        hasilProgramContainer.getChildren().clear();
        
        if (programsWithLaporan == null || programsWithLaporan.isEmpty()) {
            Label noLaporanLabel = new Label("Belum ada laporan program tersedia");
            noLaporanLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #666;");
            hasilProgramContainer.getChildren().add(noLaporanLabel);
            return;
        }
        
        for (Program program : programsWithLaporan) {
            LaporanProgram laporan = laporanProgramDAO.getLaporanByProgramId(program.getId());
            if (laporan != null) {
                VBox card = createLaporanProgramCard(program, laporan);
                hasilProgramContainer.getChildren().add(card);
            }
        }
    }
    
    private VBox createLaporanProgramCard(Program program, LaporanProgram laporan) {
        VBox card = new VBox();
        card.setPrefWidth(300);
        card.setPrefHeight(400);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setSpacing(10);
        card.setPadding(new Insets(0, 0, 15, 0));
        card.setCursor(javafx.scene.Cursor.HAND);
        
        // Hover effect
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
        
        // Click handler untuk melihat detail laporan
        card.setOnMouseClicked((MouseEvent e) -> {
            showLaporanDetail(program, laporan);
        });
        
        // Image Container
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefHeight(180);
        imageContainer.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 16 16 0 0;");
        
        ImageView imageView = new ImageView();
        imageView.setFitWidth(300);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(false);
        
        Rectangle clip = new Rectangle(300, 180);
        clip.setArcWidth(16);
        clip.setArcHeight(16);
        imageView.setClip(clip);
        
        // Load program image
        loadProgramImageForCard(imageView, program.getCover());
        
        imageContainer.getChildren().add(imageView);
        
        // Content Box
        VBox contentBox = new VBox(8);
        contentBox.setPadding(new Insets(15));
        
        // Program Name
        Label nameLabel = new Label(program.getNama());
        nameLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #020E4C;");
        nameLabel.setWrapText(true);
        
        // Badge "Selesai"
        Label statusBadge = new Label("✓ SELESAI");
        statusBadge.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 4 12; -fx-background-radius: 8; -fx-font-size: 11; -fx-font-weight: bold;");
        statusBadge.setMaxWidth(Double.MAX_VALUE);
        
        // Tanggal Pelaksanaan
        Label tanggalLabel = new Label();
        if (laporan.getTanggalPelaksanaan() != null) {
            tanggalLabel.setText("📅 " + laporan.getTanggalPelaksanaan().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"))));
        } else {
            tanggalLabel.setText("📅 Tanggal tidak tersedia");
        }
        tanggalLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666;");
        
        // Laporan Preview (potong jika terlalu panjang)
        String laporanText = laporan.getLaporan();
        if (laporanText != null && laporanText.length() > 120) {
            laporanText = laporanText.substring(0, 117) + "...";
        }
        Label laporanPreviewLabel = new Label(laporanText != null ? laporanText : "Tidak ada laporan");
        laporanPreviewLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #333;");
        laporanPreviewLabel.setWrapText(true);
        laporanPreviewLabel.setMaxHeight(60);
        
        // Dokumentasi indicator
        HBox dokumentasiBox = new HBox(5);
        if (laporan.getDokumentasi() != null && !laporan.getDokumentasi().isEmpty()) {
            Label docLabel = new Label("📎 Dokumentasi tersedia");
            docLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #1976D2;");
            dokumentasiBox.getChildren().add(docLabel);
        }
        
        contentBox.getChildren().addAll(nameLabel, statusBadge, tanggalLabel, laporanPreviewLabel, dokumentasiBox);
        
        card.getChildren().addAll(imageContainer, contentBox);
        
        return card;
    }
    
    private void loadProgramImageForCard(ImageView imageView, String coverPath) {
        if (coverPath == null || coverPath.isEmpty()) {
            try {
                Image defaultImage = new Image(getClass().getResourceAsStream("/assets/proker.jpeg"));
                imageView.setImage(defaultImage);
            } catch (Exception e) {
                // Ignore
            }
            return;
        }
        
        try {
            File file = new File("src/main/java/assets/donasi/cover/" + coverPath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
                return;
            }
            
            File altFile = new File("src/main/java/assets/" + coverPath);
            if (altFile.exists()) {
                Image image = new Image(altFile.toURI().toString());
                imageView.setImage(image);
                return;
            }
        } catch (Exception ex) {
            // Ignore
        }
        
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/assets/proker.jpeg"));
            imageView.setImage(defaultImage);
        } catch (Exception e) {
            // Ignore
        }
    }
    
    private void showLaporanDetail(Program program, LaporanProgram laporan) {
        Alert dialog = new Alert(AlertType.INFORMATION);
        dialog.setTitle("Laporan Program - " + program.getNama());
        dialog.setHeaderText("Laporan Pelaksanaan Program");
        
        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));
        contentBox.setPrefWidth(600);
        
        Label programLabel = new Label("Program: " + program.getNama());
        programLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #020E4C;");
        
        Label tanggalLabel = new Label();
        if (laporan.getTanggalPelaksanaan() != null) {
            tanggalLabel.setText("Tanggal Pelaksanaan: " + laporan.getTanggalPelaksanaan().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"))));
        } else {
            tanggalLabel.setText("Tanggal Pelaksanaan: Tidak tersedia");
        }
        tanggalLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");
        
        Label laporanLabel = new Label("Laporan:");
        laporanLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        
        ScrollPane scrollPane = new ScrollPane();
        Label laporanTextLabel = new Label(laporan.getLaporan() != null ? laporan.getLaporan() : "Tidak ada laporan");
        laporanTextLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #333;");
        laporanTextLabel.setWrapText(true);
        laporanTextLabel.setPrefWidth(550);
        scrollPane.setContent(laporanTextLabel);
        scrollPane.setPrefHeight(200);
        scrollPane.setFitToWidth(true);
        
        Label dokumentasiLabel = new Label();
        if (laporan.getDokumentasi() != null && !laporan.getDokumentasi().isEmpty()) {
            dokumentasiLabel.setText("📎 Dokumentasi: " + laporan.getDokumentasi());
            dokumentasiLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #1976D2;");
        } else {
            dokumentasiLabel.setText("Tidak ada dokumentasi");
            dokumentasiLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #999;");
        }
        
        contentBox.getChildren().addAll(programLabel, tanggalLabel, laporanLabel, scrollPane, dokumentasiLabel);
        
        dialog.getDialogPane().setContent(contentBox);
        dialog.getDialogPane().setPrefWidth(650);
        dialog.showAndWait();
    }
    
    @FXML
    private void handleLogout(javafx.event.ActionEvent event) {
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
}
