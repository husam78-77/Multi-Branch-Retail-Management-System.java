package util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import model.BestProductReport;
import model.BranchSalesReport;
import model.SaleItem;

import java.io.FileOutputStream;
import java.util.List;

public class PDFGenerator {

    public static void generateInvoice(
            int saleId,
            String cashierName,
            List<SaleItem> items,
            double total
    ) {

        Document document = new Document();

        try {
            String fileName = "Invoice_" + saleId + ".pdf";
            PdfWriter.getInstance(document,
                    new FileOutputStream(fileName));

            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Sales Invoice", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" "));

            document.add(new Paragraph("Invoice ID: " + saleId));
            document.add(new Paragraph("Cashier: " + cashierName));
            document.add(new Paragraph("Date: " + new java.util.Date()));

            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 1, 1, 1});

            table.addCell("Product");
            table.addCell("Qty");
            table.addCell("Price");
            table.addCell("Subtotal");

            for (SaleItem item : items) {
                table.addCell(item.getProductName());
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell(String.valueOf(item.getPrice()));
                table.addCell(String.valueOf(item.getSubtotal()));
            }

            document.add(table);

            document.add(new Paragraph(" "));

            Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Paragraph totalPara =
                    new Paragraph("Total: " + total, totalFont);
            totalPara.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalPara);

            document.close();

            System.out.println("✅ Invoice PDF generated: " + fileName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate PDF report for Sales Per Branch
     */
    public static String generateBranchSalesReport(List<BranchSalesReport> data, String filePath) {
        Document document = new Document();
        
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("Sales Per Branch Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Date
            Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            Paragraph date = new Paragraph("Generated on: " + new java.util.Date(), dateFont);
            date.setAlignment(Element.ALIGN_CENTER);
            date.setSpacingAfter(20);
            document.add(date);

            // Calculate totals
            double totalSales = data.stream()
                    .mapToDouble(BranchSalesReport::getTotalSales)
                    .sum();

            // Summary box
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(50);
            summaryTable.setHorizontalAlignment(Element.ALIGN_CENTER);
            summaryTable.setSpacingAfter(20);
            
            PdfPCell summaryHeader = new PdfPCell(new Phrase("Summary", 
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            summaryHeader.setColspan(2);
            summaryHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            summaryHeader.setBackgroundColor(BaseColor.LIGHT_GRAY);
            summaryTable.addCell(summaryHeader);
            
            summaryTable.addCell("Total Branches:");
            summaryTable.addCell(String.valueOf(data.size()));
            summaryTable.addCell("Total Sales:");
            summaryTable.addCell(String.format("RM %.2f", totalSales));
            
            document.add(summaryTable);

            // Data table
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 3, 2});
            table.setSpacingBefore(10);

            // Header
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
            PdfPCell headerCell;

            headerCell = new PdfPCell(new Phrase("#", headerFont));
            headerCell.setBackgroundColor(new BaseColor(52, 152, 219));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPadding(8);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Branch Name", headerFont));
            headerCell.setBackgroundColor(new BaseColor(52, 152, 219));
            headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerCell.setPadding(8);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Total Sales (RM)", headerFont));
            headerCell.setBackgroundColor(new BaseColor(52, 152, 219));
            headerCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerCell.setPadding(8);
            table.addCell(headerCell);

            // Data rows
            Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
            int rank = 1;
            for (BranchSalesReport report : data) {
                PdfPCell cell;

                cell = new PdfPCell(new Phrase(String.valueOf(rank++), dataFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(report.getBranchName(), dataFont));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPadding(6);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.format("%.2f", report.getTotalSales()), dataFont));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setPadding(6);
                table.addCell(cell);
            }

            document.add(table);

            // Footer
            document.add(new Paragraph(" "));
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, BaseColor.GRAY);
            Paragraph footer = new Paragraph("Report generated by Retail Management System", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            System.out.println("✅ Branch Sales Report PDF generated: " + filePath);
            return filePath;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generate PDF report for Best Selling Products
     */
    public static String generateBestProductsReport(List<BestProductReport> data, String filePath) {
        Document document = new Document();
        
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("Best Selling Products Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Date
            Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            Paragraph date = new Paragraph("Generated on: " + new java.util.Date(), dateFont);
            date.setAlignment(Element.ALIGN_CENTER);
            date.setSpacingAfter(20);
            document.add(date);

            // Calculate totals
            int totalUnits = data.stream()
                    .mapToInt(BestProductReport::getTotalSold)
                    .sum();

            // Summary box
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(50);
            summaryTable.setHorizontalAlignment(Element.ALIGN_CENTER);
            summaryTable.setSpacingAfter(20);
            
            PdfPCell summaryHeader = new PdfPCell(new Phrase("Summary", 
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            summaryHeader.setColspan(2);
            summaryHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            summaryHeader.setBackgroundColor(BaseColor.LIGHT_GRAY);
            summaryTable.addCell(summaryHeader);
            
            summaryTable.addCell("Total Products:");
            summaryTable.addCell(String.valueOf(data.size()));
            summaryTable.addCell("Total Units Sold:");
            summaryTable.addCell(String.valueOf(totalUnits));
            
            if (!data.isEmpty()) {
                summaryTable.addCell("Top Product:");
                summaryTable.addCell(data.get(0).getProductName());
            }
            
            document.add(summaryTable);

            // Data table
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 4, 2});
            table.setSpacingBefore(10);

            // Header
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
            PdfPCell headerCell;

            headerCell = new PdfPCell(new Phrase("Rank", headerFont));
            headerCell.setBackgroundColor(new BaseColor(243, 156, 18));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPadding(8);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Product Name", headerFont));
            headerCell.setBackgroundColor(new BaseColor(243, 156, 18));
            headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            headerCell.setPadding(8);
            table.addCell(headerCell);

            headerCell = new PdfPCell(new Phrase("Units Sold", headerFont));
            headerCell.setBackgroundColor(new BaseColor(243, 156, 18));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPadding(8);
            table.addCell(headerCell);

            // Data rows with medals for top 3
            Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
            int rank = 1;
            for (BestProductReport report : data) {
                PdfPCell cell;

                // Rank with medal emoji for top 3
                String rankText = String.valueOf(rank);
                if (rank == 1) rankText = "🥇 1";
                else if (rank == 2) rankText = "🥈 2";
                else if (rank == 3) rankText = "🥉 3";
                
                cell = new PdfPCell(new Phrase(rankText, dataFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(report.getProductName(), dataFont));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setPadding(6);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(report.getTotalSold()), dataFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(6);
                table.addCell(cell);
                
                rank++;
            }

            document.add(table);

            // Footer
            document.add(new Paragraph(" "));
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, BaseColor.GRAY);
            Paragraph footer = new Paragraph("Report generated by Retail Management System", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            System.out.println("✅ Best Products Report PDF generated: " + filePath);
            return filePath;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}