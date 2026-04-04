package com.hainv.tourbooking.controller;

import org.springframework.web.bind.annotation.RestController;

import com.hainv.tourbooking.domain.Tour;
import com.hainv.tourbooking.domain.response.ResultPaginationDTO;
import com.hainv.tourbooking.service.TourService;
import com.hainv.tourbooking.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1")
public class TourController {
    private final TourService TourService;

    public TourController(TourService TourService) {
        this.TourService = TourService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Tour> createCompany(@Valid @RequestBody Tour company) {
        // TODO: process POST request
        Tour newCompany = this.TourService.handleCreateCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
    }

    @GetMapping("/companies")
    @ApiMessage("fetch all companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompanies(
            @Filter Specification spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.TourService.handleGetCompany(spec, pageable));
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<Tour> getCompanyById(@PathVariable("id") long id) {
        Tour company = this.TourService.handleGetCompanyById(id);
        return ResponseEntity.status(HttpStatus.OK).body(company);
    }

    @PutMapping("/companies")
    public ResponseEntity<Tour> updateCompany(@Valid @RequestBody Tour company) {
        Tour updatedCompany = this.TourService.handleUpdateCompany(company);
        return ResponseEntity.status(HttpStatus.OK).body(updatedCompany);
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") long id) {
        this.TourService.handleDeleteCompany(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
