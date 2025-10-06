package com.company.inventory3.service;

import com.company.inventory3.model.Customer;
import com.company.inventory3.model.Product;
import com.company.inventory3.service.OrderService.OrderItem;
import com.company.inventory3.dao.ProductDAO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PDFService {
    private ProductDAO productDAO;

    public PDFService() {
        this.productDAO = new ProductDAO();
    }

    public boolean generateInvoice(List<OrderItem> orderItems, Customer customer, String filePath) {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        try (PDPageContentStream content = new PDPageContentStream(document, page)) {

            float y = page.getMediaBox().getHeight() - 50;
            content.setFont(PDType1Font.HELVETICA_BOLD, 20);
            content.beginText();
            content.newLineAtOffset(50, y);
            content.showText("INVENTORY MANAGEMENT SYSTEM");
            content.endText();

            y -= 30;
            content.setFont(PDType1Font.HELVETICA_BOLD, 16);
            content.beginText();
            content.newLineAtOffset(50, y);
            content.showText("INVOICE");
            content.endText();

            y -= 30;
            content.setFont(PDType1Font.HELVETICA, 12);
            content.beginText();
            content.newLineAtOffset(50, y);
            content.showText("Invoice Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            content.endText();

            y -= 20;
            content.beginText();
            content.newLineAtOffset(50, y);
            content.showText("Customer: " + customer.getName());
            content.endText();

            y -= 20;
            content.beginText();
            content.newLineAtOffset(50, y);
            content.showText("Email: " + customer.getEmail());
            content.endText();

            y -= 30;
            content.beginText();
            content.newLineAtOffset(50, y);
            content.showText("Product\tQty\tUnit Price\tTotal");
            content.endText();

            double grandTotal = 0;
            y -= 20;

            for (OrderItem item : orderItems) {
                Product product = productDAO.getProductById(item.getProductId());
                double total = product.getPrice() * item.getQuantity();
                grandTotal += total;

                content.beginText();
                content.newLineAtOffset(50, y);
                content.showText(product.getName() + "\t" + item.getQuantity() + "\t$" + product.getPrice() + "\t$" + total);
                content.endText();
                y -= 20;
            }

            y -= 20;
            content.beginText();
            content.newLineAtOffset(50, y);
            content.showText("GRAND TOTAL: $" + grandTotal);
            content.endText();

            y -= 30;
            content.beginText();
            content.newLineAtOffset(50, y);
            content.showText("Thank you for your business!");
            content.endText();

            content.close();
            document.save(filePath);
            document.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
