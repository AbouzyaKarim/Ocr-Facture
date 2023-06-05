package org.rma.ocrfacturebackend.utils;
import com.azure.ai.formrecognizer.documentanalysis.models.CurrencyValue;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldsExtractionUtils {


    public static String extractCodeDossier(Map<String, Object> dataMap) {

        String codeDossierKey = "Code dossier :";


        // Apply the current logic
        String pattern = "([RMTY]\\s*\\d{0,2}\\s*\\d{0,2}\\s*\\d{0,4}\\s*\\w{0,3}\\s*[GHX]?)";

        Pattern regex = Pattern.compile(pattern);
        String maxCompleteCode = null;
        String maxIncompleteCode = null;
        int maxCompleteLength = 0;
        int maxIncompleteLength = 0;

        if (dataMap.containsKey(codeDossierKey)) {
            Object codeDossierObject = dataMap.get(codeDossierKey);
            if (codeDossierObject instanceof String) {
                String codeDossier = (String) codeDossierObject;
                codeDossier = codeDossier.replaceAll("\\s", "");



                for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                    String value = entry.getValue().toString();
                    Matcher matcher = regex.matcher(value);
                    while (matcher.find()) {
                        String code = matcher.group(1);
                        int codeLength = code.replaceAll("\\s", "").length();
                        if (codeLength == 13) {
                            return code;
                        } else if (codeLength > maxCompleteLength) {
                            maxCompleteCode = code;
                            maxCompleteLength = codeLength;
                        } else if (codeLength > maxIncompleteLength) {
                            maxIncompleteCode = code;
                            maxIncompleteLength = codeLength;
                        }
                    }
                }

                // Compare the length of codeDossier with maxCompleteCode and maxIncompleteCode
                int codeDossierLength = codeDossier.length();
                if (codeDossierLength >= maxCompleteLength && codeDossierLength >= maxIncompleteLength) {
                    return codeDossier.replaceAll("\\s", "");
                } else if (maxCompleteLength >= maxIncompleteLength) {
                    return maxCompleteCode != null ? maxCompleteCode.replaceAll("\\s", "") : "";
                } else {
                    return maxIncompleteCode != null ? maxIncompleteCode.replaceAll("\\s", "") : "";
                }
            }
        }

        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            String value = entry.getValue().toString();
            Matcher matcher = regex.matcher(value);
            while (matcher.find()) {
                String code = matcher.group(1);
                int codeLength = code.replaceAll("\\s", "").length();
                if (codeLength == 13) {
                    return code;
                } else if (codeLength > maxCompleteLength) {
                    maxCompleteCode = code;
                    maxCompleteLength = codeLength;
                } else if (codeLength > maxIncompleteLength) {
                    maxIncompleteCode = code;
                    maxIncompleteLength = codeLength;
                }
            }
        }

        return maxCompleteCode != null ? maxCompleteCode.replaceAll("\\s", "") : maxIncompleteCode.replaceAll("\\s", "");
    }



    // ICE
    public static String extractICE(Map<String, Object> dataMap) {
        String iceValue;
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            if (key.matches("(?i).*\\bI\\.?C\\.?E\\.?\\b.*") && entry.getValue() instanceof String) {
                String value = (String) entry.getValue();
                if (value.startsWith("00") && !value.equals("001903162000085") && !value.contains("001903162000085")) {
                    iceValue = value.replaceAll("\\D", "");
                    return iceValue;
                }
            }
        }

        return null; // Key not found
    }







    // Total TTC
    public static Double extractTotal(Map<String, Object> extractedData) {
        if (extractedData.containsKey("InvoiceTotal")) {
            Object invoiceTotalObject = extractedData.get("InvoiceTotal");
            if (invoiceTotalObject instanceof com.azure.ai.formrecognizer.documentanalysis.models.CurrencyValue) {
                CurrencyValue invoiceTotal = (CurrencyValue) invoiceTotalObject;
                Double invoiceTotalAmount = invoiceTotal.getAmount();
                return invoiceTotalAmount;
            } else {
                // Handle the case when the object is not of type CurrencyValue
                System.out.println("Invalid InvoiceTotal value type");
                return null;
            }
        } else {
            // InvoiceTotal doesn't exist, return null or handle accordingly
            System.out.println("Total TTC doesn't exist");
            return null;
        }
    }


    // Prestataire
    public static String extractPrestataire(Map<String, Object> extractedData) {
        if (extractedData.containsKey("Destinataire :")) {
            String prestataire = (String) extractedData.get("Destinataire :");
            return prestataire.replaceAll("\\n"," ");
        } else if (extractedData.containsKey("VendorAddressRecipient")) {
            String prestataire = (String) extractedData.get("VendorAddressRecipient");
            return prestataire.replaceAll("\\n", " ");
        } else if (extractedData.containsKey("VendorName")) {
            String prestataire = (String) extractedData.get("VendorName");
            return prestataire;
        } else {
            // Prestataire information not found, handle accordingly
            System.out.println("Prestataire information not found");
            return null;
        }
    }



    // Invoice ID
    public static String extractInvoiceId(Map<String, Object> extractedData) {
        if (extractedData.containsKey("InvoiceId")) {
            String ID = (String) extractedData.get("InvoiceId");
            String invoiceID= ID.replaceAll("\\n", " ");
            return invoiceID;

        } else {
            // InvoiceID doesn't exist, return null or handle accordingly
            System.out.println("N Facture doesn't exist");
            return null;
        }
    }

    // Total HT
    public static Double extractTotalHt(Map<String, Object> extractedData) {
        if(extractedData.containsKey("SubTotal")){
            if (extractedData.get("SubTotal") instanceof com.azure.ai.formrecognizer.documentanalysis.models.CurrencyValue) {
                com.azure.ai.formrecognizer.documentanalysis.models.CurrencyValue totalHt = (com.azure.ai.formrecognizer.documentanalysis.models.CurrencyValue) extractedData.get("SubTotal");
                double totalHtValue = totalHt.getAmount();
                return totalHtValue;
            }
            else {
                // Handle the case when the object is not of type CurrencyValue
                System.out.println("Invalid totalHt value type");
                return null;
            }

        }
        else if (extractedData.containsKey("InvoiceTotal")) {
            Object invoiceTotalObject = extractedData.get("InvoiceTotal");
            if (invoiceTotalObject instanceof com.azure.ai.formrecognizer.documentanalysis.models.CurrencyValue) {
                com.azure.ai.formrecognizer.documentanalysis.models.CurrencyValue invoiceTotal = (com.azure.ai.formrecognizer.documentanalysis.models.CurrencyValue) invoiceTotalObject;
                double invoiceTotalAmount = invoiceTotal.getAmount();
                double invoiceTotalHt = invoiceTotalAmount / 1.2;

                // Format invoiceTotalHt to have two decimal places
                DecimalFormat decimalFormat = new DecimalFormat("#.00");
                String formattedInvoiceTotalHt = decimalFormat.format(invoiceTotalHt);
                invoiceTotalHt = Double.parseDouble(formattedInvoiceTotalHt);

                return invoiceTotalHt;
            } else {
                // Handle the case when the object is not of type CurrencyValue
                System.out.println("Invalid InvoiceTotal value type");
                return null;
            }
        } else {
            // InvoiceTotal doesn't exist, return null or handle accordingly
            System.out.println("InvoiceTotal doesn't exist. Please check");
            return null;
        }
    }

    // Invoice Date
    public static LocalDate extractInvoiceDate(Map<String, Object> extractedData) {
        if (extractedData.containsKey("InvoiceDate")) {
            LocalDate date = (LocalDate) extractedData.get("InvoiceDate");
            return date;

        } else {
            // InvoiceID doesn't exist, return null or handle accordingly
            System.out.println("Date Facture doesn't exist");
            return null;
        }
    }

    // Nom Client RMA
    public static String extractClientName(Map<String, Object> extractedData) {
        for (Map.Entry<String, Object> entry : extractedData.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();

            // Check if the key or value contains "RMA" (case-insensitive)
            if (key.matches("(?i).*RMA.*") || value.matches("(?i).*RMA.*")) {
                return "RMA ASSISTANCE";
            }
        }

        // Return  if "RMA" is not found in any key or value
        return "RMA ASSISTANCE";
    }






}

