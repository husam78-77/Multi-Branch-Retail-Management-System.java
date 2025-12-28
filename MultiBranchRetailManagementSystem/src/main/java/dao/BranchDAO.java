package dao;

import database.DBConnection;
import model.Branch;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BranchDAO {

    
    public void insert(Branch branch) {
        String sql = """
            INSERT INTO branches (branch_name, city, phone)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, branch.getBranchName());
            stmt.setString(2, branch.getCity());
            stmt.setString(3, branch.getPhone());

            stmt.executeUpdate();
            System.out.println("✅ Branch inserted successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Branch> getAll() {
        List<Branch> branches = new ArrayList<>();

        String sql = "SELECT * FROM branches WHERE is_deleted = false";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Branch branch = new Branch(
                        rs.getInt("branch_id"),
                        rs.getString("branch_name"),
                        rs.getString("city"),
                        rs.getString("phone"),
                        rs.getBoolean("is_deleted")
                );
                branches.add(branch);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return branches;
    }

    public void update(Branch branch) {
        String sql = """
            UPDATE branches
            SET branch_name = ?, city = ?, phone = ?
            WHERE branch_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, branch.getBranchName());
            stmt.setString(2, branch.getCity());
            stmt.setString(3, branch.getPhone());
            stmt.setInt(4, branch.getBranchId());

            stmt.executeUpdate();
            System.out.println("✏️ Branch updated successfully");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean softDelete(int branchId) {

        if (branchId == 1) {
            System.out.println("❌ Main branch cannot be deleted");
            return false;
        }

        String deleteBranch = """
            UPDATE branches
            SET is_deleted = true
            WHERE branch_id = ?
        """;

        String deleteEmployees = """
            UPDATE employees
            SET is_deleted = true
            WHERE branch_id = ?
            AND role IN ('MANAGER', 'CASHIER')
        """;

        String deleteProducts = """
            UPDATE products
            SET is_deleted = true
            WHERE branch_id = ?
        """;

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false); // 🔐 Transaction

            try (
                PreparedStatement stmtBranch = conn.prepareStatement(deleteBranch);
                PreparedStatement stmtEmployees = conn.prepareStatement(deleteEmployees);
                PreparedStatement stmtProducts = conn.prepareStatement(deleteProducts)
            ) {
                stmtBranch.setInt(1, branchId);
                stmtEmployees.setInt(1, branchId);
                stmtProducts.setInt(1, branchId);

                stmtBranch.executeUpdate();
                stmtEmployees.executeUpdate();
                stmtProducts.executeUpdate();

                conn.commit(); // ✅
                System.out.println("🗑 Branch deleted with all related data");
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Branch> searchByName(String keyword) {
        List<Branch> branches = new ArrayList<>();

        String sql = """
            SELECT * FROM branches
            WHERE is_deleted = false
            AND branch_name ILIKE ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Branch branch = new Branch(
                        rs.getInt("branch_id"),
                        rs.getString("branch_name"),
                        rs.getString("city"),
                        rs.getString("phone"),
                        rs.getBoolean("is_deleted")
                );
                branches.add(branch);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return branches;
    }
}
