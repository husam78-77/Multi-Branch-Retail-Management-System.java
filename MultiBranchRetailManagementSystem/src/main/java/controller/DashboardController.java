package controller;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import com.husam.app.SceneManager;
import model.Employee;
import service.AuditService;
import util.PermissionManager;
import util.SessionManager;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleDescriptionLabel; 

    @FXML private StackPane productsCard;
    @FXML private StackPane branchesCard;
    @FXML private StackPane salesCard;
    @FXML private StackPane reportsCard;
    @FXML private StackPane chartsCard;
    @FXML private StackPane employeesCard;
    @FXML private StackPane auditLogsCard;

    @FXML private Button productsBtn;
    @FXML private Button branchesBtn;
    @FXML private Button salesBtn;
    @FXML private Button reportsBtn;
    @FXML private Button chartsBtn;
    @FXML private Button employeesBtn;
    @FXML private Button auditLogsBtn;

    @FXML private FlowPane cardsContainer; 

    private final AuditService auditService = new AuditService();

    @FXML
    public void initialize() {
        if (!SessionManager.isLoggedIn()) {
            SceneManager.switchScene("/view/LoginView.fxml", "Login");
            return;
        }

        Employee user = SessionManager.getCurrentUser();
        
        setupWelcomeMessage(user);

        applyRolePermissions();

        setupButtonActions();

    }

    private void setupWelcomeMessage(Employee user) {
        String branchInfo = "";
        
        if (user.getBranchId() > 0) {
            branchInfo = " | Branch ID: " + user.getBranchId();
        }
        
        String roleIcon = switch (user.getRole()) {
            case "ADMIN" -> "👑";
            case "MANAGER" -> "👨‍💼";
            case "CASHIER" -> "👤";
            default -> "👤";
        };
        
        welcomeLabel.setText(
            roleIcon + " " + user.getFullName() + 
            " (" + user.getRole() + ")" + branchInfo
        );

        if (roleDescriptionLabel != null) {
            String description = switch (user.getRole()) {
                case "ADMIN" -> "System Administrator - Manage branches, managers, and oversee operations";
                case "MANAGER" -> "Branch Manager - Manage products, cashiers, and monitor branch performance";
                case "CASHIER" -> "Cashier - Process sales and customer transactions";
                default -> "";
            };
            roleDescriptionLabel.setText(description);
        }
    }

    private void applyRolePermissions() {
        setCardVisibility(productsCard, productsBtn, PermissionManager.canManageProducts());

        setCardVisibility(branchesCard, branchesBtn, PermissionManager.canManageBranches());

        setCardVisibility(salesCard, salesBtn, PermissionManager.canPerformSales());

        setCardVisibility(reportsCard, reportsBtn, PermissionManager.canViewReports());

        setCardVisibility(chartsCard, chartsBtn, PermissionManager.canViewCharts());

        setCardVisibility(employeesCard, employeesBtn, PermissionManager.canManageEmployees());

        setCardVisibility(auditLogsCard, auditLogsBtn, PermissionManager.canViewAuditLogs());
    }

    private void setCardVisibility(StackPane card, Button button, boolean hasPermission) {
        if (card != null) {
            card.setVisible(hasPermission);
            card.setManaged(hasPermission);
        }
        if (button != null) {
            button.setDisable(!hasPermission);
        }
    }

    private void setupButtonActions() {
        if (productsBtn != null) {
            productsBtn.setOnAction(e -> {
                SceneManager.switchScene("/view/ProductView.fxml", "Products");
            });
        }

        if (branchesBtn != null) {
            branchesBtn.setOnAction(e -> {
                SceneManager.switchScene("/view/BranchView.fxml", "Branches");
            });
        }

        if (salesBtn != null) {
            salesBtn.setOnAction(e -> {
                SceneManager.switchScene("/view/SalesView.fxml", "Sales");
            });
        }

        if (reportsBtn != null) {
            reportsBtn.setOnAction(e -> {
                SceneManager.switchScene("/view/ReportsMenuView.fxml", "Reports");
            });
        }

        if (chartsBtn != null) {
            chartsBtn.setOnAction(e -> {
                SceneManager.switchScene("/view/ChartView.fxml", "Charts");
            });
        }

        if (employeesBtn != null) {
            employeesBtn.setOnAction(e -> {
                SceneManager.switchScene("/view/EmployeeView.fxml", "Employees");
            });
        }

        if (auditLogsBtn != null) {
            auditLogsBtn.setOnAction(e -> {
                SceneManager.switchScene("/view/AuditLogView.fxml", "Audit Logs");
            });
        }
    }

    @FXML
    private void handleLogout() {
        auditService.logLogout();
        SessionManager.logout();
        SceneManager.switchScene("/view/LoginView.fxml", "Login");
    }
}