package org.me.gcu.CurrencyConverter;

import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

// Parses XML currency feed and returns a list of CurrencyRate objects
public class XmlParser {

    private static final String TAG = "XmlParser";
    private String lastBuildDate = null;

    public String getLastBuildDate() {
        return lastBuildDate;
    }

    public List<CurrencyRate> parseXml(String xmlData) {
        List<CurrencyRate> ratesList = new ArrayList<>();
        CurrencyRate currentRate = null;
        lastBuildDate = null;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));

            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = xpp.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("lastBuildDate".equalsIgnoreCase(tagName)) {
                            lastBuildDate = xpp.nextText().trim();
                            Log.d(TAG, "Last Build Date found: " + lastBuildDate);
                        }

                        if ("item".equalsIgnoreCase(tagName)) {
                            currentRate = new CurrencyRate();
                            currentRate.setLastUpdated(lastBuildDate);
                        } else if (currentRate != null) {
                            if ("title".equalsIgnoreCase(tagName)) {
                                String title = xpp.nextText().trim();
                                int separatorIndex = title.indexOf('/');
                                if (separatorIndex != -1) {
                                    String newTitle = title.substring(separatorIndex + 1).trim();
                                    currentRate.setTitle(newTitle);

                                    int openParen = newTitle.lastIndexOf('(');
                                    int closeParen = newTitle.lastIndexOf(')');
                                    if (openParen != -1 && closeParen != -1 && closeParen > openParen) {
                                        String code = newTitle.substring(openParen + 1, closeParen);
                                        currentRate.setCurrencyCode(code.trim());
                                    }
                                } else {
                                    currentRate.setTitle(title);
                                }

                                currentRate.setLastUpdated(lastBuildDate);
                            } else if ("description".equalsIgnoreCase(tagName)) {
                                String description = xpp.nextText().trim();
                                if (description.contains("=")) {
                                    try {
                                        String afterEquals = description.substring(description.indexOf('=') + 1).trim();
                                        String rateString = afterEquals.split(" ")[0];
                                        currentRate.setRate(Float.parseFloat(rateString));
                                    } catch (Exception e) {
                                        currentRate.setRate(0.0f);
                                    }
                                }
                                currentRate.setLastUpdated(lastBuildDate);
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if ("item".equalsIgnoreCase(tagName) && currentRate != null) {
                            if (currentRate.getRate() > 0.0f) {
                                ratesList.add(currentRate);
                            }
                            currentRate = null;
                        }
                        break;
                }
                eventType = xpp.next();
            }

            // Add GBP base rate
            CurrencyRate gbpBaseRate = new CurrencyRate("Great British Pound", 1.0f, "GBP", lastBuildDate);
            ratesList.add(0, gbpBaseRate);

        } catch (Exception e) {
            Log.e(TAG, "XML Parsing Error: " + e.getMessage(), e);
        }

        return ratesList;
    }
}
