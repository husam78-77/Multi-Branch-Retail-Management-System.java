package dao;

import model.SaleItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SaleDetailDAO {

    public double insertDetail(Connection conn, int saleId, SaleItem item) throws SQLException {

        String checkSql = """
            SELECT quantity
            FROM products
            WHERE product_id = ?
              AND is_deleted = false
        """;

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, item.getProductId());
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                throw new SQLException("Product not found or deleted");
            }

            int availableQty = rs.getInt("quantity");
            if (availableQty < item.getQuantity()) {
                throw new SQLException("Insufficient stock");
            }
        }

        String insertSql = """
            INSERT INTO sale_details
            (sale_id, product_id, quantity, price, subtotal)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setInt(1, saleId);
            stmt.setInt(2, item.getProductId());
            stmt.setInt(3, item.getQuantity());
            stmt.setDouble(4, item.getPrice());
            stmt.setDouble(5, item.getSubtotal());
            stmt.executeUpdate();
        }

        String updateStock = """
            UPDATE products
            SET quantity = quantity - ?
            WHERE product_id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(updateStock)) {
            stmt.setInt(1, item.getQuantity());
            stmt.setInt(2, item.getProductId());
            stmt.executeUpdate();
        }

        return item.getSubtotal();
    }
}
