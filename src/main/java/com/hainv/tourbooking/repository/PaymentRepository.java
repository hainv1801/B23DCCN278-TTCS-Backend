package com.hainv.tourbooking.repository;

import com.hainv.tourbooking.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {
    Payment findByBookingId(long bookingId);

    Payment findByTransactionCode(String transactionCode);
}