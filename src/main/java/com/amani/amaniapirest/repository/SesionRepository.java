package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Sesion;
import com.amani.amaniapirest.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SesionRepository extends JpaRepository<Sesion, Long>{

}