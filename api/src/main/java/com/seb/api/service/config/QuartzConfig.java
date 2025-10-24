package com.seb.api.service.config;

import com.seb.api.service.job.CurrencyRefreshJob;
import com.seb.api.service.job.FxRateRefreshJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    private static final int HOURS_TO_START_QUARTZ = 5;
    private static final int MINUTES_TO_START_QUARTZ = 0;

    @Bean
    public JobDetail currencyRefreshJobDetail() {
        return JobBuilder.newJob(CurrencyRefreshJob.class)
                .withIdentity("currencyRefreshJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger currencyRefreshTrigger(JobDetail currencyRefreshJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(currencyRefreshJobDetail)
                .withIdentity("currencyRefreshTrigger")
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(HOURS_TO_START_QUARTZ, MINUTES_TO_START_QUARTZ))
                .build();
    }

    @Bean
    public JobDetail fxRateRefreshJobDetail() {
        return JobBuilder.newJob(FxRateRefreshJob.class)
                .withIdentity("fxRateRefreshJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger fxRateRefreshTrigger(JobDetail fxRateRefreshJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(fxRateRefreshJobDetail)
                .withIdentity("fxRateRefreshTrigger")
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(HOURS_TO_START_QUARTZ, MINUTES_TO_START_QUARTZ+1))
                .build();
    }
}
