package model;

public class BranchSalesReport {

    private String branchName;
    private double totalSales;

    public BranchSalesReport(String branchName, double totalSales) {
        this.branchName = branchName;
        this.totalSales = totalSales;
    }

    public String getBranchName() {
        return branchName;
    }

    public double getTotalSales() {
        return totalSales;
    }
}
