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
    public void initialize() {

        Employee user = SessionManager.getCurrentUser();

        welcomeLabel.setText(
                "Welcome, " + user.getFullName() + " (" + user.getRole() + ")"
        );

        applyRolePermissions(user.getRole());

        branchesBtn.setOnAction(e ->
            SceneManager.switchScene("/view/BranchView.fxml", "Branches")
        );

        productsBtn.setOnAction(e ->
            SceneManager.switchScene("/view/ProductView.fxml", "Products")
        );
        reportsBtn.setOnAction(e ->
        SceneManager.switchScene("/view/BestProductReportView.fxml", "Reports")
    );
        reportsBtn.setOnAction(e ->
        SceneManager.switchScene("/view/ChartView.fxml", "Charts")
    );

    }


    private void applyRolePermissions(String role) {

        switch (role) {
            case "ADMIN":
                break;

            case "MANAGER":
                branchesBtn.setDisable(true);
                break;

            case "CASHIER":
                productsBtn.setDisable(true);
                branchesBtn.setDisable(true);
                reportsBtn.setDisable(true);
                break;
        }
    }

    @FXML
    private void handleLogout() {
        SessionManager.logout();
        SceneManager.switchScene("/view/LoginView.fxml", "Login");

    }
}
