package controller;

import dao.LaporanProgramDAO;
import dao.ProgramDAO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.LaporanProgram;
import model.Program;

/**
 * Controller untuk form modal laporan program
 */
public class LaporanProgramFormModalController implements Initializable {
    
    @FXML private Label formTitleLabel;
    @FXML private ComboBox<Program> programComboBox;
    @FXML private DatePicker tanggalPelaksanaanPicker;
    @FXML private TextArea laporanField;
    @FXML private Button btnUploadDokumentasi;
    @FXML private Label dokumentasiLabel;
    
    private LaporanProgramDAO laporanProgramDAO;
    private ProgramDAO programDAO;
    private Program program;
    private File selectedDokumentasiFile;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        laporanProgramDAO = new LaporanProgramDAO();
        programDAO = new ProgramDAO();
        
        // Load program yang belum memiliki laporan
        loadProgramsWithoutLaporan();
        
        // Setup ComboBox untuk menampilkan nama program
        if (programComboBox != null) {
            programComboBox.setCellFactory(param -> new ListCell<Program>() {
                @Override
                protected void updateItem(Program program, boolean empty) {
                    super.updateItem(program, empty);
                    if (empty || program == null) {
                        setText(null);
                    } else {
                        setText(program.getNama());
                    }
                }
            });
            
            programComboBox.setButtonCell(new ListCell<Program>() {
                @Override
                protected void updateItem(Program program, boolean empty) {
                    super.updateItem(program, empty);
                    if (empty || program == null) {
                        setText(null);
                    } else {
                        setText(program.getNama());
                    }
                }
            });
            
            // Handler ketika program dipilih
            programComboBox.setOnAction(e -> {
                program = programComboBox.getValue();
            });
        }
        
        // Pastikan semua field bisa diakses
        if (tanggalPelaksanaanPicker != null) {
            tanggalPelaksanaanPicker.setMouseTransparent(false);
            tanggalPelaksanaanPicker.setFocusTraversable(true);
        }
        if (laporanField != null) {
            laporanField.setMouseTransparent(false);
            laporanField.setFocusTraversable(true);
        }
    }
    
    private void loadProgramsWithoutLaporan() {
        if (programComboBox == null) {
            return;
        }
        
        List<Program> allPrograms = programDAO.getAllPrograms();
        List<Program> programsWithoutLaporan = allPrograms.stream()
            .filter(p -> {
                LaporanProgram existingLaporan = laporanProgramDAO.getLaporanByProgramId(p.getId());
                return existingLaporan == null;
            })
            .collect(java.util.stream.Collectors.toList());
        
        ObservableList<Program> programList = FXCollections.observableArrayList(programsWithoutLaporan);
        programComboBox.setItems(programList);
    }
    
    public void setProgram(Program program) {
        this.program = program;
        if (program != null && programComboBox != null) {
            programComboBox.setValue(program);
        }
    }
    
    @FXML
    private void handleUploadDokumentasi(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih File Dokumentasi");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Files", "*.*"),
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
            new FileChooser.ExtensionFilter("PDF", "*.pdf"),
            new FileChooser.ExtensionFilter("Documents", "*.doc", "*.docx")
        );
        
        File file = fileChooser.showOpenDialog(((Button) event.getSource()).getScene().getWindow());
        if (file != null) {
            selectedDokumentasiFile = file;
            if (dokumentasiLabel != null) {
                dokumentasiLabel.setText("File: " + file.getName());
            }
        }
    }
    
    @FXML
    private void handleSimpanLaporan(ActionEvent event) {
        // Validasi
        if (programComboBox.getValue() == null) {
            showAlert(AlertType.WARNING, "Validasi", "Program Kerja harus dipilih!");
            return;
        }
        
        program = programComboBox.getValue();
        
        if (tanggalPelaksanaanPicker.getValue() == null) {
            showAlert(AlertType.WARNING, "Validasi", "Tanggal laporan harus diisi!");
            return;
        }
        
        if (laporanField == null || laporanField.getText().trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Laporan tidak boleh kosong!");
            return;
        }
        
        // Simpan file dokumentasi jika ada
        String dokumentasiPath = null;
        if (selectedDokumentasiFile != null) {
            dokumentasiPath = saveDokumentasiFile(selectedDokumentasiFile);
            if (dokumentasiPath == null) {
                showAlert(AlertType.ERROR, "Error", "Gagal menyimpan file dokumentasi!");
                return;
            }
        }
        
        // Buat object LaporanProgram
        LaporanProgram laporan = new LaporanProgram();
        laporan.setIdProgram(program.getId());
        laporan.setLaporan(laporanField.getText().trim());
        laporan.setDokumentasi(dokumentasiPath);
        laporan.setTanggalPelaksanaan(tanggalPelaksanaanPicker.getValue());
        
        // Simpan ke database
        boolean success = laporanProgramDAO.save(laporan);
        
        if (success) {
            showAlert(AlertType.INFORMATION, "Berhasil", "Laporan program berhasil disimpan!");
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.close();
        } else {
            showAlert(AlertType.ERROR, "Error", "Gagal menyimpan laporan program!");
        }
    }
    
    @FXML
    private void handleBatalForm(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    private String saveDokumentasiFile(File file) {
        try {
            // Buat direktori jika belum ada
            Path targetDir = Paths.get("src/main/java/assets/laporan_program");
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            
            // Generate nama file unik
            String fileName = "dokumentasi_" + program.getId() + "_" + System.currentTimeMillis() + "_" + file.getName();
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
            System.err.println("Error menyimpan dokumentasi: " + e.getMessage());
            e.printStackTrace();
            return null;
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

