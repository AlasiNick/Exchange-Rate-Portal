package com.seb.api.service.job;

import com.seb.api.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrencyRefreshJob implements Job {

    private final CurrencyService currencyService;

    @Override
    public void execute(JobExecutionContext context) {
        currencyService.refreshCurrencies();
    }
}
