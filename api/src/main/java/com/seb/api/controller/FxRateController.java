package com.seb.api.controller;

import com.seb.api.controller.dto.conversion.ConversionResultDto;
import com.seb.api.controller.dto.fxRate.RateDto;
import com.seb.api.service.CurrencyConversionService;
import com.seb.api.service.FxRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/rates")
@RequiredArgsConstructor
public class FxRateController {

    private final FxRateService fxRateService;
    private final CurrencyConversionService currencyConversionService;

    @GetMapping
    public List<RateDto> getFxRates(@RequestParam(required = false) LocalDate date) {
        return fxRateService.getRatesForDate(date != null ? date : LocalDate.now());
    }

    @GetMapping("/convert")
    public ConversionResultDto convertCurrency(
            @RequestParam BigDecimal amount,
            @RequestParam String currencyFrom,
            @RequestParam String currencyTo,
            @RequestParam(required = false) LocalDate date
    ) {
        if (date == null) {
            return currencyConversionService.convertWithLatestRate(amount, currencyFrom, currencyTo);
        } else {
            return currencyConversionService.convertWithHistoricalRate(amount, currencyFrom, currencyTo, date);
        }
    }

    @GetMapping("/history/{currencyCode}")
    public List<RateDto> getCurrencyHistory(@PathVariable String currencyCode) {
        return fxRateService.getHistoricalRates(currencyCode);
    }
}
