package org.rma.ocrfacturebackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FactureRMADto {

    private Long id;

    private String icePrestataire;

    private String codeDossier;

    private String numeroFacture;

    private LocalDate dateFacturation;

    private double totalTtc;

    private double totalHT;

    private String nomPrestataire;

    private String nomClient;

    private String iceClient;

    private Long statusId;
    private String status;

}
