package service;

import dao.AuditLogDAO;
import model.Employee;
import util.SessionManager;

public class AuditService {

    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    private void logInternal(String action) {
        Employee currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            auditLogDAO.insert(currentUser.getEmployeeId(), action);
        }
    }

    public void log(String action) {
        logInternal(action);
    }

    public void logWithDetails(String actionType, String details) {
        logInternal(actionType + ": " + details);
    }

    public void logLogin() {
        logInternal("LOGIN - User logged in");
    }

    public void logLogout() {
        logInternal("LOGOUT - User logged out");
    }

    public void logProductAdd(String productName, int branchId) {
        logInternal("PRODUCT_ADD - Added product '" + productName + "' to Branch " + branchId);
    }

    public void logProductUpdate(String productName, int branchId) {
        logInternal("PRODUCT_UPDATE - Updated product '" + productName + "' in Branch " + branchId);
    }

    public void logProductDelete(String productName, int branchId) {
        logInternal("PRODUCT_DELETE - Deleted product '" + productName + "' from Branch " + branchId);
    }

    public void logBranchAdd(String branchName) {
        logInternal("BRANCH_ADD - Created new branch '" + branchName + "'");
    }

    public void logBranchUpdate(String branchName) {
        logInternal("BRANCH_UPDATE - Updated branch '" + branchName + "'");
    }

    public void logBranchDelete(String branchName) {
        logInternal("BRANCH_DELETE - Deleted branch '" + branchName + "'");
    }

    public void logEmployeeAdd(String fullName, String role, int branchId) {
        logInternal("EMPLOYEE_ADD - Added " + role + " employee '" + fullName + "' to Branch " + branchId);
    }

    public void logEmployeeUpdate(String fullName, String role) {
        logInternal("EMPLOYEE_UPDATE - Updated " + role + " employee '" + fullName + "'");
    }

    public void logEmployeeDelete(String fullName, String role) {
        logInternal("EMPLOYEE_DELETE - Deleted " + role + " employee '" + fullName + "'");
    }

    public void logSale(int saleId, double totalAmount, int branchId) {
        logInternal(
            "SALE - Processed sale #" + saleId +
            " for $" + String.format("%.2f", totalAmount) +
            " at Branch " + branchId
        );
    }

    public void logReportGenerated(String reportType) {
        logInternal("REPORT_GENERATED - Generated " + reportType + " report");
    }

    public void logReportDownloaded(String reportType) {
        logInternal("REPORT_DOWNLOADED - Downloaded " + reportType + " report as PDF");
    }

    public void logAccessDenied(String attemptedAction) {
        logInternal("ACCESS_DENIED - Attempted unauthorized action: " + attemptedAction);
    }
    public void logEmployeeStatusChange(String fullName, String role, String action) {
        logInternal(
            "EMPLOYEE_STATUS_CHANGE - " +
            action + " " + role + " employee '" + fullName + "'"
        );
    }


}