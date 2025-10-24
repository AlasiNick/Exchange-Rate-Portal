package com.seb.api.service.job;

import com.seb.api.service.FxRateService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class FxRateRefreshJob implements Job {

    private final FxRateService fxRateService;

    @Override
    public void execute(JobExecutionContext context) {
        fxRateService.refreshRatesForDate(LocalDate.now());
    }
}
