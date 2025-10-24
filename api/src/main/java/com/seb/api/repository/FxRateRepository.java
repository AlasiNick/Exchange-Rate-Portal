package com.seb.api.repository;

import com.seb.api.repository.entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FxRateRepository extends JpaRepository<Rate, Long> {

    Optional<Rate> findByCurrencyCodeAndRateDate(String currencyCode, LocalDate rateDate);

    List<Rate> findAllByCurrencyCodeOrderByRateDateAsc(String currencyCode);

    List<Rate> findAllByRateDate(LocalDate rateDate);
}
