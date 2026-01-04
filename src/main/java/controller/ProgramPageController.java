package controller;

import java.io.File;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Program;
import model.LaporanProgram;
import controller.ProgramDetailPublicModalController;
import dao.ProgramDAO;
import dao.LaporanProgramDAO;
import java.time.format.DateTimeFormatter;
import javafx.scene.layout.HBox;

public class ProgramPageController implements Initializable {

    @FXML
    private FlowPane programListContainer;
    
    @FXML
    private TextField searchField;
    
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
    private Button btnProgramBerjalan;
    
    @FXML
    private Button btnProgramTerlaksana;
    
    @FXML
    private FlowPane hasilProgramContainer;
    
    private ProgramDAO programDAO;
    private LaporanProgramDAO laporanProgramDAO;
    private List<Program> allPrograms;
    private NumberFormat currencyFormat;
    private boolean showingTerlaksana = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        programDAO = new ProgramDAO();
        laporanProgramDAO = new LaporanProgramDAO();
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        loadPrograms();
        loadHasilPrograms();
        setupHoverEffects();
        setupLogoClickable();
    }
    
    private void setupLogoClickable() {
        if (logoImageView != null) {
            logoImageView.setCursor(Cursor.HAND);
            logoImageView.setOnMouseClicked(e -> navigateToHome(e));
        }
    }
    
    private void loadPrograms() {
        allPrograms = programDAO.getAllPrograms();
        filterAndRenderPrograms();
    }
    
    private void renderPrograms(List<Program> programs) {
        programListContainer.getChildren().clear();
        
        if (programs == null || programs.isEmpty()) {
            Label noProgramLabel = new Label("Belum ada program tersedia");
            noProgramLabel.setStyle("-fx-font-size: 18; -fx-text-fill: #666;");
            programListContainer.getChildren().add(noProgramLabel);
            return;
        }
        
        for (Program program : programs) {
            VBox card = createProgramCard(program);
            programListContainer.getChildren().add(card);
        }
    }
    
    private VBox createProgramCard(Program program) {
        VBox card = new VBox();
        card.setPrefWidth(280);
        card.setPrefHeight(400);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setSpacing(0);
        card.setCursor(Cursor.HAND);
        
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
        
        // Image Container
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefHeight(180);
        imageContainer.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 16 16 0 0;");
        
        ImageView imageView = new ImageView();
        imageView.setFitWidth(280);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(false);
        
        Rectangle clip = new Rectangle(280, 180);
        clip.setArcWidth(16);
        clip.setArcHeight(16);
        imageView.setClip(clip);
        
        loadProgramImage(imageView, program.getCover());
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
        String deskripsi = program.getDeskripsi();
        if (deskripsi != null && deskripsi.length() > 100) {
            deskripsi = deskripsi.substring(0, 97) + "...";
        }
        Label descLabel = new Label(deskripsi != null ? deskripsi : "Tidak ada deskripsi");
        descLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");
        descLabel.setWrapText(true);
        descLabel.setMaxHeight(50);
        
        // Location
        Label locationLabel = new Label("📍 " + (program.getTempat() != null ? program.getTempat() : "Lokasi tidak tersedia"));
        locationLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #999;");
        
        // Status Badge (jika program selesai)
        Label statusBadge = null;
        String status = programDAO.getStatusProgram(program.getId());
        if ("selesai".equals(status)) {
            statusBadge = new Label("✓ SELESAI");
            statusBadge.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 4 12; -fx-background-radius: 8; -fx-font-size: 11; -fx-font-weight: bold;");
            statusBadge.setMaxWidth(Double.MAX_VALUE);
        }
        
        // Donation Info
        VBox donationBox = new VBox();
        donationBox.setSpacing(5);
        donationBox.setPadding(new Insets(10, 0, 0, 0));
        
        long terkumpul = program.getDonasiTerkumpul();
        long target = program.getTargetDonasi() > 0 ? program.getTargetDonasi() : 1;
        double progress = Math.min((double) terkumpul / target, 1.0);
        
        Label progressLabel = new Label("Terkumpul: " + currencyFormat.format(terkumpul) + " / " + currencyFormat.format(target));
        progressLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #020E4C;");
        
        javafx.scene.control.ProgressBar progressBar = new javafx.scene.control.ProgressBar(progress);
        progressBar.setPrefWidth(250);
        progressBar.setStyle("-fx-accent: #FFA726;");
        
        Label donaturLabel = new Label(program.getJumlahDonatur() + " Donatur");
        donaturLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #999;");
        
        donationBox.getChildren().addAll(progressLabel, progressBar, donaturLabel);
        
        if (statusBadge != null) {
            contentBox.getChildren().addAll(nameLabel, statusBadge, descLabel, locationLabel, donationBox);
        } else {
            contentBox.getChildren().addAll(nameLabel, descLabel, locationLabel, donationBox);
        }
        
        card.getChildren().addAll(imageContainer, contentBox);
        
        // Click handler untuk membuka modal
        card.setOnMouseClicked((MouseEvent e) -> {
            openProgramDetailModal(program);
        });
        
        return card;
    }
    
    private void loadProgramImage(ImageView imageView, String coverPath) {
        if (coverPath == null || coverPath.isEmpty()) {
            try {
                Image defaultImage = new Image(getClass().getResourceAsStream("/assets/proker.jpeg"));
                imageView.setImage(defaultImage);
            } catch (Exception e) {
                System.err.println("Gagal memuat gambar default");
            }
            return;
        }
        
        try {
            String resourceCoverPath = "/assets/donasi/cover/" + coverPath;
            Image image = new Image(getClass().getResourceAsStream(resourceCoverPath));
            if (image != null && !image.isError()) {
                imageView.setImage(image);
                return;
            }
        } catch (Exception e) {
            // Lanjut ke metode lain
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
            System.err.println("Gagal memuat gambar: " + ex.getMessage());
        }
        
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/assets/proker.jpeg"));
            imageView.setImage(defaultImage);
        } catch (Exception e) {
            System.err.println("Gagal memuat gambar default");
        }
    }
    
    @FXML
    private void onSearchKeyReleased(KeyEvent event) {
        filterAndRenderPrograms();
    }
    
    @FXML
    private void showProgramBerjalan(ActionEvent event) {
        showingTerlaksana = false;
        if (btnProgramBerjalan != null && btnProgramTerlaksana != null) {
            btnProgramBerjalan.setStyle("-fx-background-color: #020E4C; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 10 20;");
            btnProgramTerlaksana.setStyle("-fx-background-color: #d9d9d9; -fx-text-fill: #020E4C; -fx-background-radius: 10; -fx-padding: 10 20;");
        }
        filterAndRenderPrograms();
    }
    
    @FXML
    private void showProgramTerlaksana(ActionEvent event) {
        showingTerlaksana = true;
        if (btnProgramBerjalan != null && btnProgramTerlaksana != null) {
            btnProgramTerlaksana.setStyle("-fx-background-color: #020E4C; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 10 20;");
            btnProgramBerjalan.setStyle("-fx-background-color: #d9d9d9; -fx-text-fill: #020E4C; -fx-background-radius: 10; -fx-padding: 10 20;");
        }
        filterAndRenderPrograms();
    }
    
    private void filterAndRenderPrograms() {
        List<Program> programsToShow;
        
        if (showingTerlaksana) {
            // Program yang sudah terlaksana (sudah ada laporan)
            programsToShow = programDAO.getProgramsWithLaporan();
        } else {
            // Program yang belum terlaksana (belum ada laporan)
            List<Program> allProgramsList = programDAO.getAllPrograms();
            List<Program> programsWithLaporan = programDAO.getProgramsWithLaporan();
            programsToShow = allProgramsList.stream()
                .filter(p -> programsWithLaporan.stream().noneMatch(lp -> lp.getId() == p.getId()))
                .collect(java.util.stream.Collectors.toList());
        }
        
        // Apply search filter
        String searchText = searchField.getText().toLowerCase();
        if (!searchText.isEmpty()) {
            programsToShow = programsToShow.stream()
                .filter(p -> (p.getNama() != null && p.getNama().toLowerCase().contains(searchText)) ||
                            (p.getDeskripsi() != null && p.getDeskripsi().toLowerCase().contains(searchText)) ||
                            (p.getTempat() != null && p.getTempat().toLowerCase().contains(searchText)))
                .collect(java.util.stream.Collectors.toList());
        }
        
        renderPrograms(programsToShow);
    }
    
    private void setupHoverEffects() {
        // Setup navigation labels dengan click handler
        if (navHome != null) {
            navHome.setOnMouseClicked(e -> navigateToHome(e));
            setupLabelHover(navHome, Color.WHITE, Color.web("#FFA726"));
        }
        if (navProgram != null) {
            navProgram.setOnMouseClicked(e -> navigateToProgram(e));
            setupLabelHover(navProgram, Color.ORANGE, Color.web("#FFA726"));
        }
        if (navHubungi != null) {
            navHubungi.setOnMouseClicked(e -> navigateToHubungi(e));
            setupLabelHover(navHubungi, Color.WHITE, Color.web("#FFA726"));
        }
        if (navTentang != null) {
            navTentang.setOnMouseClicked(e -> navigateToTentang(e));
            setupLabelHover(navTentang, Color.WHITE, Color.web("#FFA726"));
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
        // Already on program page - scroll to top
        if (navProgram != null && navProgram.getScene() != null) {
            AnchorPane root = (AnchorPane) navProgram.getScene().getRoot();
            if (root != null) {
                // Scroll to top jika ada ScrollPane
                for (Node node : root.getChildrenUnmodifiable()) {
                    if (node instanceof ScrollPane) {
                        ((ScrollPane) node).setVvalue(0);
                        break;
                    }
                }
            }
        }
    }
    
    @FXML
    private void navigateToHubungi(MouseEvent event) {
        navigateToPage("src/main/java/view/HubungiKami.fxml", event);
    }
    
    @FXML
    private void navigateToTentang(MouseEvent event) {
        navigateToPage("src/main/java/view/TentangKami.fxml", event);
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
    
    private void openProgramDetailModal(Program program) {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = new File("src/main/java/view/ProgramDetailPublicModal.fxml").toURI().toURL();
            loader.setLocation(url);
            Parent root = loader.load();
            
            ProgramDetailPublicModalController controller = loader.getController();
            controller.setProgram(program);
            
            Stage modalStage = new Stage();
            modalStage.setTitle("Detail Program");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            if (programListContainer.getScene() != null) {
                modalStage.initOwner(programListContainer.getScene().getWindow());
            }
            
            Scene scene = new Scene(root);
            modalStage.setScene(scene);
            modalStage.showAndWait();
        } catch (Exception e) {
            System.err.println("Error membuka modal: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadHasilPrograms() {
        if (hasilProgramContainer == null) {
            return;
        }
        
        List<Program> programsWithLaporan = programDAO.getProgramsWithLaporan();
        hasilProgramContainer.getChildren().clear();
        
        if (programsWithLaporan == null || programsWithLaporan.isEmpty()) {
            Label noLaporanLabel = new Label("Belum ada laporan program tersedia");
            noLaporanLabel.setStyle("-fx-font-size: 16; -fx-text-fill: white;");
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
        card.setCursor(Cursor.HAND);
        
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
        javafx.scene.control.Alert dialog = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
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
}

