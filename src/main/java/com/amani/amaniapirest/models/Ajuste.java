package com.amani.amaniapirest.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ajustes")
public class Ajuste {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAjuste;
    private String idioma;
    private boolean notificaciones;
    private boolean darkMode;
    private String timezone;
    private LocalDateTime updatedAt;
    @OneToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
}
