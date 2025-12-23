package service;

import dao.EmployeeDAO;
import model.Employee;
import util.AccessDeniedException;
import util.PermissionManager;
import util.Role;
import util.SessionManager;

import java.util.List;
import java.util.stream.Collectors;

public class EmployeeService {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    public List<Employee> getAllEmployees() {
        PermissionManager.requireManagerOrAbove("view employees");

        List<Employee> allEmployees = employeeDAO.getAll();
        
        if (PermissionManager.isAdmin()) {
            return allEmployees; 
        }
        
        if (PermissionManager.isManager()) {
            Employee currentUser = SessionManager.getCurrentUser();
            return allEmployees.stream()
                    .filter(e -> e.getRole().equals("CASHIER"))
                    .filter(e -> e.getBranchId() == currentUser.getBranchId())
                    .collect(Collectors.toList());
        }
        
        return List.of(); 
    }

    public void addEmployee(Employee employee) {
        PermissionManager.requireManagerOrAbove("add employee");

        Role targetRole = Role.fromString(employee.getRole());
        
        PermissionManager.requireRoleManagementPermission(targetRole, "add");

        Employee currentUser = SessionManager.getCurrentUser();
        
        if (PermissionManager.isManager()) {
            if (employee.getBranchId() != currentUser.getBranchId()) {
                throw AccessDeniedException.forBranch(
                    "add employees",
                    currentUser.getBranchId(),
                    employee.getBranchId()
                );
            }
        }

        validateBranchAssignment(employee);

        employeeDAO.insert(employee);
    }

    public void updateEmployee(Employee employee) {
        PermissionManager.requireManagerOrAbove("update employee");

        Role targetRole = Role.fromString(employee.getRole());
        
        PermissionManager.requireRoleManagementPermission(targetRole, "update");

        Employee currentUser = SessionManager.getCurrentUser();
        
        if (PermissionManager.isManager()) {
            if (employee.getBranchId() != currentUser.getBranchId()) {
                throw AccessDeniedException.forBranch(
                    "update employees",
                    currentUser.getBranchId(),
                    employee.getBranchId()
                );
            }
        }

        validateBranchAssignment(employee);

        employeeDAO.update(employee);
    }

    public void deleteEmployee(int employeeId) {
        PermissionManager.requireManagerOrAbove("delete employee");

        Employee employee = getEmployeeById(employeeId);
        if (employee == null) {
            throw new IllegalArgumentException("Employee not found");
        }

        Role targetRole = Role.fromString(employee.getRole());
        
        PermissionManager.requireRoleManagementPermission(targetRole, "delete");

        Employee currentUser = SessionManager.getCurrentUser();
        
        if (PermissionManager.isManager()) {
            if (employee.getBranchId() != currentUser.getBranchId()) {
                throw AccessDeniedException.forBranch(
                    "delete employees",
                    currentUser.getBranchId(),
                    employee.getBranchId()
                );
            }
        }

        employeeDAO.softDelete(employeeId);
    }

    public List<Employee> searchEmployees(String keyword) {
        PermissionManager.requireManagerOrAbove("search employees");

        List<Employee> results = employeeDAO.searchByName(keyword);
        
        if (PermissionManager.isAdmin()) {
            return results; 
        }
        
        if (PermissionManager.isManager()) {
            Employee currentUser = SessionManager.getCurrentUser();
            return results.stream()
                    .filter(e -> e.getRole().equals("CASHIER"))
                    .filter(e -> e.getBranchId() == currentUser.getBranchId())
                    .collect(Collectors.toList());
        }
        
        return List.of();
    }

    public List<Employee> getEmployeesByRole(String role) {
        PermissionManager.requireManagerOrAbove("view employees by role");

        List<Employee> allEmployees = employeeDAO.getAll();
        return allEmployees.stream()
                .filter(e -> e.getRole().equals(role))
                .collect(Collectors.toList());
    }

    public List<Employee> getEmployeesByBranch(int branchId) {
        PermissionManager.requireManagerOrAbove("view branch employees");

        // Ensure user can access this branch
        PermissionManager.requireBranchAccess(branchId, "view employees");

        List<Employee> allEmployees = employeeDAO.getAll();
        return allEmployees.stream()
                .filter(e -> e.getBranchId() == branchId)
                .collect(Collectors.toList());
    }

    private void validateBranchAssignment(Employee employee) {
        Role role = Role.fromString(employee.getRole());
        
        if (role == Role.ADMIN) {
            if (employee.getBranchId() != 0 && employee.getBranchId() != -1) {
                throw new IllegalArgumentException(
                    "ADMIN users should not be assigned to a specific branch"
                );
            }
        } else {
            if (employee.getBranchId() <= 0) {
                throw new IllegalArgumentException(
                    role + " must be assigned to a valid branch"
                );
            }
        }
    }

    private Employee getEmployeeById(int employeeId) {
        List<Employee> allEmployees = employeeDAO.getAll();
        return allEmployees.stream()
                .filter(e -> e.getEmployeeId() == employeeId)
                .findFirst()
                .orElse(null);
    }

    public boolean isUsernameExists(String username) {
        return employeeDAO.isUsernameExists(username);
    }
}