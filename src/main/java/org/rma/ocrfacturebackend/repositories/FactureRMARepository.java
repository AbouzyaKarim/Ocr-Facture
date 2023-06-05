package org.rma.ocrfacturebackend.repositories;

import org.rma.ocrfacturebackend.dtos.FactureRMADto;
import org.rma.ocrfacturebackend.entities.FactureRMA;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface FactureRMARepository extends JpaRepository<FactureRMA,Long> {
    List<FactureRMADto> getAllFacturesRMA();


}
