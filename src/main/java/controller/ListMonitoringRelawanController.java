package controller;

import dao.AnakDAO;
import dao.LaporanMonitoringDAO;
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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Anak;
import model.LaporanMonitoring;
import util.SessionManager;

/**
 * Controller untuk halaman List Monitoring Relawan
 */
public class ListMonitoringRelawanController implements Initializable {
    
    @FXML
    private TableView<LaporanMonitoring> monitoringTable;
    
    @FXML
    private TableColumn<LaporanMonitoring, Integer> colId;
    
    @FXML
    private TableColumn<LaporanMonitoring, String> colNama;
    
    @FXML
    private TableColumn<LaporanMonitoring, String> colNamaAnak;
    
    @FXML
    private TableColumn<LaporanMonitoring, String> colStatus;
    
    @FXML
    private TableColumn<LaporanMonitoring, String> colTanggalMonitoring;
    
    @FXML
    private TableColumn<LaporanMonitoring, String> colProgressPendidikan;
    
    @FXML
    private TableColumn<LaporanMonitoring, String> colAksi;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private Label emptyLabel;
    
    @FXML
    private Button btnTambahMonitoring;
    
    @FXML
    private Button btnRefresh;
    
    private LaporanMonitoringDAO laporanDAO;
    private AnakDAO anakDAO;
    private ObservableList<LaporanMonitoring> monitoringList;
    private List<LaporanMonitoring> allMonitoringData;
    private int currentRelawanId;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        laporanDAO = new LaporanMonitoringDAO();
        anakDAO = new AnakDAO();
        monitoringList = FXCollections.observableArrayList();
        
        // Ambil ID relawan dari session
        if (SessionManager.getCurrentUser() != null) {
            currentRelawanId = SessionManager.getCurrentUser().getId();
        }
        
        // Setup table columns
        setupTableColumns();
        
        // Load data
        loadMonitoringData();
    }
    
    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        // Nama Laporan
        colNama.setCellValueFactory(cellData -> {
            LaporanMonitoring laporan = cellData.getValue();
            String nama = laporan.getNama();
            if (nama != null && nama.length() > 30) {
                nama = nama.substring(0, 30) + "...";
            }
            return new javafx.beans.property.SimpleStringProperty(nama != null ? nama : "-");
        });
        
        // Nama Anak - ambil dari join atau dari anakDAO
        colNamaAnak.setCellValueFactory(cellData -> {
            LaporanMonitoring laporan = cellData.getValue();
            Anak anak = anakDAO.getAnakById(laporan.getIdAnak());
            return new javafx.beans.property.SimpleStringProperty(
                anak != null ? anak.getNama() : "-"
            );
        });
        
        // Status
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(column -> new TableCell<LaporanMonitoring, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    // Style berdasarkan status
                    switch (status) {
                        case "Disetujui":
                            setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                            break;
                        case "Dikembalikan":
                            setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
                            break;
                        case "Diajukan":
                            setStyle("-fx-text-fill: #2196F3; -fx-font-weight: bold;");
                            break;
                        case "Draft":
                            setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
        
        // Tanggal Monitoring - format tanggal
        colTanggalMonitoring.setCellValueFactory(cellData -> {
            LaporanMonitoring laporan = cellData.getValue();
            if (laporan.getTanggalMonitoring() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    laporan.getTanggalMonitoring().format(DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("id")))
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        
        // Progress Pendidikan
        colProgressPendidikan.setCellValueFactory(cellData -> {
            LaporanMonitoring laporan = cellData.getValue();
            String progress = laporan.getProgressPendidikan();
            if (progress != null && progress.length() > 40) {
                progress = progress.substring(0, 40) + "...";
            }
            return new javafx.beans.property.SimpleStringProperty(progress != null ? progress : "-");
        });
        
        // Setup column aksi dengan tombol Detail, Edit (conditional), dan Hapus
        colAksi.setCellFactory(new Callback<TableColumn<LaporanMonitoring, String>, TableCell<LaporanMonitoring, String>>() {
            @Override
            public TableCell<LaporanMonitoring, String> call(TableColumn<LaporanMonitoring, String> param) {
                return new TableCell<LaporanMonitoring, String>() {
                    private final Button btnDetail = new Button("Detail");
                    private final Button btnEdit = new Button("Edit");
                    private final Button btnDiajukan = new Button("Diajukan");
                    private final HBox hbox = new HBox(5);
                    
                    {
                        btnDetail.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 11px;");
                        btnEdit.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 11px;");
                        btnDiajukan.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 11px;");
                        hbox.setPadding(new Insets(3));
                        hbox.setSpacing(5);
                        
                        btnDetail.setOnAction(e -> {
                            LaporanMonitoring laporan = getTableView().getItems().get(getIndex());
                            handleDetailLaporan(laporan);
                        });
                        
                        btnEdit.setOnAction(e -> {
                            LaporanMonitoring laporan = getTableView().getItems().get(getIndex());
                            handleEditLaporan(laporan);
                        });
                        
                        btnDiajukan.setOnAction(e -> {
                            LaporanMonitoring laporan = getTableView().getItems().get(getIndex());
                            handleDiajukanLaporan(laporan);
                        });
                    }
                    
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            LaporanMonitoring laporan = getTableView().getItems().get(getIndex());
                            String status = laporan.getStatus();
                            
                            hbox.getChildren().clear();
                            
                            // Jika status Dikembalikan, tidak tampilkan tombol Detail (hanya Edit)
                            if (status != null && status.equals("Dikembalikan")) {
                                // Tidak ada tombol Detail untuk status Dikembalikan, hanya Edit
                                hbox.getChildren().add(btnEdit);
                            } else {
                                // Untuk status lainnya, tampilkan tombol Detail
                                hbox.getChildren().add(btnDetail);
                                
                                // Hanya tampilkan tombol Edit jika status Draft
                                if (status != null && status.equals("Draft")) {
                                    hbox.getChildren().add(btnEdit);
                                }
                            }
                            
                            // Tampilkan tombol Diajukan jika status Draft atau Dikembalikan
                            if (status != null && (status.equals("Draft") || status.equals("Dikembalikan"))) {
                                hbox.getChildren().add(btnDiajukan);
                            }
                            
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
        
        monitoringTable.setItems(monitoringList);
    }
    
    private void loadMonitoringData() {
        // Ambil semua laporan monitoring untuk relawan ini
        allMonitoringData = laporanDAO.getLaporanByUser(currentRelawanId);
        
        // Filter data
        filterAndDisplayData();
    }
    
    private void filterAndDisplayData() {
        monitoringList.clear();
        
        String keyword = searchField != null ? searchField.getText() : "";
        String lowerKeyword = keyword.toLowerCase().trim();
        
        if (allMonitoringData == null) {
            return;
        }
        
        if (lowerKeyword.isEmpty()) {
            monitoringList.addAll(allMonitoringData);
        } else {
            for (LaporanMonitoring laporan : allMonitoringData) {
                Anak anak = anakDAO.getAnakById(laporan.getIdAnak());
                String namaAnak = anak != null ? anak.getNama().toLowerCase() : "";
                String namaLaporan = laporan.getNama() != null ? laporan.getNama().toLowerCase() : "";
                String status = laporan.getStatus() != null ? laporan.getStatus().toLowerCase() : "";
                
                if (namaAnak.contains(lowerKeyword) ||
                    namaLaporan.contains(lowerKeyword) ||
                    status.contains(lowerKeyword) ||
                    (laporan.getProgressPendidikan() != null && laporan.getProgressPendidikan().toLowerCase().contains(lowerKeyword)) ||
                    (laporan.getKondisiKesehatan() != null && laporan.getKondisiKesehatan().toLowerCase().contains(lowerKeyword)) ||
                    (laporan.getCatatan() != null && laporan.getCatatan().toLowerCase().contains(lowerKeyword))) {
                    monitoringList.add(laporan);
                }
            }
        }
        
        // Update empty label
        if (emptyLabel != null) {
            emptyLabel.setVisible(monitoringList.isEmpty());
        }
    }
    
    @FXML
    private void handleSearch() {
        filterAndDisplayData();
    }
    
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadMonitoringData();
        if (searchField != null) {
            searchField.clear();
        }
    }
    
    @FXML
    private void handleTambahMonitoring(ActionEvent event) {
        navigateToPage("src/main/java/view/LaporanView.fxml", event);
    }
    
    private void handleDetailLaporan(LaporanMonitoring laporan) {
        // Buat dialog custom untuk menampilkan detail dengan foto
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Detail Laporan Monitoring");
        dialogStage.setResizable(true);
        
        VBox dialogVBox = new VBox(15);
        dialogVBox.setPadding(new Insets(20));
        dialogVBox.setStyle("-fx-background-color: white;");
        dialogVBox.setPrefWidth(700);
        
        Anak anak = anakDAO.getAnakById(laporan.getIdAnak());
        String namaAnak = anak != null ? anak.getNama() : "-";
        
        // Header
        Label titleLabel = new Label("Detail Laporan Monitoring");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #020E4C;");
        
        // Informasi dasar
        VBox infoBox = new VBox(10);
        infoBox.setPadding(new Insets(10));
        infoBox.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10; -fx-padding: 15;");
        
        Label idLabel = new Label("ID Laporan: " + laporan.getId());
        idLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #333;");
        
        Label namaLabel = new Label("Nama Laporan: " + (laporan.getNama() != null ? laporan.getNama() : "-"));
        namaLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #333;");
        
        Label statusLabel = new Label("Status: " + (laporan.getStatus() != null ? laporan.getStatus() : "Draft"));
        // Set warna berdasarkan status
        if (laporan.getStatus() != null) {
            switch (laporan.getStatus()) {
                case "Disetujui":
                    statusLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                    break;
                case "Dikembalikan":
                    statusLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #f44336; -fx-font-weight: bold;");
                    break;
                case "Diajukan":
                    statusLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #2196F3; -fx-font-weight: bold;");
                    break;
                case "Draft":
                    statusLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #FF9800; -fx-font-weight: bold;");
                    break;
                default:
                    statusLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #333; -fx-font-weight: bold;");
            }
        } else {
            statusLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #333; -fx-font-weight: bold;");
        }
        
        Label namaAnakLabel = new Label("Nama Anak: " + namaAnak);
        namaAnakLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #333;");
        
        Label tanggalLabel = new Label("Tanggal Monitoring: " + 
            (laporan.getTanggalMonitoring() != null ? 
             laporan.getTanggalMonitoring().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id"))) : "-"));
        tanggalLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #333;");
        
        infoBox.getChildren().addAll(idLabel, namaLabel, statusLabel, namaAnakLabel, tanggalLabel);
        
        // Foto
        VBox fotoBox = new VBox(10);
        fotoBox.setPadding(new Insets(10));
        fotoBox.setAlignment(Pos.CENTER);
        
        Label fotoTitleLabel = new Label("Foto Monitoring");
        fotoTitleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #020E4C;");
        fotoBox.getChildren().add(fotoTitleLabel);
        
        boolean fotoLoaded = false;
        // Load foto jika ada
        if (laporan.getFoto() != null && !laporan.getFoto().isEmpty()) {
            try {
                File file = new File("src/main/java/assets/laporan_monitoring/" + laporan.getFoto());
                if (!file.exists()) {
                    String projectRoot = System.getProperty("user.dir");
                    file = new File(projectRoot, "src/main/java/assets/laporan_monitoring/" + laporan.getFoto());
                }
                
                if (file.exists()) {
                    ImageView fotoImageView = new ImageView();
                    Image image = new Image(file.toURI().toString());
                    fotoImageView.setImage(image);
                    fotoImageView.setFitWidth(400);
                    fotoImageView.setFitHeight(300);
                    fotoImageView.setPreserveRatio(true);
                    fotoImageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);");
                    fotoBox.getChildren().add(fotoImageView);
                    fotoLoaded = true;
                }
            } catch (Exception e) {
                System.err.println("Error loading foto: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        if (!fotoLoaded) {
            Label noFotoLabel = new Label("Tidak ada foto");
            noFotoLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #999;");
            fotoBox.getChildren().add(noFotoLabel);
        }
        
        // Progress Pendidikan
        VBox progressBox = new VBox(5);
        progressBox.setPadding(new Insets(10));
        progressBox.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10; -fx-padding: 15;");
        
        Label progressTitleLabel = new Label("Progress Pendidikan:");
        progressTitleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #020E4C;");
        
        Label progressContentLabel = new Label(laporan.getProgressPendidikan() != null && !laporan.getProgressPendidikan().isEmpty() ? 
            laporan.getProgressPendidikan() : "-");
        progressContentLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #333;");
        progressContentLabel.setWrapText(true);
        
        progressBox.getChildren().addAll(progressTitleLabel, progressContentLabel);
        
        // Kondisi Kesehatan
        VBox kesehatanBox = new VBox(5);
        kesehatanBox.setPadding(new Insets(10));
        kesehatanBox.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10; -fx-padding: 15;");
        
        Label kesehatanTitleLabel = new Label("Kondisi Kesehatan:");
        kesehatanTitleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #020E4C;");
        
        Label kesehatanContentLabel = new Label(laporan.getKondisiKesehatan() != null && !laporan.getKondisiKesehatan().isEmpty() ? 
            laporan.getKondisiKesehatan() : "-");
        kesehatanContentLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #333;");
        kesehatanContentLabel.setWrapText(true);
        
        kesehatanBox.getChildren().addAll(kesehatanTitleLabel, kesehatanContentLabel);
        
        // Catatan
        VBox catatanBox = new VBox(5);
        catatanBox.setPadding(new Insets(10));
        catatanBox.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10; -fx-padding: 15;");
        
        Label catatanTitleLabel = new Label("Catatan:");
        catatanTitleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #020E4C;");
        
        Label catatanContentLabel = new Label(laporan.getCatatan() != null && !laporan.getCatatan().isEmpty() ? 
            laporan.getCatatan() : "-");
        catatanContentLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #333;");
        catatanContentLabel.setWrapText(true);
        
        catatanBox.getChildren().addAll(catatanTitleLabel, catatanContentLabel);
        
        // Catatan Revisi (jika status Dikembalikan)
        if (laporan.getStatus() != null && laporan.getStatus().equals("Dikembalikan") 
            && laporan.getCatatanRevisi() != null && !laporan.getCatatanRevisi().isEmpty()) {
            VBox catatanRevisiBox = new VBox(5);
            catatanRevisiBox.setPadding(new Insets(10));
            catatanRevisiBox.setStyle("-fx-background-color: #FFEBEE; -fx-background-radius: 10; -fx-padding: 15; -fx-border-color: #f44336; -fx-border-width: 2; -fx-border-radius: 10;");
            
            Label catatanRevisiTitleLabel = new Label("Catatan Revisi dari Admin:");
            catatanRevisiTitleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #f44336;");
            
            Label catatanRevisiContentLabel = new Label(laporan.getCatatanRevisi());
            catatanRevisiContentLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #333;");
            catatanRevisiContentLabel.setWrapText(true);
            
            catatanRevisiBox.getChildren().addAll(catatanRevisiTitleLabel, catatanRevisiContentLabel);
            
            dialogVBox.getChildren().add(dialogVBox.getChildren().size() - 1, catatanRevisiBox);
        }
        
        // Tombol Tutup
        Button closeButton = new Button("Tutup");
        closeButton.setStyle("-fx-background-color: #020E4C; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 10 30; -fx-font-size: 14; -fx-font-weight: bold;");
        closeButton.setOnAction(e -> dialogStage.close());
        
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(closeButton);
        
        dialogVBox.getChildren().addAll(
            titleLabel,
            infoBox,
            fotoBox,
            progressBox,
            kesehatanBox,
            catatanBox,
            buttonBox
        );
        
        Scene dialogScene = new Scene(dialogVBox);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }
    
    private void handleEditLaporan(LaporanMonitoring laporan) {
        // Cek status - hanya bisa edit jika Draft atau Dikembalikan
        String status = laporan.getStatus();
        if (status != null && !status.equals("Draft") && !status.equals("Dikembalikan")) {
            showAlert(AlertType.WARNING, "Tidak Dapat Edit", 
                "Laporan dengan status \"" + status + "\" tidak dapat diedit!");
            return;
        }
        
        // Buka form edit dengan data laporan yang dipilih
        openEditForm(laporan);
    }
    
    private void openEditForm(LaporanMonitoring laporan) {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = new File("src/main/java/view/LaporanView.fxml").toURI().toURL();
            loader.setLocation(url);
            Parent root = loader.load();
            
            // Get controller dan set data untuk edit
            LaporanController controller = loader.getController();
            if (controller != null) {
                controller.setLaporanForEdit(laporan);
            } else {
                showAlert(AlertType.ERROR, "Error", "Controller tidak ditemukan!");
                return;
            }
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) monitoringTable.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "Tidak dapat membuka form edit: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleDiajukanLaporan(LaporanMonitoring laporan) {
        // Validasi status harus Draft atau Dikembalikan
        if (laporan.getStatus() == null || (!laporan.getStatus().equals("Draft") && !laporan.getStatus().equals("Dikembalikan"))) {
            showAlert(AlertType.WARNING, "Tidak Dapat Diajukan", 
                "Hanya laporan dengan status Draft atau Dikembalikan yang dapat diajukan!");
            return;
        }
        
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Pengajuan");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Apakah Anda yakin ingin mengajukan laporan ini? Laporan yang diajukan tidak dapat diedit lagi.");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                if (laporanDAO.updateStatus(laporan.getId(), "Diajukan", null)) {
                    showAlert(AlertType.INFORMATION, "Berhasil", "Laporan berhasil diajukan!");
                    loadMonitoringData();
                } else {
                    showAlert(AlertType.ERROR, "Error", "Gagal mengajukan laporan!");
                }
            }
        });
    }
    
    private void handleHapusLaporan(LaporanMonitoring laporan) {
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Hapus");
        confirmAlert.setHeaderText(null);
        Anak anak = anakDAO.getAnakById(laporan.getIdAnak());
        String namaAnak = anak != null ? anak.getNama() : "-";
        confirmAlert.setContentText("Apakah Anda yakin ingin menghapus laporan monitoring untuk " + namaAnak + "?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                if (laporanDAO.delete(laporan.getId())) {
                    showAlert(AlertType.INFORMATION, "Berhasil", "Laporan monitoring berhasil dihapus!");
                    loadMonitoringData();
                } else {
                    showAlert(AlertType.ERROR, "Error", "Gagal menghapus laporan monitoring!");
                }
            }
        });
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
