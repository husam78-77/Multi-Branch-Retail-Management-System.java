package controller;

import util.PDFGenerator;
import util.SessionManager;
import dao.ProductDAO;
import dao.SaleDAO;
import dao.SaleDetailDAO;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.SceneManager;
import model.Product;
import model.SaleItem;

public class SalesController {

    @FXML private ComboBox<Product> productComboBox;
    @FXML private TextField quantityField;

    @FXML private TableView<SaleItem> cartTable;
    @FXML private TableColumn<SaleItem, String> colName;
    @FXML private TableColumn<SaleItem, Integer> colQty;
    @FXML private TableColumn<SaleItem, Double> colPrice;
    @FXML private TableColumn<SaleItem, Double> colSubtotal;

    @FXML private Label totalLabel;

    private final ProductDAO productDAO = new ProductDAO();
    private final ObservableList<SaleItem> cart =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        // Load products
        productComboBox.getItems().addAll(productDAO.getAll());

        productComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getProductName());
            }
        });

        productComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getProductName());
            }
        });

        colName.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getProductName()));

        colQty.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());

        colPrice.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getPrice()).asObject());

        colSubtotal.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getSubtotal()).asObject());

        cartTable.setItems(cart);
    }

    @FXML
    private void handleAddToCart() {

        Product product = productComboBox.getValue();

        if (product == null) {
            showAlert("Select a product.");
            return;
        }

        try {
            int qty = Integer.parseInt(quantityField.getText());

            if (qty <= 0 || qty > product.getQuantity()) {
                showAlert("Invalid quantity.");
                return;
            }

            cart.add(new SaleItem(product, qty));
            updateTotal();
            quantityField.clear();

        } catch (NumberFormatException e) {
            showAlert("Enter valid quantity.");
        }
    }

    @FXML
    private void handleClearCart() {
        if (cart.isEmpty()) {
            showAlert("Cart is already empty.");
            return;
        }

        // Confirm with user before clearing
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Clear Cart");
        confirmAlert.setHeaderText("Clear Shopping Cart?");
        confirmAlert.setContentText("This will remove all items from your cart. Continue?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                cart.clear();
                updateTotal();
                quantityField.clear();
                productComboBox.getSelectionModel().clearSelection();
                
                // Show success message
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Cart Cleared");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Shopping cart has been cleared.");
                successAlert.showAndWait();
            }
        });
    }

    private void updateTotal() {
        double total = cart.stream()
                .mapToDouble(SaleItem::getSubtotal)
                .sum();
        totalLabel.setText("Total: " + total);
    }

    
    @FXML
    private void handleConfirmSale() {

        if (cart.isEmpty()) {
            showAlert("Cart is empty.");
            return;
        }

        int branchId = util.SessionManager.getCurrentUser().getBranchId();
        int employeeId = util.SessionManager.getCurrentUser().getEmployeeId();

        double total = cart.stream()
                .mapToDouble(SaleItem::getSubtotal)
                .sum();

        SaleDAO saleDAO = new SaleDAO();
        SaleDetailDAO detailDAO = new SaleDetailDAO();

        int saleId = saleDAO.insertSale(branchId, employeeId, total);

        if (saleId == -1) {
            showAlert("Failed to save sale.");
            return;
        }

        for (SaleItem item : cart) {

            detailDAO.insertDetail(saleId, item);

            int newQty = item.getProduct().getQuantity() - item.getQuantity();
            productDAO.updateQuantity(
                    item.getProduct().getProductId(),
                    newQty
            );
        }

        showAlert("Sale completed successfully ✔");

        cart.clear();
        updateTotal();
        PDFGenerator.generateInvoice(
                saleId,
                SessionManager.getCurrentUser().getFullName(),
                cart,
                total
        );

        
    }


    @FXML
    private void handleBack() {
        SceneManager.switchScene("/view/DashboardView.fxml", "Dashboard");
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}