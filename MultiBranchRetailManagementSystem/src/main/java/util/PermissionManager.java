package util;

import model.Employee;
public class PermissionManager {
    public static boolean isAdmin() {
        Employee user = SessionManager.getCurrentUser();
        return user != null && Role.fromString(user.getRole()) == Role.ADMIN;
    }
    public static boolean isManager() {
        Employee user = SessionManager.getCurrentUser();
        return user != null && Role.fromString(user.getRole()) == Role.MANAGER;
    }

    public static boolean isCashier() {
        Employee user = SessionManager.getCurrentUser();
        return user != null && Role.fromString(user.getRole()) == Role.CASHIER;
    }

    public static boolean hasRole(Role requiredRole) {
        Employee user = SessionManager.getCurrentUser();
        if (user == null) {
            return false;
        }
        Role userRole = Role.fromString(user.getRole());
        return userRole.hasAuthorityOver(requiredRole);
    }

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

    public static boolean canAccessBranch(int branchId) {
        Employee user = SessionManager.getCurrentUser();
        if (user == null) {
            return false;
        }

        if (isAdmin()) {
            return true;
        }

        return user.getBranchId() == branchId;
    }

    public static void requireBranchAccess(int branchId, String action) {
        Employee user = SessionManager.getCurrentUser();
        
        if (!canAccessBranch(branchId)) {
            throw AccessDeniedException.forBranch(action, user.getBranchId(), branchId);
        }
    }

    public static Integer getBranchFilter() {
        if (isAdmin()) {
            return null; 
        }
        
        Employee user = SessionManager.getCurrentUser();
        return user.getBranchId();
    }

    public static boolean canManageBranches() {
        return isAdmin();
    }

    public static boolean canManageProducts() {
        return isManager();
    }
    public static boolean canPerformSales() {
        return isCashier();
    }
    public static boolean canViewReports() {
        return isAdmin() || isManager();
    }
    public static boolean canViewCharts() {
        return isAdmin() || isManager();
    }
    public static boolean canManageEmployees() {
        return isAdmin() || isManager();
    }

    public static boolean canManageRole(Role targetRole) {
        if (isAdmin()) {
            return targetRole == Role.MANAGER;
        }
        
        if (isManager()) {
            return targetRole == Role.CASHIER;
        }
        
        return false; 
    }

    public static void requireRoleManagementPermission(Role targetRole, String action) {
        if (!canManageRole(targetRole)) {
            throw AccessDeniedException.forPermission(
                action + " " + targetRole + " employees"
            );
        }
    }

    public static boolean canViewAuditLogs() {
        return isAdmin() || isManager();
    }

    public static String getPermissionDeniedMessage(String feature) {
        Employee user = SessionManager.getCurrentUser();
        return String.format(
            "Access Denied\n\nYou (%s - %s) don't have permission to access: %s",
            user.getFullName(),
            user.getRole(),
            feature
        );
    }


    public static String getRoleDashboardMessage() {
        if (isAdmin()) {
            return "System Administrator - Manage branches, managers, and oversee operations";
        } else if (isManager()) {
            return "Branch Manager - Manage products, cashiers, and monitor branch performance";
        } else if (isCashier()) {
            return "Cashier - Process sales and transactions";
        }
        return "";
    }
}