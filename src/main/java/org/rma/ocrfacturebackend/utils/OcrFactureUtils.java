package org.rma.ocrfacturebackend.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class OcrFactureUtils {
    public OcrFactureUtils(){

    }

    public static ResponseEntity<String> getResponseEntity(String responseMessage, HttpStatus httpStatus){
        return new ResponseEntity<String>("{\"message\":\""+responseMessage+"\"}", httpStatus);
    }
}
