package com.seb.api.controller.dto.Currency;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyName {
    @JacksonXmlProperty(isAttribute = true, localName = "lang")
    private String lang;

    @JacksonXmlText
    private String value;
}
