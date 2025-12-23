package model;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

/**
 * Model class for audit log entries
 */
public class AuditLog {

    private int logId;
    private int employeeId;
    private String employeeName;
    private String employeeRole;
    private String action;
    private Timestamp actionTime;

    public AuditLog(int logId, int employeeId, String employeeName,
                    String employeeRole, String action, Timestamp actionTime) {
        this.logId = logId;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.employeeRole = employeeRole;
        this.action = action;
        this.actionTime = actionTime;
    }

    // Getters
    public int getLogId() {
        return logId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getEmployeeRole() {
        return employeeRole;
    }

    public String getAction() {
        return action;
    }

    public Timestamp getActionTime() {
        return actionTime;
    }

    /**
     * Get formatted action time
     */
    public String getFormattedActionTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return actionTime.toLocalDateTime().format(formatter);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s): %s",
                getFormattedActionTime(), employeeName, employeeRole, action);
    }
}