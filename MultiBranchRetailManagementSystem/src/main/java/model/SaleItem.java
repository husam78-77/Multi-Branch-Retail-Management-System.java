package model;

public class SaleItem {

    private int productId;
    private String productName;
    private double priceAtSale;
    private int quantity;

    public SaleItem(int productId, String productName,
                    double priceAtSale, int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0");
        }

        this.productId = productId;
        this.productName = productName;
        this.priceAtSale = priceAtSale;
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return priceAtSale;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return priceAtSale * quantity;
    }
}
