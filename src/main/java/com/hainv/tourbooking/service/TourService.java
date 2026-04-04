package com.hainv.tourbooking.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hainv.tourbooking.config.DateTimeFormatConfiguration;
import com.hainv.tourbooking.domain.Tour;
import com.hainv.tourbooking.domain.User;
import com.hainv.tourbooking.domain.response.ResultPaginationDTO;
import com.hainv.tourbooking.repository.TourRepository;
import com.hainv.tourbooking.repository.UserRepository;

@Service
public class TourService {
    private final DateTimeFormatConfiguration dateTimeFormatConfiguration;
    private final TourRepository TourRepository;
    private final UserRepository userRepository;

    public TourService(TourRepository TourRepository,
            DateTimeFormatConfiguration dateTimeFormatConfiguration,
            UserRepository userRepository) {
        this.TourRepository = TourRepository;
        this.dateTimeFormatConfiguration = dateTimeFormatConfiguration;
        this.userRepository = userRepository;
    }

    public Tour handleCreateCompany(Tour company) {
        return this.TourRepository.save(company);
    }

    public ResultPaginationDTO handleGetCompany(Specification<Tour> spec, Pageable pageable) {
        Page<Tour> pCompany = this.TourRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pCompany.getTotalPages());
        mt.setTotal(pCompany.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pCompany.getContent());
        return rs;
    }

    public Tour handleGetCompanyById(long id) {
        Optional<Tour> optionalCompany = this.TourRepository.findById(id);
        return optionalCompany.isPresent() == true
                ? optionalCompany.get()
                : null;
    }

    public Tour handleUpdateCompany(Tour company) {
        Tour updatedCompany = this.handleGetCompanyById(company.getId());
        if (updatedCompany != null) {
            updatedCompany.setAddress(company.getAddress());
            updatedCompany.setDescription(company.getDescription());
            updatedCompany.setLogo(company.getLogo());
            updatedCompany.setName(company.getName());
            return this.TourRepository.save(updatedCompany);
        }
        return null;
    }

    public void handleDeleteCompany(long id) {
        Optional<Tour> optionalCompany = this.findCompanyById(id);
        if (optionalCompany.isPresent()) {
            Tour company = optionalCompany.get();
            List<User> users = this.userRepository.findByCompany(company);
            this.userRepository.deleteAll(users);
        }
        this.TourRepository.deleteById(id);
    }

    public Optional<Tour> findCompanyById(long id) {
        return this.TourRepository.findById(id);
    }
}
