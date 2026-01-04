package controller;

import dao.AnakDAO;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Anak;
import util.SessionManager;

/**
 * Controller untuk halaman Manajemen Data Anak
 */
public class AnakController implements Initializable {
    @FXML private Label formTitleLabel;
    @FXML private TextField namaField;
    @FXML private DatePicker tanggalLahirPicker;
    @FXML private ComboBox<String> jenisKelaminCombo;
    @FXML private ComboBox<String> statusPendidikanCombo;
    @FXML private TextArea alamatField;
    @FXML private TextField namaOrangtuaField;
    @FXML private TextField noTelpOrangtuaField;
    
    @FXML
    private TableView<Anak> anakTable;
    
    @FXML
    private TableColumn<Anak, Integer> colId;
    
    @FXML
    private TableColumn<Anak, String> colNama;
    
    @FXML
    private TableColumn<Anak, LocalDate> colTanggalLahir;
    
    @FXML
    private TableColumn<Anak, String> colJenisKelamin;
    
    @FXML
    private TableColumn<Anak, String> colStatusPendidikan;
    
    
    @FXML
    private TableColumn<Anak, String> colAlamat;
    
    @FXML
    private TableColumn<Anak, String> colAksi;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private ComboBox<String> filterStatusPendidikan;
    
    @FXML
    private Label emptyLabel;
    
    @FXML
    private Button btnTambahAnak;
    
    private AnakDAO anakDAO;
    private ObservableList<Anak> anakList;
    private int currentRelawanId;
    private Anak anakEdit; // Untuk menyimpan data anak yang sedang diedit
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        anakDAO = new AnakDAO();
        anakList = FXCollections.observableArrayList();
        
        // Ambil ID relawan dari session
        if (SessionManager.getCurrentUser() != null) {
            currentRelawanId = SessionManager.getCurrentUser().getId();
        }
        
        // Setup filter combo box
        filterStatusPendidikan.getItems().addAll("Semua", "Diajukan", "Rentan", "Dalam Pemantauan", "Stabil");
        filterStatusPendidikan.setValue("Semua");
        
        // Setup table columns
        setupTableColumns();
        
        // Load data
        loadAnakData();
    }
    
    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colTanggalLahir.setCellValueFactory(new PropertyValueFactory<>("tanggalLahir"));
        colJenisKelamin.setCellValueFactory(new PropertyValueFactory<>("jenisKelamin"));
        colStatusPendidikan.setCellValueFactory(new PropertyValueFactory<>("statusPendidikan"));
        colAlamat.setCellValueFactory(new PropertyValueFactory<>("alamat"));
        
        // Setup column aksi dengan tombol Edit dan Hapus
        colAksi.setCellFactory(new Callback<TableColumn<Anak, String>, TableCell<Anak, String>>() {
            @Override
            public TableCell<Anak, String> call(TableColumn<Anak, String> param) {
                return new TableCell<Anak, String>() {
                    private final Button btnEdit = new Button("Edit");
                    private final Button btnDetail = new Button("Detail");
                    private final HBox hbox = new HBox(5);
                    
                    {
                        btnEdit.setStyle("-fx-background-color: #020E4C; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 11px;");
                        btnDetail.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 11px;");
                        hbox.setPadding(new Insets(3));
                        hbox.setSpacing(5);
                        
                        btnEdit.setOnAction(e -> {
                            Anak anak = getTableView().getItems().get(getIndex());
                            handleEditAnak(anak);
                        });
                        
                        btnDetail.setOnAction(e -> {
                            Anak anak = getTableView().getItems().get(getIndex());
                            handleDetailAnak(anak);
                        });
                    }
                    
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            hbox.getChildren().clear();
                            hbox.getChildren().addAll(btnEdit, btnDetail);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
        
        anakTable.setItems(anakList);
    }
    
    @FXML
    private void handleDetailAnak(Anak anak) {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = new File("src/main/java/view/DetailAnak.fxml").toURI().toURL();
            loader.setLocation(url);
            Parent root = loader.load();
            
            // Set ID anak ke controller
            DetailAnakController controller = loader.getController();
            controller.setAnakId(anak.getId());
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) anakTable.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "Tidak dapat membuka halaman detail: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadAnakData() {
        anakList.clear();
        java.util.List<Anak> list = anakDAO.getAnakByRelawan(currentRelawanId);
        // Filter out "Selesai" status - tidak ditampilkan ke relawan
        list = list.stream()
            .filter(anak -> !"Selesai".equals(anak.getStatusPendidikan()))
            .collect(java.util.stream.Collectors.toList());
        anakList.addAll(list);
        
        // Update empty label
        emptyLabel.setVisible(anakList.isEmpty());
    }
    
    @FXML
    private void handleTambahAnak(ActionEvent event) {
        anakEdit = null; // Reset edit mode
        openAnakFormModal();
    }
    
    @FXML
    private void handleEditAnak(Anak anak) {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = new File("src/main/java/view/DetailAnakFormModal.fxml").toURI().toURL();
            loader.setLocation(url);
            Parent root = loader.load();
            
            DetailAnakFormModalController controller = loader.getController();
            controller.setAnakId(anak.getId());
            
            Stage modalStage = new Stage();
            modalStage.setTitle("Edit Data & Detail Anak");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            
            if (anakTable.getScene() != null) {
                modalStage.initOwner(anakTable.getScene().getWindow());
            }
            
            Scene scene = new Scene(root);
            modalStage.setScene(scene);
            modalStage.showAndWait();
            
            // Refresh data setelah modal ditutup
            loadAnakData();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "Tidak dapat membuka form: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void openAnakFormModal() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = new File("src/main/java/view/AnakFormModal.fxml").toURI().toURL();
            loader.setLocation(url);
            loader.setController(this); // Set controller yang sama
            Parent root = loader.load();
            
            // Setup form fields
            setupFormFields(root);
            
            Stage modalStage = new Stage();
            modalStage.setTitle(anakEdit == null ? "Tambah Data Anak" : "Edit Data Anak");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            
            if (anakTable.getScene() != null) {
                modalStage.initOwner(anakTable.getScene().getWindow());
            }
            
            Scene scene = new Scene(root);
            modalStage.setScene(scene);
            modalStage.showAndWait();
            
            // Refresh data setelah modal ditutup
            loadAnakData();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "Tidak dapat membuka form: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setupFormFields(Parent root) {
        // Find form fields
        Label formTitleLabel = (Label) root.lookup("#formTitleLabel");
        TextField namaField = (TextField) root.lookup("#namaField");
        DatePicker tanggalLahirPicker = (DatePicker) root.lookup("#tanggalLahirPicker");
        ComboBox<String> jenisKelaminCombo = (ComboBox<String>) root.lookup("#jenisKelaminCombo");
        ComboBox<String> statusPendidikanCombo = (ComboBox<String>) root.lookup("#statusPendidikanCombo");
        TextArea alamatField = (TextArea) root.lookup("#alamatField");
        TextField namaOrangtuaField = (TextField) root.lookup("#namaOrangtuaField");
        TextField noTelpOrangtuaField = (TextField) root.lookup("#noTelpOrangtuaField");
        
        if (anakEdit == null) {
            // Mode tambah - set default status ke "Diajukan"
            if (formTitleLabel != null) formTitleLabel.setText("Tambah Data Anak");
            if (namaField != null) namaField.clear();
            if (tanggalLahirPicker != null) tanggalLahirPicker.setValue(null);
            if (jenisKelaminCombo != null) jenisKelaminCombo.setValue(null);
            if (statusPendidikanCombo != null) {
                statusPendidikanCombo.setValue("Diajukan"); // Default status untuk anak baru
            }
            if (alamatField != null) alamatField.clear();
            if (namaOrangtuaField != null) namaOrangtuaField.clear();
            if (noTelpOrangtuaField != null) noTelpOrangtuaField.clear();
        } else {
            // Mode edit
            if (formTitleLabel != null) formTitleLabel.setText("Edit Data Anak");
            if (namaField != null) namaField.setText(anakEdit.getNama());
            if (tanggalLahirPicker != null) tanggalLahirPicker.setValue(anakEdit.getTanggalLahir());
            if (jenisKelaminCombo != null) jenisKelaminCombo.setValue(anakEdit.getJenisKelamin());
            if (statusPendidikanCombo != null) statusPendidikanCombo.setValue(anakEdit.getStatusPendidikan());
            if (alamatField != null) alamatField.setText(anakEdit.getAlamat());
            if (namaOrangtuaField != null) namaOrangtuaField.setText(anakEdit.getNamaOrangtua());
            if (noTelpOrangtuaField != null) noTelpOrangtuaField.setText(anakEdit.getNoTelpOrangtua());
        }
    }
    
    @FXML
    private void handleSimpanAnak(ActionEvent event) {
        // Get form fields dari modal
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent root = stage.getScene().getRoot();
        
        TextField namaField = (TextField) root.lookup("#namaField");
        DatePicker tanggalLahirPicker = (DatePicker) root.lookup("#tanggalLahirPicker");
        ComboBox<String> jenisKelaminCombo = (ComboBox<String>) root.lookup("#jenisKelaminCombo");
        ComboBox<String> statusPendidikanCombo = (ComboBox<String>) root.lookup("#statusPendidikanCombo");
        TextArea alamatField = (TextArea) root.lookup("#alamatField");
        TextField namaOrangtuaField = (TextField) root.lookup("#namaOrangtuaField");
        TextField noTelpOrangtuaField = (TextField) root.lookup("#noTelpOrangtuaField");
        
        // Validasi
        if (namaField == null || namaField.getText().trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Nama tidak boleh kosong!");
            return;
        }
        
        if (tanggalLahirPicker == null || tanggalLahirPicker.getValue() == null) {
            showAlert(AlertType.WARNING, "Validasi", "Tanggal lahir harus diisi!");
            return;
        }
        
        if (jenisKelaminCombo == null || jenisKelaminCombo.getValue() == null) {
            showAlert(AlertType.WARNING, "Validasi", "Jenis kelamin harus dipilih!");
            return;
        }
        
        if (statusPendidikanCombo == null || statusPendidikanCombo.getValue() == null) {
            showAlert(AlertType.WARNING, "Validasi", "Status pendidikan harus dipilih!");
            return;
        }
        
        if (alamatField == null || alamatField.getText().trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Alamat tidak boleh kosong!");
            return;
        }
        
        if (namaOrangtuaField == null || namaOrangtuaField.getText().trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Nama orangtua tidak boleh kosong!");
            return;
        }
        
        if (noTelpOrangtuaField == null || noTelpOrangtuaField.getText().trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "No. telepon orangtua tidak boleh kosong!");
            return;
        }
        
        // Buat object Anak
        Anak anak;
        if (anakEdit == null) {
            anak = new Anak();
            anak.setIdRelawan(currentRelawanId);
        } else {
            anak = anakEdit;
        }
        
        anak.setNama(namaField.getText().trim());
        anak.setTanggalLahir(tanggalLahirPicker.getValue());
        anak.setJenisKelamin(jenisKelaminCombo.getValue());
        anak.setStatusPendidikan(statusPendidikanCombo.getValue());
        anak.setAlamat(alamatField.getText().trim());
        anak.setNamaOrangtua(namaOrangtuaField.getText().trim());
        anak.setNoTelpOrangtua(noTelpOrangtuaField.getText().trim());
        
        // Simpan ke database
        boolean success;
        if (anakEdit == null) {
            success = anakDAO.save(anak);
        } else {
            success = anakDAO.update(anak);
        }
        
        if (success) {
            showAlert(AlertType.INFORMATION, "Berhasil", 
                anakEdit == null ? "Data anak berhasil ditambahkan!" : "Data anak berhasil diupdate!");
            stage.close();
        } else {
            showAlert(AlertType.ERROR, "Error", "Gagal menyimpan data anak!");
        }
    }
    
    @FXML
    private void handleBatalForm(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void handleHapusAnak(Anak anak) {
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Hapus");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Apakah Anda yakin ingin menghapus data anak " + anak.getNama() + "?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (anakDAO.delete(anak.getId())) {
                    showAlert(AlertType.INFORMATION, "Berhasil", "Data anak berhasil dihapus!");
                    loadAnakData();
                } else {
                    showAlert(AlertType.ERROR, "Error", "Gagal menghapus data anak!");
                }
            }
        });
    }
    
    @FXML
    private void handleFilterStatus(ActionEvent event) {
        String status = filterStatusPendidikan.getValue();
        anakList.clear();
        
        java.util.List<Anak> list;
        if (status == null || status.equals("Semua")) {
            list = anakDAO.getAnakByRelawan(currentRelawanId);
        } else {
            list = anakDAO.getAnakByRelawanAndStatus(currentRelawanId, status);
        }
        
        // Filter out "Selesai" status - tidak ditampilkan ke relawan
        list = list.stream()
            .filter(anak -> !"Selesai".equals(anak.getStatusPendidikan()))
            .collect(java.util.stream.Collectors.toList());
        
        anakList.addAll(list);
        emptyLabel.setVisible(anakList.isEmpty());
    }
    
    @FXML
    private void handleSearch(javafx.scene.input.KeyEvent event) {
        String keyword = searchField.getText().trim();
        anakList.clear();
        
        java.util.List<Anak> list;
        if (keyword.isEmpty()) {
            String status = filterStatusPendidikan.getValue();
            if (status == null || status.equals("Semua")) {
                list = anakDAO.getAnakByRelawan(currentRelawanId);
            } else {
                list = anakDAO.getAnakByRelawanAndStatus(currentRelawanId, status);
            }
        } else {
            list = anakDAO.searchAnak(keyword, currentRelawanId);
            // Filter by status jika ada
            if (filterStatusPendidikan.getValue() != null && !filterStatusPendidikan.getValue().equals("Semua")) {
                list = list.stream()
                    .filter(a -> a.getStatusPendidikan().equals(filterStatusPendidikan.getValue()))
                    .collect(java.util.stream.Collectors.toList());
            }
        }
        
        // Filter out "Selesai" status - tidak ditampilkan ke relawan
        list = list.stream()
            .filter(anak -> !"Selesai".equals(anak.getStatusPendidikan()))
            .collect(java.util.stream.Collectors.toList());
        
        anakList.addAll(list);
        emptyLabel.setVisible(anakList.isEmpty());
    }
    
    @FXML
    private void handleNavigateToMonitoring(ActionEvent event) {
        navigateToPage("src/main/java/view/LaporanView.fxml");
    }
    
    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        navigateToPage("src/main/java/view/DashboardRelawan.fxml");
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
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
            Stage stage = (Stage) anakTable.getScene().getWindow();
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

