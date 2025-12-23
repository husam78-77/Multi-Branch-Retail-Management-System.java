package dao;

import database.DBConnection;
import model.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
}
