package com.seb.api.service;

import com.seb.api.controller.dto.fxRate.RateDto;
import com.seb.api.controller.dto.fxRateForCurrency.FxRates;
import com.seb.api.repository.FxRateRepository;
import com.seb.api.repository.entity.Rate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class FxRateService {

    private final ExternalApiService externalApiService;
    private final FxRateRepository fxRateRepository;

    private static final String DEFAULT_TYPE = "EU";

    @Transactional(readOnly = true)
    public List<RateDto> getRatesForDate(LocalDate date) {
        List<Rate> rates = fxRateRepository.findAllByRateDate(date);
        return rates.stream()
                .map(rate -> new RateDto(rate.getCurrencyCode(), rate.getRateToEur(), rate.getRateDate()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RateDto> getHistoricalRates(String currencyCode) {
        List<Rate> rates = fxRateRepository.findAllByCurrencyCodeOrderByRateDateAsc(currencyCode);
        return rates.stream()
                .map(rate -> new RateDto(rate.getCurrencyCode(), rate.getRateToEur(), rate.getRateDate()))
                .toList();
    }

    @Transactional
    public void refreshRatesForDate(LocalDate date) {
        try {
            FxRates fxRates = externalApiService.fetchRatesForDate(DEFAULT_TYPE, date);
            if (fxRates == null || fxRates.getRates() == null) {
                log.warn("No rates found for {}", date);
                return;
            }

            List<Rate> newRates = fxRates.getRates().stream()
                    .flatMap(rate -> rate.getCcyAmt().stream())
                    .filter(ccyAmt -> ccyAmt.getAmt() != null)
                    .map(ccyAmt -> {
                        Rate rate = new Rate();
                        rate.setCurrencyCode(ccyAmt.getCcy());
                        rate.setRateToEur(ccyAmt.getAmt());
                        rate.setRateDate(date);
                        return rate;
                    })
                    .filter(distinctByKey(rate -> rate.getCurrencyCode() + "_" + rate.getRateDate()))
                    .filter(rate -> fxRateRepository.findByCurrencyCodeAndRateDate(rate.getCurrencyCode(), date).isEmpty())
                    .toList();

            fxRateRepository.saveAll(newRates);
            log.info("Saved {} new rates for {}", newRates.size(), date);
        } catch (Exception e) {
            log.error("Failed to refresh rates for {}", date, e);
        }
    }

    @Transactional
    public void backfillRates(int days) {
        LocalDate today = LocalDate.now();
        for (int i = 0; i < days; i++) {
            LocalDate date = today.minusDays(i);
            refreshRatesForDate(date);
        }
        log.info("Backfilled rates for last {} days", days);
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
