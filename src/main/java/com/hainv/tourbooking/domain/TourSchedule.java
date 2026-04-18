package com.hainv.tourbooking.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hainv.tourbooking.util.SecurityUtil;
import com.hainv.tourbooking.util.constant.TourStatusEnum;

@Entity
@Table(name = "tour_schedules")
@Getter
@Setter
public class TourSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id")
    @JsonIgnoreProperties("tourSchedules")
    private Tour tour;

    @OneToMany(mappedBy = "tourSchedule", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Booking> bookings;

    private LocalDate departureDate; // Ngày khởi hành
    private LocalDate returnDate; // Ngày về
    private double priceAdult; // Giá người lớn cho chuyến này
    private double priceChild; // Giá trẻ em cho chuyến này
    private int capacity; // Tổng số chỗ của chuyến này
    private int bookedSeats; // Số chỗ đã có người đặt

    @Enumerated(EnumType.STRING)
    private TourStatusEnum status;

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