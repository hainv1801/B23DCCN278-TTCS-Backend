package com.hainv.tourbooking.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.hainv.tourbooking.domain.Booking;
import com.hainv.tourbooking.domain.TourSchedule;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {
    List<Booking> findByUserId(long userId);

    List<Booking> findByTourScheduleId(long scheduleId);

    Page<Booking> findByUserEmail(String email, Pageable pageable);

    void deleteAllByTourSchedule(TourSchedule schedule);
}
