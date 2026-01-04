package controller;

import dao.AnakDAO;
import dao.LaporanMonitoringDAO;
import dao.UserDAO;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
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
import model.LaporanMonitoring;
import model.User;
import util.SessionManager;

/**
 * Controller untuk halaman List Monitoring Admin
 */
public class ListMonitoringAdminController implements Initializable {
    
    @FXML
    private TableView<LaporanMonitoring> monitoringTable;
    
    @FXML
    private TableColumn<LaporanMonitoring, Integer> colId;
    
    @FXML
    private TableColumn<LaporanMonitoring, String> colNama;
    
    @FXML
    private TableColumn<LaporanMonitoring, String> colNamaAnak;
    
    @FXML
    private TableColumn<LaporanMonitoring, String> colRelawan;
    
    @FXML
    private TableColumn<LaporanMonitoring, String> colStatus;
    
    @FXML
    private TableColumn<LaporanMonitoring, String> colTanggalMonitoring;
    
    @FXML
    private TableColumn<LaporanMonitoring, String> colAksi;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private ComboBox<String> statusFilterComboBox;
    
    @FXML
    private Label emptyLabel;
    
    @FXML
    private Button btnRefresh;
    
    private LaporanMonitoringDAO laporanDAO;
    private AnakDAO anakDAO;
    private UserDAO userDAO;
    private ObservableList<LaporanMonitoring> monitoringList;
    private List<LaporanMonitoring> allMonitoringData;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        laporanDAO = new LaporanMonitoringDAO();
        anakDAO = new AnakDAO();
        userDAO = new UserDAO();
        monitoringList = FXCollections.observableArrayList();
        
        // Setup status filter
        statusFilterComboBox.getItems().addAll("Semua", "Draft", "Diajukan", "Disetujui", "Dikembalikan");
        statusFilterComboBox.setValue("Semua");
        statusFilterComboBox.setOnAction(e -> filterAndDisplayData());
        
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
        
        // Nama Anak
        colNamaAnak.setCellValueFactory(cellData -> {
            LaporanMonitoring laporan = cellData.getValue();
            Anak anak = anakDAO.getAnakById(laporan.getIdAnak());
            return new javafx.beans.property.SimpleStringProperty(
                anak != null ? anak.getNama() : "-"
            );
        });
        
        // Nama Relawan
        colRelawan.setCellValueFactory(cellData -> {
            LaporanMonitoring laporan = cellData.getValue();
            User user = userDAO.getUserById(laporan.getIdUser());
            return new javafx.beans.property.SimpleStringProperty(
                user != null ? user.getNama() : "-"
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
        
        // Tanggal Monitoring
        colTanggalMonitoring.setCellValueFactory(cellData -> {
            LaporanMonitoring laporan = cellData.getValue();
            if (laporan.getTanggalMonitoring() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    laporan.getTanggalMonitoring().format(DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("id")))
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        
        // Setup column aksi dengan tombol Detail, Approve, Reject
        colAksi.setCellFactory(new Callback<TableColumn<LaporanMonitoring, String>, TableCell<LaporanMonitoring, String>>() {
            @Override
            public TableCell<LaporanMonitoring, String> call(TableColumn<LaporanMonitoring, String> param) {
                return new TableCell<LaporanMonitoring, String>() {
                    private final Button btnDetail = new Button("Detail");
                    private final Button btnApprove = new Button("Approve");
                    private final Button btnReject = new Button("Reject");
                    private final HBox hbox = new HBox(5);
                    
                    {
                        btnDetail.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 11px;");
                        btnApprove.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 11px;");
                        btnReject.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 11px;");
                        hbox.setPadding(new Insets(3));
                        hbox.setSpacing(5);
                        
                        btnDetail.setOnAction(e -> {
                            LaporanMonitoring laporan = getTableView().getItems().get(getIndex());
                            handleDetailLaporan(laporan);
                        });
                        
                        btnApprove.setOnAction(e -> {
                            LaporanMonitoring laporan = getTableView().getItems().get(getIndex());
                            handleApproveLaporan(laporan);
                        });
                        
                        btnReject.setOnAction(e -> {
                            LaporanMonitoring laporan = getTableView().getItems().get(getIndex());
                            handleRejectLaporan(laporan);
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
                            hbox.getChildren().add(btnDetail);
                            
                            // Hanya tampilkan tombol Approve/Reject jika status Draft atau Diajukan
                            if (status != null && (status.equals("Draft") || status.equals("Diajukan"))) {
                                hbox.getChildren().add(btnApprove);
                                hbox.getChildren().add(btnReject);
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
        allMonitoringData = laporanDAO.getAllLaporan();
        filterAndDisplayData();
    }
    
    private void filterAndDisplayData() {
        monitoringList.clear();
        
        String keyword = searchField != null ? searchField.getText() : "";
        String lowerKeyword = keyword.toLowerCase().trim();
        String statusFilter = statusFilterComboBox != null && statusFilterComboBox.getValue() != null 
            ? statusFilterComboBox.getValue() 
            : "Semua";
        
        if (allMonitoringData == null) {
            return;
        }
        
        for (LaporanMonitoring laporan : allMonitoringData) {
            // Filter status
            if (!statusFilter.equals("Semua")) {
                if (!statusFilter.equals(laporan.getStatus())) {
                    continue;
                }
            }
            
            // Filter keyword
            if (!lowerKeyword.isEmpty()) {
                Anak anak = anakDAO.getAnakById(laporan.getIdAnak());
                User user = userDAO.getUserById(laporan.getIdUser());
                String namaAnak = anak != null ? anak.getNama().toLowerCase() : "";
                String namaRelawan = user != null ? user.getNama().toLowerCase() : "";
                String namaLaporan = laporan.getNama() != null ? laporan.getNama().toLowerCase() : "";
                
                if (!namaAnak.contains(lowerKeyword) && 
                    !namaRelawan.contains(lowerKeyword) &&
                    !namaLaporan.contains(lowerKeyword)) {
                    continue;
                }
            }
            
            monitoringList.add(laporan);
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
        if (statusFilterComboBox != null) {
            statusFilterComboBox.setValue("Semua");
        }
    }
    
    private void handleDetailLaporan(LaporanMonitoring laporan) {
        // Reuse detail dialog from ListMonitoringRelawanController
        // Untuk sekarang, buat dialog sederhana
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Detail Laporan Monitoring");
        alert.setHeaderText(null);
        
        Anak anak = anakDAO.getAnakById(laporan.getIdAnak());
        User user = userDAO.getUserById(laporan.getIdUser());
        
        String content = "ID: " + laporan.getId() + "\n" +
                        "Nama Laporan: " + (laporan.getNama() != null ? laporan.getNama() : "-") + "\n" +
                        "Status: " + (laporan.getStatus() != null ? laporan.getStatus() : "Draft") + "\n" +
                        "Nama Anak: " + (anak != null ? anak.getNama() : "-") + "\n" +
                        "Relawan: " + (user != null ? user.getNama() : "-") + "\n" +
                        "Tanggal: " + (laporan.getTanggalMonitoring() != null ? 
                            laporan.getTanggalMonitoring().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id"))) : "-") + "\n" +
                        "Progress Pendidikan: " + (laporan.getProgressPendidikan() != null ? laporan.getProgressPendidikan() : "-") + "\n" +
                        "Kondisi Kesehatan: " + (laporan.getKondisiKesehatan() != null ? laporan.getKondisiKesehatan() : "-") + "\n" +
                        "Catatan: " + (laporan.getCatatan() != null ? laporan.getCatatan() : "-");
        
        if (laporan.getCatatanRevisi() != null && !laporan.getCatatanRevisi().isEmpty()) {
            content += "\n\nCatatan Revisi:\n" + laporan.getCatatanRevisi();
        }
        
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void handleApproveLaporan(LaporanMonitoring laporan) {
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi Approve");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Apakah Anda yakin ingin menyetujui laporan ini?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (laporanDAO.updateStatus(laporan.getId(), "Disetujui", null)) {
                    showAlert(AlertType.INFORMATION, "Berhasil", "Laporan berhasil disetujui!");
                    loadMonitoringData();
                } else {
                    showAlert(AlertType.ERROR, "Error", "Gagal menyetujui laporan!");
                }
            }
        });
    }
    
    private void handleRejectLaporan(LaporanMonitoring laporan) {
        // Buat dialog untuk input catatan revisi
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Tolak Laporan - Catatan Revisi");
        dialogStage.setResizable(false);
        
        VBox dialogVBox = new VBox(15);
        dialogVBox.setPadding(new Insets(20));
        dialogVBox.setStyle("-fx-background-color: white;");
        dialogVBox.setPrefWidth(500);
        
        Label titleLabel = new Label("Catatan Revisi");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #020E4C;");
        
        Label infoLabel = new Label("Berikan catatan revisi untuk laporan yang akan ditolak:");
        infoLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #333;");
        infoLabel.setWrapText(true);
        
        TextArea catatanRevisiField = new TextArea();
        catatanRevisiField.setPromptText("Masukkan catatan revisi...");
        catatanRevisiField.setPrefRowCount(8);
        catatanRevisiField.setWrapText(true);
        catatanRevisiField.setStyle("-fx-background-radius: 5;");
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        
        Button cancelButton = new Button("Batal");
        cancelButton.setStyle("-fx-background-color: #999; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 8 20;");
        cancelButton.setOnAction(e -> dialogStage.close());
        
        Button rejectButton = new Button("Tolak");
        rejectButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 8 20; -fx-font-weight: bold;");
        rejectButton.setOnAction(e -> {
            String catatanRevisi = catatanRevisiField.getText().trim();
            if (catatanRevisi.isEmpty()) {
                showAlert(AlertType.WARNING, "Validasi", "Catatan revisi harus diisi!");
                return;
            }
            
            if (laporanDAO.updateStatus(laporan.getId(), "Dikembalikan", catatanRevisi)) {
                showAlert(AlertType.INFORMATION, "Berhasil", "Laporan berhasil ditolak dan catatan revisi telah disimpan!");
                dialogStage.close();
                loadMonitoringData();
            } else {
                showAlert(AlertType.ERROR, "Error", "Gagal menolak laporan!");
            }
        });
        
        buttonBox.getChildren().addAll(cancelButton, rejectButton);
        
        dialogVBox.getChildren().addAll(titleLabel, infoLabel, catatanRevisiField, buttonBox);
        
        Scene dialogScene = new Scene(dialogVBox);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
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

