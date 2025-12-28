package dao;

import database.DBConnection;

import java.sql.*;

public class SaleDAO {

    public int createSale(int branchId, int employeeId) {

        String validateSql = """
            SELECT 1
            FROM employees e
            JOIN branches b ON e.branch_id = b.branch_id
            WHERE e.employee_id = ?
              AND e.branch_id = ?
              AND e.is_deleted = false
              AND b.is_deleted = false
        """;

        String insertSaleSql = """
            INSERT INTO sales (branch_id, employee_id, total_amount)
            VALUES (?, ?, 0)
            RETURNING sale_id
        """;

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false); 

            try (PreparedStatement validateStmt = conn.prepareStatement(validateSql)) {
                validateStmt.setInt(1, employeeId);
                validateStmt.setInt(2, branchId);
                ResultSet rs = validateStmt.executeQuery();

                if (!rs.next()) {
                    conn.rollback();
                    System.out.println("❌ Invalid sale (branch/employee)");
                    return -1;
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(insertSaleSql)) {
                stmt.setInt(1, branchId);
                stmt.setInt(2, employeeId);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    conn.commit();
                    return rs.getInt("sale_id");
                }
            }

            conn.rollback();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public boolean updateTotal(int saleId, double total) {

        String sql = """
            UPDATE sales
            SET total_amount = ?
            WHERE sale_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, total);
            stmt.setInt(2, saleId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
