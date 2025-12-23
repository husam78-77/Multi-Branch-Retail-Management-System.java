package controller;

import dao.BranchDAO;
import dao.ProductDAO;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.SceneManager;
import model.Branch;
import model.Product;

public class ProductController {

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Integer> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Integer> colQuantity;
    @FXML private TableColumn<Product, Integer> colBranch;

    @FXML private TextField searchField;
    @FXML private TextField nameField;
    @FXML private TextField categoryField;
    @FXML private TextField priceField;
    @FXML private TextField quantityField;
    @FXML private ComboBox<Branch> branchComboBox;

    private final ProductDAO productDAO = new ProductDAO();
    private final BranchDAO branchDAO = new BranchDAO();
    private final ObservableList<Product> productList =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        colId.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getProductId()).asObject());

        colName.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getProductName()));

        colCategory.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getCategory()));

        colPrice.setCellValueFactory(data ->
                new SimpleDoubleProperty(
                        data.getValue().getPrice()).asObject());

        colQuantity.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getQuantity()).asObject());

        colBranch.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getBranchId()).asObject());

        branchComboBox.getItems().addAll(branchDAO.getAll());

        branchComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Branch item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getBranchName());
            }
        });

        branchComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Branch item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getBranchName());
            }
        });

        	productTable.getSelectionModel().selectedItemProperty().addListener(
        	    (obs, oldVal, selected) -> {
        	        if (selected != null) {

        	            nameField.setText(selected.getProductName());
        	            categoryField.setText(selected.getCategory());
        	            priceField.setText(String.valueOf(selected.getPrice()));
        	            quantityField.setText(String.valueOf(selected.getQuantity()));

        	            branchComboBox.getItems().forEach(branch -> {
        	                if (branch.getBranchId() == selected.getBranchId()) {
        	                    branchComboBox.setValue(branch);
        	                }
        	            });
        	        }
        	    }
        	);

        loadProducts();
    }

    @FXML
    public void loadProducts() {
        productList.clear();
        productList.addAll(productDAO.getAll());
        productTable.setItems(productList);
    }

    @FXML
    private void handleSearch() {
        productList.clear();
        productList.addAll(productDAO.searchByName(searchField.getText()));
        productTable.setItems(productList);
    }

    @FXML
    private void handleAdd() {

        Branch selectedBranch = branchComboBox.getValue();

        if (selectedBranch == null) {
            showAlert("Please select a branch.");
            return;
        }

        try {
            Product product = new Product(
                    nameField.getText(),
                    categoryField.getText(),
                    Double.parseDouble(priceField.getText()),
                    0,
                    Integer.parseInt(quantityField.getText()),
                    selectedBranch.getBranchId()
            );

            productDAO.insert(product);
            loadProducts();
            clearFields();

        } catch (NumberFormatException e) {
            showAlert("Invalid number format.");
        }
    }

    @FXML
    private void handleUpdate() {

        Product selected = productTable.getSelectionModel().getSelectedItem();
        Branch selectedBranch = branchComboBox.getValue();

        if (selected == null || selectedBranch == null) {
            showAlert("Please select a product and a branch.");
            return;
        }

        try {
            Product updated = new Product(
                    selected.getProductId(),
                    nameField.getText(),
                    categoryField.getText(),
                    Double.parseDouble(priceField.getText()),
                    selected.getCost(),
                    Integer.parseInt(quantityField.getText()),
                    selectedBranch.getBranchId(),
                    false
            );

            productDAO.update(updated);
            loadProducts();
            clearFields();

        } catch (NumberFormatException e) {
            showAlert("Invalid number format.");
        }
    }

    @FXML
    private void handleDelete() {

        Product selected = productTable.getSelectionModel().getSelectedItem();

        if (selected != null) {
            productDAO.softDelete(selected.getProductId());
            loadProducts();
        }
    }

    @FXML
    private void handleBack() {
        SceneManager.switchScene("/view/DashboardView.fxml", "Dashboard");
    }

    private void clearFields() {
        nameField.clear();
        categoryField.clear();
        priceField.clear();
        quantityField.clear();
        branchComboBox.setValue(null);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
