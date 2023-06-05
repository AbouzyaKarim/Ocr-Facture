package org.rma.ocrfacturebackend.services;

import org.rma.ocrfacturebackend.dtos.FactureRMADto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FactureRMAService {


    ResponseEntity<String> addNewFactures(List<MultipartFile> files);

    ResponseEntity<List<FactureRMADto>> getAllFactures();

    ResponseEntity<String> updateFacture(Map<String, String> requestMap);
}
