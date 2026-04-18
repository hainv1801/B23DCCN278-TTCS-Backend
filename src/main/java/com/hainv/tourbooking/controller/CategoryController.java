package com.hainv.tourbooking.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import com.hainv.tourbooking.domain.Category;
import com.hainv.tourbooking.domain.response.ResultPaginationDTO;
import com.hainv.tourbooking.service.CategoryService;
import com.hainv.tourbooking.util.annotation.ApiMessage;
import com.hainv.tourbooking.util.error.IdInvalidException;

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
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/categories")
    @ApiMessage("create a category")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) throws IdInvalidException {
        if (category.getName() != null && this.categoryService.existByName(category.getName())) {
            throw new IdInvalidException("Category đã tồn tại!!!");
        }
        Category newCategory = this.categoryService.handleCreateCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCategory);
    }

    @GetMapping("/categories")
    @ApiMessage("fetch all categories")
    public ResponseEntity<ResultPaginationDTO> getAllCategories(
            @Filter Specification<Category> spec, Pageable pageable) {
        return ResponseEntity.ok(this.categoryService.fetchAllCategories(spec, pageable));
    }

    @PutMapping("/categories")
    @ApiMessage("update category")
    public ResponseEntity<Category> putMethodName(@Valid @RequestBody Category category) throws IdInvalidException {
        Category currentCategory = this.categoryService.findCategoryById(category.getId());
        if (currentCategory == null) {
            throw new IdInvalidException("Category với id = " + category.getId() + " không tồn tại!");
        }
        if (category.getName() != null && this.categoryService.existByName(category.getName())) {
            throw new IdInvalidException("Category đã tồn tại!!!");
        }
        currentCategory.setName(category.getName());
        return ResponseEntity.ok(this.categoryService.handleUpdateSkill(currentCategory));
    }

    @DeleteMapping("/categories/{id}")
    @ApiMessage("Delete a category")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        // check id
        Category currentCategory = this.categoryService.findCategoryById(id);
        if (currentCategory == null) {
            throw new IdInvalidException("Category id = " + id + " không tồn tại");
        }
        this.categoryService.deleteCategory(id);
        return ResponseEntity.ok().body(null);
    }
}
