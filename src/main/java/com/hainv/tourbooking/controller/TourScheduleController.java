package com.hainv.tourbooking.controller;

import com.hainv.tourbooking.domain.TourSchedule;
import com.hainv.tourbooking.domain.response.ResultPaginationDTO;
import com.hainv.tourbooking.domain.response.tour_schedule.ResTourScheduleDTO;
import com.hainv.tourbooking.service.TourScheduleService;
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
public class TourScheduleController {

    private final TourScheduleService tourScheduleService;

    public TourScheduleController(TourScheduleService tourScheduleService) {
        this.tourScheduleService = tourScheduleService;
    }

    @PostMapping("/tour-schedules")
    @ApiMessage("Create a new tour schedule")
    public ResponseEntity<ResTourScheduleDTO> createSchedule(@Valid @RequestBody TourSchedule schedule) {
        ResTourScheduleDTO newSchedule = this.tourScheduleService.handleCreateSchedule(schedule);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSchedule);
    }

    @GetMapping("/tour-schedules/{id}")
    @ApiMessage("Get tour schedule by id")
    public ResponseEntity<ResTourScheduleDTO> getScheduleById(@PathVariable("id") long id) throws IdInvalidException {
        TourSchedule schedule = this.tourScheduleService.fetchScheduleById(id);
        if (schedule == null) {
            throw new IdInvalidException("Lịch trình với id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok(this.tourScheduleService.convertToResTourScheduleDTO(schedule));
    }

    @PutMapping("/tour-schedules")
    @ApiMessage("Update a tour schedule")
    public ResponseEntity<ResTourScheduleDTO> updateSchedule(@Valid @RequestBody TourSchedule schedule)
            throws IdInvalidException {
        TourSchedule updated = this.tourScheduleService.fetchScheduleById(schedule.getId());
        if (updated == null) {
            throw new IdInvalidException("Lịch trình với id = " + schedule.getId() + " không tồn tại");
        }
        return ResponseEntity.ok(this.tourScheduleService.convertToResTourScheduleDTO(updated));
    }

    @GetMapping("/tour-schedules")
    @ApiMessage("Fetch all tour schedules with pagination and filter")
    public ResponseEntity<ResultPaginationDTO> getAllSchedules(
            @Filter Specification<TourSchedule> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.tourScheduleService.fetchAllSchedules(spec, pageable));
    }

    @DeleteMapping("/tour-schedules/{id}")
    @ApiMessage("Delete a tour schedule")
    public ResponseEntity<Void> deleteSchedule(@PathVariable("id") long id) throws IdInvalidException {
        if (this.tourScheduleService.fetchScheduleById(id) == null) {
            throw new IdInvalidException("Lịch trình với id = " + id + " không tồn tại");
        }
        this.tourScheduleService.handleDeleteSchedule(id);
        return ResponseEntity.ok(null);
    }
}