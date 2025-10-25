package com.seb.api.service;

import com.seb.api.controller.dto.conversion.ConversionResultDto;
import com.seb.api.repository.CurrencyRepository;
import com.seb.api.repository.FxRateRepository;
import com.seb.api.repository.entity.Currency;
import com.seb.api.repository.entity.Rate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CurrencyConversionService {

    private final CurrencyRepository currencyRepository;
    private final FxRateRepository fxRateRepository;

    @Transactional(readOnly = true)
    public ConversionResultDto convertWithLatestRate(BigDecimal amount, String from, String to) {
        BigDecimal fromRate = currencyRepository.findByCode(from)
                .map(Currency::getRateToEur)
                .orElseThrow(() -> new IllegalArgumentException("Missing latest rate for: " + from));

        BigDecimal toRate = currencyRepository.findByCode(to)
                .map(Currency::getRateToEur)
                .orElseThrow(() -> new IllegalArgumentException("Missing latest rate for: " + to));

        return performConversion(amount, from, to, fromRate, toRate, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public ConversionResultDto convertWithHistoricalRate(BigDecimal amount, String from, String to, LocalDate date) {
        BigDecimal fromRate = fxRateRepository.findByCurrencyCodeAndRateDate(from, date)
                .map(Rate::getRateToEur)
                .orElseThrow(() -> new IllegalArgumentException("Missing historical rate for: " + from + " on " + date));

        BigDecimal toRate = fxRateRepository.findByCurrencyCodeAndRateDate(to, date)
                .map(Rate::getRateToEur)
                .orElseThrow(() -> new IllegalArgumentException("Missing historical rate for: " + to + " on " + date));

        return performConversion(amount, from, to, fromRate, toRate, date);
    }

    private ConversionResultDto performConversion(BigDecimal amount, String from, String to,
                                                  BigDecimal fromRate, BigDecimal toRate, LocalDate rateDate) {

        BigDecimal eurAmount = amount.divide(fromRate, 6, RoundingMode.HALF_UP);
        BigDecimal convertedAmount = eurAmount.multiply(toRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal directRate = toRate.divide(fromRate, 6, RoundingMode.HALF_UP);

        return new ConversionResultDto(amount, from, to, convertedAmount, directRate, rateDate);
    }
}
