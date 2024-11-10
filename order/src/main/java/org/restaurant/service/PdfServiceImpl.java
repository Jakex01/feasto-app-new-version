package org.restaurant.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.restaurant.request.OrderRequest;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService{

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 21, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD);
    private static final Font CONTENT_FONT = new Font(Font.FontFamily.HELVETICA, 15);

    @Override
    @SneakyThrows
    public byte[] generatePdf(OrderRequest orderRequest) {


        AtomicReference<Double> additivesPrice = new AtomicReference<>(0.0);

        orderRequest.items().forEach(t->{
            additivesPrice.updateAndGet(v -> v + sumMapValues(t.foodAdditivePrices()));
        });


        Document document = new Document();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
        document.open();
        try {


        Paragraph restaurantTitle = new Paragraph(orderRequest.restaurantName(), TITLE_FONT);

        restaurantTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(restaurantTitle);
        document.add(new Paragraph(" "));


        PdfPTable table = new PdfPTable(new float[]{1.5f, 1, 1, 1.1f, 1.5f,1.0f});
        table.setWidthPercentage(100);
        table.setSpacingBefore(25f);
        table.setSpacingAfter(20f);

        PdfPCell cell = new PdfPCell(new Paragraph("Item", HEADER_FONT));
        cell.setPadding(5);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("Size", HEADER_FONT));
        cell.setPadding(10);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("Price", HEADER_FONT));
        cell.setPadding(10);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("no.", HEADER_FONT));
        cell.setPadding(10);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("Additives", HEADER_FONT));
        cell.setPadding(10);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("Total", HEADER_FONT));
        cell.setPadding(10);
        table.addCell(cell);

        orderRequest.items()
                .forEach(t->{
            addOrderRow(table, t.name(), t.selectedSize(), t.selectedPrice(), t.foodAdditivePrices(),t.quantity());
        });

        document.add(table);


        Paragraph tip = new Paragraph("Tip: $"+orderRequest.tip(), HEADER_FONT);
        tip.setAlignment(Element.ALIGN_RIGHT);
        document.add(tip);

        Paragraph additives = new Paragraph("Additives: $"+additivesPrice, HEADER_FONT);
        additives.setAlignment(Element.ALIGN_RIGHT);
        document.add(additives);

        Paragraph delivery = new Paragraph("Delivery: $"+orderRequest.deliveryFee(), HEADER_FONT);
        delivery.setAlignment(Element.ALIGN_RIGHT);
        document.add(delivery);

        Paragraph separatorLine = new Paragraph("----------------------------------", HEADER_FONT);
        separatorLine.setAlignment(Element.ALIGN_RIGHT);
        document.add(separatorLine);

        Paragraph summary = new Paragraph("Suma: $"+orderRequest.totalPrice(), HEADER_FONT);
        summary.setAlignment(Element.ALIGN_RIGHT);
        document.add(summary);

        document.add(new Paragraph("\n"));
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("\n"));


        Paragraph appInfo = new Paragraph("PROVIDED BY FEASTO", new Font(Font.FontFamily.HELVETICA, 24, Font.ITALIC, BaseColor.CYAN));
        appInfo.setAlignment(Element.ALIGN_CENTER);
        document.add(appInfo);
        document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        byte[] pdfBytes = byteArrayOutputStream.toByteArray();
        ByteArrayOutputStream encryptedPdfOutput = new ByteArrayOutputStream();

        PdfReader pdfReader = new PdfReader(new ByteArrayInputStream(pdfBytes));

        PdfStamper pdfStamper = new PdfStamper(pdfReader, encryptedPdfOutput);


        pdfStamper.setEncryption(
                "userpass".getBytes(),
                "ownerpass".getBytes(),
                PdfWriter.ALLOW_PRINTING,
                PdfWriter.ENCRYPTION_AES_256
        );

        pdfStamper.close();
        pdfReader.close();

        return byteArrayOutputStream.toByteArray();
    }
    private void addOrderRow(PdfPTable table, String name, String size, double price, Map<String,Double> additives, int quantity) {
        PdfPCell cell = new PdfPCell(new Paragraph(name, CONTENT_FONT));
        cell.setPadding(8);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(size, CONTENT_FONT));
        cell.setPadding(8);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("$" + price, CONTENT_FONT));
        cell.setPadding(8);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(String.valueOf(quantity), CONTENT_FONT));
        cell.setPadding(8);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph(mapToString(additives), CONTENT_FONT));
        cell.setPadding(8);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("$" + (price * quantity), CONTENT_FONT));
        cell.setPadding(8);
        table.addCell(cell);
    }

    private String mapToString(Map<String, Double> map) {
        String mapContents = map.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
        return "String(" + mapContents + ")";
    }
    private double sumMapValues(Map<String, Double> map) {
        double sum = 0.0;
        for (Double value : map.values()) {
            sum += value;
        }
        return sum;
    }
}
