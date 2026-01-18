package util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import model.BestProductReport;
import model.BranchSalesReport;
import model.SaleItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.util.List;

public class PDFGenerator {

	public static void generateInvoiceFull(
	        int saleId,
	        String date,
	        String customerName,
	        String customerPhone,
	        String cashierName,
	        String branchName,
	        String branchCity,
	        List<SaleItem> items,
	        double total
	) {
	    Document document = new Document();
	    try {
	        FileChooser fileChooser = new FileChooser();
	        fileChooser.setTitle("Save Invoice PDF");
	        fileChooser.getExtensionFilters().add(
	                new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf")
	        );
	        fileChooser.setInitialFileName("Invoice_" + saleId + ".pdf");
	        java.io.File file = fileChooser.showSaveDialog(new Stage());
	        if (file == null) {
	            return;
	        }
	        PdfWriter.getInstance(document, new FileOutputStream(file));
	        document.open();
	        
	        document.setMargins(40, 40, 40, 40);
	        
	        Font businessFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.DARK_GRAY);
	        Paragraph businessName = new Paragraph(branchName, businessFont);
	        businessName.setAlignment(Element.ALIGN_CENTER);
	        document.add(businessName);
	        
	        Font taglineFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
	        Paragraph tagline = new Paragraph(branchCity, taglineFont);
	        tagline.setAlignment(Element.ALIGN_CENTER);
	        document.add(tagline);
	        
	        // Divider line
	        PdfPTable divider = new PdfPTable(1);
	        divider.setWidthPercentage(100);
	        PdfPCell dividerCell = new PdfPCell();
	        dividerCell.setBorder(Rectangle.BOTTOM);
	        dividerCell.setBorderColor(BaseColor.LIGHT_GRAY);
	        dividerCell.setBorderWidth(1);
	        dividerCell.setFixedHeight(1);
	        divider.addCell(dividerCell);
	        document.add(divider);
	        document.add(new Paragraph(" "));
	        
	        // Invoice title and info in two columns
	        PdfPTable headerTable = new PdfPTable(2);
	        headerTable.setWidthPercentage(100);
	        headerTable.setWidths(new float[]{1, 1});
	        
	        // Left column - Invoice title
	        PdfPCell leftCell = new PdfPCell();
	        leftCell.setBorder(Rectangle.NO_BORDER);
	        Font invoiceTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28, new BaseColor(41, 128, 185));
	        Paragraph invoiceTitle = new Paragraph("INVOICE", invoiceTitleFont);
	        leftCell.addElement(invoiceTitle);
	        headerTable.addCell(leftCell);
	        
	        // Right column - Invoice details
	        PdfPCell rightCell = new PdfPCell();
	        rightCell.setBorder(Rectangle.NO_BORDER);
	        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        Font detailFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
	        Font detailBoldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
	        
	        Paragraph invoiceNum = new Paragraph();
	        invoiceNum.add(new Chunk("Invoice #: ", detailFont));
	        invoiceNum.add(new Chunk(String.valueOf(saleId), detailBoldFont));
	        invoiceNum.setAlignment(Element.ALIGN_RIGHT);
	        rightCell.addElement(invoiceNum);
	        
	        Paragraph dateP = new Paragraph();
	        dateP.add(new Chunk("Date: ", detailFont));
	        dateP.add(new Chunk(date, detailBoldFont));
	        dateP.setAlignment(Element.ALIGN_RIGHT);
	        rightCell.addElement(dateP);
	        
	        headerTable.addCell(rightCell);
	        document.add(headerTable);
	        document.add(new Paragraph(" "));
	        
	        // Customer and Branch info in two columns
	        PdfPTable infoTable = new PdfPTable(2);
	        infoTable.setWidthPercentage(100);
	        infoTable.setWidths(new float[]{1, 1});
	        infoTable.setSpacingBefore(10);
	        infoTable.setSpacingAfter(20);
	        
	        // Bill To section
	        PdfPCell billToCell = new PdfPCell();
	        billToCell.setBorder(Rectangle.NO_BORDER);
	        billToCell.setPadding(10);
	        billToCell.setBackgroundColor(new BaseColor(245, 245, 245));
	        
	        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new BaseColor(52, 73, 94));
	        Paragraph billToTitle = new Paragraph("BILL TO", sectionFont);
	        billToCell.addElement(billToTitle);
	        billToCell.addElement(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 6)));
	        billToCell.addElement(new Paragraph(customerName, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
	        billToCell.addElement(new Paragraph(
	                customerPhone == null || customerPhone.isBlank() ? "No phone provided" : customerPhone,
	                FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY)
	        ));
	        infoTable.addCell(billToCell);
	        
	        // Branch Info section
	        PdfPCell branchCell = new PdfPCell();
	        branchCell.setBorder(Rectangle.NO_BORDER);
	        branchCell.setPadding(10);
	        branchCell.setBackgroundColor(new BaseColor(245, 245, 245));
	        
	        Paragraph branchTitle = new Paragraph("BRANCH & STAFF", sectionFont);
	        branchCell.addElement(branchTitle);
	        branchCell.addElement(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 6)));
	        branchCell.addElement(new Paragraph(branchName, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
	        branchCell.addElement(new Paragraph(
	                "Served by: " + cashierName,
	                FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY)
	        ));
	        infoTable.addCell(branchCell);
	        
	        document.add(infoTable);
	        
	        // Items table with improved styling
	        PdfPTable itemsTable = new PdfPTable(4);
	        itemsTable.setWidthPercentage(100);
	        itemsTable.setWidths(new float[]{4, 1, 2, 2});
	        itemsTable.setSpacingBefore(10);
	        
	        // Table header
	        Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE);
	        BaseColor headerColor = new BaseColor(52, 73, 94);
	        
	        String[] headers = {"Product", "Qty", "Price", "Subtotal"};
	        for (String header : headers) {
	            PdfPCell headerCell = new PdfPCell(new Phrase(header, tableHeaderFont));
	            headerCell.setBackgroundColor(headerColor);
	            headerCell.setPadding(8);
	            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
	            headerCell.setBorder(Rectangle.NO_BORDER);
	            itemsTable.addCell(headerCell);
	        }
	        
	        // Table rows with alternating colors
	        Font tableFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
	        Font tableBoldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
	        BaseColor rowColor1 = BaseColor.WHITE;
	        BaseColor rowColor2 = new BaseColor(249, 249, 249);
	        
	        for (int i = 0; i < items.size(); i++) {
	            SaleItem item = items.get(i);
	            BaseColor rowColor = (i % 2 == 0) ? rowColor1 : rowColor2;
	            
	            PdfPCell cell1 = new PdfPCell(new Phrase(item.getProductName(), tableFont));
	            cell1.setBackgroundColor(rowColor);
	            cell1.setPadding(8);
	            cell1.setBorder(Rectangle.NO_BORDER);
	            itemsTable.addCell(cell1);
	            
	            PdfPCell cell2 = new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), tableFont));
	            cell2.setBackgroundColor(rowColor);
	            cell2.setPadding(8);
	            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
	            cell2.setBorder(Rectangle.NO_BORDER);
	            itemsTable.addCell(cell2);
	            
	            PdfPCell cell3 = new PdfPCell(new Phrase(String.format("%.2f", item.getPrice()), tableFont));
	            cell3.setBackgroundColor(rowColor);
	            cell3.setPadding(8);
	            cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
	            cell3.setBorder(Rectangle.NO_BORDER);
	            itemsTable.addCell(cell3);
	            
	            PdfPCell cell4 = new PdfPCell(new Phrase(String.format("%.2f", item.getSubtotal()), tableBoldFont));
	            cell4.setBackgroundColor(rowColor);
	            cell4.setPadding(8);
	            cell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
	            cell4.setBorder(Rectangle.NO_BORDER);
	            itemsTable.addCell(cell4);
	        }
	        
	        document.add(itemsTable);
	        
	        // Total section
	        PdfPTable totalTable = new PdfPTable(2);
	        totalTable.setWidthPercentage(40);
	        totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        totalTable.setSpacingBefore(20);
	        
	        PdfPCell totalLabelCell = new PdfPCell(new Phrase("TOTAL", 
	                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.WHITE)));
	        totalLabelCell.setBackgroundColor(new BaseColor(41, 128, 185));
	        totalLabelCell.setPadding(10);
	        totalLabelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
	        totalLabelCell.setBorder(Rectangle.NO_BORDER);
	        totalTable.addCell(totalLabelCell);
	        
	        PdfPCell totalAmountCell = new PdfPCell(new Phrase(String.format("%.2f", total),
	                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.WHITE)));
	        totalAmountCell.setBackgroundColor(new BaseColor(41, 128, 185));
	        totalAmountCell.setPadding(10);
	        totalAmountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	        totalAmountCell.setBorder(Rectangle.NO_BORDER);
	        totalTable.addCell(totalAmountCell);
	        
	        document.add(totalTable);
	        
	        // Footer
	        document.add(new Paragraph(" "));
	        document.add(new Paragraph(" "));
	        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.GRAY);
	        Paragraph footer = new Paragraph("Thank you for your business!", footerFont);
	        footer.setAlignment(Element.ALIGN_CENTER);
	        document.add(footer);
	        
	        document.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

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