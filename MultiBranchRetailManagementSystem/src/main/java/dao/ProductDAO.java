package dao;

import database.DBConnection;
import model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public boolean insert(Product product) {

        if (!isBranchActive(product.getBranchId())) {
            System.out.println("❌ Cannot add product to deleted branch");
            return false;
        }

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
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

	public List<Product> getAll() {
	
	    List<Product> products = new ArrayList<>();
	
	    String sql = """
	        SELECT p.*
	        FROM products p
	        JOIN branches b ON p.branch_id = b.branch_id
	        WHERE b.is_deleted = false
	        ORDER BY p.product_id
	    """;
	
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {
	
	        while (rs.next()) {
	            products.add(mapProduct(rs));
	        }
	
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	
	    return products;
	}
	

    public boolean update(Product product) {

        if (!isBranchActive(product.getBranchId())) {
            System.out.println("❌ Cannot move product to deleted branch");
            return false;
        }

        String sql = """
            UPDATE products
            SET product_name = ?,
                category = ?,
                price = ?,
                cost = ?,
                quantity = ?,
                branch_id = ?
            WHERE product_id = ?
              AND is_deleted = false
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

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ===================== TOGGLE ACTIVE / DEACTIVE ===================== */
    public boolean toggleProductStatus(int productId) {

        String sql = "UPDATE products SET is_deleted = NOT is_deleted WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public List<Product> searchByName(String keyword) {

        List<Product> products = new ArrayList<>();

        String sql = """
                SELECT p.*
                FROM products p
                JOIN branches b ON p.branch_id = b.branch_id
                WHERE p.product_name ILIKE ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(mapProduct(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    public boolean updateQuantity(int productId, int newQuantity) {

        String sql = """
            UPDATE products p
            SET quantity = ?
            FROM branches b
            WHERE p.product_id = ?
              AND p.branch_id = b.branch_id
              AND p.is_deleted = false
              AND b.is_deleted = false
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newQuantity);
            stmt.setInt(2, productId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isBranchActive(int branchId) {

        String sql = "SELECT 1 FROM branches WHERE branch_id = ? AND is_deleted = false";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, branchId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            return false;
        }
    }

    private Product mapProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("product_id"),
                rs.getString("product_name"),
                rs.getString("category"),
                rs.getDouble("price"),
                rs.getDouble("cost"),
                rs.getInt("quantity"),
                rs.getInt("branch_id"),
                rs.getBoolean("is_deleted")
        );
    }
    public Product getById(int productId) {

        String sql = """
            SELECT *
            FROM products
            WHERE product_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapProduct(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
