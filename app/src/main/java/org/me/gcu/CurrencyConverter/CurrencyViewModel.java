package org.me.gcu.CurrencyConverter;

import androidx.lifecycle.ViewModel;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.util.Log;

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
        if (currencyRates == null || code == null) {
            return null;
        }

        for (CurrencyRate rate : currencyRates) {
            if (code.equalsIgnoreCase(rate.getCurrencyCode())) {
                return rate;
            }
        }
        return null;
    }

    // Fetch currency data in a background thread
    public void fetchData(Runnable callback) {
        if (dataLoaded) {
            if (callback != null) callback.run(); // run callback immediately if data is loaded
            return;
        }

        new Thread(() -> {
            String xmlData = "";
            try {
                URL url = new URL("https://www.fx-exchange.com/gbp/rss.xml");
                URLConnection connection = url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) sb.append(line);
                in.close();

                xmlData = sb.toString();

                int start = xmlData.indexOf("<?");
                if (start != -1) xmlData = xmlData.substring(start);
                int end = xmlData.indexOf("</rss>");
                if (end != -1) xmlData = xmlData.substring(0, end + 6);

            } catch (Exception e) {
                Log.e("CurrencyViewModel", "Error fetching XML data", e);
            }

            List<CurrencyRate> parsedRates = xmlParser.parseXml(xmlData); // parse XML
            lastBuildTime = xmlParser.getLastBuildDate(); // store lastBuildDate

            if (parsedRates != null) {
                for (CurrencyRate rate : parsedRates) {
                    rate.setLastUpdated(lastBuildTime); // set timestamp for all rates
                }
                currencyRates = parsedRates;
            } else {
                currencyRates = Collections.emptyList(); // fallback if parsing failed
            }

            dataLoaded = true;

            if (callback != null) { // run callback on main thread
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(callback);
            }
        }).start();
    }
}