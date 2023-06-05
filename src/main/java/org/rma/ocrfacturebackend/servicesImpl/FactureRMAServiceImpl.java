package org.rma.ocrfacturebackend.servicesImpl;

import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClient;
import com.azure.ai.formrecognizer.documentanalysis.DocumentAnalysisClientBuilder;
import com.azure.ai.formrecognizer.documentanalysis.models.AnalyzeResult;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentKeyValuePair;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.polling.SyncPoller;
import lombok.extern.slf4j.Slf4j;
import org.rma.ocrfacturebackend.constants.OcrFactureConstant;
import org.rma.ocrfacturebackend.dtos.FactureRMADto;
import org.rma.ocrfacturebackend.entities.FactureRMA;
import org.rma.ocrfacturebackend.entities.StatusFacture;
import org.rma.ocrfacturebackend.repositories.FactureRMARepository;
import org.rma.ocrfacturebackend.repositories.StatusFactureRepository;
import org.rma.ocrfacturebackend.utils.ArchivageUtils;
import org.rma.ocrfacturebackend.utils.FieldsExtractionUtils;
import org.rma.ocrfacturebackend.utils.FileUtils;
import org.rma.ocrfacturebackend.utils.OcrFactureUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.azure.ai.formrecognizer.documentanalysis.models.AnalyzedDocument;
import com.azure.ai.formrecognizer.documentanalysis.models.DocumentField;

import com.azure.ai.formrecognizer.documentanalysis.models.OperationResult;
import org.springframework.web.multipart.MultipartFile;

import java.net.SocketException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


import org.rma.ocrfacturebackend.services.FactureRMAService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.rma.ocrfacturebackend.utils.FileUtils.*;


@Slf4j
@Service
public class FactureRMAServiceImpl implements FactureRMAService {

    @Value("${cognitiveservices.key}")
    private String key;

    @Value("${cognitiveservices.endpoint}")
    private String endpoint;

    @Value("${cognitiveservices.modelId}")
    private String modelId;
    
    @Autowired
    private FactureRMARepository factureRMARepository;

    @Autowired
    private StatusFactureRepository statusFactureRepository;



    public boolean validatePdf(MultipartFile file) {
        if (file.getContentType().equalsIgnoreCase("application/pdf")) {
            log.info("The uploaded file is a valid PDF.");
            return true;
        } else {
            log.info("The uploaded file is not a valid PDF.");
            return false;
        }
    }



    public Map<String, String> extractInvoiceData(MultipartFile file) throws  ClassCastException {
        log.info("Inside extract Invoice");
        try {
            if (validatePdf(file)) {
                Map<String, Object> extractedData = new HashMap<>();


                // Create DocumentAnalysisClient instance and AzureKeyCredential
                DocumentAnalysisClient client = new DocumentAnalysisClientBuilder()
                        .credential(new AzureKeyCredential(key))
                        .endpoint(endpoint)
                        .buildClient();

                // Analyze the invoice document
                //Key value pairs
                AnalyzeResult analyzeInvoiceResult = client.beginAnalyzeDocument(modelId, FileUtils.convertFirstTwoPagesAsBinaryData(file)).getFinalResult();



                if (analyzeInvoiceResult.getKeyValuePairs() != null) {
                    List<DocumentKeyValuePair> documentKeyValuePairs = analyzeInvoiceResult.getKeyValuePairs();

                    // Convert key-value pairs to a simplified representation Map
                    for (DocumentKeyValuePair keyValuePair : documentKeyValuePairs) {
                        String keyContent = keyValuePair.getKey().getContent();
                        String valueContent = keyValuePair.getValue() != null ? keyValuePair.getValue().getContent() : "";

                        // Skip the key-value pair if the key has a specific value
                        if (valueContent.equals("001903162000085")) {
                            continue;
                        }

                        // Add the extracted key-value pair to the extractedData map
                        extractedData.put(keyContent, valueContent);
                    }
                }





                // Pre-built fields for the two first pages
                SyncPoller<OperationResult, AnalyzeResult> analyzeInvoicesPoller =
                        client.beginAnalyzeDocument(modelId, convertFirstTwoPagesAsBinaryData(file));

                AnalyzeResult analyzeInvoiceResultDoc = analyzeInvoicesPoller.getFinalResult();

                // Analyze each document/page
                List<AnalyzedDocument> analyzedDocuments = analyzeInvoiceResultDoc.getDocuments();

                for (int i = 0; i < analyzedDocuments.size(); i++) {
                    AnalyzedDocument analyzedInvoice = analyzedDocuments.get(i);

                    // Add the prebuilt fields to the extractedData map
                    Map<String, DocumentField> invoiceFields = analyzedInvoice.getFields();
                    for (Map.Entry<String, DocumentField> entry : invoiceFields.entrySet()) {
                        String fieldName = entry.getKey();
                        DocumentField field = entry.getValue();

                        extractedData.put(fieldName, field.getValue());
                    }

                }


                // Extract specific values from the extractedData map
                String iceValue = FieldsExtractionUtils.extractICE(extractedData);
                Double totalTtc = FieldsExtractionUtils.extractTotal(extractedData);
                Double totalHT = FieldsExtractionUtils.extractTotalHt(extractedData);
                String prestataire = FieldsExtractionUtils.extractPrestataire(extractedData);
                String invoiceId = FieldsExtractionUtils.extractInvoiceId(extractedData);
                String codeDossier = FieldsExtractionUtils.extractCodeDossier(extractedData);
                LocalDate invoiceDate = FieldsExtractionUtils.extractInvoiceDate(extractedData);
                String clientName = FieldsExtractionUtils.extractClientName(extractedData);
                String rmaIce = "001903162000085";



                // Create and add to the final HashMap
                HashMap<String, String> extractedDataFinal = new HashMap<>();
                extractedDataFinal.put("ice", iceValue);
                extractedDataFinal.put("totalTtc", totalTtc.toString());
                extractedDataFinal.put("totalHt", totalHT.toString());
                extractedDataFinal.put("prestataire", prestataire);
                extractedDataFinal.put("factureN", invoiceId);
                extractedDataFinal.put("codeDossier", codeDossier);
                extractedDataFinal.put("dateFacture", invoiceDate.toString());
                extractedDataFinal.put("client",clientName);
                extractedDataFinal.put("clientIce",rmaIce);


                log.info("Facture extracted successfully");
                return extractedDataFinal;

            } else {
                log.info("Please upload a valid PDF.");
                return new HashMap<>();
            }
        } catch (Exception ex) {
            log.error("An error occurred while processing the file.", ex);
            return new HashMap<>();
        }
    }




    public ResponseEntity<String> addNewFactures(List<MultipartFile> files) {
        try {
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (MultipartFile file : files) {
                CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> extractInvoiceData(file))
                        .thenAcceptAsync(requestMap -> {

                                String archivedFilePath = ArchivageUtils.saveInvoice(file); // Archive the uploaded PDF file

                                if (archivedFilePath != null) {
                                    FactureRMA factureRMA = getFactureAdd(requestMap);
                                    factureRMA.setPdfPath(archivedFilePath); // Set the archived file path
                                    factureRMARepository.save(factureRMA);
                                    log.info("Facture " + file.getOriginalFilename() + " added and archived successfully");
                                } else {
                                    log.error("Failed to save and archive the uploaded PDF file");
                                }

                        });

                futures.add(future);
            }

            // Wait for all the futures to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            return OcrFactureUtils.getResponseEntity("All files processed successfully", HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Failed to add the factures.", ex);
            return OcrFactureUtils.getResponseEntity(OcrFactureConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public ResponseEntity<List<FactureRMADto>> getAllFactures() {
        try {
            log.info("inside getAllFactures");

            return new ResponseEntity<>(factureRMARepository.getAllFacturesRMA(), HttpStatus.OK);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateFacture(Map<String, String> requestMap) {
        try {
            log.info("Inside Update");
            factureRMARepository.save(getFactureUpdate(requestMap));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return OcrFactureUtils.getResponseEntity(OcrFactureConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }





    private FactureRMA getFactureAdd(Map<String, String> requestMap) {
        FactureRMA factureRMA=new FactureRMA();
        StatusFacture statusFacture = new StatusFacture();

        factureRMA.setNumeroFacture(requestMap.get("factureN"));
        factureRMA.setIcePrestaire(requestMap.get("ice"));
        factureRMA.setTotalTtc(Double.parseDouble(requestMap.get("totalTtc")));
        factureRMA.setTotalHT(Double.parseDouble(requestMap.get("totalHt")));
        factureRMA.setNumeroFacture(requestMap.get("factureN"));
        factureRMA.setNomPrestataire(requestMap.get("prestataire"));
        factureRMA.setDateFacturation(LocalDate.parse(requestMap.get("dateFacture")));
        factureRMA.setCodeDossier(requestMap.get("codeDossier"));
        factureRMA.setNomClient(requestMap.get("client"));
        factureRMA.setIceClient(requestMap.get("clientIce"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        // Format the current LocalDateTime object
        String formattedDateTime = LocalDateTime.now().format(formatter);

        // Set the formatted date and time to the createdAt field
        factureRMA.setCreatedAt(LocalDateTime.parse(formattedDateTime, formatter));
        //?
        statusFacture.setId(1L);
        factureRMA.setStatusFacture(statusFacture);


        return factureRMA;
    }

    private FactureRMA getFactureUpdate(Map<String, String> requestMap) {
        FactureRMA factureRMA=new FactureRMA();
        StatusFacture statusFacture = new StatusFacture();

        factureRMA.setId(Long.valueOf(requestMap.get("id")));

        factureRMA.setNumeroFacture(requestMap.get("factureN"));
        factureRMA.setNomPrestataire(requestMap.get("prestataire"));


      /*  factureRMA.setNumeroFacture(requestMap.get("factureN"));
        factureRMA.setIcePrestaire(requestMap.get("ice"));
        factureRMA.setTotalTtc(Double.parseDouble(requestMap.get("totalTtc")));
        factureRMA.setTotalHT(Double.parseDouble(requestMap.get("totalHt")));
        factureRMA.setNumeroFacture(requestMap.get("factureN"));
        factureRMA.setNomPrestataire(requestMap.get("prestataire"));
        factureRMA.setDateFacturation(LocalDate.parse(requestMap.get("dateFacture")));
        factureRMA.setCodeDossier(requestMap.get("codeDossier"));
        factureRMA.setNomClient(requestMap.get("client"));
        factureRMA.setIceClient(requestMap.get("clientIce"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        // Format the current LocalDateTime object
        String formattedDateTime = LocalDateTime.now().format(formatter);

        // Set the formatted date and time to the createdAt field
        factureRMA.setCreatedAt(LocalDateTime.parse(formattedDateTime, formatter));*/
        //?
        statusFacture.setId(2L);
        factureRMA.setStatusFacture(statusFacture);


        return factureRMA;
    }



   /* private boolean validateFactureMap(Map<String, String> requestMap, boolean idValidated) {
        if(requestMap.containsKey("factureN")){
            if(requestMap.containsKey("id") && idValidated){
                return true;
            }else if(!idValidated){
                return true;
            }
        }
        return false;
    }*/
}


