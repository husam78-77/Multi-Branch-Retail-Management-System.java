package dao;

import database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SaleDAO {

    public int insertSale(int branchId, int employeeId, double total) {

        String sql = """
            INSERT INTO sales (branch_id, employee_id, total_amount)
            VALUES (?, ?, ?)
            RETURNING sale_id
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, branchId);
            stmt.setInt(2, employeeId);
            stmt.setDouble(3, total);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("sale_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}
