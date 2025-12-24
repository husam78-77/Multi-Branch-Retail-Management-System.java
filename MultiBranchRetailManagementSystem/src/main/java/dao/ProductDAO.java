package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import database.DBConnection;
import model.Product;

public class ProductDAO {

    
    public void insert(Product product) {
        String sql = """
            INSERT INTO products
            (product_name, category, price, cost, quantity, branch_id)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getProductName());
            stmt.setString(2, product.getCategory());
            stmt.setDouble(3, product.getPrice());
            stmt.setDouble(4, product.getCost());
            stmt.setInt(5, product.getQuantity());
            stmt.setInt(6, product.getBranchId()); 

            stmt.executeUpdate();
            System.out.println("✅ Product inserted successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Product> getAll() {
        List<Product> products = new ArrayList<>();

        String sql = "SELECT * FROM products WHERE is_deleted = false";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_id"),    
                        rs.getString("product_name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getDouble("cost"),
                        rs.getInt("quantity"),
                        rs.getInt("branch_id"),     
                        rs.getBoolean("is_deleted")
                );
                products.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    public void softDelete(int productId) {
        String sql = "UPDATE products SET is_deleted = true WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            stmt.executeUpdate();
            System.out.println("🗑 Product soft deleted");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
 // UPDATE
    public void update(Product product) {
        String sql = """
            UPDATE products
            SET product_name = ?,
                category = ?,
                price = ?,
                cost = ?,
                quantity = ?,
                branch_id = ?
            WHERE product_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getProductName());
            stmt.setString(2, product.getCategory());
            stmt.setDouble(3, product.getPrice());
            stmt.setDouble(4, product.getCost());
            stmt.setInt(5, product.getQuantity());
            stmt.setInt(6, product.getBranchId());
            stmt.setInt(7, product.getProductId());

            stmt.executeUpdate();
            System.out.println("✏️ Product updated successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    public List<Product> searchByName(String keyword) {
        List<Product> products = new ArrayList<>();

        String sql = """
            SELECT * FROM products
            WHERE is_deleted = false
            AND product_name ILIKE ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getDouble("cost"),
                        rs.getInt("quantity"),
                        rs.getInt("branch_id"),
                        rs.getBoolean("is_deleted")
                );
                products.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }
    public void updateQuantity(int productId, int newQuantity) {

        String sql = """
            UPDATE products
            SET quantity = ?
            WHERE product_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newQuantity);
            stmt.setInt(2, productId);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
