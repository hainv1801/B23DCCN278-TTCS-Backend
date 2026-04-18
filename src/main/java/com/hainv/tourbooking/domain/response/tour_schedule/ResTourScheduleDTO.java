package com.hainv.tourbooking.domain.response.tour_schedule;

import java.time.LocalDate;

import com.hainv.tourbooking.util.constant.TourStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResTourScheduleDTO {
    private long id;
    private LocalDate departureDate;
    private LocalDate returnDate;
    private double priceAdult; // Giá người lớn cho chuyến này
    private double priceChild; // Giá trẻ em cho chuyến này
    private int capacity; // Tổng số chỗ của chuyến này
    private int bookedSeats; // Số chỗ đã có người đặt
    private TourStatusEnum status;
    private TourInfo tourInfo;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TourInfo {
        private long id;
        private String name;
    }
}
