package org.me.gcu.CurrencyConverter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CurrencyAdapter extends ArrayAdapter<CurrencyRate> {

    private static final int LIST_ITEM_LAYOUT = R.layout.item_currency;

    private final int listLayoutId;

    // Constructor
    public CurrencyAdapter(@NonNull Context context, @NonNull List<CurrencyRate> rates) {
        super(context, LIST_ITEM_LAYOUT, rates);
        this.listLayoutId = LIST_ITEM_LAYOUT;
    }

    // getView handles ListView items
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // SIMPLIFIED: Adapter only handles ListView items now
        return getCurrencyListItemView(position, convertView, parent);
    }

    // To hold views for each list item
    private static class CurrencyListItemHolder {
        ImageView flagImage;
        TextView titleText;
        TextView rateText;
        TextView staticLabel;
        Button strengthButton;
    }

    // Creates or reuses list item views
    private View getCurrencyListItemView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CurrencyRate rate = getItem(position);
        CurrencyListItemHolder holder;

        if (convertView == null || convertView.findViewById(R.id.titleTextView) == null) {
            convertView = LayoutInflater.from(getContext()).inflate(listLayoutId, parent, false);

            holder = new CurrencyListItemHolder();
            holder.flagImage = convertView.findViewById(R.id.img_flag);
            holder.titleText = convertView.findViewById(R.id.titleTextView);
            holder.rateText = convertView.findViewById(R.id.rateValueTextView);
            holder.staticLabel = convertView.findViewById(R.id.staticRateLabel);
            holder.strengthButton = convertView.findViewById(R.id.strengthButton); // bind the button

            convertView.setTag(holder);
        } else {
            holder = (CurrencyListItemHolder) convertView.getTag();
        }

        if (rate != null) {
            holder.titleText.setText(rate.getTitle());
            holder.staticLabel.setText(R.string.base_currency_label);

            NumberFormat nf = NumberFormat.getNumberInstance(Locale.getDefault());
            nf.setMinimumFractionDigits(2);
            nf.setMaximumFractionDigits(2);
            holder.rateText.setText(nf.format(rate.getRate()));

            // Set flag
            holder.flagImage.setImageResource(CurrencyFlagMap.getFlagResource(rate.getCurrencyCode()));

            // Strength label
            String strengthLabel = RateColorHelper.getStrengthLabel(rate.getRate());
            holder.strengthButton.setText(strengthLabel);

            // Strength color
            int colorRes = RateColorHelper.getColorForRate(rate.getRate());
            holder.strengthButton.setBackgroundTintList(
                    ContextCompat.getColorStateList(getContext(), colorRes)
            );
            holder.strengthButton.setTextColor(Color.WHITE);

            // Click to navigate to currency calculator
            holder.strengthButton.setOnClickListener(v -> {
                if (getContext() instanceof MainActivity) {
                    ((MainActivity) getContext()).navigateToConverter(rate);
                }
            });
        }

        return convertView;
    }
}