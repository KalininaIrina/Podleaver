package com.podlive.tracker.category.repository;

import com.podlive.tracker.category.model.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Integer> {
}
