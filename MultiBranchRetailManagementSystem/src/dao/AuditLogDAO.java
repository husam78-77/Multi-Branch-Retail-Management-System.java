package dao;

import database.DBConnection;
import model.AuditLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for audit_logs table operations
 */
public class AuditLogDAO {

    /**
     * Insert a new audit log entry
     */
    public void insert(int employeeId, String action) {
        String sql = """
            INSERT INTO audit_logs (employee_id, action)
            VALUES (?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);
            stmt.setString(2, action);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Failed to insert audit log: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get all audit logs (for ADMIN)
     */
    public List<AuditLog> getAll() {
        List<AuditLog> logs = new ArrayList<>();

        String sql = """
            SELECT al.log_id, al.employee_id, e.full_name, e.role, 
                   al.action, al.action_time
            FROM audit_logs al
            JOIN employees e ON al.employee_id = e.employee_id
            ORDER BY al.action_time DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                AuditLog log = new AuditLog(
                        rs.getInt("log_id"),
                        rs.getInt("employee_id"),
                        rs.getString("full_name"),
                        rs.getString("role"),
                        rs.getString("action"),
                        rs.getTimestamp("action_time")
                );
                logs.add(log);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logs;
    }

    /**
     * Get audit logs for a specific employee
     */
    public List<AuditLog> getByEmployeeId(int employeeId) {
        List<AuditLog> logs = new ArrayList<>();

        String sql = """
            SELECT al.log_id, al.employee_id, e.full_name, e.role, 
                   al.action, al.action_time
            FROM audit_logs al
            JOIN employees e ON al.employee_id = e.employee_id
            WHERE al.employee_id = ?
            ORDER BY al.action_time DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                AuditLog log = new AuditLog(
                        rs.getInt("log_id"),
                        rs.getInt("employee_id"),
                        rs.getString("full_name"),
                        rs.getString("role"),
                        rs.getString("action"),
                        rs.getTimestamp("action_time")
                );
                logs.add(log);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logs;
    }

    /**
     * Get audit logs for a specific branch (via employee's branch)
     */
    public List<AuditLog> getByBranchId(int branchId) {
        List<AuditLog> logs = new ArrayList<>();

        String sql = """
            SELECT al.log_id, al.employee_id, e.full_name, e.role, 
                   al.action, al.action_time
            FROM audit_logs al
            JOIN employees e ON al.employee_id = e.employee_id
            WHERE e.branch_id = ?
            ORDER BY al.action_time DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                AuditLog log = new AuditLog(
                        rs.getInt("log_id"),
                        rs.getInt("employee_id"),
                        rs.getString("full_name"),
                        rs.getString("role"),
                        rs.getString("action"),
                        rs.getTimestamp("action_time")
                );
                logs.add(log);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logs;
    }

    /**
     * Search audit logs by action keyword
     */
    public List<AuditLog> searchByAction(String keyword) {
        List<AuditLog> logs = new ArrayList<>();

        String sql = """
            SELECT al.log_id, al.employee_id, e.full_name, e.role, 
                   al.action, al.action_time
            FROM audit_logs al
            JOIN employees e ON al.employee_id = e.employee_id
            WHERE al.action ILIKE ?
            ORDER BY al.action_time DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                AuditLog log = new AuditLog(
                        rs.getInt("log_id"),
                        rs.getInt("employee_id"),
                        rs.getString("full_name"),
                        rs.getString("role"),
                        rs.getString("action"),
                        rs.getTimestamp("action_time")
                );
                logs.add(log);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logs;
    }

    /**
     * Get recent audit logs (last N entries)
     */
    public List<AuditLog> getRecent(int limit) {
        List<AuditLog> logs = new ArrayList<>();

        String sql = """
            SELECT al.log_id, al.employee_id, e.full_name, e.role, 
                   al.action, al.action_time
            FROM audit_logs al
            JOIN employees e ON al.employee_id = e.employee_id
            ORDER BY al.action_time DESC
            LIMIT ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                AuditLog log = new AuditLog(
                        rs.getInt("log_id"),
                        rs.getInt("employee_id"),
                        rs.getString("full_name"),
                        rs.getString("role"),
                        rs.getString("action"),
                        rs.getTimestamp("action_time")
                );
                logs.add(log);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logs;
    }
}