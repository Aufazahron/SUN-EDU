package controller;

import dao.LaporanProgramDAO;
import dao.ProgramDAO;
import java.io.File;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Program;
import util.SessionManager;

/**
 * Controller untuk halaman Manajemen Program Kerja (Admin)
 */
public class ProgramAdminController implements Initializable {
    
    @FXML
    private TableView<Program> programTable;
    
    @FXML
    private TableColumn<Program, Integer> colId;
    
    @FXML
    private TableColumn<Program, String> colNama;
    
    @FXML
    private TableColumn<Program, String> colDeskripsi;
    
    @FXML
    private TableColumn<Program, String> colKategori;
    
    @FXML
    private TableColumn<Program, String> colTempat;
    
    @FXML
    private TableColumn<Program, Long> colTargetDonasi;
    
    @FXML
    private TableColumn<Program, Long> colDonasiTerkumpul;
    
    @FXML
    private TableColumn<Program, Integer> colJumlahDonatur;
    
    @FXML
    private TableColumn<Program, String> colAksi;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private Label emptyLabel;
    
    @FXML
    private Button btnTambahProgram;
    
    @FXML
    private Label statTotalLabel;
    
    @FXML
    private Label statDonasiLabel;
    
    @FXML
    private Label statDonaturLabel;
    
    private ProgramDAO programDAO;
    private LaporanProgramDAO laporanProgramDAO;
    private ObservableList<Program> programList;
    private List<Program> allProgramData; // Store all data
    private Program programEdit; // Untuk menyimpan data program yang sedang diedit
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        programDAO = new ProgramDAO();
        laporanProgramDAO = new LaporanProgramDAO();
        programList = FXCollections.observableArrayList();
        
        // Setup table columns
        setupTableColumns();
        
        // Load data
        loadProgramData();
        loadStatistik();
    }
    
    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colKategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        colDeskripsi.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
        colTempat.setCellValueFactory(new PropertyValueFactory<>("tempat"));
        colTargetDonasi.setCellValueFactory(new PropertyValueFactory<>("targetDonasi"));
        colDonasiTerkumpul.setCellValueFactory(new PropertyValueFactory<>("donasiTerkumpul"));
        colJumlahDonatur.setCellValueFactory(new PropertyValueFactory<>("jumlahDonatur"));
        
        // Format currency untuk target donasi dan donasi terkumpul
        colTargetDonasi.setCellFactory(new Callback<TableColumn<Program, Long>, TableCell<Program, Long>>() {
            @Override
            public TableCell<Program, Long> call(TableColumn<Program, Long> param) {
                return new TableCell<Program, Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                            setText(formatter.format(item));
                        }
                    }
                };
            }
        });
        
        colDonasiTerkumpul.setCellFactory(new Callback<TableColumn<Program, Long>, TableCell<Program, Long>>() {
            @Override
            public TableCell<Program, Long> call(TableColumn<Program, Long> param) {
                return new TableCell<Program, Long>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                            setText(formatter.format(item));
                        }
                    }
                };
            }
        });
        
        // Setup column aksi dengan tombol Detail, Edit, Buat Laporan, dan Hapus
        colAksi.setCellFactory(new Callback<TableColumn<Program, String>, TableCell<Program, String>>() {
            @Override
            public TableCell<Program, String> call(TableColumn<Program, String> param) {
                return new TableCell<Program, String>() {
                    private final Button btnDetail = new Button("Detail");
                    private final Button btnEdit = new Button("Edit");
                    private final Button btnLaporan = new Button("Buat Laporan");
                    private final Button btnHapus = new Button("Hapus");
                    private final HBox hbox = new HBox(5);
                    
                    {
                        btnDetail.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 11px;");
                        btnEdit.setStyle("-fx-background-color: #020E4C; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 15; -fx-font-size: 11px;");
                        btnLaporan.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 15; -fx-font-size: 11px;");
                        btnHapus.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 15; -fx-font-size: 11px;");
                        hbox.setPadding(new Insets(5));
                        
                        btnDetail.setOnAction(e -> {
                            Program program = getTableView().getItems().get(getIndex());
                            handleDetailProgram(program);
                        });
                        
                        btnEdit.setOnAction(e -> {
                            Program program = getTableView().getItems().get(getIndex());
                            handleEditProgram(program);
                        });
                        
                        btnLaporan.setOnAction(e -> {
                            Program program = getTableView().getItems().get(getIndex());
                            handleBuatLaporan(program);
                        });
                        
                        btnHapus.setOnAction(e -> {
                            Program program = getTableView().getItems().get(getIndex());
                            handleHapusProgram(program);
                        });
                    }
                    
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Program program = getTableView().getItems().get(getIndex());
                            String status = programDAO.getStatusProgram(program.getId());
                            
                            hbox.getChildren().clear();
                            hbox.getChildren().add(btnDetail);
                            hbox.getChildren().add(btnEdit);
                            
                            // Tampilkan tombol "Buat Laporan" hanya jika status "selesai" dan belum ada laporan
                            if ("selesai".equals(status)) {
                                // Cek apakah sudah ada laporan
                                if (laporanProgramDAO.getLaporanByProgramId(program.getId()) == null) {
                                    hbox.getChildren().add(btnLaporan);
                                }
                            }
                            
                            hbox.getChildren().add(btnHapus);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
        
        programTable.setItems(programList);
    }
    
    private void loadProgramData() {
        // Load all data from database
        allProgramData = programDAO.getAllPrograms();
        
        // Apply filters
        applyFilters();
    }
    
    private void applyFilters() {
        String searchKeyword = searchField.getText().trim();
        
        List<Program> filtered;
        if (searchKeyword.isEmpty()) {
            filtered = allProgramData;
        } else {
            // Filter dari data yang sudah dimuat
            String lowerKeyword = searchKeyword.toLowerCase();
            filtered = allProgramData.stream()
                .filter(program -> 
                    program.getNama().toLowerCase().contains(lowerKeyword) ||
                    program.getDeskripsi().toLowerCase().contains(lowerKeyword) ||
                    program.getTempat().toLowerCase().contains(lowerKeyword)
                )
                .collect(Collectors.toList());
        }
        
        programList.clear();
        programList.addAll(filtered);
        
        // Update empty label
        emptyLabel.setVisible(programList.isEmpty());
    }
    
    private void loadStatistik() {
        int total = programDAO.getAllPrograms().size();
        statTotalLabel.setText(String.valueOf(total));
        
        long totalDonasi = programDAO.getAllPrograms().stream()
            .mapToLong(Program::getDonasiTerkumpul)
            .sum();
        
        int totalDonatur = programDAO.getAllPrograms().stream()
            .mapToInt(Program::getJumlahDonatur)
            .sum();
        
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        statDonasiLabel.setText(formatter.format(totalDonasi));
        statDonaturLabel.setText(String.valueOf(totalDonatur));
    }
    
    @FXML
    private void handleTambahProgram(ActionEvent event) {
        programEdit = null; // Reset edit mode
        openProgramFormModal();
    }
    
    @FXML
    private void handleEditProgram(Program program) {
        programEdit = program;
        openProgramFormModal();
    }
    
    private void openProgramFormModal() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = new File("src/main/java/view/ProgramFormModal.fxml").toURI().toURL();
            loader.setLocation(url);
            loader.setController(this); // Set controller yang sama
            Parent root = loader.load();
            
            // Setup form fields
            setupFormFields(root);
            
            Stage modalStage = new Stage();
            modalStage.setTitle(programEdit == null ? "Tambah Program Kerja" : "Edit Program Kerja");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            
            if (programTable.getScene() != null) {
                modalStage.initOwner(programTable.getScene().getWindow());
            }
            
            Scene scene = new Scene(root);
            modalStage.setScene(scene);
            modalStage.showAndWait();
            
            // Refresh data setelah modal ditutup
            loadProgramData();
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
        TextArea deskripsiField = (TextArea) root.lookup("#deskripsiField");
        TextField tempatField = (TextField) root.lookup("#tempatField");
        ComboBox<String> kategoriComboBox = (ComboBox<String>) root.lookup("#kategoriComboBox");
        TextField coverField = (TextField) root.lookup("#coverField");
        DatePicker tanggalPelaksanaanField = (DatePicker) root.lookup("#tanggalPelaksanaanField");
        TextField targetDonasiField = (TextField) root.lookup("#targetDonasiField");
        
        // Setup kategori combo box
        if (kategoriComboBox != null) {
            kategoriComboBox.getItems().clear();
            kategoriComboBox.getItems().addAll("Pendidikan", "Pengembangan Skill", "Pendampingan");
        }
        
        if (programEdit == null) {
            // Mode tambah
            if (formTitleLabel != null) formTitleLabel.setText("Tambah Program Kerja");
            if (namaField != null) namaField.clear();
            if (deskripsiField != null) deskripsiField.clear();
            if (tempatField != null) tempatField.clear();
            if (kategoriComboBox != null) kategoriComboBox.setValue(null);
            if (coverField != null) coverField.clear();
            if (tanggalPelaksanaanField != null) tanggalPelaksanaanField.setValue(null);
            if (targetDonasiField != null) targetDonasiField.clear();
        } else {
            // Mode edit
            if (formTitleLabel != null) formTitleLabel.setText("Edit Program Kerja");
            if (namaField != null) namaField.setText(programEdit.getNama());
            if (deskripsiField != null) deskripsiField.setText(programEdit.getDeskripsi());
            if (tempatField != null) tempatField.setText(programEdit.getTempat());
            if (kategoriComboBox != null) kategoriComboBox.setValue(programEdit.getKategori());
            if (coverField != null) coverField.setText(programEdit.getCover());
            if (tanggalPelaksanaanField != null) tanggalPelaksanaanField.setValue(programEdit.getTanggalPelaksanaan());
            if (targetDonasiField != null) targetDonasiField.setText(String.valueOf(programEdit.getTargetDonasi()));
        }
    }
    
    @FXML
    private void handleSimpanProgram(ActionEvent event) {
        // Get form fields dari modal
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent root = stage.getScene().getRoot();
        
        TextField namaField = (TextField) root.lookup("#namaField");
        TextArea deskripsiField = (TextArea) root.lookup("#deskripsiField");
        TextField tempatField = (TextField) root.lookup("#tempatField");
        ComboBox<String> kategoriComboBox = (ComboBox<String>) root.lookup("#kategoriComboBox");
        TextField coverField = (TextField) root.lookup("#coverField");
        DatePicker tanggalPelaksanaanField = (DatePicker) root.lookup("#tanggalPelaksanaanField");
        TextField targetDonasiField = (TextField) root.lookup("#targetDonasiField");
        
        // Validasi
        if (namaField == null || namaField.getText().trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Nama program tidak boleh kosong!");
            return;
        }
        
        if (deskripsiField == null || deskripsiField.getText().trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Deskripsi tidak boleh kosong!");
            return;
        }
        
        if (tempatField == null || tempatField.getText().trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Tempat tidak boleh kosong!");
            return;
        }
        
        if (kategoriComboBox == null || kategoriComboBox.getValue() == null) {
            showAlert(AlertType.WARNING, "Validasi", "Kategori tidak boleh kosong!");
            return;
        }
        
        if (coverField == null || coverField.getText().trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi", "Cover (nama file gambar) tidak boleh kosong!");
            return;
        }
        
        // Parse target donasi
        long targetDonasi = 0;
        if (targetDonasiField != null && !targetDonasiField.getText().trim().isEmpty()) {
            try {
                targetDonasi = Long.parseLong(targetDonasiField.getText().trim());
            } catch (NumberFormatException e) {
                showAlert(AlertType.WARNING, "Validasi", "Target donasi harus berupa angka!");
                return;
            }
        }
        
        // Buat object Program
        Program program;
        if (programEdit == null) {
            program = new Program();
            // Set id_user dari session (admin)
            if (SessionManager.getCurrentUser() != null) {
                program.setIdUser(SessionManager.getCurrentUser().getId());
            }
        } else {
            program = programEdit;
        }
        
        program.setNama(namaField.getText().trim());
        program.setDeskripsi(deskripsiField.getText().trim());
        program.setTempat(tempatField.getText().trim());
        program.setKategori(kategoriComboBox.getValue());
        program.setCover(coverField.getText().trim());
        if (tanggalPelaksanaanField != null) {
            program.setTanggalPelaksanaan(tanggalPelaksanaanField.getValue());
        }
        program.setTargetDonasi(targetDonasi);
        
        // Simpan ke database
        boolean success;
        if (programEdit == null) {
            success = programDAO.save(program);
        } else {
            success = programDAO.update(program);
        }
        
        if (success) {
            showAlert(AlertType.INFORMATION, "Berhasil", 
                programEdit == null ? "Program kerja berhasil ditambahkan!" : "Program kerja berhasil diupdate!");
            stage.close();
        } else {
            showAlert(AlertType.ERROR, "Error", "Gagal menyimpan program kerja!");
        }
    }
    
    @FXML
    private void handleBatalForm(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void handleHapusProgram(Program program) {
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Hapus");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Apakah Anda yakin ingin menghapus program " + program.getNama() + "?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (programDAO.delete(program.getId())) {
                    showAlert(AlertType.INFORMATION, "Berhasil", "Program kerja berhasil dihapus!");
                    loadProgramData();
                    loadStatistik();
                } else {
                    showAlert(AlertType.ERROR, "Error", "Gagal menghapus program kerja!");
                }
            }
        });
    }
    
    @FXML
    private void handleDetailProgram(Program program) {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = new File("src/main/java/view/ProgramDetailAdminModal.fxml").toURI().toURL();
            loader.setLocation(url);
            Parent root = loader.load();
            
            ProgramDetailAdminModalController controller = loader.getController();
            controller.setProgram(program);
            
            Stage modalStage = new Stage();
            modalStage.setTitle("Detail Program - " + program.getNama());
            modalStage.initModality(Modality.APPLICATION_MODAL);
            
            if (programTable.getScene() != null) {
                modalStage.initOwner(programTable.getScene().getWindow());
            }
            
            Scene scene = new Scene(root);
            modalStage.setScene(scene);
            modalStage.showAndWait();
            
            // Refresh data setelah modal ditutup
            loadProgramData();
            loadStatistik();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "Tidak dapat membuka detail program: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleBuatLaporan(Program program) {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = new File("src/main/java/view/LaporanProgramFormModal.fxml").toURI().toURL();
            loader.setLocation(url);
            Parent root = loader.load();
            
            // Set controller dan program ID
            LaporanProgramFormModalController controller = loader.getController();
            controller.setProgram(program);
            
            Stage modalStage = new Stage();
            modalStage.setTitle("Buat Laporan Program - " + program.getNama());
            modalStage.initModality(Modality.APPLICATION_MODAL);
            
            if (programTable.getScene() != null) {
                modalStage.initOwner(programTable.getScene().getWindow());
            }
            
            Scene scene = new Scene(root);
            modalStage.setScene(scene);
            modalStage.showAndWait();
            
            // Refresh data setelah modal ditutup
            loadProgramData();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "Tidak dapat membuka form laporan: " + e.getMessage());
            e.printStackTrace();
        }
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
    private void handleAnakMenu(ActionEvent event) {
        navigateToPage("src/main/java/view/AnakAdminView.fxml", event);
    }
    
    @FXML
    private void handleRelawanMenu(ActionEvent event) {
        navigateToPage("src/main/java/view/ListRelawan.fxml", event);
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

