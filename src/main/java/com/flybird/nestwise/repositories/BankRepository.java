package com.flybird.nestwise.repositories;

import com.flybird.nestwise.domain.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {
    Optional<Bank> findByCode(String code);
}
