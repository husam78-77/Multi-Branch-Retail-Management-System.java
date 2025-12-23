package controller;

import javafx.fxml.FXML;
import main.SceneManager;

public class ReportsMenuController {

    @FXML
    private void handleBranchReport() {
        SceneManager.switchScene("/view/ReportView.fxml", "Sales Per Branch Report");
    }

    @FXML
    private void handleProductReport() {
        SceneManager.switchScene("/view/BestProductReportView.fxml", "Best Selling Products Report");
    }

    @FXML
    private void handleBack() {
        SceneManager.switchScene("/view/DashboardView.fxml", "Dashboard");
    }
}