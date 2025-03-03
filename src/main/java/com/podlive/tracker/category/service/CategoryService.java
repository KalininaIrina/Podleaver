package com.podlive.tracker.category.service;

import com.podlive.tracker.category.dto.CategoryRequestDto;
import com.podlive.tracker.category.model.Category;
import com.podlive.tracker.category.repository.CategoryRepository;
import com.podlive.tracker.common.service.CrudService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CategoryService extends CrudService<Category, Integer> {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository){
        super(categoryRepository);
        this.categoryRepository = categoryRepository;
    }

    public Category create(CategoryRequestDto requestDto){
        Category category = Category.builder().name(requestDto.getName()).icon(requestDto.getIcon()).build();
        return save(category);
    }

    public Category update(Integer id, CategoryRequestDto requestDto){
        Category category = categoryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        category.setName(requestDto.getName());
        category.setIcon(requestDto.getIcon());
        return save(category);
    }

}
