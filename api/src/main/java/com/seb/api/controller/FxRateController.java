package com.seb.api.controller;

import com.seb.api.controller.dto.FxRate.RateDto;
import com.seb.api.service.FxRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/rates")
@RequiredArgsConstructor
public class FxRateController {

    private final FxRateService fxRateService;

    @GetMapping
    public List<RateDto> getFxRates(@RequestParam(required = false) LocalDate date) {
        return fxRateService.getRatesForDate(date != null ? date : LocalDate.now());
    }
}
