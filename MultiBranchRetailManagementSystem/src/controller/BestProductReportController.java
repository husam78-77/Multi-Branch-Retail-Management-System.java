package controller;

import dao.ReportDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.SceneManager;
import model.BestProductReport;

public class BestProductReportController {

    @FXML private TableView<BestProductReport> productReportTable;
    @FXML private TableColumn<BestProductReport, String> colProduct;
    @FXML private TableColumn<BestProductReport, Integer> colTotalSold;

    private final ReportDAO reportDAO = new ReportDAO();

    @FXML
    public void initialize() {

        colProduct.setCellValueFactory(
                new PropertyValueFactory<>("productName"));

        colTotalSold.setCellValueFactory(
                new PropertyValueFactory<>("totalSold"));

        productReportTable.setItems(
                FXCollections.observableArrayList(
                        reportDAO.getBestSellingProducts()
                )
        );
    }

    @FXML
    private void handleBack() {
        SceneManager.switchScene("/view/DashboardView.fxml", "Dashboard");
    }
}
