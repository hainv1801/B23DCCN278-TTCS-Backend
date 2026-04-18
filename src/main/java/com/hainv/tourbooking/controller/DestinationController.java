package com.hainv.tourbooking.controller;

import org.springframework.web.bind.annotation.RestController;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import com.hainv.tourbooking.domain.Destination;
import com.hainv.tourbooking.domain.response.ResultPaginationDTO;
import com.hainv.tourbooking.service.DestinationService;
import com.hainv.tourbooking.util.annotation.ApiMessage;
import com.hainv.tourbooking.util.error.IdInvalidException;

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
public class DestinationController {
    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    @PostMapping("/destinations")
    @ApiMessage("creation a destination")
    public ResponseEntity<Destination> createDestinaton(@Valid @RequestBody Destination destinaton) {
        Destination newDestinaton = this.destinationService.handleCreateDestination(destinaton);
        return ResponseEntity.status(HttpStatus.CREATED).body(newDestinaton);
    }

    @GetMapping("/destinations")
    @ApiMessage("fetch all destinations")
    public ResponseEntity<ResultPaginationDTO> getAllCompanies(
            @Filter Specification spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.destinationService.handleGetDestination(spec, pageable));
    }

    @GetMapping("/destinations/{id}")
    public ResponseEntity<Destination> getDestinatonById(@PathVariable("id") long id) throws IdInvalidException {
        if (this.destinationService.findDestinationById(id).isPresent() == false) {
            throw new IdInvalidException("Destination is not exist!");
        }
        Destination Destinaton = this.destinationService.handleGetDestinationById(id);
        return ResponseEntity.status(HttpStatus.OK).body(Destinaton);
    }

    @PutMapping("/destinations")
    public ResponseEntity<Destination> updateDestinaton(@Valid @RequestBody Destination destinaton)
            throws IdInvalidException {
        if (this.destinationService.findDestinationById(destinaton.getId()).isPresent() == false) {
            throw new IdInvalidException("Destination is not exist!");
        }
        Destination updatedDestinaton = this.destinationService.handleUpdateDestination(destinaton);
        return ResponseEntity.status(HttpStatus.OK).body(updatedDestinaton);
    }

    @DeleteMapping("/destinations/{id}")
    public ResponseEntity<Void> deleteDestinaton(@PathVariable("id") long id) throws IdInvalidException {
        if (this.destinationService.findDestinationById(id).isPresent() == false) {
            throw new IdInvalidException("Destination is not exist!");
        }
        this.destinationService.handleDeleteDestination(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
