package com.hainv.tourbooking.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import com.hainv.tourbooking.domain.Tour;
import com.hainv.tourbooking.domain.request.ReqTourDTO;
import com.hainv.tourbooking.domain.response.ResultPaginationDTO;
import com.hainv.tourbooking.domain.response.tour.ResCreateTourDTO;
import com.hainv.tourbooking.domain.response.tour.ResTourDTO;
import com.hainv.tourbooking.domain.response.tour.ResUpdateTourDTO;
import com.hainv.tourbooking.service.TourService;
import com.hainv.tourbooking.util.annotation.ApiMessage;
import com.hainv.tourbooking.util.error.IdInvalidException;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")
public class TourController {
    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @PostMapping("/tours")
    @ApiMessage("create a tour")
    public ResponseEntity<ResTourDTO> createTour(@Valid @RequestBody ReqTourDTO reqDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.tourService.handleCreateTour(reqDTO));
    }

    @GetMapping("/tours")
    @ApiMessage("get all tours")
    public ResponseEntity<ResultPaginationDTO> getAllTours(
            @Filter Specification<Tour> spec, Pageable pageable) {
        return ResponseEntity.ok(this.tourService.fetchAllTours(spec, pageable));
    }

    @GetMapping("/tours/{id}")
    @ApiMessage("get tour by id")
    public ResponseEntity<ResTourDTO> getTourById(@PathVariable("id") Long id) throws IdInvalidException {
        Optional<Tour> optionalTour = this.tourService.fetchTourById(id);
        if (optionalTour.isPresent() == false) {
            throw new IdInvalidException("Tour với id = " + id + "không tồn tại!!");
        }
        return ResponseEntity.ok(this.tourService.convertToResTourDTO(optionalTour.get()));
    }

    @PutMapping("/tours")
    @ApiMessage("update a tour")
    public ResponseEntity<ResTourDTO> updateTour(@Valid @RequestBody ReqTourDTO reqTourDTO) throws IdInvalidException {
        Optional<Tour> optionalTour = this.tourService.fetchTourById(reqTourDTO.getId());
        if (optionalTour.isPresent() == false) {
            throw new IdInvalidException("Tour với id = " + reqTourDTO.getId() + "không tồn tại!!");
        }
        ResTourDTO res = this.tourService.handleUpdateTour(reqTourDTO, optionalTour.get());
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/tours/{id}")
    @ApiMessage("delete a tour")
    public ResponseEntity<Void> deleteTour(@PathVariable("id") Long id) throws IdInvalidException {
        Optional<Tour> optionalTour = this.tourService.fetchTourById(id);
        if (optionalTour.isPresent() == false) {
            throw new IdInvalidException("Tour với id = " + id + "không tồn tại!!");
        }
        this.tourService.handleDeleteTour(id);
        return ResponseEntity.ok(null);
    }
}
