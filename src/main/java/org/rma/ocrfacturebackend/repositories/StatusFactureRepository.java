package org.rma.ocrfacturebackend.repositories;

import org.rma.ocrfacturebackend.entities.StatusFacture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "statusFacture")
public interface StatusFactureRepository extends JpaRepository<StatusFacture, Long> {

}
