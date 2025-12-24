package dao;

import database.DBConnection;
import model.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public Employee login(String username, String password) {
        String sql = """
            SELECT * FROM employees
            WHERE username = ?
              AND password = ?
              AND is_deleted = false
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("full_name"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getInt("branch_id"),
                        rs.getBoolean("is_deleted")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Employee> getAll() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees WHERE is_deleted = false ORDER BY employee_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Employee employee = new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("full_name"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getInt("branch_id"),
                        rs.getBoolean("is_deleted")
                );
                employees.add(employee);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }

    public void insert(Employee employee) {
        String sql = """
            INSERT INTO employees (full_name, username, password, role, branch_id)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employee.getFullName());
            stmt.setString(2, employee.getUsername());
            stmt.setString(3, "password123");
            stmt.setString(4, employee.getRole());
            
            if (employee.getBranchId() <= 0) {
                stmt.setNull(5, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(5, employee.getBranchId());
            }

            stmt.executeUpdate();
            System.out.println("✅ Employee inserted successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Employee employee) {
        String sql = """
            UPDATE employees
            SET full_name = ?,
                username = ?,
                role = ?,
                branch_id = ?
            WHERE employee_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employee.getFullName());
            stmt.setString(2, employee.getUsername());
            stmt.setString(3, employee.getRole());
            
            if (employee.getBranchId() <= 0) {
                stmt.setNull(4, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(4, employee.getBranchId());
            }
            
            stmt.setInt(5, employee.getEmployeeId());

            stmt.executeUpdate();
            System.out.println("✏️ Employee updated successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void softDelete(int employeeId) {
        String sql = "UPDATE employees SET is_deleted = true WHERE employee_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);
            stmt.executeUpdate();
            System.out.println("🗑️ Employee soft deleted");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Employee> searchByName(String keyword) {
        List<Employee> employees = new ArrayList<>();

        String sql = """
            SELECT * FROM employees
            WHERE is_deleted = false
            AND full_name ILIKE ?
            ORDER BY employee_id
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Employee employee = new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("full_name"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getInt("branch_id"),
                        rs.getBoolean("is_deleted")
                );
                employees.add(employee);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }

    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM employees WHERE username = ? AND is_deleted = false";

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

    public Employee getById(int employeeId) {
        String sql = "SELECT * FROM employees WHERE employee_id = ? AND is_deleted = false";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("full_name"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getInt("branch_id"),
                        rs.getBoolean("is_deleted")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}