package com.seb.api.controller;

import com.seb.api.service.ExternalApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/LbLt")
public class ExternalController {

    @Autowired
    private ExternalApiService externalApiService;

    @GetMapping("/currencies")
    public String getCurrencies() {
        return externalApiService.getCurrencyListRaw();
    }

    @GetMapping("/current-rates")
    public String getCurrentRatesForCurrency(@RequestParam String type) {
        return externalApiService.getCurrentRatesRaw(type);
    }

    @GetMapping("/rates")
    public String getRatesForCurrency(@RequestParam String type, String date) {
        return externalApiService.getRatesForCurrency(type, date);
    }

    @GetMapping("/historical-rates")
    public String getHistoricalRatesForCurrency(@RequestParam String type, String currency,
                                                String dateFrom, String dateTo) {
        return externalApiService.getHistoricalRatesForCurrency(type, currency, dateFrom, dateTo);
    }

}
