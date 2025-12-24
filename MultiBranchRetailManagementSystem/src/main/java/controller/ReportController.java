package controller;

import dao.ReportDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import com.husam.app.SceneManager;
import model.BranchSalesReport;
import util.PDFGenerator;

import java.io.File;
import java.util.List;

public class ReportController {

    @FXML private TableView<BranchSalesReport> branchReportTable;
    @FXML private TableColumn<BranchSalesReport, String> colBranch;
    @FXML private TableColumn<BranchSalesReport, Double> colTotal;

    private final ReportDAO reportDAO = new ReportDAO();
    private List<BranchSalesReport> reportData;

    @FXML
    public void initialize() {
        colBranch.setCellValueFactory(new PropertyValueFactory<>("branchName"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalSales"));

        reportData = reportDAO.getSalesPerBranch();
        branchReportTable.setItems(FXCollections.observableArrayList(reportData));
    }

    @FXML
    private void handleDownloadPDF() {
        if (reportData == null || reportData.isEmpty()) {
            showAlert("No data available to generate report", Alert.AlertType.WARNING);
            return;
        }

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Sales Per Branch Report");
            fileChooser.setInitialFileName("Sales_Per_Branch_Report.pdf");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );

            File file = fileChooser.showSaveDialog(branchReportTable.getScene().getWindow());

            if (file != null) {
                String filePath = PDFGenerator.generateBranchSalesReport(reportData, file.getAbsolutePath());
                
                if (filePath != null) {
                    showAlert("Report downloaded successfully!\n\nSaved to: " + filePath, 
                             Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Failed to generate PDF report", Alert.AlertType.ERROR);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error generating PDF: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleBack() {
        SceneManager.switchScene("/view/ReportsMenuView.fxml", "Reports");
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.INFORMATION ? "Success" : "Notice");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}