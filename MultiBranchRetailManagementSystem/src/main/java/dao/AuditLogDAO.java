package dao;

import database.DBConnection;
import model.AuditLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAO {

    public boolean insert(int employeeId, String action) {

        String checkSql = """
            SELECT 1
            FROM employees
            WHERE employee_id = ?
        """;

        String insertSql = """
            INSERT INTO audit_logs (employee_id, action)
            VALUES (?, ?)
        """;

        try (Connection conn = DBConnection.getConnection()) {

            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, employeeId);
                ResultSet rs = checkStmt.executeQuery();

                if (!rs.next()) {
                    System.out.println("❌ Audit log failed: employee not found");
                    return false;
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setInt(1, employeeId);
                stmt.setString(2, action);
                stmt.executeUpdate();
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static final String BASE_QUERY = """
        SELECT al.log_id,
               al.employee_id,
               e.full_name,
               e.role,
               al.action,
               al.action_time
        FROM audit_logs al
        JOIN employees e ON al.employee_id = e.employee_id
    """;

    public List<AuditLog> getAll() {

        List<AuditLog> logs = new ArrayList<>();

        String sql = BASE_QUERY + " ORDER BY al.action_time DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                logs.add(mapAuditLog(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logs;
    }
    public List<AuditLog> getByEmployeeId(int employeeId) {

        List<AuditLog> logs = new ArrayList<>();

        String sql = BASE_QUERY + """
            WHERE al.employee_id = ?
            ORDER BY al.action_time DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                logs.add(mapAuditLog(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logs;
    }
    public List<AuditLog> getByBranchId(int branchId) {

        List<AuditLog> logs = new ArrayList<>();

        String sql = BASE_QUERY + """
            WHERE e.branch_id = ?
            ORDER BY al.action_time DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                logs.add(mapAuditLog(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logs;
    }

    public List<AuditLog> searchByAction(String keyword) {

        List<AuditLog> logs = new ArrayList<>();

        String sql = BASE_QUERY + """
            WHERE al.action ILIKE ?
            ORDER BY al.action_time DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                logs.add(mapAuditLog(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logs;
    }
    public List<AuditLog> getRecent(int limit) {

        List<AuditLog> logs = new ArrayList<>();

        String sql = BASE_QUERY + """
            ORDER BY al.action_time DESC
            LIMIT ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                logs.add(mapAuditLog(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logs;
    }

    private AuditLog mapAuditLog(ResultSet rs) throws SQLException {

        return new AuditLog(
                rs.getInt("log_id"),
                rs.getInt("employee_id"),
                rs.getString("full_name"),
                rs.getString("role"),
                rs.getString("action"),
                rs.getTimestamp("action_time")
        );
    }
}
