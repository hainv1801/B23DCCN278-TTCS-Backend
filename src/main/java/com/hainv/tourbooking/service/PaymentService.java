package com.hainv.tourbooking.service;

import com.hainv.tourbooking.domain.Booking;
import com.hainv.tourbooking.domain.Payment;

import com.hainv.tourbooking.domain.response.ResultPaginationDTO;
import com.hainv.tourbooking.repository.BookingRepository;
import com.hainv.tourbooking.repository.PaymentRepository;
import com.hainv.tourbooking.util.constant.PaymentStatusEnum;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentService(PaymentRepository paymentRepository, BookingRepository bookingRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public Payment handleCreatePayment(Payment payment) {
        Booking booking = bookingRepository.findById(payment.getBooking().getId())
                .orElse(null);
        Payment newPayment = new Payment();
        newPayment.setBooking(booking);
        newPayment.setPaymentDate(Instant.now());

        if (PaymentStatusEnum.SUCCESS.equals(payment.getStatus())) {
            booking.setPaymentStatus(PaymentStatusEnum.SUCCESS);
            bookingRepository.save(booking);
        }

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment handleUpdatePayment(Payment paymentReq) {
        Optional<Payment> paymentOptional = this.paymentRepository.findById(paymentReq.getId());
        if (paymentOptional.isPresent()) {
            Payment currentPayment = paymentOptional.get();

            currentPayment.setStatus(paymentReq.getStatus());
            currentPayment.setAmount(paymentReq.getAmount());
            currentPayment.setTransactionCode(paymentReq.getTransactionCode());
            currentPayment.setBankCode(paymentReq.getBankCode());
            currentPayment.setPaymentResponse(paymentReq.getPaymentResponse());
            currentPayment.setPaymentDate(paymentReq.getPaymentDate());

            // Cập nhật lại trạng thái Booking nếu thanh toán thành công
            Booking booking = currentPayment.getBooking();
            if (PaymentStatusEnum.SUCCESS.equals(paymentReq.getStatus())) {
                booking.setPaymentStatus(PaymentStatusEnum.SUCCESS);
            } else if (PaymentStatusEnum.REFUNDED.equals(paymentReq.getStatus())) {
                booking.setPaymentStatus(PaymentStatusEnum.REFUNDED);
            }
            this.bookingRepository.save(booking);

            return this.paymentRepository.save(currentPayment);
        }
        return null;
    }

    public Payment fetchPaymentById(long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    public ResultPaginationDTO fetchAllPayments(Specification<Payment> spec, Pageable pageable) {
        Page<Payment> pagePayment = paymentRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pagePayment.getTotalPages());
        mt.setTotal(pagePayment.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pagePayment.getContent());
        return rs;
    }

    public Payment fetchPaymentByBookingId(long id) {
        return this.paymentRepository.findByBookingId(id);
    }
}