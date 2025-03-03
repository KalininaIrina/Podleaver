package com.podlive.tracker.category.dto;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDto {
    private Integer id;
    private String name;
    private String icon;
}
