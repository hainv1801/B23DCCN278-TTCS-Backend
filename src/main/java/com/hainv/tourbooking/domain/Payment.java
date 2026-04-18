package com.hainv.tourbooking.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

import com.hainv.tourbooking.util.SecurityUtil;
import com.hainv.tourbooking.util.constant.PaymentMethodEnum;
import com.hainv.tourbooking.util.constant.PaymentStatusEnum;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // Quan hệ 1-1: 1 Thanh toán chỉ dành cho ĐÚNG 1 Đơn đặt tour
    // unique = true đảm bảo không bao giờ có 2 record payment trỏ cùng về 1
    // booking_id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    private double amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethodEnum paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatusEnum status;

    private String transactionCode;
    private String bankCode;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String paymentResponse;

    private Instant paymentDate;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        this.updatedAt = Instant.now();
    }
}