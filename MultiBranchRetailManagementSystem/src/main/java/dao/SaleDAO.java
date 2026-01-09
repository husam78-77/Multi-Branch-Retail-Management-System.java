package dao;

import database.DBConnection;

import java.sql.*;

public class SaleDAO {

	public int createSale(Connection conn, int branchId, int employeeId) throws SQLException {
	
	    String insertSaleSql = """
	        INSERT INTO sales (branch_id, employee_id, total_amount)
	        VALUES (?, ?, 0)
	        RETURNING sale_id
	    """;
	
	    try (PreparedStatement stmt = conn.prepareStatement(insertSaleSql)) {
	        stmt.setInt(1, branchId);
	        stmt.setInt(2, employeeId);
	
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            return rs.getInt("sale_id");
	        }
	    }
	
	    return -1;
	}
	public boolean updateTotal(Connection conn, int saleId, double total) throws SQLException {
	
	    String sql = """
	        UPDATE sales
	        SET total_amount = ?
	        WHERE sale_id = ?
	    """;
	
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setDouble(1, total);
	        stmt.setInt(2, saleId);
	        return stmt.executeUpdate() > 0;
	    }
	}

}
