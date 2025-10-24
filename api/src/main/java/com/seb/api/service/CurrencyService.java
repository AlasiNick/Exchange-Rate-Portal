package com.seb.api.service;

import com.seb.api.controller.dto.Currency.CurrencyDto;
import com.seb.api.controller.dto.Currency.CurrencyTable;
import com.seb.api.controller.dto.FxRateForCurrency.CcyAmt;
import com.seb.api.controller.dto.FxRateForCurrency.FxRates;
import com.seb.api.repository.CurrencyRepository;
import com.seb.api.repository.entity.Currency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final ExternalApiService externalApiService;

    @Transactional(readOnly = true)
    public List<CurrencyDto> getCurrencies() {
        List<Currency> currencies = currencyRepository.findAll();
        return currencies.stream()
                .map(c -> new CurrencyDto(c.getId(), c.getCode(), c.getName(), c.getRateToEur()))
                .toList();
    }

    @Transactional
    public void refreshCurrencies() {
        try {
            CurrencyTable table = externalApiService.fetchCurrencyTable();
            FxRates fxRates = externalApiService.fetchCurrentRates("EU");

            Map<String, BigDecimal> ratesByCode = fxRates.getRates().stream()
                    .flatMap(rate -> rate.getCcyAmt().stream())
                    .filter(ccyAmt -> !"EUR".equalsIgnoreCase(ccyAmt.getCcy()))
                    .filter(ccyAmt -> ccyAmt.getAmt() != null)
                    .collect(Collectors.toMap(
                            CcyAmt::getCcy,
                            CcyAmt::getAmt,
                            (r1, r2) -> r1
                    ));

            List<Currency> mergedEntities = table.getCurrencies().stream()
                    .filter(Objects::nonNull)
                    .filter(e -> hasText(e.getCode()))
                    .filter(e -> !"XXX".equalsIgnoreCase(e.getCode()))
                    .filter(e -> hasText(e.getId()) && !"N/A".equalsIgnoreCase(e.getId()))
                    .map(e -> {
                        BigDecimal rate = ratesByCode.get(e.getCode());
                        if (rate == null) return null;
                        return new Currency(
                                Objects.toString(e.getId(), "0").trim(),
                                e.getCode().trim(),
                                e.getEnglishName() != null ? e.getEnglishName().trim() : null,
                                rate
                        );
                    })
                    .filter(Objects::nonNull)
                    .filter(c -> hasText(c.getName()))
                    .toList();

            currencyRepository.deleteAllInBatch();
            currencyRepository.saveAll(mergedEntities);

            log.info("Refreshed {} currencies with valid EUR rates", mergedEntities.size());
        } catch (Exception e) {
            log.error("Failed to refresh currencies", e);
            throw new RuntimeException("Failed to refresh currencies: " + e.getMessage(), e);
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
