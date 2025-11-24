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

    // Key for saving and restoring the conversion result
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

    public static ConvertFragment newInstance(CurrencyRate rate) {
        ConvertFragment fragment = new ConvertFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_INITIAL_RATE, rate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            initialRate = (CurrencyRate) getArguments().getSerializable(ARG_INITIAL_RATE);
        }
        currencyViewModel = new ViewModelProvider(requireActivity()).get(CurrencyViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_convert, container, false);

        editAmount = view.findViewById(R.id.editAmount);
        editConvertedAmount = view.findViewById(R.id.editConvertedAmount);
        btnConvert = view.findViewById(R.id.btnConvert);
        btnSwap = view.findViewById(R.id.btnSwap);

        // Fixed Display View Bindings
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

        // START: STATE RESTORATION LOGIC
        if (isRestoringState) {
            String savedResult = savedInstanceState.getString(KEY_CONVERTED_AMOUNT);

            // Restore the converted amount (input field is saved automatically by Android)
            if (savedResult != null) {
                editConvertedAmount.setText(savedResult);
            }
        }
        // END: STATE RESTORATION LOGIC

        currencyViewModel.fetchData(() -> {
            if (isAdded()) {
                List<CurrencyRate> currencyList = currencyViewModel.getCurrencyRates();
                if (currencyList != null && !currencyList.isEmpty()) {
                    // IMPORTANT: Pass the restoration status to prevent clearing the restored result
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

    // START: STATE SAVING LOGIC ADDED
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current text from the result field
        if (editConvertedAmount != null) {
            outState.putString(KEY_CONVERTED_AMOUNT, editConvertedAmount.getText().toString());
        }
    }
    // END: STATE SAVING LOGIC ADDED

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateBackButtonVisibility();
        }
    }

    // New method signature accepts the restoration flag
    private void setupCurrencyDisplay(boolean isRestoringState) {
        currentFromRate = currencyViewModel.getCurrencyRateByCode("GBP");

        currentToRate = initialRate;
        if (currentToRate == null) {
            currentToRate = currencyViewModel.getCurrencyRateByCode("USD");
        }

        // Pass the flag to updateCurrencyUI
        updateCurrencyUI(currentFromRate, currentToRate, isRestoringState);
    }

    /**
     * Helper method to update all UI views based on the current state variables.
     * New signature accepts isRestoringState to prevent clearing the converted result.
     */
    private void updateCurrencyUI(CurrencyRate fromRate, CurrencyRate toRate, boolean isRestoringState) {

        // Update 'From' side display
        if (fromRate != null) {
            textCurrencyFrom.setText(fromRate.getCurrencyCode());
            // FIX 1: Using CurrencyFlagMap instead of FlagUtility
            imageFlagFrom.setImageResource(CurrencyFlagMap.getFlagResource(fromRate.getCurrencyCode()));

            // Assuming getCurrencyName() is implemented or you changed it to getTitle()
            textCurrencyDisplayFrom.setText(String.format(
                    "%s - %s", fromRate.getCurrencyCode(), fromRate.getCurrencyName()
            ));
        }

        // Update 'To' side display
        if (toRate != null) {
            textCurrencyTo.setText(toRate.getCurrencyCode());
            // FIX 2: Using CurrencyFlagMap instead of FlagUtility
            imageFlagTo.setImageResource(CurrencyFlagMap.getFlagResource(toRate.getCurrencyCode()));

            // Assuming getCurrencyName() is implemented or you changed it to getTitle()
            textCurrencyDisplayTo.setText(String.format(
                    "%s - %s", toRate.getCurrencyCode(), toRate.getCurrencyName()
            ));
        }

        // FIX: ONLY clear the result if we are NOT restoring state.
        // If restoring state, the text was set in onCreateView.
        if (!isRestoringState) {
            editConvertedAmount.setText("");
        }
    }

    private void swapCurrencies() {
        CurrencyRate tempRate = currentFromRate;
        currentFromRate = currentToRate;
        currentToRate = tempRate;

        // Since swap calls updateCurrencyUI, we must pass the false flag here to ensure it clears/updates.
        updateCurrencyUI(currentFromRate, currentToRate, false);

        if (!editAmount.getText().toString().trim().isEmpty()) {
            performConversion();
        }

        Toast.makeText(getContext(), "Currencies Swapped!", Toast.LENGTH_SHORT).show();
    }

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