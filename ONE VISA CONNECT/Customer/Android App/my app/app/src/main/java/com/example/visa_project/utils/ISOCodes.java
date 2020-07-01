package com.example.visa_project.utils;

import java.util.HashMap;

public class ISOCodes {
    public static HashMap<String, String> countryCodes; // country to country code (Alpha code)
    public static HashMap<String, String> countryCodesNumeric; // country to country code (Numeric Code)
    public static HashMap<String, String> currencyCodes; // country to currency code (Alpha code)
    public static HashMap<String, String> currencyCodesNumeric; // country to currency code (Numeric Code)

    public ISOCodes(){
        countryCodes = new HashMap<>();
        currencyCodes = new HashMap<>();
        countryCodesNumeric = new HashMap<>();
        currencyCodesNumeric = new HashMap<>();

        countryCodes.put("India", "IND");
        countryCodes.put("IN", "IND");
        countryCodes.put("IND", "IND");
        countryCodes.put("356", "IND");
        countryCodes.put("United States of America", "USA");
        countryCodes.put("US", "USA");
        countryCodes.put("USA", "USA");
        countryCodes.put("840", "USA");

        countryCodesNumeric.put("India", "356");
        countryCodesNumeric.put("IN", "356");
        countryCodesNumeric.put("IND", "356");
        countryCodesNumeric.put("356", "356");
        countryCodesNumeric.put("United States of America", "840");
        countryCodesNumeric.put("US", "840");
        countryCodesNumeric.put("USA", "840");
        countryCodesNumeric.put("840", "840");

        currencyCodes.put("India", "INR");
        currencyCodes.put("IN", "INR");
        currencyCodes.put("IND", "INR");
        currencyCodes.put("356", "INR");
        currencyCodes.put("United States of America", "USD");
        currencyCodes.put("US", "USD");
        currencyCodes.put("USA", "USD");
        currencyCodes.put("840", "USD");

        currencyCodesNumeric.put("India", "356");
        currencyCodesNumeric.put("IN", "356");
        currencyCodesNumeric.put("IND", "356");
        currencyCodesNumeric.put("356", "356");
        currencyCodesNumeric.put("United States of America", "840");
        currencyCodesNumeric.put("US", "840");
        currencyCodesNumeric.put("USA", "840");
        currencyCodesNumeric.put("840", "840");
    }

    public String getCountryCode(String country){
        return countryCodes.get(country);
    }

    public String getCurrencyCode(String country){
        return currencyCodes.get(country);
    }

    public String getCountryCodeNumeric(String country){
        return countryCodesNumeric.get(country);
    }

    public String getCurrencyCodeNumeric(String country){
        return currencyCodesNumeric.get(country);
    }
}
