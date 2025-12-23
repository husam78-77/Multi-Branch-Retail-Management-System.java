package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import main.SceneManager;
import model.Employee;
import service.AuditService;
import util.PermissionManager;
import util.SessionManager;

public class DashboardController {

    @FXML private Label welcomeLabel;

    // Card StackPanes (for visibility control)
    @FXML private StackPane productsCard;
    @FXML private StackPane branchesCard;
    @FXML private StackPane salesCard;
    @FXML private StackPane reportsCard;
    @FXML private StackPane chartsCard;
    @FXML private StackPane employeesCard;
    @FXML private StackPane auditLogsCard;

    // Buttons
    @FXML private Button productsBtn;
    @FXML private Button branchesBtn;
    @FXML private Button salesBtn;
    @FXML private Button reportsBtn;
    @FXML private Button chartsBtn;
    @FXML private Button employeesBtn;
    @FXML private Button auditLogsBtn;

    private final AuditService auditService = new AuditService();

    @FXML
    public void initialize() {
        // Ensure user is logged in
        if (!SessionManager.isLoggedIn()) {
            SceneManager.switchScene("/view/LoginView.fxml", "Login");
            return;
        }

        Employee user = SessionManager.getCurrentUser();
        
        // Setup welcome message
        setupWelcomeMessage(user);

        // Apply role-based UI permissions
        applyRolePermissions();

        // Setup button actions
        setupButtonActions();

        auditService.log("DASHBOARD_ACCESS - Accessed dashboard");
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
    }

    private void applyRolePermissions() {
        // Products
        setCardVisibility(productsCard, productsBtn, PermissionManager.canManageProducts());

        // Branches (ADMIN only)
        setCardVisibility(branchesCard, branchesBtn, PermissionManager.canManageBranches());

        // Sales (All roles)
        setCardVisibility(salesCard, salesBtn, PermissionManager.canPerformSales());

        // Reports (ADMIN and MANAGER)
        setCardVisibility(reportsCard, reportsBtn, PermissionManager.canViewReports());

        // Charts (ADMIN and MANAGER)
        setCardVisibility(chartsCard, chartsBtn, PermissionManager.canViewCharts());

        // Employees (ADMIN and MANAGER)
        setCardVisibility(employeesCard, employeesBtn, PermissionManager.canManageEmployees());

        // Audit Logs (All roles - but filtered by role)
        setCardVisibility(auditLogsCard, auditLogsBtn, true);
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
                auditService.log("NAVIGATION - Accessed Products");
                SceneManager.switchScene("/view/ProductView.fxml", "Products");
            });
        }

        if (branchesBtn != null) {
            branchesBtn.setOnAction(e -> {
                auditService.log("NAVIGATION - Accessed Branches");
                SceneManager.switchScene("/view/BranchView.fxml", "Branches");
            });
        }

        if (salesBtn != null) {
            salesBtn.setOnAction(e -> {
                auditService.log("NAVIGATION - Accessed Sales");
                SceneManager.switchScene("/view/SalesView.fxml", "Sales");
            });
        }

        if (reportsBtn != null) {
            reportsBtn.setOnAction(e -> {
                auditService.log("NAVIGATION - Accessed Reports");
                SceneManager.switchScene("/view/ReportsMenuView.fxml", "Reports");
            });
        }

        if (chartsBtn != null) {
            chartsBtn.setOnAction(e -> {
                auditService.log("NAVIGATION - Accessed Charts");
                SceneManager.switchScene("/view/ChartView.fxml", "Charts");
            });
        }

        if (employeesBtn != null) {
            employeesBtn.setOnAction(e -> {
                auditService.log("NAVIGATION - Accessed Employees");
                SceneManager.switchScene("/view/EmployeeView.fxml", "Employees");
            });
        }

        if (auditLogsBtn != null) {
            auditLogsBtn.setOnAction(e -> {
                auditService.log("NAVIGATION - Accessed Audit Logs");
                SceneManager.switchScene("/view/AuditLogView.fxml", "Audit Logs");
            });
        }
    }

    @FXML
    private void handleLogout() {
        Employee user = SessionManager.getCurrentUser();
        auditService.logLogout(user.getUsername());
        
        SessionManager.logout();
        SceneManager.switchScene("/view/LoginView.fxml", "Login");
    }
}