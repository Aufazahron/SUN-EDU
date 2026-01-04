package controller;

import dao.AnakDAO;
import dao.UserDAO;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Anak;
import model.User;
import util.SessionManager;

/**
 * Controller untuk halaman Manajemen Data Anak (Admin)
 */
public class AnakAdminController implements Initializable {
    
    @FXML
    private TableView<AnakDAO.AnakWithRelawan> anakTable;
    
    @FXML
    private TableColumn<AnakDAO.AnakWithRelawan, Integer> colId;
    
    @FXML
    private TableColumn<AnakDAO.AnakWithRelawan, String> colNama;
    
    @FXML
    private TableColumn<AnakDAO.AnakWithRelawan, LocalDate> colTanggalLahir;
    
    @FXML
    private TableColumn<AnakDAO.AnakWithRelawan, String> colJenisKelamin;
    
    @FXML
    private TableColumn<AnakDAO.AnakWithRelawan, String> colStatusPendidikan;
    
    
    @FXML
    private TableColumn<AnakDAO.AnakWithRelawan, String> colAlamat;
    
    @FXML
    private TableColumn<AnakDAO.AnakWithRelawan, String> colRelawan;
    
    @FXML
    private TableColumn<AnakDAO.AnakWithRelawan, String> colAksi;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private ComboBox<String> filterStatusPendidikan;
    
    @FXML
    private ComboBox<String> filterRelawan;
    
    @FXML
    private Label emptyLabel;
    
    @FXML
    private Button btnTambahAnak;
    
    @FXML
    private Label statTotalLabel;
    
    @FXML
    private Label statRentanLabel;
    
    @FXML
    private Label statPemantauanLabel;
    
    @FXML
    private Label statStabilLabel;
    
    private AnakDAO anakDAO;
    private UserDAO userDAO;
    private ObservableList<AnakDAO.AnakWithRelawan> anakList;
    private List<User> allRelawan;
    private Map<Integer, String> relawanMap; // Map ID relawan ke nama
    private Anak anakEdit; // Untuk menyimpan data anak yang sedang diedit
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        anakDAO = new AnakDAO();
        userDAO = new UserDAO();
        anakList = FXCollections.observableArrayList();
        relawanMap = new HashMap<>();
        
        // Load semua relawan aktif
        allRelawan = userDAO.getActiveRelawan();
        for (User relawan : allRelawan) {
            relawanMap.put(relawan.getId(), relawan.getNama());
        }
        
        // Setup filter combo box
        filterStatusPendidikan.getItems().addAll("Semua", "Diajukan", "Rentan", "Dalam Pemantauan", "Stabil", "Selesai");
        filterStatusPendidikan.setValue("Semua");
        
        // Setup filter relawan
        filterRelawan.getItems().add("Semua Relawan");
        for (User relawan : allRelawan) {
            filterRelawan.getItems().add(relawan.getNama() + " (ID: " + relawan.getId() + ")");
        }
        filterRelawan.setValue("Semua Relawan");
        
        // Setup table columns
        setupTableColumns();
        
        // Load data
        loadAnakData();
        loadStatistik();
    }
    
    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colTanggalLahir.setCellValueFactory(new PropertyValueFactory<>("tanggalLahir"));
        colJenisKelamin.setCellValueFactory(new PropertyValueFactory<>("jenisKelamin"));
        colStatusPendidikan.setCellValueFactory(new PropertyValueFactory<>("statusPendidikan"));
        colAlamat.setCellValueFactory(new PropertyValueFactory<>("alamat"));
        colRelawan.setCellValueFactory(new PropertyValueFactory<>("namaRelawan"));
        
        // Setup column aksi dengan tombol Edit, Approve, Ubah Relawan, Detail, dan Hapus
        colAksi.setCellFactory(new Callback<TableColumn<AnakDAO.AnakWithRelawan, String>, TableCell<AnakDAO.AnakWithRelawan, String>>() {
            @Override
            public TableCell<AnakDAO.AnakWithRelawan, String> call(TableColumn<AnakDAO.AnakWithRelawan, String> param) {
                return new TableCell<AnakDAO.AnakWithRelawan, String>() {
                    private final Button btnEdit = new Button("Edit");
                    private final Button btnApprove = new Button("Approve");
                    private final Button btnUbahRelawan = new Button("Ubah Relawan");
                    private final Button btnDetail = new Button("Detail");
                    private final Button btnHapus = new Button("Hapus");
                    private final HBox hbox = new HBox(5);
                    
                    {
                        btnEdit.setStyle("-fx-background-color: #020E4C; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 11px;");
                        btnApprove.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 11px;");
                        btnUbahRelawan.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 11px;");
                        btnDetail.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 11px;");
                        btnHapus.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 11px;");
                        hbox.setPadding(new Insets(3));
                        hbox.setSpacing(5);
                        
                        btnEdit.setOnAction(e -> {
                            AnakDAO.AnakWithRelawan anakWR = getTableView().getItems().get(getIndex());
                            Anak anak = convertToAnak(anakWR);
                            handleEditAnak(anak);
                        });
                        
                        btnApprove.setOnAction(e -> {
                            AnakDAO.AnakWithRelawan anakWR = getTableView().getItems().get(getIndex());
                            Anak anak = convertToAnak(anakWR);
                            handleApproveAnak(anak);
                        });
                        
                        btnUbahRelawan.setOnAction(e -> {
                            AnakDAO.AnakWithRelawan anakWR = getTableView().getItems().get(getIndex());
                            Anak anak = convertToAnak(anakWR);
                            handleUbahRelawan(anak);
                        });
                        
                        btnDetail.setOnAction(e -> {
                            AnakDAO.AnakWithRelawan anakWR = getTableView().getItems().get(getIndex());
                            Anak anak = convertToAnak(anakWR);
                            handleDetailAnak(anak);
                        });
                        
                        btnHapus.setOnAction(e -> {
                            AnakDAO.AnakWithRelawan anakWR = getTableView().getItems().get(getIndex());
                            Anak anak = convertToAnak(anakWR);
                            handleHapusAnak(anak);
                        });
                    }
                    
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            AnakDAO.AnakWithRelawan anakWR = getTableView().getItems().get(getIndex());
                            hbox.getChildren().clear();
                            hbox.getChildren().add(btnEdit);
                            
                            // Tampilkan tombol Approve hanya jika status "Diajukan"
                            if ("Diajukan".equals(anakWR.getStatusPendidikan())) {
                                hbox.getChildren().add(btnApprove);
                            }
                            
                            hbox.getChildren().add(btnUbahRelawan);
                            hbox.getChildren().add(btnDetail);
                            hbox.getChildren().add(btnHapus);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
        
        anakTable.setItems(anakList);
    }
    
    private Anak convertToAnak(AnakDAO.AnakWithRelawan anakWR) {
        Anak anak = new Anak();
        anak.setId(anakWR.getId());
        anak.setIdRelawan(anakWR.getIdRelawan());
        anak.setNama(anakWR.getNama());
        anak.setTanggalLahir(anakWR.getTanggalLahir());
        anak.setJenisKelamin(anakWR.getJenisKelamin());
        anak.setAlamat(anakWR.getAlamat());
        anak.setStatusPendidikan(anakWR.getStatusPendidikan());
        anak.setNamaOrangtua(anakWR.getNamaOrangtua());
        anak.setNoTelpOrangtua(anakWR.getNoTelpOrangtua());
        return anak;
    }
    
    private List<AnakDAO.AnakWithRelawan> allAnakData; // Store all data
    
    private void loadAnakData() {
        // Load all data from database
        allAnakData = anakDAO.getAllAnakWithRelawan();
        
        // Apply filters
        applyFilters();
    }
    
    private void applyFilters() {
        String statusFilter = filterStatusPendidikan.getValue();
        String relawanFilter = filterRelawan.getValue();
        String searchKeyword = searchField.getText().trim();
        
        List<AnakDAO.AnakWithRelawan> filtered = allAnakData.stream()
            .filter(anak -> {
                // Filter by status
                if (statusFilter != null && !statusFilter.equals("Semua")) {
                    if (!anak.getStatusPendidikan().equals(statusFilter)) {
                        return false;
                    }
                }
                
                // Filter by relawan
                if (relawanFilter != null && !relawanFilter.equals("Semua Relawan")) {
                    // Extract ID from "Nama (ID: X)"
                    try {
                        int idStart = relawanFilter.indexOf("ID: ") + 4;
                        int idEnd = relawanFilter.indexOf(")", idStart);
                        if (idEnd > idStart) {
                            int relawanId = Integer.parseInt(relawanFilter.substring(idStart, idEnd));
                            if (anak.getIdRelawan() != relawanId) {
                                return false;
                            }
                        }
                    } catch (Exception e) {
                        // If parsing fails, skip this filter
                    }
                }
                
                // Filter by search keyword
                if (!searchKeyword.isEmpty()) {
                    String lowerKeyword = searchKeyword.toLowerCase();
                    if (!anak.getNama().toLowerCase().contains(lowerKeyword) &&
                        !anak.getAlamat().toLowerCase().contains(lowerKeyword) &&
                        !(anak.getNamaRelawan() != null && anak.getNamaRelawan().toLowerCase().contains(lowerKeyword))) {
                        return false;
                    }
                }
                
                return true;
            })
            .collect(Collectors.toList());
        
        anakList.clear();
        anakList.addAll(filtered);
        
        // Update empty label
        emptyLabel.setVisible(anakList.isEmpty());
    }
    
    private void loadStatistik() {
        Map<String, Integer> statistik = anakDAO.getStatistikByStatus();
        
        int total = anakDAO.getAllAnak().size();
        statTotalLabel.setText(String.valueOf(total));
        
        statRentanLabel.setText(String.valueOf(statistik.getOrDefault("Rentan", 0)));
        statPemantauanLabel.setText(String.valueOf(statistik.getOrDefault("Dalam Pemantauan", 0)));
        statStabilLabel.setText(String.valueOf(statistik.getOrDefault("Stabil", 0)));
    }
    
    @FXML
    private void handleTambahAnak(ActionEvent event) {
        anakEdit = null; // Reset edit mode
        openAnakFormModal();
    }
    
    @FXML
    private void handleEditAnak(Anak anak) {
        anakEdit = anak;
        openAnakFormModal();
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
            loadStatistik();
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
        ComboBox<String> relawanCombo = (ComboBox<String>) root.lookup("#relawanCombo");
        
        // Setup relawan combo if exists (untuk admin)
        VBox relawanContainer = (VBox) root.lookup("#relawanContainer");
        if (relawanContainer != null) {
            relawanContainer.setVisible(true);
        }
        if (relawanCombo != null) {
            relawanCombo.getItems().clear();
            for (User relawan : allRelawan) {
                relawanCombo.getItems().add(relawan.getNama() + " (ID: " + relawan.getId() + ")");
            }
        }
        
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
            if (relawanCombo != null) relawanCombo.setValue(null);
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
            if (relawanCombo != null) {
                String relawanName = relawanMap.get(anakEdit.getIdRelawan());
                if (relawanName != null) {
                    relawanCombo.setValue(relawanName + " (ID: " + anakEdit.getIdRelawan() + ")");
                }
            }
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
        ComboBox<String> relawanCombo = (ComboBox<String>) root.lookup("#relawanCombo");
        
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
        
        // Get relawan ID
        int idRelawan = 0;
        if (relawanCombo != null && relawanCombo.getValue() != null) {
            try {
                String value = relawanCombo.getValue();
                int idStart = value.indexOf("ID: ") + 4;
                int idEnd = value.indexOf(")", idStart);
                if (idEnd > idStart) {
                    idRelawan = Integer.parseInt(value.substring(idStart, idEnd));
                }
            } catch (Exception e) {
                showAlert(AlertType.WARNING, "Validasi", "Relawan harus dipilih!");
                return;
            }
        } else if (anakEdit == null) {
            // For new anak, relawan must be selected
            showAlert(AlertType.WARNING, "Validasi", "Relawan harus dipilih!");
            return;
        }
        
        // Buat object Anak
        Anak anak;
        if (anakEdit == null) {
            anak = new Anak();
            anak.setIdRelawan(idRelawan);
        } else {
            anak = anakEdit;
            if (idRelawan > 0) {
                anak.setIdRelawan(idRelawan);
            }
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
                    loadStatistik();
                } else {
                    showAlert(AlertType.ERROR, "Error", "Gagal menghapus data anak!");
                }
            }
        });
    }
    
    @FXML
    private void handleApproveAnak(Anak anak) {
        // Tampilkan dialog untuk memilih status baru setelah approve
        Alert approveAlert = new Alert(AlertType.CONFIRMATION);
        approveAlert.setTitle("Approve Anak");
        approveAlert.setHeaderText("Pilih Status Baru untuk " + anak.getNama());
        approveAlert.setContentText("Status saat ini: " + anak.getStatusPendidikan() + "\n\nPilih status baru:");
        
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Rentan", "Dalam Pemantauan", "Stabil");
        statusCombo.setValue("Rentan");
        
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(new Label("Status Baru:"), statusCombo);
        approveAlert.getDialogPane().setContent(vbox);
        
        approveAlert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
        
        approveAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String newStatus = statusCombo.getValue();
                if (newStatus != null) {
                    anak.setStatusPendidikan(newStatus);
                    if (anakDAO.update(anak)) {
                        showAlert(AlertType.INFORMATION, "Berhasil", "Status anak berhasil diubah menjadi " + newStatus + "!");
                        loadAnakData();
                        loadStatistik();
                    } else {
                        showAlert(AlertType.ERROR, "Error", "Gagal mengubah status anak!");
                    }
                }
            }
        });
    }
    
    @FXML
    private void handleUbahRelawan(Anak anak) {
        // Tampilkan dialog untuk memilih relawan baru
        Alert ubahRelawanAlert = new Alert(AlertType.CONFIRMATION);
        ubahRelawanAlert.setTitle("Ubah Relawan");
        ubahRelawanAlert.setHeaderText("Pilih Relawan Baru untuk " + anak.getNama());
        
        ComboBox<String> relawanCombo = new ComboBox<>();
        for (User relawan : allRelawan) {
            relawanCombo.getItems().add(relawan.getNama() + " (ID: " + relawan.getId() + ")");
        }
        
        // Set relawan saat ini sebagai default
        String currentRelawanName = relawanMap.get(anak.getIdRelawan());
        if (currentRelawanName != null) {
            relawanCombo.setValue(currentRelawanName + " (ID: " + anak.getIdRelawan() + ")");
        }
        
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(new Label("Relawan Baru:"), relawanCombo);
        ubahRelawanAlert.getDialogPane().setContent(vbox);
        
        ubahRelawanAlert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
        
        ubahRelawanAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String selectedValue = relawanCombo.getValue();
                if (selectedValue != null) {
                    try {
                        int idStart = selectedValue.indexOf("ID: ") + 4;
                        int idEnd = selectedValue.indexOf(")", idStart);
                        if (idEnd > idStart) {
                            int newRelawanId = Integer.parseInt(selectedValue.substring(idStart, idEnd));
                            anak.setIdRelawan(newRelawanId);
                            if (anakDAO.update(anak)) {
                                showAlert(AlertType.INFORMATION, "Berhasil", "Relawan berhasil diubah!");
                                loadAnakData();
                            } else {
                                showAlert(AlertType.ERROR, "Error", "Gagal mengubah relawan!");
                            }
                        }
                    } catch (Exception e) {
                        showAlert(AlertType.ERROR, "Error", "Gagal memproses perubahan relawan: " + e.getMessage());
                    }
                }
            }
        });
    }
    
    @FXML
    private void handleFilterStatus(ActionEvent event) {
        applyFilters();
    }
    
    @FXML
    private void handleFilterRelawan(ActionEvent event) {
        applyFilters();
    }
    
    @FXML
    private void handleSearch(javafx.scene.input.KeyEvent event) {
        applyFilters();
    }
    
    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        navigateToPage("src/main/java/view/DashboardAdmin.fxml", event);
    }
    
    @FXML
    private void handleRelawanMenu(ActionEvent event) {
        navigateToPage("src/main/java/view/ListRelawan.fxml", event);
    }
    
    @FXML
    private void handleProgramKerjaMenu(ActionEvent event) {
        navigateToPage("src/main/java/view/ProgramAdminView.fxml", event);
    }
    
    @FXML
    private void handleLaporanMenu(ActionEvent event) {
        navigateToPage("src/main/java/view/ListMonitoringAdmin.fxml", event);
    }
    
    @FXML
    private void handleLaporanProgramMenu(ActionEvent event) {
        navigateToPage("src/main/java/view/LaporanProgramAdminView.fxml", event);
    }
    
    @FXML
    private void handleFaqMenu(ActionEvent event) {
        navigateToPage("src/main/java/view/FaqAdminView.fxml", event);
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

