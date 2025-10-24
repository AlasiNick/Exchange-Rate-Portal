package com.seb.api.repository;

import org.springframework.stereotype.Repository;
import com.seb.api.repository.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, String> {

    Optional<Currency> findByCode(String code);

    boolean existsByCode(String code);
}
