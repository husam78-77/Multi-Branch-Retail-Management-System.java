package controller;

import dao.ReportDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.SceneManager;
import model.BranchSalesReport;

public class ReportController {

    @FXML private TableView<BranchSalesReport> branchReportTable;
    @FXML private TableColumn<BranchSalesReport, String> colBranch;
    @FXML private TableColumn<BranchSalesReport, Double> colTotal;

    private final ReportDAO reportDAO = new ReportDAO();

    @FXML
    public void initialize() {

        colBranch.setCellValueFactory(
                new PropertyValueFactory<>("branchName"));

        colTotal.setCellValueFactory(
                new PropertyValueFactory<>("totalSales"));

        branchReportTable.setItems(
                FXCollections.observableArrayList(
                        reportDAO.getSalesPerBranch()
                )
        );
    }

    @FXML
    private void handleBack() {
        SceneManager.switchScene("/view/DashboardView.fxml", "Dashboard");
    }
}
