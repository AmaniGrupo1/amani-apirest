package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.enums.TipoDocumentoLegal;
import com.amani.amaniapirest.models.DocumentoLegal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentoLegalRepository extends JpaRepository<DocumentoLegal, Long> {

    List<DocumentoLegal> findByTipoAndActivoTrueOrderByOrdenVisualizacionAsc(
            TipoDocumentoLegal tipo
    );
}
