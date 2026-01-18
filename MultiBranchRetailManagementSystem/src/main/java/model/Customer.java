package model;

public class Customer {

    private int customerId;
    private String fullName;
    private String phone;

    public Customer(int customerId, String fullName, String phone) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.phone = phone;
    }

    public Customer(String fullName, String phone) {
        this.fullName = fullName;
        this.phone = phone;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
