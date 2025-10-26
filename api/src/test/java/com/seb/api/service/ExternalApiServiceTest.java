package com.seb.api.service;

import com.seb.api.controller.dto.currency.CurrencyTable;
import com.seb.api.controller.dto.currency.CurrencyEntry;
import com.seb.api.controller.dto.fxRateForCurrency.FxRates;
import com.seb.api.controller.dto.fxRateForCurrency.FxRate;
import com.seb.api.controller.dto.fxRateForCurrency.CcyAmt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExternalApiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private ExternalApiService externalApiService;

    @BeforeEach
    void setUp() {
        externalApiService = new ExternalApiService(restTemplate);
    }

    @Test
    void getCurrencyListRaw_Success() {
        String expectedXml = "<CcyTbl><CcyNtry><Ccy>EUR</Ccy></CcyNtry></CcyTbl>";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(expectedXml);
        String result = externalApiService.getCurrencyListRaw();
        assertEquals(expectedXml, result);
        verify(restTemplate).getForObject(anyString(), eq(String.class));
    }

    @Test
    void getCurrentRatesRaw_WithType_Success() {
        String type = "EU";
        String expectedXml = "<FxRates><FxRate><CcyAmt><Ccy>EUR</Ccy><Amt>1.0</Amt></CcyAmt></FxRate></FxRates>";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(expectedXml);
        String result = externalApiService.getCurrentRatesRaw(type);
        assertEquals(expectedXml, result);
        verify(restTemplate).getForObject(contains("tp=" + type), eq(String.class));
    }

    @Test
    void fetchCurrencyTable_WithMultipleLanguages_Success() {
        String xmlResponse = """
            <CcyTbl>
                <CcyNtry>
                    <Ccy>EUR</Ccy>
                    <CcyNm lang="EN">Euro</CcyNm>
                    <CcyNm lang="LT">Euras</CcyNm>
                    <CcyNbr>978</CcyNbr>
                    <CcyMnrUnts>2</CcyMnrUnts>
                </CcyNtry>
                <CcyNtry>
                    <Ccy>USD</Ccy>
                    <CcyNm lang="EN">US Dollar</CcyNm>
                    <CcyNm lang="LT">JAV doleriai</CcyNm>
                    <CcyNbr>840</CcyNbr>
                    <CcyMnrUnts>2</CcyMnrUnts>
                </CcyNtry>
            </CcyTbl>
            """;
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(xmlResponse);
        CurrencyTable result = externalApiService.fetchCurrencyTable();
        assertNotNull(result);
        assertNotNull(result.getCurrencies());
        assertEquals(2, result.getCurrencies().size());
        CurrencyEntry eur = result.getCurrencies().getFirst();
        assertEquals("EUR", eur.getCode());
        assertEquals("978", eur.getId());
        assertEquals("2", eur.getMinorUnits());
        assertEquals("Euro", eur.getEnglishName());
        assertEquals(2, eur.getNames().size());
        CurrencyEntry usd = result.getCurrencies().get(1);
        assertEquals("USD", usd.getCode());
        assertEquals("840", usd.getId());
        assertEquals("US Dollar", usd.getEnglishName());
    }

    @Test
    void fetchCurrentRates_WithMultipleCurrencies_Success() {
        String type = "EU";
        String xmlResponse = """
            <FxRates>
                <FxRate>
                    <Tp>EU</Tp>
                    <Dt>2024-01-15</Dt>
                    <CcyAmt>
                        <Ccy>EUR</Ccy>
                        <Amt>1.0000</Amt>
                    </CcyAmt>
                    <CcyAmt>
                        <Ccy>USD</Ccy>
                        <Amt>1.0950</Amt>
                    </CcyAmt>
                    <CcyAmt>
                        <Ccy>GBP</Ccy>
                        <Amt>0.8500</Amt>
                    </CcyAmt>
                </FxRate>
            </FxRates>
            """;
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(xmlResponse);
        FxRates result = externalApiService.fetchCurrentRates(type);
        assertNotNull(result);
        assertNotNull(result.getRates());
        assertEquals(1, result.getRates().size());
        FxRate fxRate = result.getRates().getFirst();
        assertNotNull(fxRate.getCcyAmt());
        assertEquals(3, fxRate.getCcyAmt().size());
        CcyAmt eur = fxRate.getCcyAmt().getFirst();
        assertEquals("EUR", eur.getCcy());
        assertEquals(0, new BigDecimal("1.0000").compareTo(eur.getAmt()));
        CcyAmt usd = fxRate.getCcyAmt().get(1);
        assertEquals("USD", usd.getCcy());
        assertEquals(0, new BigDecimal("1.0950").compareTo(usd.getAmt()));
    }

    @Test
    void fetchRatesForDate_Success() {
        String type = "EU";
        LocalDate date = LocalDate.of(2024, 1, 15);
        String xmlResponse = """
            <FxRates>
                <FxRate>
                    <Tp>EU</Tp>
                    <Dt>2024-01-15</Dt>
                    <CcyAmt>
                        <Ccy>EUR</Ccy>
                        <Amt>1.0000</Amt>
                    </CcyAmt>
                    <CcyAmt>
                        <Ccy>USD</Ccy>
                        <Amt>1.0950</Amt>
                    </CcyAmt>
                </FxRate>
            </FxRates>
            """;
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(xmlResponse);
        FxRates result = externalApiService.fetchRatesForDate(type, date);
        assertNotNull(result);
        assertNotNull(result.getRates());
        assertEquals(1, result.getRates().size());
        assertEquals(2, result.getRates().getFirst().getCcyAmt().size());
    }

    @Test
    void fetchCurrencyTable_EmptyList_Success() {
        String xmlResponse = "<CcyTbl></CcyTbl>";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(xmlResponse);
        CurrencyTable result = externalApiService.fetchCurrencyTable();
        assertNotNull(result);
        assertNull(result.getCurrencies());
    }

    @Test
    void fetchCurrencyTable_CurrencyWithoutEnglishName_Success() {
        String xmlResponse = """
            <CcyTbl>
                <CcyNtry>
                    <Ccy>TEST</Ccy>
                    <CcyNm lang="FR">Test Francais</CcyNm>
                    <CcyNm lang="DE">Test Deutsch</CcyNm>
                    <CcyNbr>999</CcyNbr>
                </CcyNtry>
            </CcyTbl>
            """;
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(xmlResponse);
        CurrencyTable result = externalApiService.fetchCurrencyTable();
        assertNotNull(result);
        CurrencyEntry currency = result.getCurrencies().getFirst();
        assertNull(currency.getEnglishName());
        assertEquals(2, currency.getNames().size());
    }

    @Test
    void fetchCurrencyTable_InvalidXml_ThrowsException() {
        String invalidXml = "This is not valid XML";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(invalidXml);
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> externalApiService.fetchCurrencyTable());
        assertTrue(exception.getMessage().contains("Failed to fetch currency list"));
    }

    @Test
    void fetchCurrentRates_InvalidXml_ThrowsException() {
        String type = "EU";
        String invalidXml = "Invalid XML content";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(invalidXml);
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> externalApiService.fetchCurrentRates(type));
        assertTrue(exception.getMessage().contains("Failed to fetch currency rates"));
    }

    @Test
    void getCurrencyListRaw_RestTemplateFails_ThrowsException() {
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("Network error"));
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> externalApiService.getCurrencyListRaw());

        assertTrue(exception.getMessage().contains("Failed to fetch XML"));
    }

    @Test
    void getHistoricalRatesForCurrency_WithAllParameters_Success() {
        String type = "EU";
        String currency = "USD";
        String dateFrom = "2024-01-01";
        String dateTo = "2024-01-15";
        String expectedXml = "<FxRates><FxRate><CcyAmt><Ccy>USD</Ccy><Amt>1.0950</Amt></CcyAmt></FxRate></FxRates>";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(expectedXml);
        String result = externalApiService.getHistoricalRatesForCurrency(type, currency, dateFrom, dateTo);
        assertEquals(expectedXml, result);
        verify(restTemplate).getForObject(contains("tp=" + type), eq(String.class));
        verify(restTemplate).getForObject(contains("ccy=" + currency), eq(String.class));
        verify(restTemplate).getForObject(contains("dtFrom=" + dateFrom), eq(String.class));
        verify(restTemplate).getForObject(contains("dtTo=" + dateTo), eq(String.class));
    }

    @Test
    void fetchCurrencyTable_WithNamespaces_Success() {
        String xmlWithNamespaces = """
            <CcyTbl xmlns="http://www.lb.lt/WebServices/FxRates">
                <CcyNtry>
                    <Ccy>EUR</Ccy>
                    <CcyNm lang="EN">Euro</CcyNm>
                    <CcyNbr>978</CcyNbr>
                </CcyNtry>
            </CcyTbl>
            """;
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(xmlWithNamespaces);
        CurrencyTable result = externalApiService.fetchCurrencyTable();
        assertNotNull(result);
        assertNotNull(result.getCurrencies());
        assertEquals(1, result.getCurrencies().size());
        assertEquals("EUR", result.getCurrencies().getFirst().getCode());
    }
}