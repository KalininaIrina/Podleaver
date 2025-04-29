package com.podlive.tracker.goal.model;

import com.podlive.tracker.account.model.Account;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private BigDecimal targetAmount;

    private LocalDate targetDate;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
}
