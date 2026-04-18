package com.hainv.tourbooking.repository;

import com.hainv.tourbooking.domain.Tour;
import com.hainv.tourbooking.domain.TourSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TourScheduleRepository
        extends JpaRepository<TourSchedule, Long>, JpaSpecificationExecutor<TourSchedule> {

    // Lấy tất cả lịch khởi hành của 1 tour gốc
    List<TourSchedule> findByTourId(long tourId);

    void deleteAllByTour(Tour tour);

    // Lấy các lịch khởi hành sắp tới (ngày đi lớn hơn hiện tại) và còn mở bán
    List<TourSchedule> findByDepartureDateGreaterThanEqualAndStatus(LocalDate date, String status);
}