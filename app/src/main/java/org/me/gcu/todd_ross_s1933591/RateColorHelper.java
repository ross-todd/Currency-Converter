package org.me.gcu.todd_ross_s1933591;

import androidx.annotation.ColorRes;

public class RateColorHelper {

    // Applies strength color based on rate value
    @ColorRes
    public static int getColorForRate(float currentRate) {
        if (currentRate <= 1.0f) {
            return R.color.rate_very_strong;
        }
        else if (currentRate > 1.0f && currentRate < 1.5f) {
            return R.color.rate_strong;
        }
        else if (currentRate >= 1.5f && currentRate < 5.0f) {
            return R.color.rate_weak;
        }
        else {
            return R.color.rate_very_weak;
        }
    }

    // Applies strength label based on rate value
    public static String getStrengthLabel(float currentRate) {
        if (currentRate <= 1.0f) {
            return "Very Strong";
        }
        else if (currentRate > 1.0f && currentRate < 1.5f) {
            return "Strong";
        }
        else if (currentRate >= 1.5f && currentRate < 5.0f) {
            return "Weak";
        }
        else {
            return "Very Weak";
        }
    }
}
