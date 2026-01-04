package controller;

import dao.DonasiDAO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import model.Donasi;
import model.Program;
import dao.DonasiDAO.DonaturInfo;
import util.SessionManager;

/**
 * Controller untuk modal detail program dan form donasi
 */
public class ProgramDetailModalController implements Initializable {
    
    @FXML
    private Label programTitleLabel;
    
    @FXML
    private Label descriptionLabel;
    
    @FXML
    private Label locationLabel;
    
    @FXML
    private Label progressLabel;
    
    @FXML
    private Label targetLabel;
    
    @FXML
    private Label donaturCountLabel;
    
    @FXML
    private ProgressBar progressBar;
    
    @FXML
    private ImageView coverImageView;
    
    @FXML
    private TextField nominalField;
    
    @FXML
    private TextArea catatanField;
    
    @FXML
    private Label fileLabel;
    
    @FXML
    private Button closeButton;
    
    @FXML
    private Button uploadButton;
    
    @FXML
    private Button donateButton;
    
    @FXML
    private ScrollPane donaturScrollPane;
    
    @FXML
    private VBox donaturContainer;
    
    private Program program;
    private DonasiDAO donasiDAO;
    private File selectedFile;
    private NumberFormat currencyFormat;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        donasiDAO = new DonasiDAO();
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        
        // Hanya format angka untuk nominal field
        nominalField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                nominalField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
    
    public void setProgram(Program program) {
        this.program = program;
        loadProgramData();
    }
    
    private void loadProgramData() {
        if (program == null) return;
        
        programTitleLabel.setText(program.getNama());
        descriptionLabel.setText(program.getDeskripsi());
        locationLabel.setText("📍 " + program.getTempat());
        
        long terkumpul = program.getDonasiTerkumpul();
        long target = program.getTargetDonasi() > 0 ? program.getTargetDonasi() : 1;
        
        progressLabel.setText("Terkumpul: " + currencyFormat.format(terkumpul));
        targetLabel.setText(" / " + currencyFormat.format(target));
        donaturCountLabel.setText(program.getJumlahDonatur() + " Donatur");
        
        double progress = (double) terkumpul / target;
        progressBar.setProgress(Math.min(progress, 1.0));
        
        // Load daftar donatur
        loadDaftarDonatur();
        
        // Load cover image
        try {
            String imagePath = "../assets/donasi/cover/" + program.getCover();
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            coverImageView.setImage(image);
        } catch (Exception e) {
            // Jika gambar tidak ditemukan, coba path lama
            try {
                String imagePath = "../assets/" + program.getCover();
                Image image = new Image(getClass().getResourceAsStream(imagePath));
                coverImageView.setImage(image);
            } catch (Exception ex) {
                System.err.println("Gambar tidak ditemukan: " + program.getCover());
            }
        }
    }
    
    @FXML
    private void handleUploadBukti(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Bukti Transfer");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        Stage stage = (Stage) uploadButton.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            fileLabel.setText(selectedFile.getName());
        }
    }
    
    @FXML
    private void handleDonate(ActionEvent event) {
        if (program == null) {
            showAlert(AlertType.ERROR, "Error", "Program tidak ditemukan!");
            return;
        }
        
        // Validasi nominal
        String nominalText = nominalField.getText().trim();
        if (nominalText.isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Nominal donasi tidak boleh kosong!");
            return;
        }
        
        try {
            long nominal = Long.parseLong(nominalText);
            if (nominal <= 0) {
                showAlert(AlertType.WARNING, "Validasi", "Nominal donasi harus lebih dari 0!");
                return;
            }
            
            // Simpan bukti transfer jika ada
            String buktiTransferPath = null;
            if (selectedFile != null) {
                buktiTransferPath = saveBuktiTransfer(selectedFile);
            }
            
            // Buat object donasi
            Donasi donasi = new Donasi();
            donasi.setIdProgram(program.getId());
            donasi.setIdUser(SessionManager.getCurrentUser().getId());
            donasi.setNominal(nominal);
            donasi.setBuktiTransfer(buktiTransferPath);
            donasi.setCatatanDonatur(catatanField.getText().trim());
            donasi.setTanggalDonasi(LocalDateTime.now());
            
            // Simpan ke database
            if (donasiDAO.save(donasi)) {
                showAlert(AlertType.INFORMATION, "Berhasil", 
                    "Donasi sebesar " + currencyFormat.format(nominal) + " berhasil disimpan!");
                
                // Refresh program data dan daftar donatur
                refreshProgramData();
                loadDaftarDonatur();
                
                // Reset form
                nominalField.clear();
                catatanField.clear();
                fileLabel.setText("Belum ada file dipilih");
                selectedFile = null;
            } else {
                showAlert(AlertType.ERROR, "Error", "Gagal menyimpan donasi!");
            }
            
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Error", "Format nominal tidak valid!");
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "Terjadi kesalahan: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String saveBuktiTransfer(File file) {
        try {
            // Buat direktori jika belum ada
            Path targetDir = Paths.get("src/main/java/assets/donasi/bukti_pembayaran");
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            
            // Generate nama file unik
            String fileName = "bukti_" + System.currentTimeMillis() + "_" + file.getName();
            Path targetPath = targetDir.resolve(fileName);
            
            // Copy file
            try (InputStream is = new FileInputStream(file);
                 FileOutputStream os = new FileOutputStream(targetPath.toFile())) {
                
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            }
            
            return fileName;
        } catch (IOException e) {
            System.err.println("Error menyimpan bukti transfer: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
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
    
    private void refreshProgramData() {
        // Reload program dari database
        dao.ProgramDAO programDAO = new dao.ProgramDAO();
        Program updatedProgram = programDAO.getProgramById(program.getId());
        if (updatedProgram != null) {
            this.program = updatedProgram;
            loadProgramData();
        }
    }
    
    @FXML
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

