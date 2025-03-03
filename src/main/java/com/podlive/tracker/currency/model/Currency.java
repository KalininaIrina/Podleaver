package com.podlive.tracker.currency.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "currency")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;
}
