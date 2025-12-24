package model;

public class SaleItem {

    private Product product;
    private int quantity;

    public SaleItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public String getProductName() {
        return product.getProductName();
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return product.getPrice();
    }

    public double getSubtotal() {
        return product.getPrice() * quantity;
    }

    public Product getProduct() {
        return product;
    }
}
