package com.seb.api.controller.dto.Currency;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JacksonXmlRootElement(localName = "CcyTbl")
public class CurrencyTable {

    @JacksonXmlProperty(localName = "CcyNtry")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<CurrencyEntry> currencies;
}
