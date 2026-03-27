package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.config.DateTimeFormatConfiguration;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class CompanyService {
    private final DateTimeFormatConfiguration dateTimeFormatConfiguration;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository,
            DateTimeFormatConfiguration dateTimeFormatConfiguration,
            UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.dateTimeFormatConfiguration = dateTimeFormatConfiguration;
        this.userRepository = userRepository;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO handleGetCompany(Specification<Company> spec, Pageable pageable) {
        Page<Company> pCompany = this.companyRepository.findAll(spec, pageable);
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
        Optional<Company> optionalCompany = this.findCompanyById(id);
        if (optionalCompany.isPresent()) {
            Company company = optionalCompany.get();
            List<User> users = this.userRepository.findByCompany(company);
            this.userRepository.deleteAll(users);
        }
        this.companyRepository.deleteById(id);
    }

    public Optional<Company> findCompanyById(long id) {
        return this.companyRepository.findById(id);
    }
}
