package model;

public class BestProductReport {

    private int productId;
    private String productName;
    private int totalSold;

    public BestProductReport(int productId, String productName, int totalSold) {
        this.productId = productId;
        this.productName = productName;
        this.totalSold = totalSold;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getTotalSold() {
        return totalSold;
    }
}
