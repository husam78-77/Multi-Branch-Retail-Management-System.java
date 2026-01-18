package controller;

import dao.InvoiceDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.InvoiceHeader;
import model.InvoiceItem;
import model.SaleItem;
import util.PDFGenerator;

import com.husam.app.SceneManager;

import java.util.List;

public class InvoiceController {
	private int currentSaleId;
	private String currentCashierName;
	private String currentBranchCity;
	private List<SaleItem> currentItems;
	private double currentTotal;

    // ======================
    // Labels (Header)
    // ======================
    @FXML private Label invoiceIdLabel;
    @FXML private Label dateLabel;
    @FXML private Label customerNameLabel;
    @FXML private Label customerPhoneLabel;
    @FXML private Label employeeNameLabel;
    @FXML private Label branchNameLabel;
    @FXML private Label totalLabel;

    // ======================
    // Table
    // ======================
    @FXML private TableView<InvoiceItem> invoiceTable;
    @FXML private TableColumn<InvoiceItem, String> colProduct;
    @FXML private TableColumn<InvoiceItem, Integer> colQty;
    @FXML private TableColumn<InvoiceItem, Double> colPrice;
    @FXML private TableColumn<InvoiceItem, Double> colSubtotal;

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

	public void loadInvoice(int saleId) {
	
	    InvoiceHeader header = invoiceDAO.getInvoiceHeader(saleId);
	    List<InvoiceItem> invoiceItems = invoiceDAO.getInvoiceItems(saleId);
	
	    if (header == null) {
	        showAlert("Invoice not found.");
	        return;
	    }
	    currentBranchCity = header.getBranchCity();

	    // ====== Header UI ======
	    invoiceIdLabel.setText(String.valueOf(header.getSaleId()));
	    dateLabel.setText(header.getSaleDate());
	    customerNameLabel.setText(header.getCustomerName());
	    customerPhoneLabel.setText(
	            header.getCustomerPhone() == null ? "-" : header.getCustomerPhone()
	    );
	    employeeNameLabel.setText(header.getEmployeeName());
	    branchNameLabel.setText(header.getBranchName());
	    totalLabel.setText("Total: " + String.format("%.2f", header.getTotalAmount()));
	
	    invoiceTable.setItems(FXCollections.observableArrayList(invoiceItems));
	
	    currentSaleId = header.getSaleId();
	    currentCashierName = header.getEmployeeName();
	    currentTotal = header.getTotalAmount();
	
	    currentItems = invoiceItems.stream()
	            .map(i -> new SaleItem(
	                    0,
	                    i.getProductName(),
	                    i.getPrice(),
	                    i.getQuantity()
	            ))
	            .toList();
	}


    // ======================
    // Initialize
    // ======================
    @FXML
    public void initialize() {

        colProduct.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getProductName())
        );

        colQty.setCellValueFactory(d ->
                new javafx.beans.property.SimpleIntegerProperty(d.getValue().getQuantity()).asObject()
        );

        colPrice.setCellValueFactory(d ->
                new javafx.beans.property.SimpleDoubleProperty(d.getValue().getPrice()).asObject()
        );

        colSubtotal.setCellValueFactory(d ->
                new javafx.beans.property.SimpleDoubleProperty(d.getValue().getSubtotal()).asObject()
        );
    }

    // ======================
    // Buttons
    // ======================
	@FXML
	private void handlePrint() {
	
	    if (currentItems == null || currentItems.isEmpty()) {
	        showAlert("No invoice data to print.");
	        return;
	    }
	
	    PDFGenerator.generateInvoiceFull(
	            currentSaleId,
	            dateLabel.getText(),
	            customerNameLabel.getText(),
	            customerPhoneLabel.getText(),
	            employeeNameLabel.getText(),
	            branchNameLabel.getText(),
	            currentBranchCity,
	            currentItems,
	            currentTotal
	    );

	
	    showInfo("Invoice PDF generated successfully.");
	}

    @FXML
    private void handleBack() {
        SceneManager.switchScene("/view/DashboardView.fxml", "Dashboard");
    }

    // ======================
    // Alerts
    // ======================
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
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
}
