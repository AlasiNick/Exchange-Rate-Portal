package com.seb.api.controller.dto.currency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyDto {
    private String id;
    private String code;
    private String name;
    private BigDecimal rateToEur;
}
