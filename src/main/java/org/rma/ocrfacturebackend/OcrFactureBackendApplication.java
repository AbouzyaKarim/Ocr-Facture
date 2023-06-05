package org.rma.ocrfacturebackend;

import org.rma.ocrfacturebackend.entities.FactureRMA;
import org.rma.ocrfacturebackend.entities.StatusFacture;
import org.rma.ocrfacturebackend.repositories.FactureRMARepository;
import org.rma.ocrfacturebackend.repositories.StatusFactureRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.Date;

@SpringBootApplication
public class OcrFactureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(OcrFactureBackendApplication.class, args);
    }



}
