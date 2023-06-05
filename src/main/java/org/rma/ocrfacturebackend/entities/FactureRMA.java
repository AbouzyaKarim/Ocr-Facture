package org.rma.ocrfacturebackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;



@NamedQuery(name = "FactureRMA.getAllFacturesRMA",query = "select new org.rma.ocrfacturebackend.dtos.FactureRMADto(f.id,f.icePrestaire,f.codeDossier,f.numeroFacture,f.dateFacturation,f.totalTtc,f.totalHT,f.nomPrestataire,f.nomClient,f.iceClient, f.statusFacture.id, f.statusFacture.status) from FactureRMA f ")

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@ToString

@Table(name = "factureRma")
public class FactureRMA {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "icePrestataire")
    private String icePrestaire;

    @Column(name = "codeDossier")
    private String codeDossier;

    @Column(name = "numeroPrestation")
    private Integer numeroPrestation;

    @Column(name = "numeroFacture")
    private String numeroFacture;

    @Column(name = "totalTtc")
    private double totalTtc;

    @Column(name = "totalHt")
    private double totalHT;

    @Column(name = "dateFacturation")
    private LocalDate dateFacturation;

    @Column(name = "nomPrestataire")
    private String nomPrestataire;

    @Column(name = "nomClient")
    private String nomClient;

    @Column(name = "iceClient")
    private String iceClient;

    @Column(name = "createdAt")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime createdAt;

    @Column(name = "pdf_path")
    private String pdfPath;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_fk", nullable = true)
    private StatusFacture statusFacture;

}
