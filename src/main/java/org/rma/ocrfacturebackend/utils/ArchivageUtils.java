package org.rma.ocrfacturebackend.utils;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ArchivageUtils {

    private static final String ARCHIVE_ROOT_DIRECTORY = "C:\\Users\\user\\Desktop\\FactureRMA";

    private ArchivageUtils() {
        throw new AssertionError("Utility class cannot be instantiated.");
    }

    public static String saveInvoice(MultipartFile file) {
        try {
            // Generate a unique file name
            String uniqueFileName = generateUniqueFileName(file.getOriginalFilename());

            // Create the directory structure for archiving based on the current date
            LocalDateTime currentDateTime = LocalDateTime.now();
            String year = String.valueOf(currentDateTime.getYear());
            String month = String.format("%02d", currentDateTime.getMonthValue());
            String day = String.format("%02d", currentDateTime.getDayOfMonth());

            String archiveDirectory = createArchiveDirectory(year, month, day);

            // Move the file to the appropriate archive directory
            Path uploadFilePath = fileToPath(file);
            Path archiveFilePath = Path.of(archiveDirectory, uniqueFileName);
            Files.move(uploadFilePath, archiveFilePath, StandardCopyOption.REPLACE_EXISTING);

            // Store the file path
            String filePathIn = archiveDirectory + "\\" + uniqueFileName;

            // Return the file path
            return filePathIn;
        } catch (IOException e) {
            // Handle any exceptions that occur during file handling
            e.printStackTrace();
        }

        return null;
    }

    private static Path fileToPath(MultipartFile file) throws IOException {
        Path tempFile = Files.createTempFile("", "");
        try {
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
            return tempFile;
        } catch (IOException e) {
            Files.delete(tempFile);
            throw e;
        }
    }

    private static String generateUniqueFileName(String originalFileName) {
        // Get the current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();
        String dateTime = currentDateTime.format(DateTimeFormatter.ofPattern("MM-dd-yy-HH-mm-ss"));

        // Remove any special characters and spaces from the original file name
        String fileName = StringUtils.cleanPath(originalFileName);

        // Append the date and time to the file name
        return dateTime + "_" + fileName;
    }

    private static String createArchiveDirectory(String year, String month, String day) {
        String archiveDirectory = ARCHIVE_ROOT_DIRECTORY + File.separator + year + File.separator + month + File.separator + day;

        try {
            // Create the year directory if it doesn't exist
            if (!Files.exists(Path.of(ARCHIVE_ROOT_DIRECTORY, year))) {
                Files.createDirectory(Path.of(ARCHIVE_ROOT_DIRECTORY, year));
            }

            // Create the month directory if it doesn't exist
            if (!Files.exists(Path.of(ARCHIVE_ROOT_DIRECTORY, year, month))) {
                Files.createDirectory(Path.of(ARCHIVE_ROOT_DIRECTORY, year, month));
            }

            // Create the day directory if it doesn't exist
            if (!Files.exists(Path.of(ARCHIVE_ROOT_DIRECTORY, year, month, day))) {
                Files.createDirectory(Path.of(ARCHIVE_ROOT_DIRECTORY, year, month, day));
            }
        } catch (IOException e) {
            // Handle any exceptions that occur during directory creation
            e.printStackTrace();
        }

        return archiveDirectory;
    }

}
