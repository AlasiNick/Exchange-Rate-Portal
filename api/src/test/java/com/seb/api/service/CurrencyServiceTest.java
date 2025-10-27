package com.seb.api.service;

import com.seb.api.controller.dto.currency.CurrencyDto;
import com.seb.api.controller.dto.currency.CurrencyTable;
import com.seb.api.controller.dto.currency.CurrencyEntry;
import com.seb.api.controller.dto.currency.CurrencyName;
import com.seb.api.controller.dto.fxRateForCurrency.FxRates;
import com.seb.api.controller.dto.fxRateForCurrency.FxRate;
import com.seb.api.controller.dto.fxRateForCurrency.CcyAmt;
import com.seb.api.repository.CurrencyRepository;
import com.seb.api.repository.entity.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private ExternalApiService externalApiService;

    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        currencyService = new CurrencyService(currencyRepository, externalApiService);
    }

    @Test
    void getCurrencies_Success() {
        List<Currency> currencies = List.of(
                new Currency("978", "EUR", "Euro", new BigDecimal("1.0000")),
                new Currency("840", "USD", "US Dollar", new BigDecimal("1.0950")),
                new Currency("826", "GBP", "British Pound", new BigDecimal("0.8500"))
        );

        when(currencyRepository.findAll()).thenReturn(currencies);
        List<CurrencyDto> result = currencyService.getCurrencies();
        assertNotNull(result);
        assertEquals(3, result.size());
        CurrencyDto eurDto = result.getFirst();
        assertEquals("978", eurDto.getId());
        assertEquals("EUR", eurDto.getCode());
        assertEquals("Euro", eurDto.getName());
        assertEquals(new BigDecimal("1.0000"), eurDto.getRateToEur());

        CurrencyDto usdDto = result.get(1);
        assertEquals("840", usdDto.getId());
        assertEquals("USD", usdDto.getCode());
        assertEquals("US Dollar", usdDto.getName());
        assertEquals(new BigDecimal("1.0950"), usdDto.getRateToEur());

        verify(currencyRepository).findAll();
    }

    @Test
    void getCurrencies_EmptyList_Success() {
        when(currencyRepository.findAll()).thenReturn(List.of());
        List<CurrencyDto> result = currencyService.getCurrencies();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(currencyRepository).findAll();
    }

    @Test
    void refreshCurrencies_ApiFailsForCurrencyTable_ThrowsException() {
        when(externalApiService.fetchCurrencyTable())
                .thenThrow(new RuntimeException("API unavailable"));
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> currencyService.refreshCurrencies());
        assertTrue(exception.getMessage().contains("Failed to refresh currencies"));
        verify(externalApiService).fetchCurrencyTable();
        verify(externalApiService, never()).fetchCurrentRates(anyString());
        verify(currencyRepository, never()).deleteAllInBatch();
        verify(currencyRepository, never()).saveAll(any());
    }

    @Test
    void refreshCurrencies_ApiFailsForFxRates_ThrowsException() {
        CurrencyTable currencyTable = createValidCurrencyTable();
        when(externalApiService.fetchCurrencyTable()).thenReturn(currencyTable);
        when(externalApiService.fetchCurrentRates("EU"))
                .thenThrow(new RuntimeException("FX rates API unavailable"));
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> currencyService.refreshCurrencies());
        assertTrue(exception.getMessage().contains("Failed to refresh currencies"));
        verify(externalApiService).fetchCurrencyTable();
        verify(externalApiService).fetchCurrentRates("EU");
        verify(currencyRepository, never()).deleteAllInBatch();
        verify(currencyRepository, never()).saveAll(any());
    }

    @Test
    void refreshCurrencies_RepositoryMethodsCalledInCorrectOrder() {
        CurrencyTable currencyTable = createValidCurrencyTable();
        FxRates fxRates = createValidFxRates();
        when(externalApiService.fetchCurrencyTable()).thenReturn(currencyTable);
        when(externalApiService.fetchCurrentRates("EU")).thenReturn(fxRates);
        currencyService.refreshCurrencies();

        var inOrder = inOrder(currencyRepository);
        inOrder.verify(currencyRepository).deleteAllInBatch();
        inOrder.verify(currencyRepository).saveAll(anyList());
    }


    private CurrencyTable createValidCurrencyTable() {
        CurrencyEntry eurEntry = new CurrencyEntry();
        eurEntry.setCode("EUR");
        eurEntry.setId("978");
        eurEntry.setNames(List.of(new CurrencyName("EN", "Euro")));

        CurrencyEntry usdEntry = new CurrencyEntry();
        usdEntry.setCode("USD");
        usdEntry.setId("840");
        usdEntry.setNames(List.of(new CurrencyName("EN", "US Dollar")));

        CurrencyEntry gbpEntry = new CurrencyEntry();
        gbpEntry.setCode("GBP");
        gbpEntry.setId("826");
        gbpEntry.setNames(List.of(new CurrencyName("EN", "British Pound")));

        CurrencyTable table = new CurrencyTable();
        table.setCurrencies(List.of(eurEntry, usdEntry, gbpEntry));
        return table;
    }

    private FxRates createValidFxRates() {
        CcyAmt eurAmt = new CcyAmt();
        eurAmt.setCcy("EUR");
        eurAmt.setAmt(new BigDecimal("1.0000"));

        CcyAmt usdAmt = new CcyAmt();
        usdAmt.setCcy("USD");
        usdAmt.setAmt(new BigDecimal("1.0950"));

        CcyAmt gbpAmt = new CcyAmt();
        gbpAmt.setCcy("GBP");
        gbpAmt.setAmt(new BigDecimal("0.8500"));

        FxRate fxRate = new FxRate();
        fxRate.setCcyAmt(List.of(eurAmt, usdAmt, gbpAmt));

        FxRates fxRates = new FxRates();
        fxRates.setRates(List.of(fxRate));
        return fxRates;
    }

}