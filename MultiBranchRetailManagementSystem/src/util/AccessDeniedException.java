package util;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create exception for unauthorized role access
     */
    public static AccessDeniedException forRole(String action, Role requiredRole, Role actualRole) {
        return new AccessDeniedException(
            String.format("Access Denied: '%s' requires %s role. Current role: %s",
                action, requiredRole, actualRole)
        );
    }

    /**
     * Create exception for branch access violation
     */
    public static AccessDeniedException forBranch(String action, int userBranchId, int targetBranchId) {
        return new AccessDeniedException(
            String.format("Access Denied: You can only %s for your branch (ID: %d). Attempted branch: %d",
                action, userBranchId, targetBranchId)
        );
    }

    /**
     * Create exception for general permission violation
     */
    public static AccessDeniedException forPermission(String action) {
        return new AccessDeniedException(
            String.format("Access Denied: You don't have permission to %s", action)
        );
    }
}