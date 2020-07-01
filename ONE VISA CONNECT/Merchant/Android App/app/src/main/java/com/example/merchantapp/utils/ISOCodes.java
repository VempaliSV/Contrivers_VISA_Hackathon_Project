package com.example.merchantapp.utils;

import java.util.HashMap;

public class ISOCodes {
    public static HashMap<String, String> countryCodes;
    public static HashMap<String, String> currencyCodes;

    public ISOCodes(){
        countryCodes = new HashMap<>();
        currencyCodes = new HashMap<>();

        countryCodes.put("India", "IND");
        countryCodes.put("IN", "IND");
        countryCodes.put("IND", "IND");
        countryCodes.put("356", "IND");
        countryCodes.put("United States of America", "USA");
        countryCodes.put("US", "USA");
        countryCodes.put("USA", "USA");
        countryCodes.put("840", "USA");

        currencyCodes.put("India", "INR");
        currencyCodes.put("IN", "INR");
        currencyCodes.put("IND", "INR");
        currencyCodes.put("356", "INR");
        currencyCodes.put("United States of America", "USD");
        currencyCodes.put("US", "USD");
        currencyCodes.put("USA", "USD");
        currencyCodes.put("840", "USD");
    }

    public String getCountryCode(String country){
        return countryCodes.get(country);
    }

    public String getCurrencyCode(String country){
        return currencyCodes.get(country);
    }
}
