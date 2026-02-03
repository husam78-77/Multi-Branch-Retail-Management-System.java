package dao;

import database.DBConnection;
import model.BestProductReport;
import model.BranchSalesReport;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

	public List<BranchSalesReport> getSalesPerBranch() {

	    List<BranchSalesReport> list = new ArrayList<>();

	    String sql = """
	        SELECT b.branch_id, b.branch_name, SUM(s.total_amount) AS total_sales
	        FROM sales s
	        JOIN branches b ON s.branch_id = b.branch_id
	        GROUP BY b.branch_id, b.branch_name
	    """;

	    try (Connection conn = DBConnection.getConnection();
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {

	        while (rs.next()) {
	        	list.add(new BranchSalesReport(
	        		    rs.getInt("branch_id"),
	        		    rs.getString("branch_name"),
	        		    rs.getDouble("total_sales")
	        		));

	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return list;
	}

	public List<BestProductReport> getBestSellingProducts() {

	    List<BestProductReport> list = new ArrayList<>();

	    String sql = """
	        SELECT sd.product_id, p.product_name, SUM(sd.quantity) AS total_sold
	        FROM sale_details sd
	        JOIN products p ON sd.product_id = p.product_id
	        GROUP BY sd.product_id, p.product_name
	        ORDER BY total_sold DESC
	        LIMIT 5
	    """;

	    try (Connection conn = DBConnection.getConnection();
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {

	        while (rs.next()) {
	        	list.add(new BestProductReport(
	        		    rs.getInt("product_id"),
	        		    rs.getString("product_name"),
	        		    rs.getInt("total_sold")
	        		));

	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return list;
	}

	public List<BranchSalesReport> getProfitPerBranch() {

	    List<BranchSalesReport> list = new ArrayList<>();

	    String sql = """
	        SELECT 
	            b.branch_id,
	            b.branch_name,
	            SUM((sd.price - p.cost) * sd.quantity) AS total_profit
	        FROM sale_details sd
	        JOIN sales s       ON sd.sale_id = s.sale_id
	        JOIN products p    ON sd.product_id = p.product_id
	        JOIN branches b    ON s.branch_id = b.branch_id
	        GROUP BY b.branch_id, b.branch_name
	    """;

	    try (Connection conn = DBConnection.getConnection();
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {

	        while (rs.next()) {
	            list.add(new BranchSalesReport(
	                rs.getInt("branch_id"),
	                rs.getString("branch_name"),
	                rs.getDouble("total_profit")
	            ));
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return list;
	}

}
