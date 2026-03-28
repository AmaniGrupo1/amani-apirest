package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Situacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SituacionRepository extends JpaRepository<Situacion, Long> {

    List<Situacion> findByActivoTrue();
}
