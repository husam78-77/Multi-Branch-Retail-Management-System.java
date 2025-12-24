package model;

public class Employee {

    private int employeeId;
    private String fullName;
    private String username;
    private String password;
    private String role;
    private int branchId;
    private boolean isDeleted;

    public Employee(int employeeId, String fullName, String username,
                    String role, int branchId, boolean isDeleted) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.username = username;
        this.role = role;
        this.branchId = branchId;
        this.isDeleted = isDeleted;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public int getBranchId() {
        return branchId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }
}
