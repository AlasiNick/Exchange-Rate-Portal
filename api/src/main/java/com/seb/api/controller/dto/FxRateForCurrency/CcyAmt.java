package com.seb.api.controller.dto.FxRateForCurrency;

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
