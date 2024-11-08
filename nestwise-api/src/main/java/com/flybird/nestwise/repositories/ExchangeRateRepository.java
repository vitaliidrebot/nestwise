package com.flybird.nestwise.repositories;

import com.flybird.nestwise.domain.ExchangeRate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    @Query("SELECT e FROM ExchangeRate e WHERE e.bankId = :id AND e.currencyCodeFrom = :currencyCodeFrom AND e.currencyCodeTo = :currencyCodeTo AND e.date BETWEEN :from AND :to")
    List<ExchangeRate> getExchangeRates(@Param("id") Long id, @Param("currencyCodeFrom") Integer currencyCodeFrom,
                                        @Param("currencyCodeTo") Integer currencyCodeTo,
                                        @Param("from") LocalDate from, @Param("to") LocalDate to, Sort sort);
}
