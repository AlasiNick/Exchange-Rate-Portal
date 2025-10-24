package com.seb.api;

import com.seb.api.repository.CurrencyRepository;
import com.seb.api.service.CurrencyService;
import com.seb.api.service.FxRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final CurrencyRepository currencyRepository;
    private final CurrencyService currencyService;
    private final FxRateService fxRateService;

    @Override
    public void run(ApplicationArguments args){
        try {
            if (currencyRepository.count() == 0) {
                log.info("Database is empty. Populating currencies...");
                currencyService.refreshCurrencies();
            }

            if (fxRateService != null) {
                log.info("Backfilling exchange rates for last 90 days...");
                fxRateService.backfillRates(90);
            }
        } catch (Exception e) {
            log.error("Failed to initialize data", e);
        }
    }
}
