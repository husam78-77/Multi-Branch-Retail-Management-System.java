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
            select b.branch_name, sum(s.total_amount) as total_sales
            from sales s
            join branches b on s.branch_id = b.branch_id
            group by b.branch_name
        """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new BranchSalesReport(
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
            select p.product_name, sum(sd.quantity) as total_sold
            from sale_details sd
            join products p on sd.product_id = p.product_id
            group by p.product_name
            order by total_sold desc
            limit 5
        """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new BestProductReport(
                        rs.getString("product_name"),
                        rs.getInt("total_sold")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

}
