package com.podlive.tracker.recurring_transaction.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.podlive.tracker.account.model.Account;
import com.podlive.tracker.category.model.Category;
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
@Table(name = "recurring_transaction")
@JsonIgnoreProperties({"account", "category"})
public class RecurringTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @JsonBackReference
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonBackReference
    private Category category;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "interval_type", nullable = false)
    private String intervalType; // DAILY, WEEKLY, MONTHLY

    @Column(name = "next_execution_date", nullable = false)
    private LocalDate nextExecutionDate;

    @Column(name = "description")
    private String description;

    @Column(name = "end_date")
    private LocalDate endDate;



}
