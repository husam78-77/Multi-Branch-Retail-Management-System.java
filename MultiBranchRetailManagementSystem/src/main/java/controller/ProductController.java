package controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.husam.app.SceneManager;
import model.Branch;
import model.Employee;
import model.Product;
import service.BranchService;
import service.ProductService;
import util.AccessDeniedException;
import util.PermissionManager;
import util.SessionManager;

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

    @FXML private Label userInfoLabel; 

    private final ProductService productService = new ProductService();
    private final BranchService branchService = new BranchService();
    
    private final ObservableList<Product> productList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (!PermissionManager.canManageProducts()) {
            showAlert("Access Denied", 
                     "You don't have permission to manage products.", 
                     Alert.AlertType.ERROR);
            handleBack();
            return;
        }

        setupTableColumns();
        setupBranchComboBox();
        setupUserInfo();
        setupTableSelectionListener();
        loadProducts();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getProductId()).asObject());

        colName.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getProductName()));

        colCategory.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCategory()));

        colPrice.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getPrice()).asObject());

        colQuantity.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());

        colBranch.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getBranchId()).asObject());
    }

    private void setupBranchComboBox() {
        Employee currentUser = SessionManager.getCurrentUser();

        if (PermissionManager.isAdmin()) {
            try {
                branchComboBox.getItems().addAll(branchService.getAllBranches());
                branchComboBox.setDisable(false);
            } catch (AccessDeniedException e) {
                showAlert("Error", "Failed to load branches", Alert.AlertType.ERROR);
            }
        } else if (PermissionManager.isManager()) {
            branchComboBox.setDisable(true);
        }

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
    }

    private void setupUserInfo() {
        if (userInfoLabel != null) {
            Employee user = SessionManager.getCurrentUser();
            if (PermissionManager.isManager()) {
                userInfoLabel.setText(
                    "⚠️ You can only manage products for Branch ID: " + user.getBranchId()
                );
                userInfoLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-weight: bold;");
            } else {
                userInfoLabel.setText("✓ Admin - Full Access to All Branches");
                userInfoLabel.setStyle("-fx-text-fill: #51cf66; -fx-font-weight: bold;");
            }
        }
    }

    private void setupTableSelectionListener() {
        productTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, selected) -> {
                if (selected != null) {
                    nameField.setText(selected.getProductName());
                    categoryField.setText(selected.getCategory());
                    priceField.setText(String.valueOf(selected.getPrice()));
                    quantityField.setText(String.valueOf(selected.getQuantity()));

                    if (PermissionManager.isAdmin()) {
                        branchComboBox.getItems().forEach(branch -> {
                            if (branch.getBranchId() == selected.getBranchId()) {
                                branchComboBox.setValue(branch);
                            }
                        });
                    }
                }
            }
        );
    }

    @FXML
    public void loadProducts() {
        try {
            productList.clear();
            productList.addAll(productService.getAllProducts());
            productTable.setItems(productList);
        } catch (AccessDeniedException e) {
            showAlert("Access Denied", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Failed to load products: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        
        if (keyword.isEmpty()) {
            loadProducts();
            return;
        }

        try {
            productList.clear();
            productList.addAll(productService.searchProducts(keyword));
            productTable.setItems(productList);
        } catch (AccessDeniedException e) {
            showAlert("Access Denied", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Search failed: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAdd() {
        try {
            if (nameField.getText().trim().isEmpty() ||
                categoryField.getText().trim().isEmpty() ||
                priceField.getText().trim().isEmpty() ||
                quantityField.getText().trim().isEmpty()) {
                showAlert("Validation Error", "Please fill in all fields.", Alert.AlertType.WARNING);
                return;
            }

            int branchId;
            Employee currentUser = SessionManager.getCurrentUser();

            if (PermissionManager.isAdmin()) {
                Branch selectedBranch = branchComboBox.getValue();
                if (selectedBranch == null) {
                    showAlert("Validation Error", "Please select a branch.", Alert.AlertType.WARNING);
                    return;
                }
                branchId = selectedBranch.getBranchId();
            } else {
                branchId = currentUser.getBranchId();
            }

            Product product = new Product(
                    nameField.getText().trim(),
                    categoryField.getText().trim(),
                    Double.parseDouble(priceField.getText().trim()),
                    0, 
                    Integer.parseInt(quantityField.getText().trim()),
                    branchId
            );

            productService.addProduct(product);
            
            showAlert("Success", "Product added successfully!", Alert.AlertType.INFORMATION);
            loadProducts();
            clearFields();

        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Invalid number format.", Alert.AlertType.WARNING);
        } catch (AccessDeniedException e) {
            showAlert("Access Denied", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Failed to add product: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleUpdate() {
        Product selected = productTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Selection Required", "Please select a product to update.", Alert.AlertType.WARNING);
            return;
        }

        try {
            if (nameField.getText().trim().isEmpty() ||
                categoryField.getText().trim().isEmpty() ||
                priceField.getText().trim().isEmpty() ||
                quantityField.getText().trim().isEmpty()) {
                showAlert("Validation Error", "Please fill in all fields.", Alert.AlertType.WARNING);
                return;
            }

            int branchId;
            Employee currentUser = SessionManager.getCurrentUser();

            if (PermissionManager.isAdmin()) {
                Branch selectedBranch = branchComboBox.getValue();
                if (selectedBranch == null) {
                    showAlert("Validation Error", "Please select a branch.", Alert.AlertType.WARNING);
                    return;
                }
                branchId = selectedBranch.getBranchId();
            } else {
                branchId = currentUser.getBranchId();
                
                if (selected.getBranchId() != branchId) {
                    showAlert("Access Denied", 
                             "You can only update products from your branch.", 
                             Alert.AlertType.ERROR);
                    return;
                }
            }

            Product updated = new Product(
                    selected.getProductId(),
                    nameField.getText().trim(),
                    categoryField.getText().trim(),
                    Double.parseDouble(priceField.getText().trim()),
                    selected.getCost(), // Keep existing cost
                    Integer.parseInt(quantityField.getText().trim()),
                    branchId,
                    false
            );

            productService.updateProduct(updated);
            
            showAlert("Success", "Product updated successfully!", Alert.AlertType.INFORMATION);
            loadProducts();
            clearFields();

        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Invalid number format.", Alert.AlertType.WARNING);
        } catch (AccessDeniedException e) {
            showAlert("Access Denied", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Failed to update product: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleDelete() {
        Product selected = productTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Selection Required", "Please select a product to delete.", Alert.AlertType.WARNING);
            return;
        }
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Product");
        confirmAlert.setContentText("Are you sure you want to delete: " + selected.getProductName() + "?");

        if (confirmAlert.showAndWait().get() != ButtonType.OK) {
            return;
        }

        try {
            productService.deleteProduct(selected.getProductId());
            
            showAlert("Success", "Product deleted successfully!", Alert.AlertType.INFORMATION);
            loadProducts();
            clearFields();

        } catch (AccessDeniedException e) {
            showAlert("Access Denied", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Failed to delete product: " + e.getMessage(), Alert.AlertType.ERROR);
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
        
        if (PermissionManager.isAdmin()) {
            branchComboBox.setValue(null);
        }
        
        productTable.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}