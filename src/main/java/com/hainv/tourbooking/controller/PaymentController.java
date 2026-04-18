package com.hainv.tourbooking.controller;

import com.hainv.tourbooking.domain.Booking;
import com.hainv.tourbooking.domain.Payment;
import com.hainv.tourbooking.domain.response.ResultPaginationDTO;
import com.hainv.tourbooking.service.BookingService;
import com.hainv.tourbooking.service.PaymentService;
import com.hainv.tourbooking.util.annotation.ApiMessage;
import com.hainv.tourbooking.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class PaymentController {

    private final PaymentService paymentService;
    private final BookingService bookingService;

    public PaymentController(PaymentService paymentService, BookingService bookingService) {
        this.paymentService = paymentService;
        this.bookingService = bookingService;
    }

    @PostMapping("/payments")
    @ApiMessage("Create a new payment record")
    public ResponseEntity<Payment> createPayment(@Valid @RequestBody Payment payment) throws IdInvalidException {
        Booking booking = this.bookingService.findBookingById(payment.getBooking().getId()).orElse(null);
        if (booking == null) {
            throw new IdInvalidException("Đơn hàng không tồn tại!!");
        }
        if (this.paymentService.fetchPaymentById(booking.getId()) != null) {
            throw new IdInvalidException("Đơn hàng đã được thanh toán!");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.paymentService.handleCreatePayment(payment));
    }

    @GetMapping("/payments/{id}")
    @ApiMessage("Get payment detail by id")
    public ResponseEntity<Payment> getPayment(@PathVariable("id") long id) throws IdInvalidException {
        Payment p = paymentService.fetchPaymentById(id);
        if (p == null)
            throw new IdInvalidException("Giao dịch không tồn tại");
        return ResponseEntity.ok(p);
    }

    @GetMapping("/payments")
    @ApiMessage("Fetch all payments with pagination")
    public ResponseEntity<ResultPaginationDTO> getAllPayments(
            @Filter Specification<Payment> spec,
            Pageable pageable) {
        return ResponseEntity.ok(paymentService.fetchAllPayments(spec, pageable));
    }

    @PutMapping("/payments")
    @ApiMessage("Update a payment record")
    public ResponseEntity<Payment> updatePayment(@Valid @RequestBody Payment payment) throws IdInvalidException {
        Payment updated = this.paymentService.handleUpdatePayment(payment);
        if (updated == null) {
            throw new IdInvalidException("Thông tin thanh toán không tồn tại");
        }
        return ResponseEntity.ok(updated);
    }
}