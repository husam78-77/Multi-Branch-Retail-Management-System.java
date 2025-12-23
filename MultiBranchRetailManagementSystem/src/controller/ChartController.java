package controller;

import dao.ReportDAO;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import main.SceneManager;
import model.BranchSalesReport;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class ChartController {

    @FXML
    private StackPane chartContainer;

    private final ReportDAO reportDAO = new ReportDAO();

    @FXML
    public void initialize() {
        loadBarChart();
    }

    private void loadBarChart() {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (BranchSalesReport r : reportDAO.getSalesPerBranch()) {
            dataset.addValue(
                    r.getTotalSales(),
                    "Sales",
                    r.getBranchName()
            );
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Total Sales per Branch",
                "Branch",
                "Total Sales",
                dataset
        );

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setMouseWheelEnabled(true);

        SwingNode swingNode = new SwingNode();
        swingNode.setContent(chartPanel);

        chartContainer.getChildren().add(swingNode);
    }

    @FXML
    private void handleBack() {
        SceneManager.switchScene("/view/DashboardView.fxml", "Dashboard");
    }
}
