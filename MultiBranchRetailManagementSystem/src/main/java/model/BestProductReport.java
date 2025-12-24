package model;

public class BestProductReport {

    private String productName;
    private int totalSold;

    public BestProductReport(String productName, int totalSold) {
        this.productName = productName;
        this.totalSold = totalSold;
    }

    public String getProductName() {
        return productName;
    }

    public int getTotalSold() {
        return totalSold;
    }
}
