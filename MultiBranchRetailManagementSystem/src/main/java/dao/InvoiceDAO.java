package dao;

import database.DBConnection;
import model.InvoiceHeader;
import model.InvoiceItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {
    public InvoiceHeader getInvoiceHeader(int saleId) {

        String sql = """
            SELECT 
                s.sale_id,
                s.sale_date,
                s.total_amount,
                c.full_name AS customer_name,
                c.phone,
                e.full_name AS employee_name,
                b.branch_name
            FROM sales s
            JOIN customers c ON s.customer_id = c.customer_id
            JOIN employees e ON s.employee_id = e.employee_id
            JOIN branches b ON s.branch_id = b.branch_id
            WHERE s.sale_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, saleId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new InvoiceHeader(
                        rs.getInt("sale_id"),
                        rs.getTimestamp("sale_date").toString(),
                        rs.getString("customer_name"),
                        rs.getString("phone"),
                        rs.getString("employee_name"),
                        rs.getString("branch_name"),
                        rs.getDouble("total_amount")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<InvoiceItem> getInvoiceItems(int saleId) {

        List<InvoiceItem> items = new ArrayList<>();

        String sql = """
            SELECT 
                p.product_name,
                d.quantity,
                d.price,
                d.subtotal
            FROM sale_details d
            JOIN products p ON d.product_id = p.product_id
            WHERE d.sale_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, saleId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                items.add(new InvoiceItem(
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getDouble("subtotal")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }
}
