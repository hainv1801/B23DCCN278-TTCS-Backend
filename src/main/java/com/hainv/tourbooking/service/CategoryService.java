package com.hainv.tourbooking.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hainv.tourbooking.domain.Category;
import com.hainv.tourbooking.domain.response.ResultPaginationDTO;
import com.hainv.tourbooking.repository.CategoryRepository;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category findCategoryById(long id) {
        Optional<Category> optionalCategory = this.categoryRepository.findById(id);
        return optionalCategory.isPresent() == true ? optionalCategory.get() : null;
    }

    public boolean existByName(String name) {
        return this.categoryRepository.existsByName(name);
    }

    public Category handleCreateCategory(Category category) {
        return this.categoryRepository.save(category);
    }

    public Category handleUpdateSkill(Category category) {
        return this.categoryRepository.save(category);
    }

    public void deleteCategory(long id) {
        // delete job (inside job_skill table)
        Optional<Category> optionalCategory = this.categoryRepository.findById(id);
        Category currentCategory = optionalCategory.get();
        currentCategory.getTours().forEach(tour -> tour.getCategories().remove(currentCategory));
        currentCategory.getSubscribers().forEach(subs -> subs.getCategories().remove(currentCategory));
        // delete skill
        this.categoryRepository.delete(currentCategory);
    }

    public ResultPaginationDTO fetchAllCategories(Specification<Category> spec, Pageable pageable) {
        Page<Category> pageSkill = this.categoryRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageSkill.getTotalPages());
        mt.setTotal(pageSkill.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageSkill.getContent());
        return rs;
    }
}
