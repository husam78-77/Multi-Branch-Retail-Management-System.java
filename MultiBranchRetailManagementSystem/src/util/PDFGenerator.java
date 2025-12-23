package util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

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
}
