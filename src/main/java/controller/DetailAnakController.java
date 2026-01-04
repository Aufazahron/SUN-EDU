package controller;

import dao.AnakDAO;
import dao.DetailAnakDAO;
import dao.LaporanMonitoringDAO;
import dao.UserDAO;
import java.io.File;
import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Anak;
import model.DetailAnak;
import model.LaporanMonitoring;
import model.User;
import util.SessionManager;

/**
 * Controller untuk halaman Detail Anak
 */
public class DetailAnakController implements Initializable {
    
    // Header
    @FXML private Text namaAnakHeaderText;
    @FXML private Text tanggalText;
    @FXML private Text statusText;
    
    // Informasi Anak - Data Anak
    @FXML private Text namaAnakText;
    @FXML private Text tanggalLahirText;
    @FXML private Text jenisKelaminText;
    @FXML private Text statusPendidikanText;
    @FXML private Text alamatAnakText;
    @FXML private Text namaOrangtuaAnakText;
    @FXML private Text noTelpOrangtuaAnakText;
    
    // Informasi Anak Dampingan - Detail Anak
    @FXML private Text statusOrangtuaText;
    @FXML private Text namaAyahText;
    @FXML private Text namaIbuText;
    @FXML private Text namaWaliText;
    @FXML private Text tinggalBersamaText;
    @FXML private Text deskripsiKeluargaText;
    
    // Kondisi Ekonomi
    @FXML private Text pekerjaanAyahText;
    @FXML private Text penghasilanAyahText;
    @FXML private Text pekerjaanIbuText;
    @FXML private Text penghasilanIbuText;
    @FXML private Text pekerjaanWaliText;
    @FXML private Text penghasilanWaliText;
    @FXML private Text deskripsiEkonomiText;
    
    // Kondisi Pendidikan
    @FXML private Text sekolahTerakhirText;
    @FXML private Text alasanPutusSekolahText;
    @FXML private Text minatBelajarText;
    
    // Kondisi Kesehatan
    @FXML private Text riwayatPenyakitText;
    @FXML private Text layananKesehatanText;
    @FXML private Text deskripsiKesehatanText;
    
    // Tabel Monitoring
    @FXML private TableView<LaporanMonitoring> monitoringTable;
    @FXML private TableColumn<LaporanMonitoring, String> colTanggal;
    @FXML private TableColumn<LaporanMonitoring, String> colRelawan;
    @FXML private TableColumn<LaporanMonitoring, String> colStatus;
    @FXML private TableColumn<LaporanMonitoring, String> colDetail;
    
    // Foto
    @FXML private ImageView fotoImageView;
    @FXML private javafx.scene.shape.Circle fotoCircle;
    
    // Navigation buttons
    @FXML private Button btnBeranda;
    @FXML private Button btnAnak;
    @FXML private Button btnLogout;
    
    private AnakDAO anakDAO;
    private DetailAnakDAO detailAnakDAO;
    private UserDAO userDAO;
    private LaporanMonitoringDAO laporanDAO;
    
    private int currentAnakId; // ID anak yang sedang ditampilkan
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        anakDAO = new AnakDAO();
        detailAnakDAO = new DetailAnakDAO();
        userDAO = new UserDAO();
        laporanDAO = new LaporanMonitoringDAO();
        
        // Setup table columns
        setupTableColumns();
        
        // Initialize foto ImageView
        if (fotoImageView != null) {
            fotoImageView.setVisible(false);
            System.out.println("fotoImageView initialized and set to invisible");
        } else {
            System.err.println("WARNING: fotoImageView is null in initialize()!");
        }
        if (fotoCircle != null) {
            fotoCircle.setVisible(true);
            System.out.println("fotoCircle initialized and set to visible");
        } else {
            System.err.println("WARNING: fotoCircle is null in initialize()!");
        }
        
        // Load data - akan dipanggil setelah setAnakId
    }
    
    /**
     * Set ID anak yang akan ditampilkan detailnya
     * Method ini dipanggil dari halaman lain sebelum menampilkan DetailAnak
     * @param anakId ID anak yang akan ditampilkan
     */
    public void setAnakId(int anakId) {
        this.currentAnakId = anakId;
        if (currentAnakId > 0) {
            loadData();
        }
    }
    
    /**
     * Initialize dengan ID anak dari parameter (jika ada)
     */
    public void initializeWithAnakId(int anakId) {
        this.currentAnakId = anakId;
        loadData();
    }
    
    private void setupTableColumns() {
        if (monitoringTable != null && colTanggal != null) {
            colTanggal.setCellValueFactory(cellData -> {
                LaporanMonitoring laporan = cellData.getValue();
                if (laporan.getTanggalMonitoring() != null) {
                    return new javafx.beans.property.SimpleStringProperty(
                        laporan.getTanggalMonitoring().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
                    );
                }
                return new javafx.beans.property.SimpleStringProperty("");
            });
            
            colRelawan.setCellValueFactory(cellData -> {
                LaporanMonitoring laporan = cellData.getValue();
                // Get nama relawan dari user table
                User relawan = userDAO.getUserById(laporan.getIdUser());
                return new javafx.beans.property.SimpleStringProperty(
                    relawan != null ? relawan.getNama() : "-"
                );
            });
            
            colStatus.setCellValueFactory(cellData -> {
                LaporanMonitoring laporan = cellData.getValue();
                String status = "";
                if (laporan.getProgressPendidikan() != null && !laporan.getProgressPendidikan().isEmpty()) {
                    status = laporan.getProgressPendidikan();
                }
                return new javafx.beans.property.SimpleStringProperty(status);
            });
            
            colDetail.setCellValueFactory(cellData -> {
                LaporanMonitoring laporan = cellData.getValue();
                String detail = "";
                if (laporan.getCatatan() != null && !laporan.getCatatan().isEmpty()) {
                    detail = laporan.getCatatan();
                }
                return new javafx.beans.property.SimpleStringProperty(detail);
            });
        }
    }
    
    private void loadData() {
        if (currentAnakId <= 0) {
            return;
        }
        
        // Load data anak
        Anak anak = anakDAO.getAnakById(currentAnakId);
        if (anak == null) {
            showAlert(AlertType.ERROR, "Error", "Data anak tidak ditemukan!");
            return;
        }
        
        // Load data relawan
        User relawan = userDAO.getUserById(anak.getIdRelawan());
        
        // Load detail anak
        DetailAnak detail = detailAnakDAO.getDetailByAnakId(currentAnakId);
        
        // Load laporan monitoring
        List<LaporanMonitoring> laporanList = laporanDAO.getLaporanByAnak(currentAnakId);
        
        // Populate UI
        populateHeader(anak);
        populateAnakInfo(anak, detail);
        populateKeluargaInfo(detail);
        populateEkonomiInfo(detail);
        populatePendidikanInfo(detail);
        populateKesehatanInfo(detail);
        populateFoto(detail);
        populateMonitoringTable(laporanList);
    }
    
    private void populateHeader(Anak anak) {
        if (anak != null) {
            if (namaAnakHeaderText != null) {
                namaAnakHeaderText.setText(anak.getNama() != null ? anak.getNama() : "Detail Anak");
            }
            if (anak.getCreatedAt() != null && tanggalText != null) {
                tanggalText.setText("Tanggal: " + 
                    anak.getCreatedAt().format(DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id"))));
            }
        } else {
            if (namaAnakHeaderText != null) {
                namaAnakHeaderText.setText("Detail Anak");
            }
        }
        
        if (statusText != null) {
            statusText.setText("Status: Aktif");
        }
    }
    
    public void refreshData() {
        loadData();
    }
    
    private void populateAnakInfo(Anak anak, DetailAnak detail) {
        // Populate data anak
        if (anak != null) {
            if (namaAnakText != null) namaAnakText.setText(anak.getNama() != null ? anak.getNama() : "-");
            if (tanggalLahirText != null && anak.getTanggalLahir() != null) {
                tanggalLahirText.setText(anak.getTanggalLahir().format(DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id"))));
            } else if (tanggalLahirText != null) {
                tanggalLahirText.setText("-");
            }
            if (jenisKelaminText != null) jenisKelaminText.setText(anak.getJenisKelamin() != null ? (anak.getJenisKelamin().equals("L") ? "Laki-laki" : "Perempuan") : "-");
            if (statusPendidikanText != null) statusPendidikanText.setText(anak.getStatusPendidikan() != null ? anak.getStatusPendidikan() : "-");
            if (alamatAnakText != null) alamatAnakText.setText(anak.getAlamat() != null ? anak.getAlamat() : "-");
            if (namaOrangtuaAnakText != null) namaOrangtuaAnakText.setText(anak.getNamaOrangtua() != null ? anak.getNamaOrangtua() : "-");
            if (noTelpOrangtuaAnakText != null) noTelpOrangtuaAnakText.setText(anak.getNoTelpOrangtua() != null ? anak.getNoTelpOrangtua() : "-");
        } else {
            if (namaAnakText != null) namaAnakText.setText("-");
            if (tanggalLahirText != null) tanggalLahirText.setText("-");
            if (jenisKelaminText != null) jenisKelaminText.setText("-");
            if (statusPendidikanText != null) statusPendidikanText.setText("-");
            if (alamatAnakText != null) alamatAnakText.setText("-");
            if (namaOrangtuaAnakText != null) namaOrangtuaAnakText.setText("-");
            if (noTelpOrangtuaAnakText != null) noTelpOrangtuaAnakText.setText("-");
        }
    }
    
    private void populateKeluargaInfo(DetailAnak detail) {
        if (detail != null) {
            if (statusOrangtuaText != null) {
                statusOrangtuaText.setText(detail.getStatusOrangtua() != null ? detail.getStatusOrangtua() : "-");
            }
            if (namaAyahText != null) {
                namaAyahText.setText(detail.getNamaAyah() != null ? detail.getNamaAyah() : "-");
            }
            if (namaIbuText != null) {
                namaIbuText.setText(detail.getNamaIbu() != null ? detail.getNamaIbu() : "-");
            }
            if (namaWaliText != null) {
                namaWaliText.setText(detail.getNamaWali() != null ? detail.getNamaWali() : "-");
            }
            if (tinggalBersamaText != null) {
                tinggalBersamaText.setText(detail.getTinggalBersama() != null ? detail.getTinggalBersama() : "-");
            }
            if (deskripsiKeluargaText != null) {
                deskripsiKeluargaText.setText(detail.getDeskripsiKeluarga() != null ? detail.getDeskripsiKeluarga() : "-");
            }
        } else {
            // Jika detail belum ada, tampilkan "-"
            if (statusOrangtuaText != null) statusOrangtuaText.setText("-");
            if (namaAyahText != null) namaAyahText.setText("-");
            if (namaIbuText != null) namaIbuText.setText("-");
            if (namaWaliText != null) namaWaliText.setText("-");
            if (tinggalBersamaText != null) tinggalBersamaText.setText("-");
            if (deskripsiKeluargaText != null) deskripsiKeluargaText.setText("-");
        }
    }
    
    private void populateEkonomiInfo(DetailAnak detail) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        
        if (detail != null) {
            if (pekerjaanAyahText != null) {
                pekerjaanAyahText.setText(detail.getPekerjaanAyah() != null ? detail.getPekerjaanAyah() : "-");
            }
            if (penghasilanAyahText != null) {
                penghasilanAyahText.setText(detail.getPenghasilanAyah() != null ? 
                    formatter.format(detail.getPenghasilanAyah()) : "-");
            }
            if (pekerjaanIbuText != null) {
                pekerjaanIbuText.setText(detail.getPekerjaanIbu() != null ? detail.getPekerjaanIbu() : "-");
            }
            if (penghasilanIbuText != null) {
                penghasilanIbuText.setText(detail.getPenghasilanIbu() != null ? 
                    formatter.format(detail.getPenghasilanIbu()) : "-");
            }
            if (pekerjaanWaliText != null) {
                pekerjaanWaliText.setText(detail.getPekerjaanWali() != null ? detail.getPekerjaanWali() : "-");
            }
            if (penghasilanWaliText != null) {
                penghasilanWaliText.setText(detail.getPenghasilanWali() != null ? 
                    formatter.format(detail.getPenghasilanWali()) : "-");
            }
            if (deskripsiEkonomiText != null) {
                deskripsiEkonomiText.setText(detail.getDeskripsiEkonomi() != null ? detail.getDeskripsiEkonomi() : "-");
            }
        } else {
            if (pekerjaanAyahText != null) pekerjaanAyahText.setText("-");
            if (penghasilanAyahText != null) penghasilanAyahText.setText("-");
            if (pekerjaanIbuText != null) pekerjaanIbuText.setText("-");
            if (penghasilanIbuText != null) penghasilanIbuText.setText("-");
            if (pekerjaanWaliText != null) pekerjaanWaliText.setText("-");
            if (penghasilanWaliText != null) penghasilanWaliText.setText("-");
            if (deskripsiEkonomiText != null) deskripsiEkonomiText.setText("-");
        }
    }
    
    private void populatePendidikanInfo(DetailAnak detail) {
        if (detail != null) {
            if (sekolahTerakhirText != null) {
                sekolahTerakhirText.setText(detail.getSekolahTerakhir() != null ? detail.getSekolahTerakhir() : "-");
            }
            if (alasanPutusSekolahText != null) {
                alasanPutusSekolahText.setText(detail.getAlasanPutusSekolah() != null ? detail.getAlasanPutusSekolah() : "-");
            }
            if (minatBelajarText != null) {
                minatBelajarText.setText(detail.getMinatBelajar() != null ? detail.getMinatBelajar() : "-");
            }
        } else {
            if (sekolahTerakhirText != null) sekolahTerakhirText.setText("-");
            if (alasanPutusSekolahText != null) alasanPutusSekolahText.setText("-");
            if (minatBelajarText != null) minatBelajarText.setText("-");
        }
    }
    
    private void populateKesehatanInfo(DetailAnak detail) {
        if (detail != null) {
            if (riwayatPenyakitText != null) {
                riwayatPenyakitText.setText(detail.getRiwayatPenyakit() != null ? detail.getRiwayatPenyakit() : "-");
            }
            if (layananKesehatanText != null) {
                layananKesehatanText.setText(detail.getLayananKesehatan() != null ? detail.getLayananKesehatan() : "-");
            }
            if (deskripsiKesehatanText != null) {
                deskripsiKesehatanText.setText(detail.getDeskripsiKesehatan() != null ? detail.getDeskripsiKesehatan() : "-");
            }
        } else {
            if (riwayatPenyakitText != null) riwayatPenyakitText.setText("-");
            if (layananKesehatanText != null) layananKesehatanText.setText("-");
            if (deskripsiKesehatanText != null) deskripsiKesehatanText.setText("-");
        }
    }
    
    private void populateFoto(DetailAnak detail) {
        System.out.println("=== populateFoto called ===");
        System.out.println("Detail: " + (detail != null ? "exists" : "null"));
        
        if (fotoImageView == null) {
            System.err.println("ERROR: fotoImageView is NULL!");
            return;
        }
        if (fotoCircle == null) {
            System.err.println("ERROR: fotoCircle is NULL!");
        }
        
        if (detail != null && detail.getFoto() != null && !detail.getFoto().isEmpty()) {
            System.out.println("Foto path from DB: " + detail.getFoto());
            try {
                // Try multiple paths
                File file = new File("src/main/java/assets/detail_anak/" + detail.getFoto());
                System.out.println("Trying path 1: " + file.getAbsolutePath());
                System.out.println("File exists: " + file.exists());
                
                // If file doesn't exist, try absolute path
                if (!file.exists()) {
                    String projectRoot = System.getProperty("user.dir");
                    file = new File(projectRoot, "src/main/java/assets/detail_anak/" + detail.getFoto());
                    System.out.println("Trying path 2: " + file.getAbsolutePath());
                    System.out.println("File exists: " + file.exists());
                }
                
                if (file.exists()) {
                    System.out.println("Loading foto from: " + file.getAbsolutePath());
                    Image image = new Image(file.toURI().toString());
                    
                    // Check if image loaded successfully
                    image.errorProperty().addListener((obs, wasError, isNowError) -> {
                        if (isNowError) {
                            System.err.println("Error loading image: " + image.getException().getMessage());
                        }
                    });
                    
                    if (fotoImageView != null) {
                        fotoImageView.setImage(image);
                        fotoImageView.setVisible(true);
                        System.out.println("fotoImageView.setVisible(true) called");
                        if (fotoCircle != null) {
                            fotoCircle.setVisible(false);
                            System.out.println("fotoCircle.setVisible(false) called");
                        }
                        System.out.println("Foto berhasil dimuat dan ditampilkan!");
                    } else {
                        System.err.println("ERROR: fotoImageView is null after check!");
                    }
                } else {
                    // File tidak ditemukan, tampilkan circle default
                    System.err.println("File foto tidak ditemukan: " + file.getAbsolutePath());
                    System.err.println("Mencari file: " + detail.getFoto());
                    if (fotoImageView != null) {
                        fotoImageView.setVisible(false);
                    }
                    if (fotoCircle != null) {
                        fotoCircle.setVisible(true);
                    }
                }
            } catch (Exception e) {
                System.err.println("Gagal memuat foto: " + e.getMessage());
                e.printStackTrace();
                // Tampilkan circle default jika error
                if (fotoImageView != null) {
                    fotoImageView.setVisible(false);
                }
                if (fotoCircle != null) {
                    fotoCircle.setVisible(true);
                }
            }
        } else {
            // No foto, show default circle
            System.out.println("Tidak ada foto untuk ditampilkan");
            System.out.println("Detail null: " + (detail == null));
            if (detail != null) {
                System.out.println("Foto null: " + (detail.getFoto() == null));
                System.out.println("Foto empty: " + (detail.getFoto() != null && detail.getFoto().isEmpty()));
            }
            if (fotoImageView != null) {
                fotoImageView.setVisible(false);
            }
            if (fotoCircle != null) {
                fotoCircle.setVisible(true);
            }
        }
        System.out.println("=== populateFoto finished ===");
    }
    
    private void populateMonitoringTable(List<LaporanMonitoring> laporanList) {
        if (monitoringTable != null) {
            monitoringTable.getItems().clear();
            if (laporanList != null && !laporanList.isEmpty()) {
                monitoringTable.getItems().addAll(laporanList);
            }
        }
    }
    
    @FXML
    private Button btnBack;
    @FXML
    private Button btnEditDetail;
    
    @FXML
    private void handleBack() {
        // Navigate back to previous page based on user role
        try {
            User currentUser = SessionManager.getCurrentUser();
            if (currentUser != null && currentUser.getRole().equals("admin")) {
                navigateToPage("src/main/java/view/AnakAdminView.fxml");
            } else {
                navigateToPage("src/main/java/view/AnakView.fxml");
            }
        } catch (Exception e) {
            navigateToPage("src/main/java/view/AnakView.fxml");
        }
    }
    
    @FXML
    private void handleEditDetail() {
        openEditDetailModal();
    }
    
    private void openEditDetailModal() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = new File("src/main/java/view/DetailAnakFormModal.fxml").toURI().toURL();
            loader.setLocation(url);
            Parent root = loader.load();
            
            // Get controller and set data
            DetailAnakFormModalController controller = loader.getController();
            controller.setAnakId(currentAnakId);
            controller.setParentController(this);
            
            Stage modalStage = new Stage();
            modalStage.setTitle("Edit Detail Anak");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            
            if (btnBack != null && btnBack.getScene() != null) {
                modalStage.initOwner(btnBack.getScene().getWindow());
            }
            
            Scene scene = new Scene(root);
            modalStage.setScene(scene);
            modalStage.showAndWait();
            
            // Refresh data after modal closes
            loadData();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "Tidak dapat membuka form edit: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleBeranda() {
        navigateToPage("src/main/java/view/DashboardRelawan.fxml");
    }
    
    @FXML
    private void handleAnak() {
        navigateToPage("src/main/java/view/AnakView.fxml");
    }
    
    @FXML
    private void handleLogout() {
        try {
            SessionManager.clearSession();
            navigateToPage("src/main/java/view/login.fxml");
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "Error saat logout: " + e.getMessage());
        }
    }
    
    private void navigateToPage(String fxmlPath) {
        try {
            URL url = new File(fxmlPath).toURI().toURL();
            Parent root = FXMLLoader.load(url);
            Scene scene = new Scene(root);
            Stage stage = null;
            if (btnBack != null && btnBack.getScene() != null) {
                stage = (Stage) btnBack.getScene().getWindow();
            } else if (btnBeranda != null && btnBeranda.getScene() != null) {
                stage = (Stage) btnBeranda.getScene().getWindow();
            } else {
                // Fallback: create new stage
                stage = new Stage();
            }
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

