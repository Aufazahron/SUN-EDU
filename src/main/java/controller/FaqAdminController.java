package controller;

import dao.PesanDAO;
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
import javafx.scene.control.ScrollPane;
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
import model.Pesan;
import util.SessionManager;

/**
 * Controller untuk halaman FAQ Admin (Manajemen Pesan)
 */
public class FaqAdminController implements Initializable {
    
    @FXML
    private TableView<Pesan> pesanTable;
    
    @FXML
    private TableColumn<Pesan, Integer> colId;
    
    @FXML
    private TableColumn<Pesan, String> colNama;
    
    @FXML
    private TableColumn<Pesan, String> colEmail;
    
    @FXML
    private TableColumn<Pesan, String> colSubjek;
    
    @FXML
    private TableColumn<Pesan, String> colStatus;
    
    @FXML
    private TableColumn<Pesan, String> colTanggal;
    
    @FXML
    private TableColumn<Pesan, String> colAksi;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private ComboBox<String> statusFilterComboBox;
    
    @FXML
    private Label emptyLabel;
    
    @FXML
    private Label countBaruLabel;
    
    @FXML
    private Label countDibacaLabel;
    
    @FXML
    private Label countDijawabLabel;
    
    @FXML
    private Label countTotalLabel;
    
    @FXML
    private Button btnRefresh;
    
    private PesanDAO pesanDAO;
    private ObservableList<Pesan> pesanList;
    private List<Pesan> allPesanData;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pesanDAO = new PesanDAO();
        pesanList = FXCollections.observableArrayList();
        
        // Setup status filter
        statusFilterComboBox.getItems().addAll("Semua", "baru", "dibaca", "dijawab");
        statusFilterComboBox.setValue("Semua");
        statusFilterComboBox.setOnAction(e -> filterAndDisplayData());
        
        // Setup table columns
        setupTableColumns();
        
        // Load data
        loadPesanData();
    }
    
    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        // Nama
        colNama.setCellValueFactory(cellData -> {
            Pesan pesan = cellData.getValue();
            String nama = pesan.getNama();
            if (nama != null && nama.length() > 20) {
                nama = nama.substring(0, 20) + "...";
            }
            return new javafx.beans.property.SimpleStringProperty(nama != null ? nama : "-");
        });
        
        // Email
        colEmail.setCellValueFactory(cellData -> {
            Pesan pesan = cellData.getValue();
            String email = pesan.getEmail();
            if (email != null && email.length() > 25) {
                email = email.substring(0, 25) + "...";
            }
            return new javafx.beans.property.SimpleStringProperty(email != null ? email : "-");
        });
        
        // Subjek
        colSubjek.setCellValueFactory(cellData -> {
            Pesan pesan = cellData.getValue();
            String subjek = pesan.getSubjek();
            if (subjek != null && subjek.length() > 30) {
                subjek = subjek.substring(0, 30) + "...";
            }
            return new javafx.beans.property.SimpleStringProperty(subjek != null ? subjek : "-");
        });
        
        // Status
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(column -> new TableCell<Pesan, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    // Capitalize first letter
                    String statusText = status.substring(0, 1).toUpperCase() + status.substring(1);
                    setText(statusText);
                    switch (status) {
                        case "dijawab":
                            setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                            break;
                        case "dibaca":
                            setStyle("-fx-text-fill: #2196F3; -fx-font-weight: bold;");
                            break;
                        case "baru":
                            setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
        
        // Tanggal
        colTanggal.setCellValueFactory(cellData -> {
            Pesan pesan = cellData.getValue();
            if (pesan.getCreatedAt() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    pesan.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", new Locale("id")))
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        
        // Setup column aksi dengan tombol Detail, Tandai Dibaca, Tandai Dijawab
        colAksi.setCellFactory(new Callback<TableColumn<Pesan, String>, TableCell<Pesan, String>>() {
            @Override
            public TableCell<Pesan, String> call(TableColumn<Pesan, String> param) {
                return new TableCell<Pesan, String>() {
                    private final Button btnDetail = new Button("Detail");
                    private final Button btnDibaca = new Button("Dibaca");
                    private final Button btnDijawab = new Button("Dijawab");
                    private final HBox hbox = new HBox(5);
                    
                    {
                        btnDetail.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 11px;");
                        btnDibaca.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 11px;");
                        btnDijawab.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 5 10; -fx-font-size: 11px;");
                        hbox.setPadding(new Insets(3));
                        hbox.setSpacing(5);
                        
                        btnDetail.setOnAction(e -> {
                            Pesan pesan = getTableView().getItems().get(getIndex());
                            handleDetailPesan(pesan);
                        });
                        
                        btnDibaca.setOnAction(e -> {
                            Pesan pesan = getTableView().getItems().get(getIndex());
                            handleTandaiDibaca(pesan);
                        });
                        
                        btnDijawab.setOnAction(e -> {
                            Pesan pesan = getTableView().getItems().get(getIndex());
                            handleTandaiDijawab(pesan);
                        });
                    }
                    
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Pesan pesan = getTableView().getItems().get(getIndex());
                            String status = pesan.getStatus();
                            
                            hbox.getChildren().clear();
                            hbox.getChildren().add(btnDetail);
                            
                            // Tampilkan tombol sesuai status
                            if (status == null || status.equals("baru")) {
                                hbox.getChildren().add(btnDibaca);
                                hbox.getChildren().add(btnDijawab);
                            } else if (status.equals("dibaca")) {
                                hbox.getChildren().add(btnDijawab);
                            }
                            
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
        
        pesanTable.setItems(pesanList);
    }
    
    private void loadPesanData() {
        allPesanData = pesanDAO.getAllPesan();
        updateStatistics();
        filterAndDisplayData();
    }
    
    private void updateStatistics() {
        if (countBaruLabel != null) {
            countBaruLabel.setText(String.valueOf(pesanDAO.countPesanBaru()));
        }
        if (countDibacaLabel != null) {
            countDibacaLabel.setText(String.valueOf(pesanDAO.countByStatus("dibaca")));
        }
        if (countDijawabLabel != null) {
            countDijawabLabel.setText(String.valueOf(pesanDAO.countByStatus("dijawab")));
        }
        if (countTotalLabel != null) {
            countTotalLabel.setText(String.valueOf(allPesanData != null ? allPesanData.size() : 0));
        }
    }
    
    private void filterAndDisplayData() {
        pesanList.clear();
        
        String keyword = searchField != null ? searchField.getText() : "";
        String lowerKeyword = keyword.toLowerCase().trim();
        String statusFilter = statusFilterComboBox != null && statusFilterComboBox.getValue() != null 
            ? statusFilterComboBox.getValue() 
            : "Semua";
        
        if (allPesanData == null) {
            return;
        }
        
        for (Pesan pesan : allPesanData) {
            // Filter status
            if (!statusFilter.equals("Semua")) {
                if (!statusFilter.equals(pesan.getStatus())) {
                    continue;
                }
            }
            
            // Filter keyword
            if (!lowerKeyword.isEmpty()) {
                String nama = pesan.getNama() != null ? pesan.getNama().toLowerCase() : "";
                String email = pesan.getEmail() != null ? pesan.getEmail().toLowerCase() : "";
                String subjek = pesan.getSubjek() != null ? pesan.getSubjek().toLowerCase() : "";
                
                if (!nama.contains(lowerKeyword) && 
                    !email.contains(lowerKeyword) &&
                    !subjek.contains(lowerKeyword)) {
                    continue;
                }
            }
            
            pesanList.add(pesan);
        }
        
        // Update empty label
        if (emptyLabel != null) {
            emptyLabel.setVisible(pesanList.isEmpty());
        }
    }
    
    @FXML
    private void handleSearch() {
        filterAndDisplayData();
    }
    
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadPesanData();
        showAlert(AlertType.INFORMATION, "Berhasil", "Data berhasil di-refresh!");
    }
    
    private void handleDetailPesan(Pesan pesan) {
        try {
            // Create modal window
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setTitle("Detail Pesan");
            modalStage.setResizable(false);
            
            VBox root = new VBox(15);
            root.setPadding(new Insets(20));
            root.setStyle("-fx-background-color: white;");
            
            // Header
            Label titleLabel = new Label("Detail Pesan");
            titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #020E4C;");
            
            // Info fields
            VBox infoBox = new VBox(10);
            infoBox.setPadding(new Insets(10));
            
            Label namaLabel = new Label("Nama: " + pesan.getNama());
            namaLabel.setStyle("-fx-font-size: 14px;");
            
            Label emailLabel = new Label("Email: " + pesan.getEmail());
            emailLabel.setStyle("-fx-font-size: 14px;");
            
            Label subjekLabel = new Label("Subjek: " + pesan.getSubjek());
            subjekLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            
            Label statusLabel = new Label("Status: " + pesan.getStatus());
            statusLabel.setStyle("-fx-font-size: 14px;");
            
            String tanggalText = pesan.getCreatedAt() != null 
                ? pesan.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", new Locale("id")))
                : "-";
            Label tanggalLabel = new Label("Tanggal: " + tanggalText);
            tanggalLabel.setStyle("-fx-font-size: 14px;");
            
            infoBox.getChildren().addAll(namaLabel, emailLabel, subjekLabel, statusLabel, tanggalLabel);
            
            // Pesan content
            Label pesanTitleLabel = new Label("Pesan:");
            pesanTitleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            
            TextArea pesanArea = new TextArea(pesan.getPesan());
            pesanArea.setEditable(false);
            pesanArea.setWrapText(true);
            pesanArea.setPrefRowCount(8);
            pesanArea.setStyle("-fx-font-size: 13px;");
            
            // Buttons
            HBox buttonBox = new HBox(10);
            Button btnClose = new Button("Tutup");
            btnClose.setStyle("-fx-background-color: #020E4C; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 8 20;");
            btnClose.setOnAction(e -> modalStage.close());
            
            buttonBox.getChildren().add(btnClose);
            
            root.getChildren().addAll(titleLabel, infoBox, pesanTitleLabel, pesanArea, buttonBox);
            
            Scene scene = new Scene(root, 600, 500);
            modalStage.setScene(scene);
            modalStage.show();
            
            // Update status to "dibaca" if still "baru"
            if (pesan.getStatus().equals("baru")) {
                pesanDAO.updateStatus(pesan.getId(), "dibaca");
                loadPesanData();
            }
        } catch (Exception e) {
            System.err.println("Error menampilkan detail pesan: " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Gagal menampilkan detail pesan.");
        }
    }
    
    private void handleTandaiDibaca(Pesan pesan) {
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Tandai pesan ini sebagai sudah dibaca?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (pesanDAO.updateStatus(pesan.getId(), "dibaca")) {
                    showAlert(AlertType.INFORMATION, "Berhasil", "Pesan berhasil ditandai sebagai sudah dibaca!");
                    loadPesanData();
                } else {
                    showAlert(AlertType.ERROR, "Gagal", "Gagal mengupdate status pesan.");
                }
            }
        });
    }
    
    private void handleTandaiDijawab(Pesan pesan) {
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Konfirmasi");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Tandai pesan ini sebagai sudah dijawab?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (pesanDAO.updateStatus(pesan.getId(), "dijawab")) {
                    showAlert(AlertType.INFORMATION, "Berhasil", "Pesan berhasil ditandai sebagai sudah dijawab!");
                    loadPesanData();
                } else {
                    showAlert(AlertType.ERROR, "Gagal", "Gagal mengupdate status pesan.");
                }
            }
        });
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
    private void handleLaporanProgramMenu(ActionEvent event) {
        navigateToPage("src/main/java/view/LaporanProgramAdminView.fxml", event);
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            SessionManager.clearSession();
            navigateToPage("src/main/java/view/login.fxml", event);
        } catch (Exception e) {
            System.err.println("Error saat logout: " + e.getMessage());
            e.printStackTrace();
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
            System.err.println("Error navigating to page: " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Tidak dapat membuka halaman: " + e.getMessage());
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

