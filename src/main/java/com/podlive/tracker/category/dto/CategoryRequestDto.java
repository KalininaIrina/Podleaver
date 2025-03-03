package com.podlive.tracker.category.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequestDto {
    private Integer id;
    private String name;
    private String icon;
}

