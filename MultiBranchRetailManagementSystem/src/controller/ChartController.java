package controller;

import dao.ReportDAO;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import main.SceneManager;
import model.BranchSalesReport;

import java.util.List;

public class ChartController {

    @FXML
    private StackPane chartContainer;

    private final ReportDAO reportDAO = new ReportDAO();

    @FXML
    public void initialize() {
        loadBarChart();
    }

    private void loadBarChart() {
        try {
            // Get sales data per branch
            List<BranchSalesReport> salesData = reportDAO.getSalesPerBranch();

            // Create axes
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Branch");

            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Total Sales");

            // Create bar chart
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Total Sales per Branch");
            barChart.setLegendVisible(false);

            // Create data series
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Sales");

            // Add data to series from ReportDAO
            for (BranchSalesReport report : salesData) {
                series.getData().add(
                    new XYChart.Data<>(report.getBranchName(), report.getTotalSales())
                );
            }

            // Add series to chart
            barChart.getData().add(series);

            // Style the chart
            barChart.setPrefWidth(700);
            barChart.setPrefHeight(500);
            
            // Optional: Add some styling
            barChart.setStyle("-fx-background-color: transparent;");

            // Add chart to container
            chartContainer.getChildren().clear();
            chartContainer.getChildren().add(barChart);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading chart: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        SceneManager.switchScene("/view/DashboardView.fxml", "Dashboard");
    }
}