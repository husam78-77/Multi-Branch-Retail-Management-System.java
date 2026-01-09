package controller;

import dao.ReportDAO;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
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

    // 🎨 Color palette for different branches
    private final String[] CHART_COLORS = {
        "#3498db",  // Blue
        "#e74c3c",  // Red
        "#2ecc71",  // Green
        "#f39c12",  // Orange
        "#9b59b6",  // Purple
        "#1abc9c",  // Turquoise
        "#34495e",  // Dark Gray
        "#e67e22"   // Carrot Orange
    };

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
        String activeStyle = "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 5; -fx-padding: 8 15 8 15; -fx-cursor: hand;";
        String inactiveStyle = "-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: 600; -fx-background-radius: 5; -fx-padding: 8 15 8 15; -fx-cursor: hand;";
        
        barChartBtn.setStyle(barChartBtn.isSelected() ? activeStyle : inactiveStyle);
        pieChartBtn.setStyle(pieChartBtn.isSelected() ? activeStyle : inactiveStyle);
        lineChartBtn.setStyle(lineChartBtn.isSelected() ? activeStyle : inactiveStyle);
    }

    private void loadBarChart() {
        try {
            if (salesData == null || salesData.isEmpty()) {
                showEmptyState(true);
                return;
            }

            // ✅ Create axes with VISIBLE styling
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Branch Name");
            xAxis.setTickLabelFill(javafx.scene.paint.Color.BLACK);
            xAxis.setTickLabelRotation(0);
            xAxis.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-tick-label-fill: #2c3e50;");

            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Total Sales (RM)");
            yAxis.setTickLabelFill(javafx.scene.paint.Color.BLACK);
            yAxis.setAutoRanging(true);
            yAxis.setForceZeroInRange(false);
            yAxis.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-tick-label-fill: #2c3e50;");

            // Create bar chart
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Total Sales per Branch");
            barChart.setLegendVisible(false);
            barChart.setBarGap(5);
            barChart.setCategoryGap(20);
            barChart.setAnimated(false);

            // Set chart size
            barChart.setPrefWidth(900);
            barChart.setPrefHeight(450);
            barChart.setMinHeight(400);

            // ✅ ONE series with ALL branches
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Sales");

            for (BranchSalesReport report : salesData) {
                XYChart.Data<String, Number> data = new XYChart.Data<>(
                    report.getBranchName(), 
                    report.getTotalSales()
                );
                series.getData().add(data);
            }

            barChart.getData().add(series);

            // Clear container and add chart
            chartContainer.getChildren().clear();
            chartContainer.getChildren().add(barChart);
            showEmptyState(false);

            // Apply colors after render
            applyBarChartColors(barChart);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading bar chart: " + e.getMessage());
        }
    }

    private void applyBarChartColors(BarChart<String, Number> chart) {
        javafx.application.Platform.runLater(() -> {
            try {
                Thread.sleep(200); // Give chart time to render
                
                for (XYChart.Series<String, Number> series : chart.getData()) {
                    int colorIndex = 0;
                    for (XYChart.Data<String, Number> data : series.getData()) {
                        Node bar = data.getNode();
                        if (bar != null && colorIndex < CHART_COLORS.length) {
                            // Force color by using !important-like approach with lookup
                            bar.setStyle("-fx-bar-fill: " + CHART_COLORS[colorIndex] + "; -fx-background-color: " + CHART_COLORS[colorIndex] + ";");
                            colorIndex++;
                        }
                    }
                }
                
                // Force chart to update
                chart.layout();
            } catch (Exception e) {
                System.err.println("Error applying bar colors: " + e.getMessage());
            }
        });
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
            pieChart.setLabelsVisible(true);
            pieChart.setStartAngle(90);
            pieChart.setAnimated(false);
            pieChart.setLegendVisible(true);

            // Set chart size
            pieChart.setPrefWidth(900);
            pieChart.setPrefHeight(450);

            // Calculate total for percentages
            double totalSales = salesData.stream()
                    .mapToDouble(BranchSalesReport::getTotalSales)
                    .sum();

            // Add data with percentages
            for (BranchSalesReport report : salesData) {
                double percentage = (report.getTotalSales() / totalSales) * 100;
                
                String label = String.format("%s\nRM %.2f (%.1f%%)", 
                    report.getBranchName(), 
                    report.getTotalSales(),
                    percentage);
                
                PieChart.Data slice = new PieChart.Data(label, report.getTotalSales());
                pieChart.getData().add(slice);
            }

            // Clear container and add chart
            chartContainer.getChildren().clear();
            chartContainer.getChildren().add(pieChart);
            showEmptyState(false);

            // Apply colors
            applyPieChartColors(pieChart);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading pie chart: " + e.getMessage());
        }
    }

    private void applyPieChartColors(PieChart chart) {
        javafx.application.Platform.runLater(() -> {
            try {
                Thread.sleep(200);
                
                int i = 0;
                for (PieChart.Data data : chart.getData()) {
                    if (i < CHART_COLORS.length && data.getNode() != null) {
                        // Apply color using the correct CSS property
                        data.getNode().setStyle("-fx-pie-color: " + CHART_COLORS[i] + "; -fx-background-color: " + CHART_COLORS[i] + ";");
                        i++;
                    }
                }
                
                // Force chart to update
                chart.layout();
            } catch (Exception e) {
                System.err.println("Error applying pie colors: " + e.getMessage());
            }
        });
    }

    private void loadLineChart() {
        try {
            if (salesData == null || salesData.isEmpty()) {
                showEmptyState(true);
                return;
            }

            // ✅ Create axes with VISIBLE styling
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Branch Name");
            xAxis.setTickLabelFill(javafx.scene.paint.Color.BLACK);
            xAxis.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-tick-label-fill: #2c3e50;");

            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Total Sales (RM)");
            yAxis.setTickLabelFill(javafx.scene.paint.Color.BLACK);
            yAxis.setAutoRanging(true);
            yAxis.setForceZeroInRange(false);
            yAxis.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-tick-label-fill: #2c3e50;");

            // Create line chart
            LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
            lineChart.setTitle("Sales Trend by Branch");
            lineChart.setCreateSymbols(true);
            lineChart.setLegendVisible(false);
            lineChart.setAnimated(false);

            // Set chart size
            lineChart.setPrefWidth(900);
            lineChart.setPrefHeight(450);
            lineChart.setMinHeight(400);

            // ✅ ONE series with ALL branches
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Sales");

            for (BranchSalesReport report : salesData) {
                XYChart.Data<String, Number> data = new XYChart.Data<>(
                    report.getBranchName(), 
                    report.getTotalSales()
                );
                series.getData().add(data);
            }

            lineChart.getData().add(series);

            // Clear container and add chart
            chartContainer.getChildren().clear();
            chartContainer.getChildren().add(lineChart);
            showEmptyState(false);

            // Apply colors after render
            applyLineChartColors(lineChart);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading line chart: " + e.getMessage());
        }
    }

    private void applyLineChartColors(LineChart<String, Number> chart) {
        javafx.application.Platform.runLater(() -> {
            try {
                Thread.sleep(200);
                
                for (XYChart.Series<String, Number> series : chart.getData()) {
                    // Style the connecting line
                    if (series.getNode() != null) {
                        series.getNode().setStyle("-fx-stroke: " + CHART_COLORS[0] + "; -fx-stroke-width: 3px;");
                    }
                    
                    // Style each symbol with different color
                    int colorIndex = 0;
                    for (XYChart.Data<String, Number> data : series.getData()) {
                        Node symbol = data.getNode();
                        if (symbol != null && colorIndex < CHART_COLORS.length) {
                            symbol.setStyle(
                                "-fx-background-color: " + CHART_COLORS[colorIndex] + ", white;" +
                                "-fx-background-insets: 0, 2;" +
                                "-fx-background-radius: 10px;" +
                                "-fx-padding: 8px;" +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 4, 0, 0, 1);"
                            );
                            colorIndex++;
                        }
                    }
                }
                
                // Force chart to update
                chart.layout();
            } catch (Exception e) {
                System.err.println("Error applying line colors: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleRefresh() {
        loadSalesData();
        updateStatistics();
        updateDateTime();
        
        // Reload current chart
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
                WritableImage image = chartContainer.snapshot(null, null);
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