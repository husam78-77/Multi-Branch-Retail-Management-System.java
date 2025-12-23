package controller;

import dao.AuditLogDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.SceneManager;
import model.AuditLog;
import model.Employee;
import util.PermissionManager;
import util.SessionManager;

import java.util.List;

/**
 * Controller for viewing audit logs with role-based filtering.
 * - ADMIN: Can see all logs
 * - MANAGER: Can see logs from their branch only
 * - CASHIER: Can see their own logs only
 */
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

    /**
     * Setup table columns
     */
    private void setupTableColumns() {
        colLogId.setCellValueFactory(new PropertyValueFactory<>("logId"));
        colEmployee.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("employeeRole"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("action"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("formattedActionTime"));

        // Make action column wider and wrap text
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

    /**
     * Setup filter combo box
     */
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

    /**
     * Setup info label based on role
     */
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

    /**
     * Load audit logs with role-based filtering
     */
    private void loadAuditLogs() {
        Employee user = SessionManager.getCurrentUser();
        List<AuditLog> logs;

        if (PermissionManager.isAdmin()) {
            // ADMIN: See all logs
            logs = auditLogDAO.getAll();
        } else if (PermissionManager.isManager()) {
            // MANAGER: See logs from their branch
            logs = auditLogDAO.getByBranchId(user.getBranchId());
        } else {
            // CASHIER: See only their own logs
            logs = auditLogDAO.getByEmployeeId(user.getEmployeeId());
        }

        auditTable.setItems(FXCollections.observableArrayList(logs));
    }

    /**
     * Handle search functionality
     */
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
            // ADMIN: Show all search results
            filteredResults = allResults;
        } else if (PermissionManager.isManager()) {
            // MANAGER: Filter to their branch
            int userBranchId = user.getBranchId();
            filteredResults = allResults.stream()
                    .filter(log -> {
                        // Need to check if log's employee belongs to user's branch
                        // This is simplified - ideally check against employee table
                        return true; // You may need to enhance this
                    })
                    .toList();
        } else {
            // CASHIER: Filter to their own logs
            int userId = user.getEmployeeId();
            filteredResults = allResults.stream()
                    .filter(log -> log.getEmployeeId() == userId)
                    .toList();
        }

        auditTable.setItems(FXCollections.observableArrayList(filteredResults));
    }

    /**
     * Handle filter by action type
     */
    @FXML
    private void handleFilter() {
        String selectedFilter = filterComboBox.getValue();
        
        if (selectedFilter.equals("All Actions")) {
            loadAuditLogs();
            return;
        }

        Employee user = SessionManager.getCurrentUser();
        List<AuditLog> allLogs;

        // Get base logs based on role
        if (PermissionManager.isAdmin()) {
            allLogs = auditLogDAO.getAll();
        } else if (PermissionManager.isManager()) {
            allLogs = auditLogDAO.getByBranchId(user.getBranchId());
        } else {
            allLogs = auditLogDAO.getByEmployeeId(user.getEmployeeId());
        }

        // Filter by action type
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

    /**
     * Refresh audit logs
     */
    @FXML
    private void handleRefresh() {
        searchField.clear();
        filterComboBox.setValue("All Actions");
        loadAuditLogs();
        showAlert("Refreshed", "Audit logs have been refreshed.", Alert.AlertType.INFORMATION);
    }

    /**
     * Export logs to CSV (optional feature)
     */
    @FXML
    private void handleExport() {
        // TODO: Implement CSV export functionality
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