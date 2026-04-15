package com.amani.amaniapirest.models;


import com.amani.amaniapirest.enums.EstadoPago;
import com.amani.amaniapirest.enums.MetodoPago;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPago;

    @Column(nullable = false)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estadoPago = EstadoPago.PENDIENTE;

    private LocalDateTime fechaPago;

    // 🔥 RELACIÓN PRINCIPAL
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cita", nullable = false, unique = true)
    @JsonIgnoreProperties({"pago"})
    private Cita cita;

    @PrePersist
    protected void onCreate() {
        if (fechaPago == null && estadoPago == EstadoPago.PAGADO) {
            this.fechaPago = LocalDateTime.now();
        }
    }
}
