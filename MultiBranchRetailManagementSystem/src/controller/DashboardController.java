package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import main.SceneManager;
import model.Employee;
import util.SessionManager;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button productsBtn;

    @FXML
    private Button branchesBtn;

    @FXML
    private Button salesBtn;

    @FXML
    private Button reportsBtn;

    @FXML
    private Button chartsBtn;

    @FXML
    public void initialize() {
        Employee user = SessionManager.getCurrentUser();
        welcomeLabel.setText(
                "Welcome, " + user.getFullName() + " (" + user.getRole() + ")"
        );

        applyRolePermissions(user.getRole());

        // Set up button actions
        branchesBtn.setOnAction(e ->
            SceneManager.switchScene("/view/BranchView.fxml", "Branches")
        );

        productsBtn.setOnAction(e ->
            SceneManager.switchScene("/view/ProductView.fxml", "Products")
        );

        salesBtn.setOnAction(e ->
            SceneManager.switchScene("/view/SalesView.fxml", "Sales")
        );

        // Navigate to Reports Menu instead of direct report
        reportsBtn.setOnAction(e ->
            SceneManager.switchScene("/view/ReportsMenuView.fxml", "Reports")
        );

        chartsBtn.setOnAction(e ->
            SceneManager.switchScene("/view/ChartView.fxml", "Charts")
        );
    }

    private void applyRolePermissions(String role) {
        switch (role) {
            case "ADMIN":
                // Admin has access to everything
                break;
            case "MANAGER":
                // Manager cannot manage branches
                branchesBtn.setDisable(true);
                break;
            case "CASHIER":
                // Cashier can only access sales
                productsBtn.setDisable(true);
                branchesBtn.setDisable(true);
                reportsBtn.setDisable(true);
                chartsBtn.setDisable(true);
                break;
        }
    }

    @FXML
    private void handleLogout() {
        SessionManager.logout();
        SceneManager.switchScene("/view/LoginView.fxml", "Login");
    }
}