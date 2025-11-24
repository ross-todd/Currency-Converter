package org.me.gcu.CurrencyConverter;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CurrencyRate implements Serializable {

    private static final long serialVersionUID = 1L;

    // Private fields for currency data
    private String title;
    private float rate;
    private String currencyCode;
    private String lastUpdated;

    // No-argument constructor for XmlParsing
    public CurrencyRate() {
        this("", 0.0f, "", "");
    }

    // Constructor
    public CurrencyRate(String title, float rate, String currencyCode, String lastUpdated) {
        this.title = title;
        this.rate = rate;
        this.currencyCode = currencyCode;
        this.lastUpdated = lastUpdated;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public float getRate() {
        return rate;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getCurrencyName() { return title; }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
