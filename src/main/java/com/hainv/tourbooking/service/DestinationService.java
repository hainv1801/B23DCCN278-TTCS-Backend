package com.hainv.tourbooking.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hainv.tourbooking.config.DateTimeFormatConfiguration;
import com.hainv.tourbooking.domain.Destination;
import com.hainv.tourbooking.domain.User;
import com.hainv.tourbooking.domain.response.ResultPaginationDTO;
import com.hainv.tourbooking.repository.DestinationRepository;
import com.hainv.tourbooking.repository.UserRepository;

@Service
public class DestinationService {
    private final DateTimeFormatConfiguration dateTimeFormatConfiguration;
    private final DestinationRepository destinationRepository;
    private final UserRepository userRepository;

    public DestinationService(DestinationRepository destinationRepository,
            DateTimeFormatConfiguration dateTimeFormatConfiguration,
            UserRepository userRepository) {
        this.destinationRepository = destinationRepository;
        this.dateTimeFormatConfiguration = dateTimeFormatConfiguration;
        this.userRepository = userRepository;
    }

    public Destination handleCreateDestination(Destination destination) {
        return this.destinationRepository.save(destination);
    }

    public ResultPaginationDTO handleGetDestination(Specification<Destination> spec, Pageable pageable) {
        Page<Destination> pdestination = this.destinationRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pdestination.getTotalPages());
        mt.setTotal(pdestination.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pdestination.getContent());
        return rs;
    }

    public Destination handleGetDestinationById(long id) {
        Optional<Destination> optionalDestination = this.destinationRepository.findById(id);
        return optionalDestination.isPresent() == true
                ? optionalDestination.get()
                : null;
    }

    public Destination handleUpdateDestination(Destination destination) {
        Destination updatedDestination = this.handleGetDestinationById(destination.getId());
        if (updatedDestination != null) {
            updatedDestination.setLocation(destination.getLocation());
            updatedDestination.setDescription(destination.getDescription());
            updatedDestination.setImage(destination.getImage());
            updatedDestination.setName(destination.getName());
            return this.destinationRepository.save(updatedDestination);
        }
        return null;
    }

    public void handleDeleteDestination(long id) {
        this.destinationRepository.deleteById(id);
    }

    public Optional<Destination> findDestinationById(long id) {
        return this.destinationRepository.findById(id);
    }
}
