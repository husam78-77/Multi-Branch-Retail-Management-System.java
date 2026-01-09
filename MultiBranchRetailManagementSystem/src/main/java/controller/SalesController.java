package controller;

import dao.ProductDAO;
import dao.SaleDAO;
import dao.SaleDetailDAO;
import database.DBConnection;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Product;
import model.SaleItem;
import util.PDFGenerator;
import util.SessionManager;
import com.husam.app.SceneManager;

import java.sql.Connection;

public class SalesController {
	
	private final SaleDAO saleDAO = new SaleDAO();
	private final SaleDetailDAO saleDetailDAO = new SaleDetailDAO();
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

        productComboBox.setItems(
                FXCollections.observableArrayList(productDAO.getAll())
        );

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

        colName.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getProductName()));

        colQty.setCellValueFactory(d ->
                new SimpleIntegerProperty(d.getValue().getQuantity()).asObject());

        colPrice.setCellValueFactory(d ->
                new SimpleDoubleProperty(d.getValue().getPrice()).asObject());

        colSubtotal.setCellValueFactory(d ->
                new SimpleDoubleProperty(d.getValue().getSubtotal()).asObject());

        cartTable.setItems(cart);
        updateTotal();
    }

    @FXML
    private void handleAddToCart() {

        Product product = productComboBox.getValue();

        if (product == null) {
            showAlert("Please select a product.");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            showAlert("Enter a valid quantity.");
            return;
        }

        if (qty <= 0 || qty > product.getQuantity()) {
            showAlert("Invalid quantity.");
            return;
        }

        cart.add(new SaleItem(
                product.getProductId(),
                product.getProductName(),
                product.getPrice(),
                qty
        ));

        updateTotal();
        quantityField.clear();
    }

@FXML
public void handleConfirmSale() {

    if (cart.isEmpty()) {
        showAlert("Cart is empty.");
        return;
    }

    int branchId = SessionManager.getCurrentUser().getBranchId();
    int employeeId = SessionManager.getCurrentUser().getEmployeeId();

    try (Connection conn = DBConnection.getConnection()) {

        conn.setAutoCommit(false);

        int saleId = saleDAO.createSale(conn, branchId, employeeId);
        if (saleId == -1) {
            conn.rollback();
            showAlert("Failed to create sale.");
            return;
        }

        double total = 0;

        for (SaleItem item : cart) {
            total += saleDetailDAO.insertDetail(conn, saleId, item);
        }

        saleDAO.updateTotal(conn, saleId, total);

        conn.commit();

        showInfo("Sale completed successfully.");
        cart.clear();
        updateTotal();

    } catch (Exception e) {
        e.printStackTrace();
        showAlert("Error: " + e.getMessage());
    }
}

    @FXML
    private void handleClearCart() {
        cart.clear();
        updateTotal();
    }

    private void updateTotal() {
        double total = cart.stream()
                .mapToDouble(SaleItem::getSubtotal)
                .sum();
        totalLabel.setText("Total: " + String.format("%.2f", total));
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void handleBack() {
        SceneManager.switchScene("/view/DashboardView.fxml", "Dashboard");
    }
}
