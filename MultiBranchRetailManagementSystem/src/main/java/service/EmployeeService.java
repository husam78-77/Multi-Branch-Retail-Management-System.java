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
        if (!PermissionManager.canManageEmployees()) {
            throw AccessDeniedException.forPermission("view employees");
        }

        List<Employee> allEmployees = employeeDAO.getAll();
        
        if (PermissionManager.isAdmin()) {
            return allEmployees.stream()
                    .filter(e -> e.getRole().equals("MANAGER"))
                    .collect(Collectors.toList());
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
        if (!PermissionManager.canManageEmployees()) {
            throw AccessDeniedException.forPermission("add employee");
        }

        Role targetRole = Role.fromString(employee.getRole());
        
        if (targetRole == Role.ADMIN) {
            throw new IllegalArgumentException("Cannot create ADMIN users through the application");
        }
        
        PermissionManager.requireRoleManagementPermission(targetRole, "add");

        Employee currentUser = SessionManager.getCurrentUser();
        
        if (PermissionManager.isAdmin()) {
            if (targetRole != Role.MANAGER) {
                throw new IllegalArgumentException("ADMIN can only add MANAGER employees");
            }
            
            if (employee.getBranchId() <= 0) {
                throw new IllegalArgumentException("MANAGER must be assigned to a branch");
            }
        }
        
        if (PermissionManager.isManager()) {
            if (targetRole != Role.CASHIER) {
                throw new IllegalArgumentException("MANAGER can only add CASHIER employees");
            }
            
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
        if (!PermissionManager.canManageEmployees()) {
            throw AccessDeniedException.forPermission("update employee");
        }

        Role targetRole = Role.fromString(employee.getRole());
        
        if (targetRole == Role.ADMIN) {
            throw new IllegalArgumentException("Cannot modify ADMIN users through the application");
        }
        
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

    /**
     * Delete an employee (soft delete)
     * - ADMIN can delete MANAGERS
     * - MANAGER can delete CASHIERS in their branch
     */
    public void deleteEmployee(int employeeId) {
        if (!PermissionManager.canManageEmployees()) {
            throw AccessDeniedException.forPermission("delete employee");
        }

        // Get the employee to check their role and branch
        Employee employee = getEmployeeById(employeeId);
        if (employee == null) {
            throw new IllegalArgumentException("Employee not found");
        }

        Role targetRole = Role.fromString(employee.getRole());
        
        // Cannot delete ADMIN
        if (targetRole == Role.ADMIN) {
            throw new IllegalArgumentException("Cannot delete ADMIN users through the application");
        }
        
        // Check if user can manage this role
        PermissionManager.requireRoleManagementPermission(targetRole, "delete");

        Employee currentUser = SessionManager.getCurrentUser();
        
        if (PermissionManager.isManager()) {
            // MANAGER can only delete CASHIERS in their branch
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

    /**
     * Search employees by name with role-based filtering
     */
    public List<Employee> searchEmployees(String keyword) {
        if (!PermissionManager.canManageEmployees()) {
            throw AccessDeniedException.forPermission("search employees");
        }

        List<Employee> results = employeeDAO.searchByName(keyword);
        
        if (PermissionManager.isAdmin()) {
            // ADMIN sees only MANAGERS
            return results.stream()
                    .filter(e -> e.getRole().equals("MANAGER"))
                    .collect(Collectors.toList());
        }
        
        if (PermissionManager.isManager()) {
            // MANAGER sees only CASHIERS in their branch
            Employee currentUser = SessionManager.getCurrentUser();
            return results.stream()
                    .filter(e -> e.getRole().equals("CASHIER"))
                    .filter(e -> e.getBranchId() == currentUser.getBranchId())
                    .collect(Collectors.toList());
        }
        
        return List.of();
    }

    /**
     * Get employees by role (helper for UI dropdowns)
     */
    public List<Employee> getEmployeesByRole(String role) {
        if (!PermissionManager.canManageEmployees()) {
            throw AccessDeniedException.forPermission("view employees by role");
        }

        List<Employee> allEmployees = employeeDAO.getAll();
        return allEmployees.stream()
                .filter(e -> e.getRole().equals(role))
                .collect(Collectors.toList());
    }

    /**
     * Get employees for a specific branch
     */
    public List<Employee> getEmployeesByBranch(int branchId) {
        if (!PermissionManager.canManageEmployees()) {
            throw AccessDeniedException.forPermission("view branch employees");
        }

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
        return employeeDAO.getById(employeeId);
    }
    public boolean isUsernameExists(String username) {
        return employeeDAO.isUsernameExists(username);
    }
}