package org.rma.ocrfacturebackend.utils;

import com.azure.core.util.BinaryData;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    public static BinaryData convertMultipartFileToBinaryData(MultipartFile multipartFile) throws IOException {
        byte[] binaryData = multipartFile.getBytes();
        return BinaryData.fromBytes(binaryData);
    }



    // First Page of PDF
    public static BinaryData convertFirstPageAsBinaryData(MultipartFile multipartFile) throws IOException {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            PDDocument document = PDDocument.load(inputStream);
            PDPageTree pages = document.getPages();

            // Create a new document with only the first page
            PDDocument firstPageDocument = new PDDocument();
            PDPage firstPage = pages.get(0); // Get the first page
            firstPageDocument.addPage(firstPage);

            // Convert the first page to binary data
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            firstPageDocument.save(outputStream);
            byte[] binaryData = outputStream.toByteArray();

            // Close the documents
            firstPageDocument.close();
            document.close();

            return BinaryData.fromBytes(binaryData);
        }
    }

    // Second Page of PDF

    public static BinaryData convertSecondPageAsBinaryData(MultipartFile multipartFile) throws IOException {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            PDDocument document = PDDocument.load(inputStream);
            PDPageTree pages = document.getPages();

            // Create a new document with only the second page
            PDDocument secondPageDocument = new PDDocument();
            PDPage secondPage = pages.get(1); // Get the second page (index 1)
            secondPageDocument.addPage(secondPage);

            // Convert the second page to binary data
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            secondPageDocument.save(outputStream);
            byte[] binaryData = outputStream.toByteArray();

            // Close the documents
            secondPageDocument.close();
            document.close();

            return BinaryData.fromBytes(binaryData);
        }
    }





    // TWO pages of PDF
    public static BinaryData convertFirstTwoPagesAsBinaryData(MultipartFile multipartFile) throws IOException {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            PDDocument document = PDDocument.load(inputStream);
            PDPageTree pages = document.getPages();

            // Create a new document with the first two pages
            PDDocument firstTwoPagesDocument = new PDDocument();
            PDPage firstPage = pages.get(0); // Get the first page
            PDPage secondPage = pages.get(1); // Get the second page
            firstTwoPagesDocument.addPage(firstPage);
            firstTwoPagesDocument.addPage(secondPage);

            // Convert the first two pages to binary data
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            firstTwoPagesDocument.save(outputStream);
            byte[] binaryData = outputStream.toByteArray();

            // Close the documents
            firstTwoPagesDocument.close();
            document.close();

            return BinaryData.fromBytes(binaryData);
        }
    }





}
