package controller;

import dao.ReportDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import main.SceneManager;
import model.BestProductReport;
import util.PDFGenerator;

import java.io.File;
import java.util.List;

public class BestProductReportController {

    @FXML private TableView<BestProductReport> productReportTable;
    @FXML private TableColumn<BestProductReport, String> colProduct;
    @FXML private TableColumn<BestProductReport, Integer> colTotalSold;

    private final ReportDAO reportDAO = new ReportDAO();
    private List<BestProductReport> reportData;

    @FXML
    public void initialize() {
        colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colTotalSold.setCellValueFactory(new PropertyValueFactory<>("totalSold"));

        reportData = reportDAO.getBestSellingProducts();
        productReportTable.setItems(FXCollections.observableArrayList(reportData));
    }

    @FXML
    private void handleDownloadPDF() {
        if (reportData == null || reportData.isEmpty()) {
            showAlert("No data available to generate report", Alert.AlertType.WARNING);
            return;
        }

        try {
            // File chooser to select save location
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Best Selling Products Report");
            fileChooser.setInitialFileName("Best_Selling_Products_Report.pdf");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );

            File file = fileChooser.showSaveDialog(productReportTable.getScene().getWindow());

            if (file != null) {
                String filePath = PDFGenerator.generateBestProductsReport(reportData, file.getAbsolutePath());
                
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