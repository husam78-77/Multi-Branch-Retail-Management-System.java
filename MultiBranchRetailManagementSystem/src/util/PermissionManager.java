package util;

import model.Employee;

/**
 * Central permission management class.
 * Handles all role-based access control logic.
 */
public class PermissionManager {

    /**
     * Check if current user is ADMIN
     */
    public static boolean isAdmin() {
        Employee user = SessionManager.getCurrentUser();
        return user != null && Role.fromString(user.getRole()) == Role.ADMIN;
    }

    /**
     * Check if current user is MANAGER
     */
    public static boolean isManager() {
        Employee user = SessionManager.getCurrentUser();
        return user != null && Role.fromString(user.getRole()) == Role.MANAGER;
    }

    /**
     * Check if current user is CASHIER
     */
    public static boolean isCashier() {
        Employee user = SessionManager.getCurrentUser();
        return user != null && Role.fromString(user.getRole()) == Role.CASHIER;
    }

    /**
     * Check if user has at least the specified role level
     */
    public static boolean hasRole(Role requiredRole) {
        Employee user = SessionManager.getCurrentUser();
        if (user == null) {
            return false;
        }
        Role userRole = Role.fromString(user.getRole());
        return userRole.hasAuthorityOver(requiredRole);
    }

    /**
     * Require ADMIN role or throw exception
     */
    public static void requireAdmin(String action) {
        if (!isAdmin()) {
            Employee user = SessionManager.getCurrentUser();
            throw AccessDeniedException.forRole(
                action, 
                Role.ADMIN, 
                Role.fromString(user.getRole())
            );
        }
    }

    /**
     * Require ADMIN or MANAGER role or throw exception
     */
    public static void requireManagerOrAbove(String action) {
        if (!isAdmin() && !isManager()) {
            Employee user = SessionManager.getCurrentUser();
            throw AccessDeniedException.forRole(
                action, 
                Role.MANAGER, 
                Role.fromString(user.getRole())
            );
        }
    }

    // ========== BRANCH-SPECIFIC PERMISSIONS ==========

    /**
     * Check if user can access a specific branch
     * - ADMIN can access all branches
     * - MANAGER/CASHIER can only access their own branch
     */
    public static boolean canAccessBranch(int branchId) {
        Employee user = SessionManager.getCurrentUser();
        if (user == null) {
            return false;
        }

        // ADMIN can access all branches
        if (isAdmin()) {
            return true;
        }

        // MANAGER and CASHIER can only access their own branch
        return user.getBranchId() == branchId;
    }

    /**
     * Require access to specific branch or throw exception
     */
    public static void requireBranchAccess(int branchId, String action) {
        Employee user = SessionManager.getCurrentUser();
        
        if (!canAccessBranch(branchId)) {
            throw AccessDeniedException.forBranch(action, user.getBranchId(), branchId);
        }
    }

    /**
     * Get the branch ID filter for current user
     * - ADMIN: returns null (no filter, see all branches)
     * - MANAGER/CASHIER: returns their branch ID
     */
    public static Integer getBranchFilter() {
        if (isAdmin()) {
            return null; // No filter for admin
        }
        
        Employee user = SessionManager.getCurrentUser();
        return user.getBranchId();
    }

    // ========== FEATURE-SPECIFIC PERMISSIONS ==========

    /**
     * Check if user can manage branches
     * Only ADMIN can manage branches
     */
    public static boolean canManageBranches() {
        return isAdmin();
    }

    /**
     * Check if user can manage products
     * ADMIN and MANAGER can manage products
     */
    public static boolean canManageProducts() {
        return isAdmin() || isManager();
    }

    /**
     * Check if user can perform sales
     * All roles can perform sales
     */
    public static boolean canPerformSales() {
        return SessionManager.isLoggedIn();
    }

    /**
     * Check if user can view reports
     * ADMIN and MANAGER can view reports
     */
    public static boolean canViewReports() {
        return isAdmin() || isManager();
    }

    /**
     * Check if user can view charts
     * ADMIN and MANAGER can view charts
     */
    public static boolean canViewCharts() {
        return isAdmin() || isManager();
    }

    /**
     * Check if user can manage employees
     * - ADMIN can manage all employees (ADMIN, MANAGER, CASHIER)
     * - MANAGER can only manage CASHIERS in their branch
     */
    public static boolean canManageEmployees() {
        return isAdmin() || isManager();
    }

    /**
     * Check if user can manage a specific employee role
     * - ADMIN can manage ADMIN, MANAGER, CASHIER
     * - MANAGER can only manage CASHIER
     */
    public static boolean canManageRole(Role targetRole) {
        if (isAdmin()) {
            return true; // Admin can manage all roles
        }
        
        if (isManager()) {
            return targetRole == Role.CASHIER; // Manager can only manage cashiers
        }
        
        return false; // Cashiers cannot manage anyone
    }

    /**
     * Require permission to manage specific role
     */
    public static void requireRoleManagementPermission(Role targetRole, String action) {
        if (!canManageRole(targetRole)) {
            throw AccessDeniedException.forPermission(
                action + " " + targetRole + " employees"
            );
        }
    }

    // ========== UI PERMISSION HELPERS ==========

    /**
     * Get user-friendly permission denial message
     */
    public static String getPermissionDeniedMessage(String feature) {
        Employee user = SessionManager.getCurrentUser();
        return String.format(
            "Access Denied\n\nYou (%s - %s) don't have permission to access: %s",
            user.getFullName(),
            user.getRole(),
            feature
        );
    }
}