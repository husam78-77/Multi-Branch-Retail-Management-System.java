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
            WHERE is_deleted = false
              AND role != 'ADMIN'
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
            stmt.setString(3, "password123"); // default password
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

        // 🔒 Protect Admin
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

    /* ===================== SOFT DELETE ===================== */
    public boolean softDelete(int employeeId) {

        String roleCheck = "SELECT role FROM employees WHERE employee_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(roleCheck)) {

            checkStmt.setInt(1, employeeId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && "ADMIN".equals(rs.getString("role"))) {
                System.out.println("❌ Admin cannot be deleted");
                return false;
            }

            String sql = "UPDATE employees SET is_deleted = true WHERE employee_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, employeeId);

            return stmt.executeUpdate() > 0;

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
            WHERE is_deleted = false
              AND role != 'ADMIN'
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
