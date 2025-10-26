package com.seb.api.service;

import com.seb.api.controller.dto.conversion.ConversionResultDto;
import com.seb.api.repository.CurrencyRepository;
import com.seb.api.repository.FxRateRepository;
import com.seb.api.repository.entity.Currency;
import com.seb.api.repository.entity.Rate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyConversionServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private FxRateRepository fxRateRepository;

    private CurrencyConversionService currencyConversionService;

    @BeforeEach
    void setUp() {
        currencyConversionService = new CurrencyConversionService(currencyRepository, fxRateRepository);
    }

    @Test
    void convertWithLatestRate_EurToUsd_Success() {
        BigDecimal amount = new BigDecimal("100.00");
        String fromCurrency = "EUR";
        String toCurrency = "USD";
        Currency eurCurrency = new Currency("978", "EUR", "Euro", new BigDecimal("1.0000"));
        Currency usdCurrency = new Currency("840", "USD", "US Dollar", new BigDecimal("1.0950"));
        when(currencyRepository.findByCode(fromCurrency)).thenReturn(Optional.of(eurCurrency));
        when(currencyRepository.findByCode(toCurrency)).thenReturn(Optional.of(usdCurrency));
        ConversionResultDto result = currencyConversionService.convertWithLatestRate(amount, fromCurrency, toCurrency);

        assertNotNull(result);
        assertEquals(amount, result.getOriginalAmount());
        assertEquals(fromCurrency, result.getFromCurrency());
        assertEquals(toCurrency, result.getToCurrency());
        assertEquals(new BigDecimal("109.50"), result.getConvertedAmount());
        assertEquals(new BigDecimal("1.095000"), result.getRateUsed());
        assertEquals(LocalDate.now(), result.getRateDate());
        verify(currencyRepository).findByCode(fromCurrency);
        verify(currencyRepository).findByCode(toCurrency);
    }

    @Test
    void convertWithLatestRate_UsdToEur_Success() {
        BigDecimal amount = new BigDecimal("100.00");
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        Currency usdCurrency = new Currency("840", "USD", "US Dollar", new BigDecimal("1.0950"));
        Currency eurCurrency = new Currency("978", "EUR", "Euro", new BigDecimal("1.0000"));
        when(currencyRepository.findByCode(fromCurrency)).thenReturn(Optional.of(usdCurrency));
        when(currencyRepository.findByCode(toCurrency)).thenReturn(Optional.of(eurCurrency));
        ConversionResultDto result = currencyConversionService.convertWithLatestRate(amount, fromCurrency, toCurrency);
        assertNotNull(result);
        assertEquals(amount, result.getOriginalAmount());
        assertEquals(fromCurrency, result.getFromCurrency());
        assertEquals(toCurrency, result.getToCurrency());
        assertEquals(new BigDecimal("91.32"), result.getConvertedAmount());
        assertEquals(new BigDecimal("0.913242"), result.getRateUsed());
        assertEquals(LocalDate.now(), result.getRateDate());
    }

    @Test
    void convertWithHistoricalRate_Success() {
        BigDecimal amount = new BigDecimal("200.00");
        String fromCurrency = "EUR";
        String toCurrency = "USD";
        LocalDate historicalDate = LocalDate.of(2023, 12, 1);
        LocalDateTime now = LocalDateTime.now();
        Rate eurRate = new Rate(1L, "EUR", new BigDecimal("1.0000"), historicalDate, now, now);
        Rate usdRate = new Rate(2L, "USD", new BigDecimal("1.0800"), historicalDate, now, now);
        when(fxRateRepository.findByCurrencyCodeAndRateDate(fromCurrency, historicalDate))
                .thenReturn(Optional.of(eurRate));
        when(fxRateRepository.findByCurrencyCodeAndRateDate(toCurrency, historicalDate))
                .thenReturn(Optional.of(usdRate));
        ConversionResultDto result = currencyConversionService.convertWithHistoricalRate(
                amount, fromCurrency, toCurrency, historicalDate);

        assertNotNull(result);
        assertEquals(amount, result.getOriginalAmount());
        assertEquals(fromCurrency, result.getFromCurrency());
        assertEquals(toCurrency, result.getToCurrency());
        assertEquals(new BigDecimal("216.00"), result.getConvertedAmount());
        assertEquals(new BigDecimal("1.080000"), result.getRateUsed());
        assertEquals(historicalDate, result.getRateDate());
        verify(fxRateRepository).findByCurrencyCodeAndRateDate(fromCurrency, historicalDate);
        verify(fxRateRepository).findByCurrencyCodeAndRateDate(toCurrency, historicalDate);
    }

    @Test
    void convertWithLatestRate_MissingSourceCurrency_ThrowsException() {
        BigDecimal amount = new BigDecimal("100.00");
        String fromCurrency = "XYZ";
        String toCurrency = "EUR";
        when(currencyRepository.findByCode(fromCurrency)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> currencyConversionService.convertWithLatestRate(amount, fromCurrency, toCurrency));
        assertEquals("Missing latest rate for: " + fromCurrency, exception.getMessage());
        verify(currencyRepository).findByCode(fromCurrency);
        verify(currencyRepository, never()).findByCode(toCurrency);
    }

    @Test
    void convertWithLatestRate_MissingTargetCurrency_ThrowsException() {
        BigDecimal amount = new BigDecimal("100.00");
        String fromCurrency = "EUR";
        String toCurrency = "XYZ";
        Currency eurCurrency = new Currency("978", "EUR", "Euro", new BigDecimal("1.0000"));
        when(currencyRepository.findByCode(fromCurrency)).thenReturn(Optional.of(eurCurrency));
        when(currencyRepository.findByCode(toCurrency)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> currencyConversionService.convertWithLatestRate(amount, fromCurrency, toCurrency));

        assertEquals("Missing latest rate for: " + toCurrency, exception.getMessage());
        verify(currencyRepository).findByCode(fromCurrency);
        verify(currencyRepository).findByCode(toCurrency);
    }

    @Test
    void convertWithHistoricalRate_MissingSourceHistoricalRate_ThrowsException() {
        BigDecimal amount = new BigDecimal("100.00");
        String fromCurrency = "EUR";
        String toCurrency = "USD";
        LocalDate historicalDate = LocalDate.of(2014, 12, 1);
        when(fxRateRepository.findByCurrencyCodeAndRateDate(fromCurrency, historicalDate))
                .thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> currencyConversionService.convertWithHistoricalRate(
                        amount, fromCurrency, toCurrency, historicalDate));

        assertEquals("Missing historical rate for: " + fromCurrency + " on " + historicalDate,
                exception.getMessage());
        verify(fxRateRepository).findByCurrencyCodeAndRateDate(fromCurrency, historicalDate);
        verify(fxRateRepository, never()).findByCurrencyCodeAndRateDate(toCurrency, historicalDate);
    }

    @Test
    void convertWithHistoricalRate_MissingTargetHistoricalRate_ThrowsException() {
        BigDecimal amount = new BigDecimal("100.00");
        String fromCurrency = "EUR";
        String toCurrency = "USD";
        LocalDate historicalDate = LocalDate.of(2014, 12, 1);
        LocalDateTime now = LocalDateTime.now();
        Rate eurRate = new Rate(1L, "EUR", new BigDecimal("1.0000"), historicalDate, now, now);
        when(fxRateRepository.findByCurrencyCodeAndRateDate(fromCurrency, historicalDate))
                .thenReturn(Optional.of(eurRate));
        when(fxRateRepository.findByCurrencyCodeAndRateDate(toCurrency, historicalDate))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> currencyConversionService.convertWithHistoricalRate(
                        amount, fromCurrency, toCurrency, historicalDate));
        assertEquals("Missing historical rate for: " + toCurrency + " on " + historicalDate,
                exception.getMessage());
        verify(fxRateRepository).findByCurrencyCodeAndRateDate(fromCurrency, historicalDate);
        verify(fxRateRepository).findByCurrencyCodeAndRateDate(toCurrency, historicalDate);
    }

    @Test
    void convertWithLatestRate_ZeroAmount_Success() {
        BigDecimal amount = BigDecimal.ZERO;
        String fromCurrency = "EUR";
        String toCurrency = "USD";
        Currency eurCurrency = new Currency("978", "EUR", "Euro", new BigDecimal("1.0000"));
        Currency usdCurrency = new Currency("840", "USD", "US Dollar", new BigDecimal("1.0950"));
        when(currencyRepository.findByCode(fromCurrency)).thenReturn(Optional.of(eurCurrency));
        when(currencyRepository.findByCode(toCurrency)).thenReturn(Optional.of(usdCurrency));
        ConversionResultDto result = currencyConversionService.convertWithLatestRate(amount, fromCurrency, toCurrency);

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getOriginalAmount());
        assertEquals(new BigDecimal("0.00"), result.getConvertedAmount());
        assertEquals(new BigDecimal("1.095000"), result.getRateUsed());
        assertEquals(LocalDate.now(), result.getRateDate());
    }

    @Test
    void convertWithLatestRate_LargeAmount_Success() {
        BigDecimal amount = new BigDecimal("1000000.00");
        String fromCurrency = "EUR";
        String toCurrency = "JPY";
        Currency eurCurrency = new Currency("978", "EUR", "Euro", new BigDecimal("1.0000"));
        Currency jpyCurrency = new Currency("392", "JPY", "Japanese Yen", new BigDecimal("160.5000"));
        when(currencyRepository.findByCode(fromCurrency)).thenReturn(Optional.of(eurCurrency));
        when(currencyRepository.findByCode(toCurrency)).thenReturn(Optional.of(jpyCurrency));

        ConversionResultDto result = currencyConversionService.convertWithLatestRate(amount, fromCurrency, toCurrency);

        assertNotNull(result);
        assertEquals(amount, result.getOriginalAmount());
        assertEquals(new BigDecimal("160500000.00"), result.getConvertedAmount());
        assertEquals(new BigDecimal("160.500000"), result.getRateUsed());
        assertEquals(LocalDate.now(), result.getRateDate());
    }

    @Test
    void convertWithLatestRate_RoundingBehavior_Success() {
        BigDecimal amount = new BigDecimal("100.00");
        String fromCurrency = "EUR";
        String toCurrency = "CHF";
        Currency eurCurrency = new Currency("978", "EUR", "Euro", new BigDecimal("1.0000"));
        Currency chfCurrency = new Currency("756", "CHF", "Swiss Franc", new BigDecimal("0.951234"));
        when(currencyRepository.findByCode(fromCurrency)).thenReturn(Optional.of(eurCurrency));
        when(currencyRepository.findByCode(toCurrency)).thenReturn(Optional.of(chfCurrency));
        ConversionResultDto result = currencyConversionService.convertWithLatestRate(amount, fromCurrency, toCurrency);
        assertNotNull(result);
        assertEquals(new BigDecimal("95.12"), result.getConvertedAmount());
        assertEquals(new BigDecimal("0.951234"), result.getRateUsed());
    }

    @Test
    void convertWithLatestRate_SameCurrency_Success() {
        BigDecimal amount = new BigDecimal("100.00");
        String fromCurrency = "EUR";
        String toCurrency = "EUR";
        Currency eurCurrency = new Currency("978", "EUR", "Euro", new BigDecimal("1.0000"));
        when(currencyRepository.findByCode(fromCurrency)).thenReturn(Optional.of(eurCurrency));
        when(currencyRepository.findByCode(toCurrency)).thenReturn(Optional.of(eurCurrency));

        ConversionResultDto result = currencyConversionService.convertWithLatestRate(amount, fromCurrency, toCurrency);

        assertNotNull(result);
        assertEquals(amount, result.getOriginalAmount());
        assertEquals(amount, result.getConvertedAmount());
        assertEquals(new BigDecimal("1.000000"), result.getRateUsed());
        assertEquals(LocalDate.now(), result.getRateDate());
    }

    @Test
    void convertWithLatestRate_GbpToJpy_Success() {
        BigDecimal amount = new BigDecimal("50.00");
        String fromCurrency = "GBP";
        String toCurrency = "JPY";
        Currency gbpCurrency = new Currency("826", "GBP", "British Pound", new BigDecimal("0.8500"));
        Currency jpyCurrency = new Currency("392", "JPY", "Japanese Yen", new BigDecimal("160.5000"));
        when(currencyRepository.findByCode(fromCurrency)).thenReturn(Optional.of(gbpCurrency));
        when(currencyRepository.findByCode(toCurrency)).thenReturn(Optional.of(jpyCurrency));

        ConversionResultDto result = currencyConversionService.convertWithLatestRate(amount, fromCurrency, toCurrency);

        assertNotNull(result);
        assertEquals(amount, result.getOriginalAmount());
        // Calculation: (50 / 0.85) * 160.5 = 9441.18
        assertEquals(new BigDecimal("9441.18"), result.getConvertedAmount());
        // Rate: 160.5 / 0.85 = 188.823529
        assertEquals(new BigDecimal("188.823529"), result.getRateUsed());
        assertEquals(LocalDate.now(), result.getRateDate());
    }

    @Test
    void convertWithHistoricalRate_DifferentDate_Success() {
        BigDecimal amount = new BigDecimal("500.00");
        String fromCurrency = "USD";
        String toCurrency = "CAD";
        LocalDate historicalDate = LocalDate.of(2023, 6, 15);
        LocalDateTime now = LocalDateTime.now();
        Rate usdRate = new Rate(1L, "USD", new BigDecimal("1.1200"), historicalDate, now, now);
        Rate cadRate = new Rate(2L, "CAD", new BigDecimal("1.4500"), historicalDate, now, now);
        when(fxRateRepository.findByCurrencyCodeAndRateDate(fromCurrency, historicalDate))
                .thenReturn(Optional.of(usdRate));
        when(fxRateRepository.findByCurrencyCodeAndRateDate(toCurrency, historicalDate))
                .thenReturn(Optional.of(cadRate));

        ConversionResultDto result = currencyConversionService.convertWithHistoricalRate(
                amount, fromCurrency, toCurrency, historicalDate);

        assertNotNull(result);
        assertEquals(amount, result.getOriginalAmount());
        // Calculation: (500 / 1.12) * 1.45 = 647.32
        assertEquals(new BigDecimal("647.32"), result.getConvertedAmount());
        assertEquals(new BigDecimal("1.294643"), result.getRateUsed());
        assertEquals(historicalDate, result.getRateDate());
    }

    @Test
    void convertWithLatestRate_AllDtoFieldsPopulated_Success() {
        BigDecimal amount = new BigDecimal("75.50");
        String fromCurrency = "EUR";
        String toCurrency = "USD";
        Currency eurCurrency = new Currency("978", "EUR", "Euro", new BigDecimal("1.0000"));
        Currency usdCurrency = new Currency("840", "USD", "US Dollar", new BigDecimal("1.0950"));
        when(currencyRepository.findByCode(fromCurrency)).thenReturn(Optional.of(eurCurrency));
        when(currencyRepository.findByCode(toCurrency)).thenReturn(Optional.of(usdCurrency));

        ConversionResultDto result = currencyConversionService.convertWithLatestRate(amount, fromCurrency, toCurrency);

        assertNotNull(result);
        assertEquals(amount, result.getOriginalAmount());
        assertEquals(fromCurrency, result.getFromCurrency());
        assertEquals(toCurrency, result.getToCurrency());
        assertEquals(new BigDecimal("82.67"), result.getConvertedAmount());
        assertEquals(new BigDecimal("1.095000"), result.getRateUsed());
        assertEquals(LocalDate.now(), result.getRateDate());
    }
}