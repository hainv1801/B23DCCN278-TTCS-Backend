package com.hainv.tourbooking.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;

import jakarta.validation.Valid;
import com.hainv.tourbooking.domain.Destination;
import com.hainv.tourbooking.domain.Tour;
import com.hainv.tourbooking.domain.TourSchedule;
import com.hainv.tourbooking.domain.Booking;
import com.hainv.tourbooking.domain.User;
import com.hainv.tourbooking.domain.request.ReqCreateBookingDTO;
import com.hainv.tourbooking.domain.response.ResBookingDTO;
import com.hainv.tourbooking.domain.response.ResultPaginationDTO;
import com.hainv.tourbooking.service.BookingService;
import com.hainv.tourbooking.service.TourScheduleService;
import com.hainv.tourbooking.service.UserService;
import com.hainv.tourbooking.util.SecurityUtil;
import com.hainv.tourbooking.util.annotation.ApiMessage;
import com.hainv.tourbooking.util.constant.BookingStatusEnum;
import com.hainv.tourbooking.util.constant.TourStatusEnum;
import com.hainv.tourbooking.util.error.IdInvalidException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class BookingController {
    private final AuthController authController;
    private final BookingService bookingService;
    private final UserService userService;
    private final TourScheduleService tourScheduleService;
    private final FilterBuilder filterBuilder;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public BookingController(BookingService bookingService, UserService userService, FilterBuilder filterBuilder,
            FilterSpecificationConverter filterSpecificationConverter,
            TourScheduleService tourScheduleService, AuthController authController) {
        this.bookingService = bookingService;
        this.userService = userService;
        this.tourScheduleService = tourScheduleService;
        this.filterBuilder = filterBuilder;
        this.filterSpecificationConverter = filterSpecificationConverter;
        this.authController = authController;
    }

    @PostMapping("/bookings")
    @ApiMessage("create a booking")
    public ResponseEntity<ResBookingDTO> createBooking(@Valid @RequestBody ReqCreateBookingDTO reqCreateBookingDTO)
            throws IdInvalidException {

        TourSchedule schedule = tourScheduleService.fetchScheduleById(reqCreateBookingDTO.getTourScheduleId());
        if (schedule == null) {
            throw new IdInvalidException("Lịch trình không tồn tại!");
        }

        User user = userService.fetchUserById(reqCreateBookingDTO.getUserId());
        if (user == null) {
            throw new IdInvalidException("User không tồn tại");
        }

        int totalPassengers = reqCreateBookingDTO.getTotalAdults() + reqCreateBookingDTO.getTotalChildren();

        if (!TourStatusEnum.OPEN.equals(schedule.getStatus())) {
            throw new IdInvalidException("Lịch khởi hành này đã đóng hoặc đã hủy.");
        }
        if (schedule.getBookedSeats() + totalPassengers > schedule.getCapacity()) {
            throw new IdInvalidException("Không đủ chỗ trống! Chuyến đi chỉ còn "
                    + (schedule.getCapacity() - schedule.getBookedSeats()) + " chỗ.");
        }
        ResBookingDTO res = this.bookingService.handleCreateBooking(reqCreateBookingDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/bookings")
    @ApiMessage("update booking")
    public ResponseEntity<ResBookingDTO> updateResume(@RequestBody Booking booking)
            throws IdInvalidException {
        Optional<Booking> optionalBooking = this.bookingService.findBookingById(booking.getId());
        if (optionalBooking.isPresent() == false) {
            throw new IdInvalidException("Booking không tồn tại!");
        }
        return ResponseEntity.ok(this.bookingService.handleUpdateBooking(booking));
    }

    @DeleteMapping("/bookings/{id}")
    @ApiMessage("delete booking")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Booking> optionalBooking = this.bookingService.findBookingById(id);
        if (optionalBooking.isEmpty()) {
            throw new IdInvalidException("Booking khong ton tai");
        }
        this.bookingService.deleteBookingById(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/bookings/{id}")
    public ResponseEntity<ResBookingDTO> getBookingById(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Booking> optionalBooking = this.bookingService.findBookingById(id);
        if (optionalBooking.isEmpty()) {
            throw new IdInvalidException("Booking khong ton tai");
        }
        Booking currentBooking = optionalBooking.get();
        return ResponseEntity.ok(this.bookingService.convertToResBookingDTO(currentBooking));
    }

    @GetMapping("/bookings")
    @ApiMessage("Fetch all bookings with pagination and filter")
    public ResponseEntity<ResultPaginationDTO> getAllBookings(
            @Filter Specification<Booking> spec,
            Pageable pageable) {
        return ResponseEntity.ok(bookingService.fetchAllBookings(spec, pageable));
    }

    @PostMapping("/bookings/by-user")
    @ApiMessage("Get list bookings by user")
    public ResponseEntity<ResultPaginationDTO> fetchBookingByUser(Pageable pageable) {

        return ResponseEntity.ok().body(this.bookingService.fetchBookingByUser(pageable));
    }
}
