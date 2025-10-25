package com.seb.api.controller.dto.fxRateForCurrency;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class FxRate {
    @JacksonXmlProperty(localName = "CcyAmt")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<CcyAmt> ccyAmt;
}