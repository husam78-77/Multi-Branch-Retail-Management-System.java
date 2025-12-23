package controller;

import dao.BranchDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.SceneManager;
import model.Branch;

public class BranchController {

    @FXML private TableView<Branch> branchTable;
    @FXML private TableColumn<Branch, Integer> colId;
    @FXML private TableColumn<Branch, String> colName;
    @FXML private TableColumn<Branch, String> colCity;
    @FXML private TableColumn<Branch, String> colPhone;

    @FXML private TextField searchField;
    @FXML private TextField nameField;
    @FXML private TextField cityField;
    @FXML private TextField phoneField;

    private final BranchDAO branchDAO = new BranchDAO();
    private final ObservableList<Branch> branchList =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getBranchId()).asObject());

        colName.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getBranchName()));

        colCity.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getCity()));

        colPhone.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getPhone()));

        branchTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, selected) -> {
                if (selected != null) {
                    nameField.setText(selected.getBranchName());
                    cityField.setText(selected.getCity());
                    phoneField.setText(selected.getPhone());
                }
            }
        );

        loadBranches();
    }

    @FXML
    public void loadBranches() {
        branchList.clear();
        branchList.addAll(branchDAO.getAll());
        branchTable.setItems(branchList);
    }

    @FXML
    private void handleSearch() {
        branchList.clear();
        branchList.addAll(
                branchDAO.searchByName(searchField.getText())
        );
        branchTable.setItems(branchList);
    }

    @FXML
    private void handleAdd() {

        Branch branch = new Branch(
                nameField.getText(),
                cityField.getText(),
                phoneField.getText()
        );

        branchDAO.insert(branch);
        loadBranches();
        clearFields();
    }

    @FXML
    private void handleUpdate() {

        Branch selected = branchTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Please select a branch to update.");
            return;
        }

        Branch updated = new Branch(
                selected.getBranchId(),
                nameField.getText(),
                cityField.getText(),
                phoneField.getText(),
                false
        );

        branchDAO.update(updated);
        loadBranches();
        clearFields();
    }

    @FXML
    private void handleDelete() {

        Branch selected = branchTable.getSelectionModel().getSelectedItem();

        if (selected != null) {
            branchDAO.softDelete(selected.getBranchId());
            loadBranches();
        }
    }

    @FXML
    private void handleBack() {
        SceneManager.switchScene("/view/DashboardView.fxml", "Dashboard");
    }

    private void clearFields() {
        nameField.clear();
        cityField.clear();
        phoneField.clear();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
