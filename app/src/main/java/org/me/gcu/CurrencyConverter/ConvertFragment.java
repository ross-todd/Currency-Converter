package org.me.gcu.CurrencyConverter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ConvertFragment extends Fragment {

    private static final String ARG_INITIAL_RATE = "initialRate";
    private static final String KEY_CONVERTED_AMOUNT = "convertedAmount";

    private CurrencyViewModel currencyViewModel;

    private EditText editAmount;
    private TextView editConvertedAmount;
    private Button btnConvert;
    private ImageView btnSwap;
    private TextView textCurrencyFrom, textCurrencyTo;
    private ImageView imageFlagFrom, imageFlagTo;
    private TextView textCurrencyDisplayFrom, textCurrencyDisplayTo;
    private CurrencyRate currentFromRate;
    private CurrencyRate currentToRate;
    private CurrencyRate initialRate;

    // To create new instance with optional initial rate
    public static ConvertFragment newInstance(CurrencyRate rate) {
        ConvertFragment fragment = new ConvertFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_INITIAL_RATE, rate);
        fragment.setArguments(args);
        return fragment;
    }

    // Called when fragment is created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            initialRate = (CurrencyRate) getArguments().getSerializable(ARG_INITIAL_RATE);
        }
        currencyViewModel = new ViewModelProvider(requireActivity()).get(CurrencyViewModel.class);
    }

    // Called to create the fragment's UI layout
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_convert, container, false);

        editAmount = view.findViewById(R.id.editAmount);
        editConvertedAmount = view.findViewById(R.id.editConvertedAmount);
        btnConvert = view.findViewById(R.id.btnConvert);
        btnSwap = view.findViewById(R.id.btnSwap);
        textCurrencyFrom = view.findViewById(R.id.textCurrencyFrom);
        textCurrencyTo = view.findViewById(R.id.textCurrencyTo);
        imageFlagFrom = view.findViewById(R.id.imageFlagFrom);
        imageFlagTo = view.findViewById(R.id.imageFlagTo);
        textCurrencyDisplayFrom = view.findViewById(R.id.textCurrencyDisplayFrom);
        textCurrencyDisplayTo = view.findViewById(R.id.textCurrencyDisplayTo);


        btnConvert.setEnabled(false);
        btnSwap.setEnabled(false);
        editConvertedAmount.setEnabled(false);

        btnSwap.setOnClickListener(v -> swapCurrencies());
        btnConvert.setOnClickListener(v -> performConversion());

        // Check if we have a saved state
        final boolean isRestoringState = savedInstanceState != null;

        if (isRestoringState) {
            String savedResult = savedInstanceState.getString(KEY_CONVERTED_AMOUNT);

            // Restore the converted amount
            if (savedResult != null) {
                editConvertedAmount.setText(savedResult);
            }
        }

        // Fetch currency data and setup UI
        currencyViewModel.fetchData(() -> {
            if (isAdded()) {
                List<CurrencyRate> currencyList = currencyViewModel.getCurrencyRates();
                if (currencyList != null && !currencyList.isEmpty()) {
                    setupCurrencyDisplay(isRestoringState);

                    btnConvert.setEnabled(true);
                    btnSwap.setEnabled(true);
                } else {
                    Toast.makeText(getContext(), "Failed to load currency data.", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    // To save instance state
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current text from the result field
        if (editConvertedAmount != null) {
            outState.putString(KEY_CONVERTED_AMOUNT, editConvertedAmount.getText().toString());
        }
    }

    // To update visibility of back button
    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateBackButtonVisibility();
        }
    }

    //  Setup initial currency display
    private void setupCurrencyDisplay(boolean isRestoringState) {
        currentFromRate = currencyViewModel.getCurrencyRateByCode("GBP");

        currentToRate = initialRate;
        if (currentToRate == null) {
            currentToRate = currencyViewModel.getCurrencyRateByCode("USD");
        }

        // Pass the flag to updateCurrencyUI
        updateCurrencyUI(currentFromRate, currentToRate, isRestoringState);
    }

    // Update the UI elements for selected currencies
    private void updateCurrencyUI(CurrencyRate fromRate, CurrencyRate toRate, boolean isRestoringState) {

        // Update From side display
        if (fromRate != null) {
            textCurrencyFrom.setText(fromRate.getCurrencyCode());
            imageFlagFrom.setImageResource(CurrencyFlagMap.getFlagResource(fromRate.getCurrencyCode()));
            textCurrencyDisplayFrom.setText(String.format(
                    "%s - %s", fromRate.getCurrencyCode(), fromRate.getCurrencyName()
            ));
        }

        // Update To side display
        if (toRate != null) {
            textCurrencyTo.setText(toRate.getCurrencyCode());
            imageFlagTo.setImageResource(CurrencyFlagMap.getFlagResource(toRate.getCurrencyCode()));
            textCurrencyDisplayTo.setText(String.format(
                    "%s - %s", toRate.getCurrencyCode(), toRate.getCurrencyName()
            ));
        }

        // If restoring state, the text was set in onCreateView.
        if (!isRestoringState) {
            editConvertedAmount.setText("");
        }
    }

    // Swap the selected currencies
    private void swapCurrencies() {
        CurrencyRate tempRate = currentFromRate;
        currentFromRate = currentToRate;
        currentToRate = tempRate;

        // Update UI after swap
        updateCurrencyUI(currentFromRate, currentToRate, false);

        if (!editAmount.getText().toString().trim().isEmpty()) {
            performConversion();
        }

        Toast.makeText(getContext(), "Currencies Swapped!", Toast.LENGTH_SHORT).show();
    }

    // Perform the currency conversion calculation
    private void performConversion() {
        String amountText = editAmount.getText().toString().trim();
        if (amountText.isEmpty()) {
            editConvertedAmount.setText(R.string.error_enter_amount);
            return;
        }

        double amount;
        try { amount = Double.parseDouble(amountText); }
        catch (NumberFormatException e) {
            editConvertedAmount.setText(R.string.error_invalid_number);
            return;
        }

        CurrencyRate fromRate = currentFromRate;
        CurrencyRate toRate = currentToRate;

        if (fromRate == null || toRate == null) {
            editConvertedAmount.setText(R.string.error_select_currencies);
            return;
        }

        double rateFrom = fromRate.getRate();
        double rateTo = toRate.getRate();

        if (rateFrom == 0.0) {
            editConvertedAmount.setText(R.string.error_rate_from);
            return;
        }

        double convertedAmount = amount * (rateTo / rateFrom);

        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        currencyFormat.setMaximumFractionDigits(2);
        currencyFormat.setMinimumFractionDigits(2);
        editConvertedAmount.setText(currencyFormat.format(convertedAmount));
    }
}