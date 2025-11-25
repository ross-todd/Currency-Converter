package org.me.gcu.CurrencyConverter;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CurrencyViewModel extends ViewModel {

    private List<CurrencyRate> currencyRates = new ArrayList<>();
    private String lastBuildTime;
    private final XmlParser xmlParser = new XmlParser();
    private boolean dataLoaded = false;

    // Getters
    public List<CurrencyRate> getCurrencyRates() {
        return currencyRates;
    }

    public String getLastBuildTime() {
        return lastBuildTime;
    }

    public CurrencyRate getCurrencyRateByCode(String code) {
        if (currencyRates == null || code == null) return null;

        for (CurrencyRate rate : currencyRates) {
            if (code.equalsIgnoreCase(rate.getCurrencyCode())) return rate;
        }
        return null;
    }

    // Fetch currency data in a background thread
    public void fetchData(Runnable callback) {
        if (dataLoaded) {
            if (callback != null) callback.run();
            return;
        }

        new Thread(() -> {
            String xmlData = fetchXml();
            parseAndStoreData(xmlData);

            dataLoaded = true;

            if (callback != null) {
                new Handler(Looper.getMainLooper()).post(callback);
            }
        }).start();
    }

    // Fetch XML from the URL
    private String fetchXml() {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("https://www.fx-exchange.com/gbp/rss.xml");
            URLConnection connection = url.openConnection();

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) sb.append(line);
            }

            // Clean up XML
            int start = sb.indexOf("<?");
            if (start != -1) sb = new StringBuilder(sb.substring(start));
            int end = sb.indexOf("</rss>");
            if (end != -1) sb = new StringBuilder(sb.substring(0, end + 6));

        } catch (Exception e) {
            Log.e("CurrencyViewModel", "Error fetching XML data", e);
        }
        return sb.toString();
    }

    // Parse XML and store in currencyRates
    private void parseAndStoreData(String xmlData) {
        List<CurrencyRate> parsedRates = xmlParser.parseXml(xmlData);
        lastBuildTime = xmlParser.getLastBuildDate();

        if (parsedRates != null) {
            for (CurrencyRate rate : parsedRates) {
                rate.setLastUpdated(lastBuildTime);
            }
            currencyRates = parsedRates;
        } else {
            currencyRates = Collections.emptyList();
        }
    }
}
