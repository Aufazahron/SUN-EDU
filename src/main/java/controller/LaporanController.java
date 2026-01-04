package controller;

import dao.AnakDAO;
import dao.LaporanMonitoringDAO;
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
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Anak;
import model.LaporanMonitoring;
import util.SessionManager;

/**
 * Controller untuk halaman Laporan Monitoring
 */
public class LaporanController implements Initializable {
    
    @FXML
    private TextField namaLaporanField;
    
    @FXML
    private ComboBox<Anak> anakComboBox;
    
    @FXML
    private DatePicker tanggalMonitoringPicker;
    
    @FXML
    private TextArea progressPendidikanField;
    
    @FXML
    private TextArea kondisiKesehatanField;
    
    @FXML
    private TextArea catatanField;
    
    @FXML
    private Button btnUploadFoto;
    
    @FXML
    private Label fotoLabel;
    
    @FXML
    private Button btnSimpan;
    
    @FXML
    private Button btnHapus;
    
    @FXML
    private Button btnBatal;
    
    @FXML
    private Button btnLihatLaporan;
    
    private AnakDAO anakDAO;
    private LaporanMonitoringDAO laporanDAO;
    private ObservableList<Anak> anakList;
    private int currentRelawanId;
    private File selectedFotoFile;
    private LaporanMonitoring laporanEdit; // Untuk menyimpan data laporan yang sedang diedit
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        anakDAO = new AnakDAO();
        laporanDAO = new LaporanMonitoringDAO();
        anakList = FXCollections.observableArrayList();
        
        // Ambil ID relawan dari session
        if (SessionManager.getCurrentUser() != null) {
            currentRelawanId = SessionManager.getCurrentUser().getId();
        }
        
        // Setup combo box untuk anak
        setupAnakComboBox();
        
        // Set tanggal default ke hari ini
        tanggalMonitoringPicker.setValue(LocalDate.now());
        
        // Load data anak
        loadAnakData();
    }
    
    private void setupAnakComboBox() {
        // Setup combo box untuk menampilkan nama anak
        anakComboBox.setCellFactory(param -> new javafx.scene.control.ListCell<Anak>() {
            @Override
            protected void updateItem(Anak item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNama() + " (" + item.getStatusPendidikan() + ")");
                }
            }
        });
        
        anakComboBox.setButtonCell(new javafx.scene.control.ListCell<Anak>() {
            @Override
            protected void updateItem(Anak item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNama() + " (" + item.getStatusPendidikan() + ")");
                }
            }
        });
        
        anakComboBox.setItems(anakList);
    }
    
    private void loadAnakData() {
        anakList.clear();
        java.util.List<Anak> list = anakDAO.getAnakByRelawan(currentRelawanId);
        anakList.addAll(list);
    }
    
    @FXML
    private void handleUploadFoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Foto Monitoring");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        Stage stage = (Stage) btnUploadFoto.getScene().getWindow();
        selectedFotoFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFotoFile != null) {
            fotoLabel.setText(selectedFotoFile.getName());
        }
    }
    
    @FXML
    private void handleSimpan(ActionEvent event) {
        // Validasi
        if (anakComboBox.getValue() == null) {
            showAlert(AlertType.WARNING, "Validasi", "Pilih anak yang akan dimonitoring!");
            return;
        }
        
        if (tanggalMonitoringPicker.getValue() == null) {
            showAlert(AlertType.WARNING, "Validasi", "Tanggal monitoring harus diisi!");
            return;
        }
        
        if (progressPendidikanField.getText().trim().isEmpty() && 
            kondisiKesehatanField.getText().trim().isEmpty() && 
            catatanField.getText().trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Minimal isi salah satu field (Progress Pendidikan, Kondisi Kesehatan, atau Catatan)!");
            return;
        }
        
        // Simpan foto jika ada
        String fotoPath = null;
        if (selectedFotoFile != null) {
            fotoPath = saveFoto(selectedFotoFile);
            if (fotoPath == null) {
                showAlert(AlertType.WARNING, "Peringatan", "Gagal menyimpan foto, tetapi laporan tetap akan disimpan.");
            }
        } else if (laporanEdit != null && laporanEdit.getFoto() != null) {
            // Jika edit dan tidak upload foto baru, gunakan foto lama
            fotoPath = laporanEdit.getFoto();
        }
        
        if (laporanEdit != null) {
            // Update mode
            laporanEdit.setIdAnak(anakComboBox.getValue().getId());
            // Gunakan nama dari field jika diisi, jika tidak gunakan default
            String namaLaporan = namaLaporanField != null && namaLaporanField.getText() != null && !namaLaporanField.getText().trim().isEmpty()
                ? namaLaporanField.getText().trim()
                : anakComboBox.getValue().getNama() + " - " + tanggalMonitoringPicker.getValue().toString();
            laporanEdit.setNama(namaLaporan);
            laporanEdit.setTanggalMonitoring(tanggalMonitoringPicker.getValue());
            laporanEdit.setProgressPendidikan(progressPendidikanField.getText().trim());
            laporanEdit.setKondisiKesehatan(kondisiKesehatanField.getText().trim());
            laporanEdit.setCatatan(catatanField.getText().trim());
            laporanEdit.setFoto(fotoPath);
            
            // Jangan ubah status jika sebelumnya Dikembalikan - biarkan status tetap seperti semula
            // Status akan diubah ke Draft hanya setelah admin approve atau user submit ulang
            
            if (laporanDAO.update(laporanEdit)) {
                showAlert(AlertType.INFORMATION, "Berhasil", "Laporan monitoring berhasil diupdate!");
                
                // Navigate kembali ke list
                navigateToPage("src/main/java/view/ListMonitoringRelawan.fxml", event);
            } else {
                showAlert(AlertType.ERROR, "Error", "Gagal mengupdate laporan monitoring!");
            }
        } else {
            // Insert mode
            LaporanMonitoring laporan = new LaporanMonitoring();
            laporan.setIdAnak(anakComboBox.getValue().getId());
            laporan.setIdUser(currentRelawanId); // Auto-fill dari session
            
            // Set nama laporan (gunakan dari field jika diisi, jika tidak gunakan default)
            String namaLaporan = namaLaporanField != null && namaLaporanField.getText() != null && !namaLaporanField.getText().trim().isEmpty()
                ? namaLaporanField.getText().trim()
                : anakComboBox.getValue().getNama() + " - " + tanggalMonitoringPicker.getValue().toString();
            laporan.setNama(namaLaporan);
            laporan.setStatus("Draft"); // Default status Draft
            
            laporan.setTanggalMonitoring(tanggalMonitoringPicker.getValue());
            laporan.setProgressPendidikan(progressPendidikanField.getText().trim());
            laporan.setKondisiKesehatan(kondisiKesehatanField.getText().trim());
            laporan.setCatatan(catatanField.getText().trim());
            laporan.setFoto(fotoPath);
            
            // Simpan ke database
            if (laporanDAO.save(laporan)) {
                showAlert(AlertType.INFORMATION, "Berhasil", "Laporan monitoring berhasil disimpan!");
                
                // Reset form
                resetForm();
            } else {
                showAlert(AlertType.ERROR, "Error", "Gagal menyimpan laporan monitoring!");
            }
        }
    }
    
    private String saveFoto(File file) {
        try {
            // Buat direktori jika belum ada
            Path targetDir = Paths.get("src/main/java/assets/laporan_monitoring");
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            
            // Generate nama file unik
            String fileName = "foto_" + System.currentTimeMillis() + "_" + file.getName();
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
            System.err.println("Error menyimpan foto: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private void resetForm() {
        laporanEdit = null;
        if (namaLaporanField != null) {
            namaLaporanField.clear();
        }
        anakComboBox.setValue(null);
        tanggalMonitoringPicker.setValue(LocalDate.now());
        progressPendidikanField.clear();
        kondisiKesehatanField.clear();
        catatanField.clear();
        fotoLabel.setText("Belum ada foto dipilih");
        selectedFotoFile = null;
        if (btnSimpan != null) {
            btnSimpan.setText("Simpan Laporan");
        }
        if (btnHapus != null) {
            btnHapus.setVisible(false);
        }
    }
    
    /**
     * Method untuk set data laporan yang akan diedit
     */
    public void setLaporanForEdit(LaporanMonitoring laporan) {
        this.laporanEdit = laporan;
        
        // Load data anak untuk combo box
        loadAnakData();
        
        // Set nilai form dengan data laporan
        if (laporan != null) {
            // Set anak combo box
            for (Anak anak : anakList) {
                if (anak.getId() == laporan.getIdAnak()) {
                    anakComboBox.setValue(anak);
                    break;
                }
            }
            
            // Set field lainnya
            if (namaLaporanField != null) {
                namaLaporanField.setText(laporan.getNama() != null ? laporan.getNama() : "");
            }
            if (laporan.getTanggalMonitoring() != null) {
                tanggalMonitoringPicker.setValue(laporan.getTanggalMonitoring());
            }
            progressPendidikanField.setText(laporan.getProgressPendidikan() != null ? laporan.getProgressPendidikan() : "");
            kondisiKesehatanField.setText(laporan.getKondisiKesehatan() != null ? laporan.getKondisiKesehatan() : "");
            catatanField.setText(laporan.getCatatan() != null ? laporan.getCatatan() : "");
            
            // Set label foto jika ada
            if (laporan.getFoto() != null && !laporan.getFoto().isEmpty()) {
                fotoLabel.setText("Foto saat ini: " + laporan.getFoto());
            }
            
            // Update button text
            if (btnSimpan != null) {
                btnSimpan.setText("Update Laporan");
            }
            
            // Tampilkan tombol Hapus hanya jika status Draft
            if (btnHapus != null) {
                if (laporan.getStatus() != null && laporan.getStatus().equals("Draft")) {
                    btnHapus.setVisible(true);
                } else {
                    btnHapus.setVisible(false);
                }
            }
        }
    }
    
    @FXML
    private void handleBatal(ActionEvent event) {
        resetForm();
        // Navigate kembali ke list jika dalam mode edit
        if (laporanEdit != null) {
            navigateToPage("src/main/java/view/ListMonitoringRelawan.fxml", event);
        }
    }
    
    @FXML
    private void handleHapus(ActionEvent event) {
        if (laporanEdit == null || laporanEdit.getStatus() == null || !laporanEdit.getStatus().equals("Draft")) {
            showAlert(AlertType.WARNING, "Tidak Dapat Hapus", "Hanya laporan dengan status Draft yang dapat dihapus!");
            return;
        }
        
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Hapus");
        confirmAlert.setHeaderText(null);
        Anak anak = anakDAO.getAnakById(laporanEdit.getIdAnak());
        String namaAnak = anak != null ? anak.getNama() : "-";
        confirmAlert.setContentText("Apakah Anda yakin ingin menghapus laporan monitoring untuk " + namaAnak + "?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                if (laporanDAO.delete(laporanEdit.getId())) {
                    showAlert(AlertType.INFORMATION, "Berhasil", "Laporan monitoring berhasil dihapus!");
                    // Navigate kembali ke list
                    navigateToPage("src/main/java/view/ListMonitoringRelawan.fxml", event);
                } else {
                    showAlert(AlertType.ERROR, "Error", "Gagal menghapus laporan monitoring!");
                }
            }
        });
    }
    
    @FXML
    private void handleLihatLaporan(ActionEvent event) {
        // Navigate ke halaman list monitoring
        navigateToPage("src/main/java/view/ListMonitoringRelawan.fxml", event);
    }
    
    @FXML
    private void handleNavigateToAnak(ActionEvent event) {
        navigateToPage("src/main/java/view/AnakView.fxml", event);
    }
    
    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        navigateToPage("src/main/java/view/DashboardRelawan.fxml", event);
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            SessionManager.clearSession();
            navigateToPage("src/main/java/view/login.fxml", event);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "Error saat logout: " + e.getMessage());
        }
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
            showAlert(AlertType.ERROR, "Error", "Tidak dapat membuka halaman: " + e.getMessage());
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

