package controller;

import dao.LaporanProgramDAO;
import dao.ProgramDAO;
import java.io.File;
import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.LaporanProgram;
import model.Program;

/**
 * Controller untuk modal detail program publik (tanpa form donasi)
 */
public class ProgramDetailPublicModalController implements Initializable {
    
    @FXML
    private Label programTitleLabel;
    
    @FXML
    private Label statusBadge;
    
    @FXML
    private Label tanggalPelaksanaanLabel;
    
    @FXML
    private Label descriptionLabel;
    
    @FXML
    private Label locationLabel;
    
    @FXML
    private Label kategoriLabel;
    
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
    private Button closeButton;
    
    @FXML
    private VBox laporanSection;
    
    @FXML
    private Label tanggalPelaksanaanLaporanLabel;
    
    @FXML
    private Label laporanTextLabel;
    
    @FXML
    private Label dokumentasiLabel;
    
    private Program program;
    private ProgramDAO programDAO;
    private LaporanProgramDAO laporanProgramDAO;
    private NumberFormat currencyFormat;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        programDAO = new ProgramDAO();
        laporanProgramDAO = new LaporanProgramDAO();
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
        
        if (program.getKategori() != null && !program.getKategori().isEmpty()) {
            kategoriLabel.setText("📂 Kategori: " + program.getKategori());
        } else {
            kategoriLabel.setText("📂 Kategori: Tidak Dikategorikan");
        }
        
        // Tanggal pelaksanaan program
        if (program.getTanggalPelaksanaan() != null) {
            String formattedDate = program.getTanggalPelaksanaan()
                .format(DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID")));
            tanggalPelaksanaanLabel.setText("📅 Tanggal Pelaksanaan: " + formattedDate);
        } else {
            tanggalPelaksanaanLabel.setText("");
        }
        
        // Cek status program
        String status = programDAO.getStatusProgram(program.getId());
        if ("selesai".equals(status)) {
            statusBadge.setVisible(true);
            statusBadge.setManaged(true);
        } else {
            statusBadge.setVisible(false);
            statusBadge.setManaged(false);
        }
        
        long terkumpul = program.getDonasiTerkumpul();
        long target = program.getTargetDonasi() > 0 ? program.getTargetDonasi() : 1;
        
        progressLabel.setText("Terkumpul: " + currencyFormat.format(terkumpul));
        targetLabel.setText(" / " + currencyFormat.format(target));
        donaturCountLabel.setText(program.getJumlahDonatur() + " Donatur");
        
        double progress = (double) terkumpul / target;
        progressBar.setProgress(Math.min(progress, 1.0));
        
        // Load cover image
        loadCoverImage();
        
        // Load laporan jika program selesai
        if ("selesai".equals(status)) {
            loadLaporan();
        }
    }
    
    private void loadCoverImage() {
        if (program.getCover() == null || program.getCover().isEmpty()) {
            try {
                Image defaultImage = new Image(getClass().getResourceAsStream("/assets/proker.jpeg"));
                coverImageView.setImage(defaultImage);
            } catch (Exception e) {
                // Ignore
            }
            return;
        }
        
        try {
            File file = new File("src/main/java/assets/donasi/cover/" + program.getCover());
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                coverImageView.setImage(image);
                return;
            }
            
            File altFile = new File("src/main/java/assets/" + program.getCover());
            if (altFile.exists()) {
                Image image = new Image(altFile.toURI().toString());
                coverImageView.setImage(image);
                return;
            }
        } catch (Exception ex) {
            // Ignore
        }
        
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/assets/proker.jpeg"));
            coverImageView.setImage(defaultImage);
        } catch (Exception e) {
            // Ignore
        }
    }
    
    private void loadLaporan() {
        LaporanProgram laporan = laporanProgramDAO.getLaporanByProgramId(program.getId());
        if (laporan != null) {
            laporanSection.setVisible(true);
            laporanSection.setManaged(true);
            
            // Tanggal pelaksanaan dari laporan
            if (laporan.getTanggalPelaksanaan() != null) {
                String formattedDate = laporan.getTanggalPelaksanaan()
                    .format(DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID")));
                tanggalPelaksanaanLaporanLabel.setText("📅 Tanggal Pelaksanaan: " + formattedDate);
            } else {
                tanggalPelaksanaanLaporanLabel.setText("");
            }
            
            // Isi laporan
            laporanTextLabel.setText(laporan.getLaporan() != null ? laporan.getLaporan() : "Tidak ada laporan");
            
            // Dokumentasi
            if (laporan.getDokumentasi() != null && !laporan.getDokumentasi().isEmpty()) {
                dokumentasiLabel.setText("📎 Dokumentasi: " + laporan.getDokumentasi());
            } else {
                dokumentasiLabel.setText("");
            }
        } else {
            laporanSection.setVisible(false);
            laporanSection.setManaged(false);
        }
    }
    
    @FXML
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}

