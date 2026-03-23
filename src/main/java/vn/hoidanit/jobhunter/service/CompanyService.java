package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.config.DateTimeFormatConfiguration;
import vn.hoidanit.jobhunter.domain.Company;
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

    public List<Company> handleGetAllCompanies() {
        return this.companyRepository.findAll();
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
