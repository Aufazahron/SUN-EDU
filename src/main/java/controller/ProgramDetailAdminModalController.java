package controller;

import dao.DonasiDAO;
import dao.ProgramDAO;
import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Program;
import dao.DonasiDAO.DonaturInfo;

/**
 * Controller untuk modal detail program (Admin) - tanpa form donasi
 */
public class ProgramDetailAdminModalController implements Initializable {
    
    @FXML
    private Label programTitleLabel;
    
    @FXML
    private Label descriptionLabel;
    
    @FXML
    private Label locationLabel;
    
    @FXML
    private Label kategoriLabel;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private Label progressLabel;
    
    @FXML
    private Label targetLabel;
    
    @FXML
    private Label donaturCountLabel;
    
    @FXML
    private ProgressBar progressBar;
    
    @FXML
    private Button closeButton;
    
    @FXML
    private ScrollPane donaturScrollPane;
    
    @FXML
    private VBox donaturContainer;
    
    private Program program;
    private DonasiDAO donasiDAO;
    private ProgramDAO programDAO;
    private NumberFormat currencyFormat;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        donasiDAO = new DonasiDAO();
        programDAO = new ProgramDAO();
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    }
    
    public void setProgram(Program program) {
        this.program = program;
        loadProgramData();
    }
    
    private void loadProgramData() {
        if (program == null) return;
        
        // Reload program dari database untuk mendapatkan data terbaru
        Program updatedProgram = programDAO.getProgramById(program.getId());
        if (updatedProgram != null) {
            this.program = updatedProgram;
        }
        
        programTitleLabel.setText(program.getNama());
        descriptionLabel.setText(program.getDeskripsi());
        locationLabel.setText("📍 " + program.getTempat());
        
        // Kategori
        if (program.getKategori() != null && !program.getKategori().isEmpty()) {
            kategoriLabel.setText("📂 Kategori: " + program.getKategori());
        } else {
            kategoriLabel.setText("📂 Kategori: Tidak Dikategorikan");
        }
        
        // Status
        String status = programDAO.getStatusProgram(program.getId());
        if (status != null && !status.isEmpty()) {
            String statusText = status.substring(0, 1).toUpperCase() + status.substring(1);
            statusLabel.setText("📊 Status: " + statusText);
        } else {
            statusLabel.setText("📊 Status: -");
        }
        
        long terkumpul = program.getDonasiTerkumpul();
        long target = program.getTargetDonasi() > 0 ? program.getTargetDonasi() : 1;
        
        progressLabel.setText("Terkumpul: " + currencyFormat.format(terkumpul));
        targetLabel.setText(" / " + currencyFormat.format(target));
        donaturCountLabel.setText(program.getJumlahDonatur() + " Donatur");
        
        double progress = (double) terkumpul / target;
        progressBar.setProgress(Math.min(progress, 1.0));
        
        // Load daftar donatur
        loadDaftarDonatur();
    }
    
    private void loadDaftarDonatur() {
        if (donaturContainer == null || program == null) {
            return;
        }
        
        donaturContainer.getChildren().clear();
        
        List<DonaturInfo> donaturList = donasiDAO.getDonaturByProgram(program.getId());
        
        if (donaturList.isEmpty()) {
            Label emptyLabel = new Label("Belum ada donatur untuk program ini");
            emptyLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #999; -fx-padding: 20;");
            donaturContainer.getChildren().add(emptyLabel);
            return;
        }
        
        for (DonaturInfo donatur : donaturList) {
            HBox donaturCard = createDonaturCard(donatur);
            donaturContainer.getChildren().add(donaturCard);
        }
    }
    
    private HBox createDonaturCard(DonaturInfo donatur) {
        HBox card = new HBox(15);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefWidth(770);
        
        VBox infoBox = new VBox(5);
        infoBox.setPrefWidth(500);
        
        Label namaLabel = new Label(donatur.getNamaUser());
        namaLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #020E4C;");
        
        Label nominalLabel = new Label(currencyFormat.format(donatur.getNominal()));
        nominalLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
        
        if (donatur.getCatatanDonatur() != null && !donatur.getCatatanDonatur().trim().isEmpty()) {
            Label catatanLabel = new Label("💬 " + donatur.getCatatanDonatur());
            catatanLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666;");
            catatanLabel.setWrapText(true);
            catatanLabel.setMaxWidth(500);
            infoBox.getChildren().addAll(namaLabel, nominalLabel, catatanLabel);
        } else {
            infoBox.getChildren().addAll(namaLabel, nominalLabel);
        }
        
        VBox dateBox = new VBox();
        dateBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(dateBox, javafx.scene.layout.Priority.ALWAYS);
        
        if (donatur.getTanggalDonasi() != null) {
            String formattedDate = donatur.getTanggalDonasi()
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("id", "ID")));
            Label dateLabel = new Label(formattedDate);
            dateLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #999;");
            dateBox.getChildren().add(dateLabel);
        }
        
        card.getChildren().addAll(infoBox, dateBox);
        
        return card;
    }
    
    @FXML
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}

