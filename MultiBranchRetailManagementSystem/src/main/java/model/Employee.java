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
    
    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(String role) {
        this.role = role;
    }

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setDeleted(boolean deleted) {
	    this.isDeleted = deleted;
	}


}
