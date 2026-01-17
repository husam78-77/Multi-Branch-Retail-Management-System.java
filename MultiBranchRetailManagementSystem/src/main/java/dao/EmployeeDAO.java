package dao;

import database.DBConnection;
import model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    /* ===================== LOGIN ===================== */
    public Employee login(String username, String password) {

        String sql = """
            SELECT e.*
            FROM employees e
            JOIN branches b ON e.branch_id = b.branch_id
            WHERE e.username = ?
              AND e.password = ?
              AND e.is_deleted = false
              AND b.is_deleted = false
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapEmployee(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /* ===================== GET ALL (NON-ADMIN) ===================== */
    public List<Employee> getAll() {
        List<Employee> employees = new ArrayList<>();

        String sql = """
            SELECT *
            FROM employees
            WHERE role != 'ADMIN'
            ORDER BY employee_id
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                employees.add(mapEmployee(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }

    /* ===================== INSERT ===================== */
    public boolean insert(Employee employee) {

        if ("ADMIN".equals(employee.getRole())) {
            employee.setBranchId(1);
        }

        String sql = """
            INSERT INTO employees (full_name, username, password, role, branch_id)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employee.getFullName());
            stmt.setString(2, employee.getUsername());
            stmt.setString(3, employee.getPassword());
            stmt.setString(4, employee.getRole());
            stmt.setInt(5, employee.getBranchId());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== UPDATE ===================== */
    public boolean update(Employee employee) {

        if ("ADMIN".equals(employee.getRole()) && employee.getBranchId() != 1) {
            System.out.println("❌ Admin must stay in Main Branch");
            return false;
        }

        String sql = """
            UPDATE employees
            SET full_name = ?,
                username = ?,
                role = ?,
                branch_id = ?
            WHERE employee_id = ?
              AND is_deleted = false
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employee.getFullName());
            stmt.setString(2, employee.getUsername());
            stmt.setString(3, employee.getRole());
            stmt.setInt(4, employee.getBranchId());
            stmt.setInt(5, employee.getEmployeeId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

/* ===================== TOGGLE ACTIVE / DEACTIVE ===================== */
public boolean toggleEmployeeStatus(int employeeId) {

    String checkSql = "SELECT role, is_deleted FROM employees WHERE employee_id = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

        checkStmt.setInt(1, employeeId);
        ResultSet rs = checkStmt.executeQuery();

        if (!rs.next()) {
            return false;
        }

        if ("ADMIN".equals(rs.getString("role"))) {
            System.out.println("❌ Admin cannot be deactivated");
            return false;
        }

        boolean currentStatus = rs.getBoolean("is_deleted");
        boolean newStatus = !currentStatus; // toggle

        String updateSql = "UPDATE employees SET is_deleted = ? WHERE employee_id = ?";
        PreparedStatement updateStmt = conn.prepareStatement(updateSql);
        updateStmt.setBoolean(1, newStatus);
        updateStmt.setInt(2, employeeId);

        return updateStmt.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    /* ===================== SEARCH ===================== */
    public List<Employee> searchByName(String keyword) {

        List<Employee> employees = new ArrayList<>();

        String sql = """
            SELECT *
            FROM employees
            WHERE role != 'ADMIN'
              AND full_name ILIKE ?
            ORDER BY employee_id
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                employees.add(mapEmployee(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }

    /* ===================== USERNAME CHECK ===================== */
    public boolean isUsernameExists(String username) {

        String sql = "SELECT COUNT(*) FROM employees WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /* ===================== GET BY ID ===================== */
    public Employee getById(int employeeId) {

        String sql = """
            SELECT *
            FROM employees
            WHERE employee_id = ?
              AND is_deleted = false
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapEmployee(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /* ===================== MAPPER ===================== */
    private Employee mapEmployee(ResultSet rs) throws SQLException {
        return new Employee(
                rs.getInt("employee_id"),
                rs.getString("full_name"),
                rs.getString("username"),
                rs.getString("role"),
                rs.getInt("branch_id"),
                rs.getBoolean("is_deleted")
        );
    }
}
