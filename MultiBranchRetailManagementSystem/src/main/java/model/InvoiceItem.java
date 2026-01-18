package model;

public class InvoiceItem {

    private String productName;
    private int quantity;
    private double price;
    private double subtotal;

    public InvoiceItem(String productName, int quantity, double price, double subtotal) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = subtotal;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getSubtotal() {
        return subtotal;
    }
}
