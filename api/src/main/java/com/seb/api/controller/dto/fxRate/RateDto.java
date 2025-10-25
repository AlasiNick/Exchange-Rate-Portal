package com.seb.api.controller.dto.fxRate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateDto {
    private String currencyCode;
    private BigDecimal rateToEur;
    private LocalDate rateDate;
}
