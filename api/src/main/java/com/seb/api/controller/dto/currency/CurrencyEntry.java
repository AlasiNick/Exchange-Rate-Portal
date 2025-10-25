package com.seb.api.controller.dto.currency;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CurrencyEntry {

    @JacksonXmlProperty(localName = "Ccy")
    private String code;

    @JacksonXmlProperty(localName = "CcyNm")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<CurrencyName> names = new ArrayList<>();

    @JacksonXmlProperty(localName = "CcyNbr")
    private String id;

    @JacksonXmlProperty(localName = "CcyMnrUnts")
    private String minorUnits;

    public String getEnglishName() {
        if (names == null || names.isEmpty()) return null;
        return names.stream()
                .filter(name -> "EN".equals(name.getLang()))
                .map(CurrencyName::getValue)
                .findFirst()
                .orElse(null);
    }
}
