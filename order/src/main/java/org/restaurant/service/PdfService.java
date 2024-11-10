package org.restaurant.service;

import com.itextpdf.text.DocumentException;
import org.restaurant.request.OrderRequest;

import java.io.IOException;
import java.net.URISyntaxException;

public interface PdfService {

    byte[] generatePdf(OrderRequest orderRequest) throws DocumentException, IOException, URISyntaxException;
}
