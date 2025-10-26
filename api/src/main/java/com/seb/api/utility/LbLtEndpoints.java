package com.seb.api.utility;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LbLtEndpoints {
public static final String BASE_URL = "https://www.lb.lt/webservices/FxRates/FxRates.asmx/";

public static final String GET_CURRENT_FX_RATES = BASE_URL + "getCurrentFxRates";
public static final String GET_FX_RATES = BASE_URL + "getFxRates";
public static final String GET_HISTORICAL_RATES = BASE_URL + "getFxRatesForCurrency";
public static final String GET_CURRENCIES_LIST = BASE_URL + "getCurrencyList";
}


