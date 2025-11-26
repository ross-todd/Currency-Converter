package org.me.gcu.CurrencyConverter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CurrencyAliasMap {
    public static final Map<String, String> ALIAS_TO_ISO;

    static {
        Map<String, String> map = new HashMap<>();

        // Helper method to add multiple aliases so user can enter different country names or codes and correct data will be displayed
        addAliases(map, "AFN", "AFN", "Afghanistan");
        addAliases(map, "ALL", "ALL", "Albania");
        addAliases(map, "DZD", "DZD", "Algeria");
        addAliases(map, "EUR", "EUR", "Andorra", "Austria", "Belgium", "Croatia", "Cyprus", "Estonia", "Finland", "France", "Germany", "Greece", "Ireland", "Italy", "Latvia", "Lithuania", "Luxembourg", "Malta", "Monaco", "Montenegro", "Netherlands", "Portugal", "San Marino", "Slovakia", "Slovenia", "Vatican City", "Euro");
        addAliases(map, "AOA", "AOA", "Angola");
        addAliases(map, "XCD", "XCD", "Antigua and Barbuda", "Dominica", "Grenada", "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines");
        addAliases(map, "ARS", "ARS", "Argentina");
        addAliases(map, "AMD", "AMD", "Armenia");
        addAliases(map, "AUD", "AUD", "Australia", "Australian Dollar");
        addAliases(map, "AZN", "AZN", "Azerbaijan");
        addAliases(map, "BSD", "BSD", "Bahamas");
        addAliases(map, "BHD", "BHD", "Bahrain");
        addAliases(map, "BDT", "BDT", "Bangladesh");
        addAliases(map, "BBD", "BBD", "Barbados");
        addAliases(map, "BYN", "BYN", "Belarus", "Belarusian Ruble");
        addAliases(map, "BZD", "BZD", "Belize");
        addAliases(map, "XOF", "XOF", "Benin", "Burkina Faso", "Ivory Coast", "Guinea-Bissau", "Mali", "Niger", "Senegal", "Togo");  // Benin flag selected for XOF
        addAliases(map, "BMD", "BMD", "Bermuda");
        addAliases(map, "BTN", "BTN", "Bhutan");
        addAliases(map, "BOB", "BOB", "Bolivia");
        addAliases(map, "BAM", "BAM", "Bosnia and Herzegovina");
        addAliases(map, "BWP", "BWP", "Botswana");
        addAliases(map, "BRL", "BRL", "Brazil");
        addAliases(map, "BND", "BND", "Brunei");
        addAliases(map, "BGN", "BGN", "Bulgaria");
        addAliases(map, "BIF", "BIF", "Burundi");
        addAliases(map, "CVE", "CVE", "Cape Verde");
        addAliases(map, "KHR", "KHR", "Cambodia");
        addAliases(map, "XAF", "XAF", "Cameroon", "Central African Republic", "Chad", "Republic of Congo", "Equatorial Guinea", "Gabon", "São Tomé and Príncipe");  // Cameroon flag selected for XAF
        addAliases(map, "CAD", "CAD", "Canada", "Canadian Dollar");
        addAliases(map, "CLP", "CLP", "Chile");
        addAliases(map, "CNY", "CNY", "China", "Chinese Yuan", "Renminbi");
        addAliases(map, "COP", "COP", "Colombia");
        addAliases(map, "KMF", "KMF", "Comoros");
        addAliases(map, "CDF", "CDF", "Congo (Democratic Republic)");
        addAliases(map, "CRC", "CRC", "Costa Rica");
        addAliases(map, "HRK", "HRK", "Croatia");
        addAliases(map, "CUP", "CUP", "Cuba");
        addAliases(map, "CZK", "CZK", "Czechia");
        addAliases(map, "DKK", "DKK", "Denmark");
        addAliases(map, "DJF", "DJF", "Djibouti");
        addAliases(map, "DOP", "DOP", "Dominican Republic");
        addAliases(map, "USD", "USD", "United States", "US Dollar", "USA", "America", "Ecuador", "El Salvador");
        addAliases(map, "EGP", "EGP", "Egypt");
        addAliases(map, "ERN", "ERN", "Eritrea");
        addAliases(map, "SZL", "SZL", "Eswatini");
        addAliases(map, "ETB", "ETB", "Ethiopia");
        addAliases(map, "FJD", "FJD", "Fiji");
        addAliases(map, "GMD", "GMD", "Gambia");
        addAliases(map, "GEL", "GEL", "Georgia");
        addAliases(map, "GHS", "GHS", "Ghana");
        addAliases(map, "GTQ", "GTQ", "Guatemala");
        addAliases(map, "GNF", "GNF", "Guinea");
        addAliases(map, "HTG", "HTG", "Haiti");
        addAliases(map, "HNL", "HNL", "Honduras");
        addAliases(map, "HKD", "HKD", "Hong Kong");
        addAliases(map, "HUF", "HUF", "Hungary");
        addAliases(map, "ISK", "ISK", "Iceland");
        addAliases(map, "INR", "INR", "India", "Indian Rupee");
        addAliases(map, "IDR", "IDR", "Indonesia");
        addAliases(map, "IRR", "IRR", "Iran");
        addAliases(map, "IQD", "IQD", "Iraq");
        addAliases(map, "ILS", "ILS", "Israel");
        addAliases(map, "JMD", "JMD", "Jamaica");
        addAliases(map, "JOD", "JOD", "Jordan");
        addAliases(map, "KES", "KES", "Kenya");
        addAliases(map, "KGS", "KGS", "Kyrgyzstan");
        addAliases(map, "KWD", "KWD", "Kuwait");
        addAliases(map, "KRW", "KRW", "South Korea");
        addAliases(map, "KPW", "KPW", "North Korea");
        addAliases(map, "LAK", "LAK", "Laos");
        addAliases(map, "LBP", "LBP", "Lebanon");
        addAliases(map, "LTL", "LTL", "Lithuania");
        addAliases(map, "LVL", "LVL", "Latvia");
        addAliases(map, "LKR", "LKR", "Sri Lanka");
        addAliases(map, "LRD", "LRD", "Liberia");
        addAliases(map, "LSL", "LSL", "Lesotho");
        addAliases(map, "LYD", "LYD", "Libya");
        addAliases(map, "MAD", "MAD", "Morocco");
        addAliases(map, "MDL", "MDL", "Moldova");
        addAliases(map, "MGA", "MGA", "Madagascar");
        addAliases(map, "MKD", "MKD", "North Macedonia");
        addAliases(map, "MMK", "MMK", "Myanmar");
        addAliases(map, "MNT", "MNT", "Mongolia");
        addAliases(map, "MOP", "MOP", "Macao");
        addAliases(map, "MRU", "MRU", "Mauritania");
        addAliases(map, "MUR", "MUR", "Mauritius");
        addAliases(map, "MVR", "MVR", "Maldives");
        addAliases(map, "MWK", "MWK", "Malawi");
        addAliases(map, "MXN", "MXN", "Mexico");
        addAliases(map, "MYR", "MYR", "Malaysia");
        addAliases(map, "MZN", "MZN", "Mozambique");
        addAliases(map, "NAD", "NAD", "Namibia");
        addAliases(map, "NGN", "NGN", "Nigeria");
        addAliases(map, "NIO", "NIO", "Nicaragua");
        addAliases(map, "NOK", "NOK", "Norway");
        addAliases(map, "NPR", "NPR", "Nepal");
        addAliases(map, "NZD", "NZD", "New Zealand");
        addAliases(map, "OMR", "OMR", "Oman");
        addAliases(map, "PAB", "PAB", "Panama");
        addAliases(map, "PEN", "PEN", "Peru");
        addAliases(map, "PGK", "PGK", "Papua New Guinea");
        addAliases(map, "PHP", "PHP", "Philippines");
        addAliases(map, "PKR", "PKR", "Pakistan");
        addAliases(map, "PLN", "PLN", "Poland");
        addAliases(map, "PYG", "PYG", "Paraguay");
        addAliases(map, "QAR", "QAR", "Qatar");
        addAliases(map, "RON", "RON", "Romania");
        addAliases(map, "RSD", "RSD", "Serbia");
        addAliases(map, "RWF", "RWF", "Rwanda");
        addAliases(map, "SAR", "SAR", "Saudi Arabia");
        addAliases(map, "SBD", "SBD", "Solomon Islands");
        addAliases(map, "SCR", "SCR", "Seychelles");
        addAliases(map, "SDG", "SDG", "Sudan");
        addAliases(map, "SEK", "SEK", "Sweden");
        addAliases(map, "SGD", "SGD", "Singapore");
        addAliases(map, "SHP", "SHP", "Saint Helena");
        addAliases(map, "SYP", "SYP", "Syria");
        addAliases(map, "TZS", "TZS", "Tanzania");
        addAliases(map, "THB", "THB", "Thailand");
        addAliases(map, "TJS", "TJS", "Tajikistan");
        addAliases(map, "TMT", "TMT", "Turkmenistan");
        addAliases(map, "TND", "TND", "Tunisia");
        addAliases(map, "TOP", "TOP", "Tonga");
        addAliases(map, "TRY", "TRY", "Turkey");
        addAliases(map, "TTD", "TTD", "Trinidad and Tobago");
        addAliases(map, "TWD", "TWD", "Taiwan");
        addAliases(map, "UAH", "UAH", "Ukraine");
        addAliases(map, "UGX", "UGX", "Uganda");
        addAliases(map, "UYU", "UYU", "Uruguay");
        addAliases(map, "UZS", "UZS", "Uzbekistan");
        addAliases(map, "VND", "VND", "Vietnam");
        addAliases(map, "VUV", "VUV", "Vanuatu");
        addAliases(map, "WST", "WST", "Samoa");
        addAliases(map, "YER", "YER", "Yemen");
        addAliases(map, "ZWD", "ZWD", "Zimbabwe");
        addAliases(map, "ZAR", "ZAR", "South Africa");
        addAliases(map, "ZMK", "ZMK", "Zambia");
        addAliases(map, "ZMW", "ZMW", "Zambia");

        ALIAS_TO_ISO = Collections.unmodifiableMap(map);
    }

    private static void addAliases(Map<String, String> map, String isoCode, String... aliases) {
        for (String alias : aliases) {
            map.put(alias, isoCode);
        }
    }
}
