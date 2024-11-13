package com.flybird.nestwise.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "budgets")
public class Budget {
    @Id
    @Column(name = "goal_id")
    private Long id;

    @Column(name = "min_budget", nullable = false)
    private Integer minBudget;

    @NotNull
    @Column(name = "max_budget", nullable = false)
    private Integer maxBudget;

    @Column(name = "currency_code", updatable = false, insertable = false)
    private Integer currencyCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_code")
    private Currency currency;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @OneToOne
    @MapsId
    @JoinColumn(name = "goal_id")
    private Goal goal;
}