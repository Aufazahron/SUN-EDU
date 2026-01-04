package controller;

import dao.AnakDAO;
import dao.DetailAnakDAO;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Anak;
import model.DetailAnak;

/**
 * Controller untuk form modal edit detail anak
 */
public class DetailAnakFormModalController implements Initializable {
    
    @FXML private Label formTitleLabel;
    
    // Data Anak
    @FXML private TextField namaField;
    @FXML private DatePicker tanggalLahirPicker;
    @FXML private ComboBox<String> jenisKelaminCombo;
    @FXML private ComboBox<String> statusPendidikanCombo;
    @FXML private TextArea alamatField;
    @FXML private TextField namaOrangtuaField;
    @FXML private TextField noTelpOrangtuaField;
    
    // Informasi Orang Tua/Wali
    @FXML private ComboBox<String> statusOrangtuaCombo;
    @FXML private TextField namaAyahField;
    @FXML private TextField namaIbuField;
    @FXML private TextField namaWaliField;
    @FXML private TextField tinggalBersamaField;
    @FXML private TextArea deskripsiKeluargaField;
    
    // Kondisi Ekonomi
    @FXML private TextField pekerjaanAyahField;
    @FXML private TextField penghasilanAyahField;
    @FXML private TextField pekerjaanIbuField;
    @FXML private TextField penghasilanIbuField;
    @FXML private TextField pekerjaanWaliField;
    @FXML private TextField penghasilanWaliField;
    @FXML private TextArea deskripsiEkonomiField;
    
    // Kondisi Pendidikan
    @FXML private TextField sekolahTerakhirField;
    @FXML private TextArea alasanPutusSekolahField;
    @FXML private TextArea minatBelajarField;
    
    // Kondisi Kesehatan
    @FXML private TextArea riwayatPenyakitField;
    @FXML private TextField layananKesehatanField;
    @FXML private TextArea deskripsiKesehatanField;
    
    // Foto
    @FXML private Button btnUploadFoto;
    @FXML private Label fotoLabel;
    @FXML private ImageView fotoPreview;
    
    private DetailAnakDAO detailAnakDAO;
    private AnakDAO anakDAO;
    private int anakId;
    private DetailAnakController parentController;
    private File selectedFotoFile;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        detailAnakDAO = new DetailAnakDAO();
        anakDAO = new AnakDAO();
        
        // Setup combo box
        if (statusOrangtuaCombo != null) {
            statusOrangtuaCombo.getItems().addAll("Ayah Ibu", "Ayah", "Ibu", "Wali");
        }
        if (jenisKelaminCombo != null) {
            jenisKelaminCombo.getItems().addAll("L", "P");
        }
        if (statusPendidikanCombo != null) {
            // Relawan dapat melihat dan mengubah: Diajukan, Rentan, Dalam Pemantauan, Stabil
            // Selesai tidak ditampilkan karena tidak boleh diubah oleh relawan
            statusPendidikanCombo.getItems().addAll("Diajukan", "Rentan", "Dalam Pemantauan", "Stabil");
        }
    }
    
    public void setAnakId(int anakId) {
        this.anakId = anakId;
        loadData();
    }
    
    public void setParentController(DetailAnakController parentController) {
        this.parentController = parentController;
    }
    
    private void loadData() {
        if (anakId <= 0) {
            return;
        }
        
        // Load data anak
        Anak anak = anakDAO.getAnakById(anakId);
        if (anak != null) {
            if (namaField != null) namaField.setText(anak.getNama() != null ? anak.getNama() : "");
            if (tanggalLahirPicker != null && anak.getTanggalLahir() != null) {
                tanggalLahirPicker.setValue(anak.getTanggalLahir());
            }
            if (jenisKelaminCombo != null && anak.getJenisKelamin() != null) {
                jenisKelaminCombo.setValue(anak.getJenisKelamin());
            }
            if (statusPendidikanCombo != null && anak.getStatusPendidikan() != null) {
                statusPendidikanCombo.setValue(anak.getStatusPendidikan());
            }
            if (alamatField != null) alamatField.setText(anak.getAlamat() != null ? anak.getAlamat() : "");
            if (namaOrangtuaField != null) namaOrangtuaField.setText(anak.getNamaOrangtua() != null ? anak.getNamaOrangtua() : "");
            if (noTelpOrangtuaField != null) noTelpOrangtuaField.setText(anak.getNoTelpOrangtua() != null ? anak.getNoTelpOrangtua() : "");
        }
        
        // Load detail anak
        DetailAnak detail = detailAnakDAO.getDetailByAnakId(anakId);
        
        if (detail != null) {
            // Populate form fields
            if (statusOrangtuaCombo != null && detail.getStatusOrangtua() != null) {
                statusOrangtuaCombo.setValue(detail.getStatusOrangtua());
            }
            if (namaAyahField != null) namaAyahField.setText(detail.getNamaAyah() != null ? detail.getNamaAyah() : "");
            if (namaIbuField != null) namaIbuField.setText(detail.getNamaIbu() != null ? detail.getNamaIbu() : "");
            if (namaWaliField != null) namaWaliField.setText(detail.getNamaWali() != null ? detail.getNamaWali() : "");
            if (tinggalBersamaField != null) tinggalBersamaField.setText(detail.getTinggalBersama() != null ? detail.getTinggalBersama() : "");
            if (deskripsiKeluargaField != null) deskripsiKeluargaField.setText(detail.getDeskripsiKeluarga() != null ? detail.getDeskripsiKeluarga() : "");
            
            if (pekerjaanAyahField != null) pekerjaanAyahField.setText(detail.getPekerjaanAyah() != null ? detail.getPekerjaanAyah() : "");
            if (penghasilanAyahField != null) penghasilanAyahField.setText(detail.getPenghasilanAyah() != null ? String.valueOf(detail.getPenghasilanAyah()) : "");
            if (pekerjaanIbuField != null) pekerjaanIbuField.setText(detail.getPekerjaanIbu() != null ? detail.getPekerjaanIbu() : "");
            if (penghasilanIbuField != null) penghasilanIbuField.setText(detail.getPenghasilanIbu() != null ? String.valueOf(detail.getPenghasilanIbu()) : "");
            if (pekerjaanWaliField != null) pekerjaanWaliField.setText(detail.getPekerjaanWali() != null ? detail.getPekerjaanWali() : "");
            if (penghasilanWaliField != null) penghasilanWaliField.setText(detail.getPenghasilanWali() != null ? String.valueOf(detail.getPenghasilanWali()) : "");
            if (deskripsiEkonomiField != null) deskripsiEkonomiField.setText(detail.getDeskripsiEkonomi() != null ? detail.getDeskripsiEkonomi() : "");
            
            if (sekolahTerakhirField != null) sekolahTerakhirField.setText(detail.getSekolahTerakhir() != null ? detail.getSekolahTerakhir() : "");
            if (alasanPutusSekolahField != null) alasanPutusSekolahField.setText(detail.getAlasanPutusSekolah() != null ? detail.getAlasanPutusSekolah() : "");
            if (minatBelajarField != null) minatBelajarField.setText(detail.getMinatBelajar() != null ? detail.getMinatBelajar() : "");
            
            if (riwayatPenyakitField != null) riwayatPenyakitField.setText(detail.getRiwayatPenyakit() != null ? detail.getRiwayatPenyakit() : "");
            if (layananKesehatanField != null) layananKesehatanField.setText(detail.getLayananKesehatan() != null ? detail.getLayananKesehatan() : "");
            if (deskripsiKesehatanField != null) deskripsiKesehatanField.setText(detail.getDeskripsiKesehatan() != null ? detail.getDeskripsiKesehatan() : "");
            
            // Load foto jika ada
            if (detail.getFoto() != null && !detail.getFoto().isEmpty()) {
                loadFotoPreview(detail.getFoto());
                if (fotoLabel != null) {
                    fotoLabel.setText("Foto: " + detail.getFoto());
                }
            }
        }
    }
    
    @FXML
    private void handleUploadFoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Foto Anak");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );
        
        File file = fileChooser.showOpenDialog(((Button) event.getSource()).getScene().getWindow());
        if (file != null) {
            selectedFotoFile = file;
            if (fotoLabel != null) {
                fotoLabel.setText("Foto: " + file.getName());
            }
            
            // Preview foto
            try {
                Image image = new Image(file.toURI().toString());
                if (fotoPreview != null) {
                    fotoPreview.setImage(image);
                    fotoPreview.setVisible(true);
                }
            } catch (Exception e) {
                showAlert(AlertType.ERROR, "Error", "Gagal memuat preview foto: " + e.getMessage());
            }
        }
    }
    
    private void loadFotoPreview(String fotoPath) {
        try {
            File file = new File("src/main/java/assets/detail_anak/" + fotoPath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                if (fotoPreview != null) {
                    fotoPreview.setImage(image);
                    fotoPreview.setVisible(true);
                }
            }
        } catch (Exception e) {
            System.err.println("Gagal memuat foto: " + e.getMessage());
        }
    }
    
    private String saveFoto(File file) {
        try {
            // Buat direktori jika belum ada
            Path targetDir = Paths.get("src/main/java/assets/detail_anak");
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            
            // Generate nama file unik
            String fileName = "foto_anak_" + anakId + "_" + System.currentTimeMillis() + "_" + file.getName();
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
    
    @FXML
    private void handleSimpan(ActionEvent event) {
        // Validasi data anak
        if (namaField == null || namaField.getText().trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Nama lengkap harus diisi!");
            return;
        }
        if (tanggalLahirPicker.getValue() == null) {
            showAlert(AlertType.WARNING, "Validasi", "Tanggal lahir harus dipilih!");
            return;
        }
        if (jenisKelaminCombo.getValue() == null || jenisKelaminCombo.getValue().isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Jenis kelamin harus dipilih!");
            return;
        }
        if (statusPendidikanCombo.getValue() == null || statusPendidikanCombo.getValue().isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Status pendidikan harus dipilih!");
            return;
        }
        if (alamatField == null || alamatField.getText().trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Alamat harus diisi!");
            return;
        }
        if (namaOrangtuaField == null || namaOrangtuaField.getText().trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Nama orangtua/wali harus diisi!");
            return;
        }
        if (noTelpOrangtuaField == null || noTelpOrangtuaField.getText().trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "No. telepon orangtua harus diisi!");
            return;
        }
        
        // Validasi detail anak
        if (statusOrangtuaCombo.getValue() == null || statusOrangtuaCombo.getValue().isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Status orangtua harus dipilih!");
            return;
        }
        
        // Update data anak
        Anak anak = anakDAO.getAnakById(anakId);
        if (anak != null) {
            anak.setNama(namaField.getText().trim());
            anak.setTanggalLahir(tanggalLahirPicker.getValue());
            anak.setJenisKelamin(jenisKelaminCombo.getValue());
            anak.setStatusPendidikan(statusPendidikanCombo.getValue());
            anak.setAlamat(alamatField.getText().trim());
            anak.setNamaOrangtua(namaOrangtuaField.getText().trim());
            anak.setNoTelpOrangtua(noTelpOrangtuaField.getText().trim());
            
            boolean anakUpdated = anakDAO.update(anak);
            if (!anakUpdated) {
                showAlert(AlertType.ERROR, "Error", "Gagal memperbarui data anak!");
                return;
            }
        }
        
        // Create or update DetailAnak
        DetailAnak detail = detailAnakDAO.getDetailByAnakId(anakId);
        if (detail == null) {
            detail = new DetailAnak();
            detail.setIdAnak(anakId);
        }
        
        // Set values from form
        detail.setStatusOrangtua(statusOrangtuaCombo.getValue());
        detail.setNamaAyah(namaAyahField.getText().trim());
        detail.setNamaIbu(namaIbuField.getText().trim());
        detail.setNamaWali(namaWaliField.getText().trim());
        detail.setTinggalBersama(tinggalBersamaField.getText().trim());
        detail.setDeskripsiKeluarga(deskripsiKeluargaField.getText().trim());
        
        detail.setPekerjaanAyah(pekerjaanAyahField.getText().trim());
        try {
            if (!penghasilanAyahField.getText().trim().isEmpty()) {
                detail.setPenghasilanAyah(Long.parseLong(penghasilanAyahField.getText().trim()));
            } else {
                detail.setPenghasilanAyah(null);
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.WARNING, "Validasi", "Penghasilan ayah harus berupa angka!");
            return;
        }
        
        detail.setPekerjaanIbu(pekerjaanIbuField.getText().trim());
        try {
            if (!penghasilanIbuField.getText().trim().isEmpty()) {
                detail.setPenghasilanIbu(Long.parseLong(penghasilanIbuField.getText().trim()));
            } else {
                detail.setPenghasilanIbu(null);
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.WARNING, "Validasi", "Penghasilan ibu harus berupa angka!");
            return;
        }
        
        detail.setPekerjaanWali(pekerjaanWaliField.getText().trim());
        try {
            if (!penghasilanWaliField.getText().trim().isEmpty()) {
                detail.setPenghasilanWali(Long.parseLong(penghasilanWaliField.getText().trim()));
            } else {
                detail.setPenghasilanWali(null);
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.WARNING, "Validasi", "Penghasilan wali harus berupa angka!");
            return;
        }
        
        detail.setDeskripsiEkonomi(deskripsiEkonomiField.getText().trim());
        detail.setSekolahTerakhir(sekolahTerakhirField.getText().trim());
        detail.setAlasanPutusSekolah(alasanPutusSekolahField.getText().trim());
        detail.setMinatBelajar(minatBelajarField.getText().trim());
        detail.setRiwayatPenyakit(riwayatPenyakitField.getText().trim());
        detail.setLayananKesehatan(layananKesehatanField.getText().trim());
        detail.setDeskripsiKesehatan(deskripsiKesehatanField.getText().trim());
        
        // Handle foto upload
        if (selectedFotoFile != null) {
            String fotoPath = saveFoto(selectedFotoFile);
            if (fotoPath != null) {
                detail.setFoto(fotoPath);
            }
        } else {
            // Keep existing foto if no new foto uploaded
            DetailAnak existing = detailAnakDAO.getDetailByAnakId(anakId);
            if (existing != null && existing.getFoto() != null) {
                detail.setFoto(existing.getFoto());
            }
        }
        
        // Save or update detail
        boolean success = detailAnakDAO.saveOrUpdate(detail);
        
        if (success) {
            showAlert(AlertType.INFORMATION, "Berhasil", "Data dan detail anak berhasil disimpan!");
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.close();
            
            // Refresh parent controller if exists
            if (parentController != null) {
                parentController.refreshData();
            }
        } else {
            showAlert(AlertType.ERROR, "Error", "Gagal menyimpan detail anak!");
        }
    }
    
    @FXML
    private void handleBatal(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}

