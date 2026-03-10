package com.amani.amaniapirest.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "psicologos")
public class Psicologo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPsicologo;
    private String especialidad;
    private int experiencia;
    private String descripcion;
    private String licencia;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    @OneToMany(mappedBy = "psicologo")
    private List<Cita> citas;
}
