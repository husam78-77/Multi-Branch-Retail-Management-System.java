package model;

public class BranchSalesReport {

    private int branchId;
    private String branchName;
    private double totalSales;

    public BranchSalesReport(int branchId, String branchName, double totalSales) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.totalSales = totalSales;
    }

    public int getBranchId() {
        return branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public double getTotalSales() {
        return totalSales;
    }
}
