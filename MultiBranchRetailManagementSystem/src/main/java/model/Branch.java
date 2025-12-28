package model;

public class Branch {

    private int branchId;
    private String branchName;
    private String city;
    private String phone;
    private boolean isDeleted;

    public Branch(String branchName, String city, String phone) {
        this.branchName = branchName;
        this.city = city;
        this.phone = phone;
        this.isDeleted = false;
    }

   
    public Branch(int branchId, String branchName, String city,
                  String phone, boolean isDeleted) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.city = city;
        this.phone = phone;
        this.isDeleted = isDeleted;
    }

    
    public int getBranchId() {
        return branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getCity() {
        return city;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isDeleted() {
        return isDeleted;
    }
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

}
