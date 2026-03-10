package com.amani.amaniapirest.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pacientes")
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPaciente;
    private LocalDate fechaNacimiento;
    private String genero;
    private String telefono;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    @OneToMany(mappedBy = "paciente")
    private List<Direccion> direcciones;
    @OneToMany(mappedBy = "paciente")
    private List<Cita> citas;
    @OneToMany(mappedBy = "paciente")
    private List<HistorialClinico> historiales;
}
