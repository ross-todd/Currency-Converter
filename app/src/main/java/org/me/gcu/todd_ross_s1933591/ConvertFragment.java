package org.me.gcu.todd_ross_s1933591;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ConvertFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String ARG_INITIAL_RATE = "initialRate";
    private static final int SPINNER_ITEM_LAYOUT = R.layout.item_currency_spinner;

    private CurrencyViewModel currencyViewModel;
    private Spinner spinnerFrom, spinnerTo;
    private EditText editAmount;
    private TextView editConvertedAmount;
    private Button btnConvert;
    private ImageView btnSwap;
    private TextView textCurrencyFrom, textCurrencyTo;

    private CurrencyRate initialRate;

    public static ConvertFragment newInstance(CurrencyRate rate) {
        ConvertFragment fragment = new ConvertFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_INITIAL_RATE, rate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            initialRate = (CurrencyRate) getArguments().getSerializable(ARG_INITIAL_RATE);
        }
        currencyViewModel = new ViewModelProvider(requireActivity()).get(CurrencyViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_convert, container, false);

        spinnerFrom = view.findViewById(R.id.spinnerFrom);
        spinnerTo = view.findViewById(R.id.spinnerTo);
        editAmount = view.findViewById(R.id.editAmount);
        editConvertedAmount = view.findViewById(R.id.editConvertedAmount);
        btnConvert = view.findViewById(R.id.btnConvert);
        btnSwap = view.findViewById(R.id.btnSwap);
        textCurrencyFrom = view.findViewById(R.id.textCurrencyFrom);
        textCurrencyTo = view.findViewById(R.id.textCurrencyTo);

        btnConvert.setEnabled(false);
        btnSwap.setEnabled(false);
        editConvertedAmount.setEnabled(false);

        spinnerFrom.setOnItemSelectedListener(this);
        spinnerTo.setOnItemSelectedListener(this);

        btnSwap.setOnClickListener(v -> swapCurrencies());
        btnConvert.setOnClickListener(v -> performConversion());

        // Fetch data using callback
        currencyViewModel.fetchData(() -> {
            if (isAdded()) {
                List<CurrencyRate> currencyList = currencyViewModel.getCurrencyRates();
                if (currencyList != null && !currencyList.isEmpty()) {
                    setupSpinners(currencyList);
                    btnConvert.setEnabled(true);
                    btnSwap.setEnabled(true);
                } else {
                    Toast.makeText(getContext(), "Failed to load currency data.", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    // Ensures the back button is always displayed when on the convertor screen
    @Override
    public void onResume() {
        super.onResume();

        // Call the public method in MainActivity to ensure the back button is visible
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateBackButtonVisibility();
        }
    }


    private int findCurrencyPosition(List<CurrencyRate> rates, String currencyCode) {
        for (int i = 0; i < rates.size(); i++) {
            if (rates.get(i).getCurrencyCode().equalsIgnoreCase(currencyCode)) {
                return i;
            }
        }
        return -1;
    }

    // Setup the spinners with currency data
    private void setupSpinners(List<CurrencyRate> currencyList) {
        CurrencyAdapter adapter = new CurrencyAdapter(requireContext(), currencyList);
        adapter.setDropDownViewResource(SPINNER_ITEM_LAYOUT);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        int defaultFromIndex = findCurrencyPosition(currencyList, "GBP");
        int defaultToIndex = findCurrencyPosition(currencyList, "USD");

        if (initialRate != null) {
            // Find the position of the initialRate object in the full list
            int initialIndex = currencyList.indexOf(initialRate);
            // Note: This relies on CurrencyRate having a correct equals() implementation
            if (initialIndex != -1) defaultToIndex = initialIndex;
        }

        spinnerFrom.setSelection(defaultFromIndex != -1 ? defaultFromIndex : 0);
        spinnerTo.setSelection(defaultToIndex != -1 ? defaultToIndex : 0);

        CurrencyRate fromRate = (CurrencyRate) spinnerFrom.getSelectedItem();
        CurrencyRate toRate = (CurrencyRate) spinnerTo.getSelectedItem();

        if (fromRate != null) textCurrencyFrom.setText(fromRate.getCurrencyCode());
        if (toRate != null) textCurrencyTo.setText(toRate.getCurrencyCode());
    }

    // Swap the selected currencies in the spinners
    private void swapCurrencies() {
        int fromPosition = spinnerFrom.getSelectedItemPosition();
        int toPosition = spinnerTo.getSelectedItemPosition();

        spinnerFrom.setSelection(toPosition);
        spinnerTo.setSelection(fromPosition);

        if (!editAmount.getText().toString().trim().isEmpty()) {
            performConversion();
        } else {
            editConvertedAmount.setText("");
        }

        Toast.makeText(getContext(), "Currencies Swapped!", Toast.LENGTH_SHORT).show();
    }

    // Handle spinner item selection
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        CurrencyRate selectedRate = (CurrencyRate) parent.getItemAtPosition(position);
        if (selectedRate != null) {
            if (parent == spinnerFrom) textCurrencyFrom.setText(selectedRate.getCurrencyCode());
            else if (parent == spinnerTo) textCurrencyTo.setText(selectedRate.getCurrencyCode());
        }
        // Perform conversion whenever a spinner selection changes
        if (!editAmount.getText().toString().trim().isEmpty()) {
            performConversion();
        }
    }

    // Handle no selection case
    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

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

        CurrencyRate fromRate = (CurrencyRate) spinnerFrom.getSelectedItem();
        CurrencyRate toRate = (CurrencyRate) spinnerTo.getSelectedItem();

        if (fromRate == null || toRate == null) {
            editConvertedAmount.setText(R.string.error_select_currencies);
            return;
        }

        // Note: Assumes rates are normalized (e.g., against 1 unit of the base currency)
        double rateFrom = fromRate.getRate();
        double rateTo = toRate.getRate();

        if (rateFrom == 0.0) {
            editConvertedAmount.setText(R.string.error_rate_from);
            return;
        }

        // Calculation: amount * (Target Rate / Source Rate)
        double convertedAmount = amount * (rateTo / rateFrom);

        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        currencyFormat.setMaximumFractionDigits(2);
        currencyFormat.setMinimumFractionDigits(2);
        editConvertedAmount.setText(currencyFormat.format(convertedAmount));
    }
}