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
    public String getCurrentRatesForCurrency(@RequestParam String tp) {
        return externalApiService.getCurrentRatesRaw(tp);
    }

    @GetMapping("/rates")
    public String getRatesForCurrency(@RequestParam String tp, String dt) {
        return externalApiService.getRatesForCurrency(tp, dt);
    }

    @GetMapping("/historical-rates")
    public String getHistoricalRatesForCurrency(@RequestParam String tp, String ccy, String dtFrom, String dtTo) {
        return externalApiService.getHistoricalRatesForCurrency(tp, ccy, dtFrom, dtTo);
    }

}
