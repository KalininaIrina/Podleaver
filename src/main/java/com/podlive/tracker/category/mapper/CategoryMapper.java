package com.podlive.tracker.category.mapper;

import com.podlive.tracker.category.dto.CategoryResponseDto;
import com.podlive.tracker.category.model.Category;
import com.podlive.tracker.common.service.GenericMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper extends GenericMapper<Category, CategoryResponseDto> {
}
