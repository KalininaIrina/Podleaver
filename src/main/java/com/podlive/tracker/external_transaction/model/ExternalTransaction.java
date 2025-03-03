package com.podlive.tracker.external_transaction.model;
import com.podlive.tracker.transaction.model.Transaction;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Setter
@Getter
@Table(name = "external_transaction")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExternalTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @Column(name = "external_transaction1_id")
    private String externalTransaction;

    @Column(name = "source_id")
    private String sourceId;

}

