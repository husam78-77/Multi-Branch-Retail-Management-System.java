package dao;

import database.DBConnection;
import model.SaleItem;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class SaleDetailDAO {

    public void insertDetail(int saleId, SaleItem item) {

        String sql = """
            INSERT INTO sale_details
            (sale_id, product_id, quantity, price, subtotal)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, saleId);
            stmt.setInt(2, item.getProduct().getProductId());
            stmt.setInt(3, item.getQuantity());
            stmt.setDouble(4, item.getPrice());
            stmt.setDouble(5, item.getSubtotal());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
