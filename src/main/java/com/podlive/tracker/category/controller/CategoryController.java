package com.podlive.tracker.category.controller;

import com.podlive.tracker.category.dto.CategoryRequestDto;
import com.podlive.tracker.category.dto.CategoryResponseDto;
import com.podlive.tracker.category.mapper.CategoryMapper;
import com.podlive.tracker.category.model.Category;
import com.podlive.tracker.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper responseMapper;

    @Operation(summary = "Get all categories")
    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAll(){
        return ResponseEntity.ok(categoryService.getAll().stream().map(responseMapper::map).toList());
    }

    @Operation(summary = "Get category")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getById(@PathVariable Integer id){
        return ResponseEntity.ok(responseMapper.map(categoryService.getById(id)));
    }

    @Operation(summary = "Add category")
    @PostMapping
    public ResponseEntity<CategoryResponseDto> create(@RequestBody CategoryRequestDto categoryRequestDto){
        Category category = categoryService.create(categoryRequestDto);
        return ResponseEntity.ok(responseMapper.map(category));
    }

    @Operation(summary = "Update category")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> update(@PathVariable Integer id, @RequestBody CategoryRequestDto categoryRequestDto){
        Category category = categoryService.update(id, categoryRequestDto);
        return ResponseEntity.ok(responseMapper.map(category));
    }

    @Operation(summary = "Delete category")
    @DeleteMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> delete(@PathVariable Integer id){
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
