package com.flybird.nestwise.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "exchange_rates")
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exchange_rates_id_gen")
    @SequenceGenerator(name = "exchange_rates_id_gen", sequenceName = "exchange_rates_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "bank_id", updatable = false, insertable = false)
    private Integer bankId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bank_id", nullable = false)
    private Bank bank;

    @Column(name = "currency_code_from", updatable = false, insertable = false)
    private Integer currencyCodeFrom;

    @Column(name = "currency_code_to", updatable = false, insertable = false)
    private Integer currencyCodeTo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "currency_code_from", nullable = false)
    private Currency currencyFrom;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "currency_code_to", nullable = false)
    private Currency currencyTo;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "rate_buy")
    private BigDecimal buyRate;

    @Column(name = "rate_sell")
    private BigDecimal sellRate;
}