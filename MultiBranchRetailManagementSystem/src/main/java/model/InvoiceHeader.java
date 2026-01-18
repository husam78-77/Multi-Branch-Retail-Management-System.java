package model;

public class InvoiceHeader {

    private int saleId;
    private String saleDate;
    private String customerName;
    private String customerPhone;
    private String employeeName;
    private String branchName;
    private double totalAmount;

    public InvoiceHeader(
            int saleId,
            String saleDate,
            String customerName,
            String customerPhone,
            String employeeName,
            String branchName,
            double totalAmount
    ) {
        this.saleId = saleId;
        this.saleDate = saleDate;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.employeeName = employeeName;
        this.branchName = branchName;
        this.totalAmount = totalAmount;
    }

    public int getSaleId() {
        return saleId;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getBranchName() {
        return branchName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}
