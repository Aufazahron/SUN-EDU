package controller;

import dao.LaporanProgramDAO;
import dao.ProgramDAO;
import java.io.File;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.LaporanProgram;
import model.Program;
import util.SessionManager;

/**
 * Controller untuk halaman Manajemen Laporan Program (Admin)
 */
public class LaporanProgramAdminController implements Initializable {
    
    @FXML
    private TableView<LaporanProgram> laporanTable;
    
    @FXML
    private TableColumn<LaporanProgram, Integer> colId;
    
    @FXML
    private TableColumn<LaporanProgram, String> colNamaProgram;
    
    @FXML
    private TableColumn<LaporanProgram, String> colTanggalPelaksanaan;
    
    @FXML
    private TableColumn<LaporanProgram, String> colLaporan;
    
    @FXML
    private TableColumn<LaporanProgram, String> colDokumentasi;
    
    @FXML
    private TableColumn<LaporanProgram, String> colAksi;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private Label emptyLabel;
    
    @FXML
    private Button btnRefresh;
    
    private LaporanProgramDAO laporanProgramDAO;
    private ProgramDAO programDAO;
    private ObservableList<LaporanProgram> laporanList;
    private List<LaporanProgram> allLaporanData;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        laporanProgramDAO = new LaporanProgramDAO();
        programDAO = new ProgramDAO();
        laporanList = FXCollections.observableArrayList();
        
        // Setup table columns
        setupTableColumns();
        
        // Load data
        loadLaporanData();
    }
    
    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        // Nama Program
        colNamaProgram.setCellValueFactory(cellData -> {
            LaporanProgram laporan = cellData.getValue();
            Program program = programDAO.getProgramById(laporan.getIdProgram());
            return new javafx.beans.property.SimpleStringProperty(
                program != null ? program.getNama() : "-"
            );
        });
        
        // Tanggal Pelaksanaan
        colTanggalPelaksanaan.setCellValueFactory(cellData -> {
            LaporanProgram laporan = cellData.getValue();
            if (laporan.getTanggalPelaksanaan() != null) {
                String formatted = laporan.getTanggalPelaksanaan()
                    .format(DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("id", "ID")));
                return new javafx.beans.property.SimpleStringProperty(formatted);
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });
        
        // Laporan (preview)
        colLaporan.setCellValueFactory(cellData -> {
            LaporanProgram laporan = cellData.getValue();
            String laporanText = laporan.getLaporan();
            if (laporanText != null && laporanText.length() > 50) {
                laporanText = laporanText.substring(0, 47) + "...";
            }
            return new javafx.beans.property.SimpleStringProperty(
                laporanText != null ? laporanText : "-"
            );
        });
        
        // Dokumentasi
        colDokumentasi.setCellValueFactory(cellData -> {
            LaporanProgram laporan = cellData.getValue();
            String dokumentasi = laporan.getDokumentasi();
            if (dokumentasi != null && !dokumentasi.isEmpty()) {
                return new javafx.beans.property.SimpleStringProperty("✓ Ada");
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });
        
        // Aksi
        colAksi.setCellFactory(new Callback<TableColumn<LaporanProgram, String>, TableCell<LaporanProgram, String>>() {
            @Override
            public TableCell<LaporanProgram, String> call(TableColumn<LaporanProgram, String> param) {
                return new TableCell<LaporanProgram, String>() {
                    private final Button btnDetail = new Button("Detail");
                    private final Button btnHapus = new Button("Hapus");
                    
                    {
                        btnDetail.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10;");
                        btnHapus.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10;");
                        
                        btnDetail.setOnAction(e -> {
                            LaporanProgram laporan = getTableView().getItems().get(getIndex());
                            handleDetailLaporan(laporan);
                        });
                        
                        btnHapus.setOnAction(e -> {
                            LaporanProgram laporan = getTableView().getItems().get(getIndex());
                            handleHapusLaporan(laporan);
                        });
                    }
                    
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(5);
                            hbox.getChildren().addAll(btnDetail, btnHapus);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
    }
    
    private void loadLaporanData() {
        allLaporanData = laporanProgramDAO.getAllLaporan();
        filterAndDisplayData();
    }
    
    private void filterAndDisplayData() {
        String keyword = searchField.getText().toLowerCase();
        
        List<LaporanProgram> filtered = allLaporanData.stream()
            .filter(laporan -> {
                Program program = programDAO.getProgramById(laporan.getIdProgram());
                if (program != null) {
                    String namaProgram = program.getNama().toLowerCase();
                    return namaProgram.contains(keyword);
                }
                return false;
            })
            .collect(java.util.stream.Collectors.toList());
        
        laporanList.clear();
        laporanList.addAll(filtered);
        laporanTable.setItems(laporanList);
        
        // Tampilkan empty label jika tidak ada data
        if (laporanList.isEmpty()) {
            emptyLabel.setVisible(true);
            laporanTable.setVisible(false);
        } else {
            emptyLabel.setVisible(false);
            laporanTable.setVisible(true);
        }
    }
    
    private void handleDetailLaporan(LaporanProgram laporan) {
        Program program = programDAO.getProgramById(laporan.getIdProgram());
        if (program == null) {
            showAlert(AlertType.ERROR, "Error", "Program tidak ditemukan");
            return;
        }
        
        Alert dialog = new Alert(AlertType.INFORMATION);
        dialog.setTitle("Detail Laporan Program - " + program.getNama());
        dialog.setHeaderText("Laporan Pelaksanaan Program");
        
        javafx.scene.layout.VBox contentBox = new javafx.scene.layout.VBox(15);
        contentBox.setPrefWidth(600);
        contentBox.setPadding(new javafx.geometry.Insets(20));
        
        Label programLabel = new Label("Program: " + program.getNama());
        programLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #020E4C;");
        
        Label tanggalLabel = new Label();
        if (laporan.getTanggalPelaksanaan() != null) {
            tanggalLabel.setText("Tanggal Pelaksanaan: " + 
                laporan.getTanggalPelaksanaan().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"))));
        } else {
            tanggalLabel.setText("Tanggal Pelaksanaan: Tidak tersedia");
        }
        tanggalLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #666;");
        
        Label laporanLabel = new Label("Laporan:");
        laporanLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        
        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane();
        Label laporanTextLabel = new Label(laporan.getLaporan() != null ? laporan.getLaporan() : "Tidak ada laporan");
        laporanTextLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #333;");
        laporanTextLabel.setWrapText(true);
        laporanTextLabel.setPrefWidth(550);
        scrollPane.setContent(laporanTextLabel);
        scrollPane.setPrefHeight(200);
        scrollPane.setFitToWidth(true);
        
        Label dokumentasiLabel = new Label();
        if (laporan.getDokumentasi() != null && !laporan.getDokumentasi().isEmpty()) {
            dokumentasiLabel.setText("📎 Dokumentasi: " + laporan.getDokumentasi());
            dokumentasiLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #1976D2;");
        } else {
            dokumentasiLabel.setText("Tidak ada dokumentasi");
            dokumentasiLabel.setStyle("-fx-font-size: 13; -fx-text-fill: #999;");
        }
        
        contentBox.getChildren().addAll(programLabel, tanggalLabel, laporanLabel, scrollPane, dokumentasiLabel);
        
        dialog.getDialogPane().setContent(contentBox);
        dialog.getDialogPane().setPrefWidth(650);
        dialog.showAndWait();
    }
    
    private void handleHapusLaporan(LaporanProgram laporan) {
        Program program = programDAO.getProgramById(laporan.getIdProgram());
        String programName = program != null ? program.getNama() : "Program";
        
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Hapus");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Apakah Anda yakin ingin menghapus laporan program \"" + programName + "\"?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (laporanProgramDAO.delete(laporan.getId())) {
                    showAlert(AlertType.INFORMATION, "Berhasil", "Laporan program berhasil dihapus!");
                    loadLaporanData();
                } else {
                    showAlert(AlertType.ERROR, "Gagal", "Gagal menghapus laporan program. Silakan coba lagi.");
                }
            }
        });
    }
    
    @FXML
    private void handleTambahLaporan(ActionEvent event) {
        // Langsung buka form modal dengan dropdown program di dalamnya
        openLaporanFormModal(null);
    }
    
    private void openLaporanFormModal(Program program) {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = new File("src/main/java/view/LaporanProgramFormModal.fxml").toURI().toURL();
            loader.setLocation(url);
            Parent root = loader.load();
            
            // Set controller
            LaporanProgramFormModalController formController = loader.getController();
            if (program != null) {
                formController.setProgram(program);
            }
            
            Stage modalStage = new Stage();
            modalStage.setTitle("Buat Laporan Program");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            
            if (laporanTable.getScene() != null) {
                modalStage.initOwner(laporanTable.getScene().getWindow());
            }
            
            Scene scene = new Scene(root);
            modalStage.setScene(scene);
            modalStage.showAndWait();
            
            // Refresh data setelah modal ditutup
            loadLaporanData();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "Tidak dapat membuka form laporan: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleSearch(javafx.scene.input.KeyEvent event) {
        filterAndDisplayData();
    }
    
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadLaporanData();
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
    private void handleProgramKerjaMenu(ActionEvent event) {
        navigateToPage("src/main/java/view/ProgramAdminView.fxml", event);
    }
    
    @FXML
    private void handleLaporanMenu(ActionEvent event) {
        navigateToPage("src/main/java/view/ListMonitoringAdmin.fxml", event);
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

