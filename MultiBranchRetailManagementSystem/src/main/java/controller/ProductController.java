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
    @FXML private TableColumn<Product, String> colStatus;

    @FXML private TextField searchField;
    @FXML private TextField nameField;
    @FXML private TextField categoryField;
    @FXML private TextField priceField;
    @FXML private TextField quantityField;  // ✅ Added quantity field
    @FXML private ComboBox<Branch> branchComboBox;

    @FXML private Label userInfoLabel;
    @FXML private Button deleteBtn;

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

    /* ===================== TABLE ===================== */
    private void setupTableColumns() {

        colId.setCellValueFactory(d ->
                new SimpleIntegerProperty(d.getValue().getProductId()).asObject());

        colName.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getProductName()));

        colCategory.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getCategory()));

        colPrice.setCellValueFactory(d ->
                new SimpleDoubleProperty(d.getValue().getPrice()).asObject());

        colQuantity.setCellValueFactory(d ->
                new SimpleIntegerProperty(d.getValue().getQuantity()).asObject());

        colBranch.setCellValueFactory(d ->
                new SimpleIntegerProperty(d.getValue().getBranchId()).asObject());
        colStatus.setCellValueFactory(cellData ->
        new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().isDeleted() ? "Deactivated" : "Active"
        )
);
    }
    

    /* ===================== BRANCH COMBO ===================== */
    private void setupBranchComboBox() {

        if (PermissionManager.isAdmin()) {
            try {
                branchComboBox.getItems().addAll(branchService.getAllBranches());
                branchComboBox.setDisable(false);
            } catch (AccessDeniedException e) {
                showAlert("Error", "Failed to load branches", Alert.AlertType.ERROR);
            }
        } else {
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

    /* ===================== USER INFO ===================== */
    private void setupUserInfo() {

        Employee user = SessionManager.getCurrentUser();

        if (PermissionManager.isManager()) {
            userInfoLabel.setText(
                    "⚠️ You can manage products only for Branch ID: " + user.getBranchId()
            );
            userInfoLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-weight: bold;");
        } else {
            userInfoLabel.setText("✓ Admin - Full Access to All Branches");
            userInfoLabel.setStyle("-fx-text-fill: #51cf66; -fx-font-weight: bold;");
        }
    }

    /* ===================== SELECTION ===================== */
    private void setupTableSelectionListener() {

        productTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, selected) -> {
                    if (selected != null) {
                        nameField.setText(selected.getProductName());
                        categoryField.setText(selected.getCategory());
                        priceField.setText(String.valueOf(selected.getPrice()));
                        quantityField.setText(String.valueOf(selected.getQuantity()));

                        deleteBtn.setText(
                                selected.isDeleted() ? "Activate" : "Deactivate"
                        );

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


    /* ===================== LOAD ===================== */
    @FXML
    public void loadProducts() {
        try {
            productList.clear();
            productList.addAll(productService.getAllProducts());
            
            productTable.setItems(productList);
        } catch (Exception e) {
            showAlert("Error", "Failed to load products", Alert.AlertType.ERROR);
        }
    }

    /* ===================== SEARCH ===================== */
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
        } catch (Exception e) {
            showAlert("Error", "Search failed", Alert.AlertType.ERROR);
        }
    }

    /* ===================== ADD ===================== */
    @FXML
    private void handleAdd() {

        try {
            if (nameField.getText().isBlank() ||
                categoryField.getText().isBlank() ||
                priceField.getText().isBlank() ||
                quantityField.getText().isBlank()) {  // ✅ Validate quantity

                showAlert("Validation Error",
                        "Please fill all required fields.",
                        Alert.AlertType.WARNING);
                return;
            }

            int branchId;
            Employee user = SessionManager.getCurrentUser();

            if (PermissionManager.isAdmin()) {
                Branch branch = branchComboBox.getValue();
                if (branch == null) {
                    showAlert("Validation Error",
                            "Please select a branch.",
                            Alert.AlertType.WARNING);
                    return;
                }
                branchId = branch.getBranchId();
            } else {
                branchId = user.getBranchId();
            }

            Product product = new Product(
                    nameField.getText().trim(),
                    categoryField.getText().trim(),
                    Double.parseDouble(priceField.getText().trim()),
                    0,      // cost
                    Integer.parseInt(quantityField.getText().trim()),  // ✅ Use quantity from form
                    branchId
            );

            productService.addProduct(product);
            showAlert("Success", "Product added successfully!", Alert.AlertType.INFORMATION);
            loadProducts();
            clearFields();

        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Invalid price or quantity format.", Alert.AlertType.WARNING);
        } catch (Exception e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /* ===================== UPDATE ===================== */
    @FXML
    private void handleUpdate() {

        Product selected = productTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Selection Required",
                    "Please select a product to update.",
                    Alert.AlertType.WARNING);
            return;
        }

        try {
            if (nameField.getText().isBlank() ||
                categoryField.getText().isBlank() ||
                priceField.getText().isBlank() ||
                quantityField.getText().isBlank()) {  // ✅ Validate quantity

                showAlert("Validation Error",
                        "Please fill all required fields.",
                        Alert.AlertType.WARNING);
                return;
            }

            int branchId;
            Employee user = SessionManager.getCurrentUser();

            if (PermissionManager.isAdmin()) {
                Branch branch = branchComboBox.getValue();
                if (branch == null) {
                    showAlert("Validation Error",
                            "Please select a branch.",
                            Alert.AlertType.WARNING);
                    return;
                }
                branchId = branch.getBranchId();
            } else {
                branchId = user.getBranchId();

                if (selected.getBranchId() != branchId) {
                    showAlert("Access Denied",
                            "You can update products only in your branch.",
                            Alert.AlertType.ERROR);
                    return;
                }
            }

            Product updated = new Product(
                    selected.getProductId(),
                    nameField.getText().trim(),
                    categoryField.getText().trim(),
                    Double.parseDouble(priceField.getText().trim()),
                    selected.getCost(),
                    Integer.parseInt(quantityField.getText().trim()),  // ✅ Use NEW quantity from form
                    branchId,
                    false
            );

            productService.updateProduct(updated);
            showAlert("Success", "Product updated successfully!", Alert.AlertType.INFORMATION);
            loadProducts();
            clearFields();

        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Invalid price or quantity format.", Alert.AlertType.WARNING);
        } catch (Exception e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /* ===================== DELETE ===================== */
    @FXML
    private void handleDelete() {

        Product selected = productTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(
                    "Selection Required",
                    "Please select a product",
                    Alert.AlertType.WARNING
            );
            return;
        }

        boolean isDeactivated = selected.isDeleted();

        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION,
                (isDeactivated ? "Activate" : "Deactivate") +
                        " product: " + selected.getProductName() + " ?",
                ButtonType.OK,
                ButtonType.CANCEL
        );

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            productService.toggleProductStatus(selected.getProductId());

            selected.setDeleted(!selected.isDeleted());

            showAlert(
                    "Success",
                    isDeactivated
                            ? "Product activated successfully!"
                            : "Product deactivated successfully!",
                    Alert.AlertType.INFORMATION
            );

            loadProducts();
            clearFields();

        } catch (Exception e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    /* ===================== NAV ===================== */
    @FXML
    private void handleBack() {
        SceneManager.switchScene("/view/DashboardView.fxml", "Dashboard");
    }

    /* ===================== HELPERS ===================== */
    private void clearFields() {
        nameField.clear();
        categoryField.clear();
        priceField.clear();
        quantityField.clear();  // ✅ Clear quantity field
        branchComboBox.setValue(null);
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