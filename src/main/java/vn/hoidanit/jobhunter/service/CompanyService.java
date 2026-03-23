package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.config.DateTimeFormatConfiguration;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;

@Service
public class CompanyService {
    private final DateTimeFormatConfiguration dateTimeFormatConfiguration;
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository,
            DateTimeFormatConfiguration dateTimeFormatConfiguration) {
        this.companyRepository = companyRepository;
        this.dateTimeFormatConfiguration = dateTimeFormatConfiguration;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO handleGetCompany(Pageable pageable) {
        Page<Company> pCompany = this.companyRepository.findAll(pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta mt = new Meta();

        mt.setPage(pCompany.getNumber() + 1);
        mt.setPageSize(pCompany.getSize());

        mt.setPages(pCompany.getTotalPages());
        mt.setTotal(pCompany.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pCompany.getContent());
        return rs;
    }

    public Company handleGetCompanyById(long id) {
        Optional<Company> optionalCompany = this.companyRepository.findById(id);
        return optionalCompany.isPresent() == true
                ? optionalCompany.get()
                : null;
    }

    public Company handleUpdateCompany(Company company) {
        Company updatedCompany = this.handleGetCompanyById(company.getId());
        if (updatedCompany != null) {
            updatedCompany.setAddress(company.getAddress());
            updatedCompany.setDescription(company.getDescription());
            updatedCompany.setLogo(company.getLogo());
            updatedCompany.setName(company.getName());
            return this.companyRepository.save(updatedCompany);
        }
        return null;
    }

    public void handleDeleteCompany(long id) {
        Company deletedCompany = this.handleGetCompanyById(id);
        if (deletedCompany != null) {
            this.companyRepository.delete(deletedCompany);
        }
    }
}
