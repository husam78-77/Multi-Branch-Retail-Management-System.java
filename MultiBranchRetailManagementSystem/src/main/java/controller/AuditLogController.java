package controller;

import dao.AuditLogDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import com.husam.app.SceneManager;
import model.AuditLog;
import model.Employee;
import util.PermissionManager;
import util.SessionManager;

import java.util.List;

public class AuditLogController {

    @FXML private TableView<AuditLog> auditTable;
    @FXML private TableColumn<AuditLog, Integer> colLogId;
    @FXML private TableColumn<AuditLog, String> colEmployee;
    @FXML private TableColumn<AuditLog, String> colRole;
    @FXML private TableColumn<AuditLog, String> colAction;
    @FXML private TableColumn<AuditLog, String> colTime;

    @FXML private TextField searchField;
    @FXML private Label infoLabel;
    @FXML private ComboBox<String> filterComboBox;

    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupFilterComboBox();
        setupInfoLabel();
        loadAuditLogs();
    }

    private void setupTableColumns() {
        colLogId.setCellValueFactory(new PropertyValueFactory<>("logId"));
        colEmployee.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("employeeRole"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("action"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("formattedActionTime"));

        colAction.setPrefWidth(400);
        colAction.setCellFactory(tc -> {
            TableCell<AuditLog, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(colAction.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });
    }

    private void setupFilterComboBox() {
        filterComboBox.getItems().addAll(
            "All Actions",
            "LOGIN/LOGOUT",
            "PRODUCTS",
            "BRANCHES",
            "EMPLOYEES",
            "SALES",
            "REPORTS"
        );
        filterComboBox.setValue("All Actions");
        
        filterComboBox.setOnAction(e -> handleFilter());
    }

    private void setupInfoLabel() {
        Employee user = SessionManager.getCurrentUser();
        
        if (PermissionManager.isAdmin()) {
            infoLabel.setText("📋 Viewing: All Audit Logs (Admin View)");
            infoLabel.setStyle("-fx-text-fill: #51cf66; -fx-font-weight: bold;");
        } else if (PermissionManager.isManager()) {
            infoLabel.setText("📋 Viewing: Audit Logs for Branch " + user.getBranchId());
            infoLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-weight: bold;");
        } else {
            infoLabel.setText("📋 Viewing: Your Personal Activity Logs");
            infoLabel.setStyle("-fx-text-fill: #4dabf7; -fx-font-weight: bold;");
        }
    }

    private void loadAuditLogs() {
        Employee user = SessionManager.getCurrentUser();
        List<AuditLog> logs;

        if (PermissionManager.isAdmin()) {
            logs = auditLogDAO.getAll();
        } else if (PermissionManager.isManager()) {
            logs = auditLogDAO.getByBranchId(user.getBranchId());
        } else {
            logs = auditLogDAO.getByEmployeeId(user.getEmployeeId());
        }

        auditTable.setItems(FXCollections.observableArrayList(logs));
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        
        if (keyword.isEmpty()) {
            loadAuditLogs();
            return;
        }

        Employee user = SessionManager.getCurrentUser();
        List<AuditLog> allResults = auditLogDAO.searchByAction(keyword);
        List<AuditLog> filteredResults;

        if (PermissionManager.isAdmin()) {
            filteredResults = allResults;
        } else if (PermissionManager.isManager()) {
            int userBranchId = user.getBranchId();
            filteredResults = allResults.stream()
                    .filter(log -> {
                        return true; 
                    })
                    .toList();
        } else {
            int userId = user.getEmployeeId();
            filteredResults = allResults.stream()
                    .filter(log -> log.getEmployeeId() == userId)
                    .toList();
        }

        auditTable.setItems(FXCollections.observableArrayList(filteredResults));
    }

    @FXML
    private void handleFilter() {
        String selectedFilter = filterComboBox.getValue();
        
        if (selectedFilter.equals("All Actions")) {
            loadAuditLogs();
            return;
        }

        Employee user = SessionManager.getCurrentUser();
        List<AuditLog> allLogs;

        if (PermissionManager.isAdmin()) {
            allLogs = auditLogDAO.getAll();
        } else if (PermissionManager.isManager()) {
            allLogs = auditLogDAO.getByBranchId(user.getBranchId());
        } else {
            allLogs = auditLogDAO.getByEmployeeId(user.getEmployeeId());
        }

        List<AuditLog> filtered = allLogs.stream()
                .filter(log -> {
                    String action = log.getAction().toUpperCase();
                    return switch (selectedFilter) {
                        case "LOGIN/LOGOUT" -> action.contains("LOGIN") || action.contains("LOGOUT");
                        case "PRODUCTS" -> action.contains("PRODUCT");
                        case "BRANCHES" -> action.contains("BRANCH");
                        case "EMPLOYEES" -> action.contains("EMPLOYEE");
                        case "SALES" -> action.contains("SALE");
                        case "REPORTS" -> action.contains("REPORT");
                        default -> true;
                    };
                })
                .toList();

        auditTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void handleRefresh() {
        searchField.clear();
        filterComboBox.setValue("All Actions");
        loadAuditLogs();
        showAlert("Refreshed", "Audit logs have been refreshed.", Alert.AlertType.INFORMATION);
    }
    @FXML
    private void handleExport() {
        showAlert("Export", "Export feature coming soon!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleBack() {
        SceneManager.switchScene("/view/DashboardView.fxml", "Dashboard");
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}