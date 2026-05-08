package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Pago, Long> {

    Optional<Pago> findByCitaIdCita(Long citaId);

    Optional<Pago> findByStripePaymentIntentId(String paymentIntentId);

    Optional<Pago> findByStripeChargeId(String chargeId);

    boolean existsByStripePaymentIntentId(String paymentIntentId);
}
