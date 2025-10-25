package com.seb.api.controller.dto.fxRateForCurrency;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CcyAmt {
    @JacksonXmlProperty(localName = "Ccy")
    private String ccy;

    @JacksonXmlProperty(localName = "Amt")
    private BigDecimal amt;
}
