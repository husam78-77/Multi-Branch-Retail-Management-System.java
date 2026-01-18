package dao;

import database.DBConnection;
import model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public int insert(Connection conn, Customer customer) throws SQLException {

        String sql = """
            INSERT INTO customers (full_name, phone)
            VALUES (?, ?)
            RETURNING customer_id
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getFullName());
            stmt.setString(2, customer.getPhone());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("customer_id");
            }
        }
        return -1;
    }

    /* ===================== GET ALL ===================== */
    public List<Customer> getAll() {

        List<Customer> customers = new ArrayList<>();

        String sql = """
            SELECT customer_id, full_name, phone
            FROM customers
            ORDER BY full_name
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                customers.add(
                    new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("full_name"),
                        rs.getString("phone")
                    )
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customers;
    }

    /* ===================== SEARCH ===================== */
    public List<Customer> search(String keyword) {

        List<Customer> customers = new ArrayList<>();

        String sql = """
            SELECT customer_id, full_name, phone
            FROM customers
            WHERE full_name ILIKE ?
               OR phone ILIKE ?
            ORDER BY full_name
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                customers.add(
                    new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("full_name"),
                        rs.getString("phone")
                    )
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customers;
    }

    /* ===================== GET BY ID ===================== */
    public Customer getById(int customerId) {

        String sql = """
            SELECT customer_id, full_name, phone
            FROM customers
            WHERE customer_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Customer(
                    rs.getInt("customer_id"),
                    rs.getString("full_name"),
                    rs.getString("phone")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    public Customer getLatestCustomer() {

        String sql = """
            SELECT customer_id, full_name, phone
            FROM customers
            ORDER BY customer_id DESC
            LIMIT 1
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return new Customer(
                    rs.getInt("customer_id"),
                    rs.getString("full_name"),
                    rs.getString("phone")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
