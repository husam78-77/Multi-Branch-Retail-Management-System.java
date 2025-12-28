package controller;

import com.husam.app.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Branch;
import service.BranchService;
import util.AccessDeniedException;
import util.PermissionManager;

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

    private final BranchService branchService = new BranchService();
    private final ObservableList<Branch> branchList =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        // Permission check
        if (!PermissionManager.canManageBranches()) {
            showAlert("Access Denied", "You are not allowed to manage branches.", Alert.AlertType.ERROR);
            handleBack();
            return;
        }

        colId.setCellValueFactory(d ->
                new javafx.beans.property.SimpleIntegerProperty(
                        d.getValue().getBranchId()).asObject());

        colName.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getBranchName()));

        colCity.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getCity()));

        colPhone.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getPhone()));

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
        try {
            branchList.clear();
            branchList.addAll(branchService.getAllBranches());
            branchTable.setItems(branchList);
        } catch (AccessDeniedException e) {
            showAlert("Access Denied", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSearch() {

        String keyword = searchField.getText().trim();

        try {
            branchList.clear();

            if (keyword.isEmpty()) {
                branchList.addAll(branchService.getAllBranches());
            } else {
                branchList.addAll(branchService.searchBranches(keyword));
            }

        } catch (AccessDeniedException e) {
            showAlert("Access Denied", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAdd() {

        if (!validateInputs()) return;

        try {
            Branch branch = new Branch(
                    nameField.getText().trim(),
                    cityField.getText().trim(),
                    phoneField.getText().trim()
            );

            branchService.addBranch(branch);
            showAlert("Success", "Branch added successfully!", Alert.AlertType.INFORMATION);
            loadBranches();
            clearFields();

        } catch (Exception e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUpdate() {

        Branch selected = branchTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Selection Required", "Please select a branch to update.", Alert.AlertType.WARNING);
            return;
        }

        if (selected.getBranchId() == 1) {
            showAlert("Not Allowed", "Main Branch cannot be modified.", Alert.AlertType.ERROR);
            return;
        }

        if (!validateInputs()) return;

        try {
            Branch updated = new Branch(
                    selected.getBranchId(),
                    nameField.getText().trim(),
                    cityField.getText().trim(),
                    phoneField.getText().trim(),
                    false
            );

            branchService.updateBranch(updated);
            showAlert("Success", "Branch updated successfully!", Alert.AlertType.INFORMATION);
            loadBranches();
            clearFields();

        } catch (Exception e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDelete() {

        Branch selected = branchTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Selection Required", "Please select a branch to delete.", Alert.AlertType.WARNING);
            return;
        }

        if (selected.getBranchId() == 1) {
            showAlert("Not Allowed", "Main Branch cannot be deleted.", Alert.AlertType.ERROR);
            return;
        }

        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete this branch?",
                ButtonType.OK,
                ButtonType.CANCEL
        );

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            branchService.deleteBranch(selected.getBranchId());
            showAlert("Success", "Branch deleted successfully!", Alert.AlertType.INFORMATION);
            loadBranches();
            clearFields();

        } catch (Exception e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleBack() {
        SceneManager.switchScene("/view/DashboardView.fxml", "Dashboard");
    }

    private boolean validateInputs() {

        if (nameField.getText().trim().isEmpty() ||
            cityField.getText().trim().isEmpty() ||
            phoneField.getText().trim().isEmpty()) {

            showAlert("Validation Error", "All fields are required.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void clearFields() {
        nameField.clear();
        cityField.clear();
        phoneField.clear();
        branchTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
