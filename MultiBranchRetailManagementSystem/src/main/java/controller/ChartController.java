package controller;

import dao.ReportDAO;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.BranchSalesReport;

import javax.imageio.ImageIO;

import com.husam.app.SceneManager;

import java.awt.print.PrinterJob;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChartController {

    @FXML private StackPane chartContainer;
    @FXML private ToggleButton barChartBtn;
    @FXML private ToggleButton pieChartBtn;
    @FXML private ToggleButton lineChartBtn;
    @FXML private VBox emptyState;
    @FXML private Label totalSalesLabel;
    @FXML private Label avgSalesLabel;
    @FXML private Label topBranchLabel;
    @FXML private Label chartDateLabel;

    private final ReportDAO reportDAO = new ReportDAO();
    private List<BranchSalesReport> salesData;

    @FXML
    public void initialize() {
        loadSalesData();
        updateStatistics();
        loadBarChart();
        updateDateTime();
    }

    private void loadSalesData() {
        try {
            salesData = reportDAO.getSalesPerBranch();
            if (salesData == null || salesData.isEmpty()) {
                showEmptyState(true);
            } else {
                showEmptyState(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading sales data: " + e.getMessage());
            showEmptyState(true);
        }
    }

    private void updateStatistics() {
        if (salesData == null || salesData.isEmpty()) {
            totalSalesLabel.setText("RM 0.00");
            avgSalesLabel.setText("RM 0.00");
            topBranchLabel.setText("-");
            return;
        }

        double totalSales = salesData.stream()
                .mapToDouble(BranchSalesReport::getTotalSales)
                .sum();
        totalSalesLabel.setText(String.format("RM %.2f", totalSales));

        double avgSales = totalSales / salesData.size();
        avgSalesLabel.setText(String.format("RM %.2f", avgSales));

        BranchSalesReport topBranch = salesData.stream()
                .max((a, b) -> Double.compare(a.getTotalSales(), b.getTotalSales()))
                .orElse(null);
        
        if (topBranch != null) {
            topBranchLabel.setText(topBranch.getBranchName());
        } else {
            topBranchLabel.setText("-");
        }
    }

    private void updateDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        chartDateLabel.setText("Updated: " + LocalDateTime.now().format(formatter));
    }

    private void showEmptyState(boolean show) {
        if (emptyState != null) {
            emptyState.setVisible(show);
        }
    }

    @FXML
    private void handleBarChart() {
        loadBarChart();
        updateButtonStyles();
    }

    @FXML
    private void handlePieChart() {
        loadPieChart();
        updateButtonStyles();
    }

    @FXML
    private void handleLineChart() {
        loadLineChart();
        updateButtonStyles();
    }

    private void updateButtonStyles() {
        if (barChartBtn.isSelected()) {
            barChartBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 5; -fx-padding: 8 15 8 15; -fx-cursor: hand;");
            pieChartBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 5; -fx-padding: 8 15 8 15; -fx-cursor: hand;");
            lineChartBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 5; -fx-padding: 8 15 8 15; -fx-cursor: hand;");
        } else if (pieChartBtn.isSelected()) {
            barChartBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 5; -fx-padding: 8 15 8 15; -fx-cursor: hand;");
            pieChartBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 5; -fx-padding: 8 15 8 15; -fx-cursor: hand;");
            lineChartBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 5; -fx-padding: 8 15 8 15; -fx-cursor: hand;");
        } else if (lineChartBtn.isSelected()) {
            barChartBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 5; -fx-padding: 8 15 8 15; -fx-cursor: hand;");
            pieChartBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 5; -fx-padding: 8 15 8 15; -fx-cursor: hand;");
            lineChartBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 5; -fx-padding: 8 15 8 15; -fx-cursor: hand;");
        }
    }

    private void loadBarChart() {
        try {
            if (salesData == null || salesData.isEmpty()) {
                showEmptyState(true);
                return;
            }

            // Create axes
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Branch");

            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Total Sales (RM)");

            // Create bar chart
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Total Sales per Branch");
            barChart.setLegendVisible(false);

            // Create data series
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Sales");

            // Add data to series
            for (BranchSalesReport report : salesData) {
                series.getData().add(
                    new XYChart.Data<>(report.getBranchName(), report.getTotalSales())
                );
            }

            // Add series to chart
            barChart.getData().add(series);

            // Style the chart
            barChart.setPrefWidth(700);
            barChart.setPrefHeight(400);
            barChart.setStyle("-fx-background-color: transparent;");

            // Add chart to container
            chartContainer.getChildren().clear();
            chartContainer.getChildren().add(barChart);
            showEmptyState(false);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading bar chart: " + e.getMessage());
        }
    }

    private void loadPieChart() {
        try {
            if (salesData == null || salesData.isEmpty()) {
                showEmptyState(true);
                return;
            }

            // Create pie chart
            PieChart pieChart = new PieChart();
            pieChart.setTitle("Sales Distribution by Branch");

            // Add data to pie chart
            for (BranchSalesReport report : salesData) {
                PieChart.Data slice = new PieChart.Data(
                    report.getBranchName(), 
                    report.getTotalSales()
                );
                pieChart.getData().add(slice);
            }

            // Style the chart
            pieChart.setPrefWidth(700);
            pieChart.setPrefHeight(400);
            pieChart.setStyle("-fx-background-color: transparent;");
            pieChart.setLegendVisible(true);

            // Add chart to container
            chartContainer.getChildren().clear();
            chartContainer.getChildren().add(pieChart);
            showEmptyState(false);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading pie chart: " + e.getMessage());
        }
    }

    private void loadLineChart() {
        try {
            if (salesData == null || salesData.isEmpty()) {
                showEmptyState(true);
                return;
            }

            // Create axes
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Branch");

            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Total Sales (RM)");

            // Create line chart
            LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
            lineChart.setTitle("Sales Trend by Branch");
            lineChart.setLegendVisible(false);

            // Create data series
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Sales");

            // Add data to series
            for (BranchSalesReport report : salesData) {
                series.getData().add(
                    new XYChart.Data<>(report.getBranchName(), report.getTotalSales())
                );
            }

            // Add series to chart
            lineChart.getData().add(series);

            // Style the chart
            lineChart.setPrefWidth(700);
            lineChart.setPrefHeight(400);
            lineChart.setStyle("-fx-background-color: transparent;");
            lineChart.setCreateSymbols(true);

            // Add chart to container
            chartContainer.getChildren().clear();
            chartContainer.getChildren().add(lineChart);
            showEmptyState(false);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading line chart: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        loadSalesData();
        updateStatistics();
        updateDateTime();
        
        // Reload the current chart type
        if (barChartBtn.isSelected()) {
            loadBarChart();
        } else if (pieChartBtn.isSelected()) {
            loadPieChart();
        } else if (lineChartBtn.isSelected()) {
            loadLineChart();
        }
        
        showInfoAlert("Data refreshed successfully!");
    }

    @FXML
    private void handlePrint() {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            if (job.printDialog()) {
                showInfoAlert("Print functionality - Feature coming soon!");
            }
        } catch (Exception e) {
            showAlert("Print error: " + e.getMessage());
        }
    }

    @FXML
    private void handleExport() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Chart as PNG");
            fileChooser.setInitialFileName("sales_chart.png");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG Files", "*.png")
            );

            File file = fileChooser.showSaveDialog(chartContainer.getScene().getWindow());
            
            if (file != null) {
                // Take snapshot of chart container
                WritableImage image = chartContainer.snapshot(null, null);
                
                // Save to file
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                
                showInfoAlert("Chart exported successfully to:\n" + file.getAbsolutePath());
            }
        } catch (Exception e) {
            showAlert("Export error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        SceneManager.switchScene("/view/DashboardView.fxml", "Dashboard");
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInfoAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}