package org.rma.ocrfacturebackend.controllers;

import org.rma.ocrfacturebackend.dtos.FactureRMADto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/api")
public interface FactureRMAContoller {


    @GetMapping(path = "/getAll")
    ResponseEntity<List<FactureRMADto>> getAllFactures();

    @PostMapping(path = "/addNewFactures",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    ResponseEntity<String> addNewFactures(@RequestParam("files") List<MultipartFile> files);

    @PostMapping(path = "/updateFacture")
    ResponseEntity<String> updateFacture(@RequestBody(required = true)Map<String,String> requestMap);




}
