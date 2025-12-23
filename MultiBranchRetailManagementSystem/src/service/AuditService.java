package service;

import dao.AuditLogDAO;
import model.Employee;
import util.SessionManager;

/**
 * Service for logging user actions (audit trail).
 * Automatically logs actions with current user info.
 */
public class AuditService {

    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    /**
     * Log an action performed by current user
     */
    public void log(String action) {
        Employee currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            auditLogDAO.insert(currentUser.getEmployeeId(), action);
        }
    }

    /**
     * Log an action with specific details
     */
    public void logWithDetails(String actionType, String details) {
        String action = actionType + ": " + details;
        log(action);
    }

    // ========== Specific Action Loggers ==========

    public void logLogin(String username) {
        log("LOGIN - User '" + username + "' logged in");
    }

    public void logLogout(String username) {
        log("LOGOUT - User '" + username + "' logged out");
    }

    // Products
    public void logProductAdd(String productName, int branchId) {
        log("PRODUCT_ADD - Added product '" + productName + "' to Branch " + branchId);
    }

    public void logProductUpdate(String productName, int branchId) {
        log("PRODUCT_UPDATE - Updated product '" + productName + "' in Branch " + branchId);
    }

    public void logProductDelete(String productName, int branchId) {
        log("PRODUCT_DELETE - Deleted product '" + productName + "' from Branch " + branchId);
    }

    // Branches
    public void logBranchAdd(String branchName) {
        log("BRANCH_ADD - Created new branch '" + branchName + "'");
    }

    public void logBranchUpdate(String branchName) {
        log("BRANCH_UPDATE - Updated branch '" + branchName + "'");
    }

    public void logBranchDelete(String branchName) {
        log("BRANCH_DELETE - Deleted branch '" + branchName + "'");
    }

    // Employees
    public void logEmployeeAdd(String fullName, String role, int branchId) {
        log("EMPLOYEE_ADD - Added " + role + " employee '" + fullName + "' to Branch " + branchId);
    }

    public void logEmployeeUpdate(String fullName, String role) {
        log("EMPLOYEE_UPDATE - Updated " + role + " employee '" + fullName + "'");
    }

    public void logEmployeeDelete(String fullName, String role) {
        log("EMPLOYEE_DELETE - Deleted " + role + " employee '" + fullName + "'");
    }

    // Sales
    public void logSale(int saleId, double totalAmount, int branchId) {
        log("SALE - Processed sale #" + saleId + " for $" + 
            String.format("%.2f", totalAmount) + " at Branch " + branchId);
    }

    // Reports
    public void logReportGenerated(String reportType) {
        log("REPORT_GENERATED - Generated " + reportType + " report");
    }

    public void logReportDownloaded(String reportType) {
        log("REPORT_DOWNLOADED - Downloaded " + reportType + " report as PDF");
    }

    // Access Denied
    public void logAccessDenied(String attemptedAction) {
        log("ACCESS_DENIED - Attempted unauthorized action: " + attemptedAction);
    }
}