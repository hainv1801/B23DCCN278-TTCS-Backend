package com.hainv.tourbooking.domain.response;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

import com.hainv.tourbooking.util.constant.PaymentMethodEnum;

@Getter
@Setter
public class ResPaymentDTO {
    private long id;
    private double amount;
    private PaymentMethodEnum paymentMethod;
    private String status;
    private String transactionCode;
    private String bankCode;
    private Instant paymentDate;
    private long bookingId;
}