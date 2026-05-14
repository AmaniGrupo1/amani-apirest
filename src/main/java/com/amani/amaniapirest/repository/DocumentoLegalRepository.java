package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.enums.TipoDocumentoLegal;
import com.amani.amaniapirest.models.DocumentoLegal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentoLegalRepository extends JpaRepository<DocumentoLegal, Long> {

    List<DocumentoLegal> findByTipoAndActivoTrueOrderByOrdenVisualizacionAsc(
            TipoDocumentoLegal tipo
    );

    Optional<DocumentoLegal> findByTipoAndActivoTrue(
            TipoDocumentoLegal tipo
    );
}
