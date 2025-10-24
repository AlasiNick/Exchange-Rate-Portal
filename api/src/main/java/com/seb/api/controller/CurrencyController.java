package com.seb.api.controller;

import com.seb.api.controller.dto.Currency.CurrencyDto;
import com.seb.api.controller.dto.FxRate.RateDto;
import com.seb.api.service.CurrencyService;
import com.seb.api.service.FxRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/currencies")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;
    @GetMapping
    public List<CurrencyDto> getCurrencies() {
        return currencyService.getCurrencies();
    }
}
