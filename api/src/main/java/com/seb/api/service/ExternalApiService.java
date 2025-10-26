package com.seb.api.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import com.seb.api.controller.dto.currency.CurrencyTable;
import com.seb.api.controller.dto.fxRateForCurrency.FxRates;
import com.seb.api.service.config.UrlBuilder;
import com.seb.api.utility.LbLtEndpoints;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Service
@Slf4j
public class ExternalApiService {

    private final RestTemplate restTemplate;
    private final XmlMapper xmlMapper;

    public ExternalApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.xmlMapper = new XmlMapper();
        this.xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.xmlMapper.configure(FromXmlParser.Feature.EMPTY_ELEMENT_AS_NULL, true);
    }

    public String getCurrencyListRaw() {
        return fetchXml(LbLtEndpoints.GET_CURRENCIES_LIST);
    }

    public String getCurrentRatesRaw(String type) {
        return fetchXml(UrlBuilder.from(LbLtEndpoints.GET_CURRENT_FX_RATES)
                .param("tp", type)
                .build());
    }

    public String getRatesForCurrency(String type, String date) {
        return fetchXml(UrlBuilder.from(LbLtEndpoints.GET_FX_RATES)
                .param("tp", type)
                .param("dt", date)
                .build());
    }

    public String getHistoricalRatesForCurrency(String type, String currency, String dateFrom, String dateTo) {
        return fetchXml(UrlBuilder.from(LbLtEndpoints.GET_HISTORICAL_RATES)
                .param("tp", type)
                .param("ccy", currency)
                .param("dtFrom", dateFrom)
                .param("dtTo", dateTo)
                .build());
    }

    public CurrencyTable fetchCurrencyTable() {
        try {
            String xmlCurrencies = stripNamespaces(getCurrencyListRaw());
            return xmlMapper.readValue(xmlCurrencies, CurrencyTable.class);
        } catch (Exception e) {
            log.error("Failed to fetch or parse currency list", e);
            throw new RuntimeException("Failed to fetch currency list: " + e.getMessage(), e);
        }
    }

    public FxRates fetchCurrentRates(String tp) {
        try {
            String xmlRates = stripNamespaces(getCurrentRatesRaw(tp));
            return xmlMapper.readValue(xmlRates, FxRates.class);
        } catch (Exception e) {
            log.error("Failed to fetch or parse currency rates", e);
            throw new RuntimeException("Failed to fetch currency rates: " + e.getMessage(), e);
        }
    }

    public FxRates fetchRatesForDate(String tp, LocalDate date) {
        try {
            String xml = stripNamespaces(getRatesForCurrency(tp, date.toString()));
            return xmlMapper.readValue(xml, FxRates.class);
        } catch (Exception e) {
            log.error("Failed to fetch rates for date {}", date, e);
            throw new RuntimeException("Failed to fetch rates for " + date + ": " + e.getMessage(), e);
        }
    }

    private String stripNamespaces(String xml) {
        return xml.replaceAll(" xmlns=\"[^\"]*\"", "");
    }

    private String fetchXml(String url) {
        try {
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            log.error("Failed to fetch XML from {}", url, e);
            throw new RuntimeException("Failed to fetch XML: " + e.getMessage(), e);
        }
    }
}
