package model;


public class Product {

    private int productId;
    private String productName;
    private String category;
    private double price;
    private double cost;
    private int quantity;
    private int branchId;
    private boolean isDeleted;

    	public Product(String productName, String category, double price,
                   double cost, int quantity, int branchId) {
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.cost = cost;
        this.quantity = quantity;
        this.branchId = branchId;
        this.isDeleted = false;
    }

    	public Product(int productId, String productName, String category,
                   double price, double cost, int quantity,
                   int branchId, boolean isDeleted) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.cost = cost;
        this.quantity = quantity;
        this.branchId = branchId;
        this.isDeleted = isDeleted;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public double getCost() {
        return cost;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getBranchId() {
        return branchId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

}
