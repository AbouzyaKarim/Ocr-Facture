package org.rma.ocrfacturebackend.controllersImpl;

import org.rma.ocrfacturebackend.constants.OcrFactureConstant;
import org.rma.ocrfacturebackend.controllers.FactureRMAContoller;
import org.rma.ocrfacturebackend.dtos.FactureRMADto;
import org.rma.ocrfacturebackend.services.FactureRMAService;
import org.rma.ocrfacturebackend.utils.OcrFactureUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController

public class FactureRMAControllerImpl implements FactureRMAContoller {

    @Autowired
    FactureRMAService factureRMAService;



    @Override
    public ResponseEntity<List<FactureRMADto>> getAllFactures() {
        try{
            return factureRMAService.getAllFactures();
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return new ResponseEntity<>(new ArrayList<>() , HttpStatus.INTERNAL_SERVER_ERROR);

    }


    @Override
    public ResponseEntity<String> addNewFactures(List<MultipartFile> files) {
        try{
            return factureRMAService.addNewFactures(files);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return OcrFactureUtils.getResponseEntity(OcrFactureConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateFacture(Map<String, String> requestMap) {
        try {
            return factureRMAService.updateFacture(requestMap);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return OcrFactureUtils.getResponseEntity(OcrFactureConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}




