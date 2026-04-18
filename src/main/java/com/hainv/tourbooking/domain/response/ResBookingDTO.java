package com.hainv.tourbooking.domain.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

import com.hainv.tourbooking.util.constant.BookingStatusEnum;
import com.hainv.tourbooking.util.constant.PaymentStatusEnum;

@Getter
@Setter
@NoArgsConstructor
public class ResBookingDTO {
    private long id;
    private Instant bookingDate;
    private int totalAdults;
    private int totalChildren;
    private double totalPrice;
    private BookingStatusEnum status;
    private PaymentStatusEnum paymentStatus;
    private String note;

    private UserInfo user;
    private ScheduleInfo schedule;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserInfo {
        private long id;
        private String name;
        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ScheduleInfo {
        private long id;
        private String tourName; // Lấy từ Tour gốc
    }
}