package com.amani.amaniapirest.models;

import com.amani.amaniapirest.enums.EstadoPago;
import com.amani.amaniapirest.enums.MetodoPago;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que registra la transacción económica asociada a una cita terapéutica.
 *
 * <p>Cada pago está vinculado de forma unívoca a una {@link Cita} y gestiona el
 * estado del cobro a través del proveedor de pagos Stripe. Almacena los identificadores
 * de PaymentIntent y Charge de Stripe para permitir auditorías, reembolsos y
 * reconciliación. La clave de idempotencia evita duplicados en caso de reintentos.</p>
 *
 * @author Ivan Lopez
 * @since 1.0
 */
@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    /** Identificador único del pago. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPago;

    /** Importe a cobrar por la sesión, en la moneda indicada por {@link #currency}. */
    @Column(nullable = false)
    private BigDecimal monto;

    /** Método de pago utilizado según {@link MetodoPago} (p.ej. TARJETA, SEPA). */
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, length = 50)
    private MetodoPago metodoPago;

    /** Estado actual del pago según {@link EstadoPago}. Por defecto {@code PENDIENTE}. */
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private EstadoPago estadoPago = EstadoPago.PENDIENTE;

    /** Identificador del PaymentIntent de Stripe asociado a esta transacción. */
    @Column(name = "stripe_payment_intent_id", length = 255)
    private String stripePaymentIntentId;

    /** Identificador del Charge de Stripe emitido al confirmar el pago. */
    @Column(name = "stripe_charge_id", length = 255)
    private String stripeChargeId;

    /** Clave de idempotencia para evitar cobros duplicados en caso de reintentos. */
    @Column(name = "idempotency_key", length = 255)
    private String idempotencyKey;

    /** Código ISO 4217 de la moneda del pago (por defecto {@code EUR}). */
    @Column(nullable = false, length = 3)
    @Builder.Default
    private String currency = "EUR";

    /** Fecha y hora en que el pago fue efectivamente confirmado por Stripe. */
    private LocalDateTime fechaPago;

    /** Fecha y hora de creación del registro de pago. */
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /** Fecha y hora de la última actualización del registro de pago. */
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    /** Cita a la que corresponde este pago. La relación es 1:1 y obligatoria. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cita", nullable = false, unique = true)
    @JsonIgnoreProperties({"pago"})
    private Cita cita;

    /**
     * Inicializa las fechas de auditoría al persistir el pago por primera vez.
     *
     * <p>Si el estado ya es {@code PAGADO} en el momento de la creación, también
     * registra la {@link #fechaPago}.</p>
     */
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        if (fechaPago == null && estadoPago == EstadoPago.PAGADO) {
            this.fechaPago = LocalDateTime.now();
        }
    }

    /**
     * Actualiza la fecha de modificación y registra la {@link #fechaPago} cuando el
     * estado transiciona a {@code PAGADO}.
     */
    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
        if (estadoPago == EstadoPago.PAGADO && fechaPago == null) {
            this.fechaPago = LocalDateTime.now();
        }
    }
}
