package org.me.gcu.CurrencyConverter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchFragment extends Fragment {

    private static final String KEY_QUERY = "searchQuery";
    private static final String KEY_ACTIVE = "searchActive";
    private static final String KEY_RESULTS = "searchResults";

    private EditText searchInput;
    private MaterialButton searchButton;
    private LinearLayout resultsUiContainer;
    private TextView noResultsText;
    private ListView searchResultsListView;
    private CurrencyAdapter resultsAdapter;

    private CurrencyViewModel viewModel;
    private final List<CurrencyRate> currentRates = new ArrayList<>();

    private boolean isSearchActive = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchInput = view.findViewById(R.id.search_input);
        searchButton = view.findViewById(R.id.search_button);
        resultsUiContainer = view.findViewById(R.id.results_ui_container);
        noResultsText = view.findViewById(R.id.no_results_text);
        searchResultsListView = view.findViewById(R.id.result_display);

        resultsAdapter = new CurrencyAdapter(requireContext(), new ArrayList<>());
        searchResultsListView.setAdapter(resultsAdapter);

        viewModel = new ViewModelProvider(requireActivity()).get(CurrencyViewModel.class);

        searchInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchButton.setEnabled(!s.toString().trim().isEmpty());
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        searchButton.setEnabled(false);

        // Restore state on rotation
        if (savedInstanceState != null) {
            isSearchActive = savedInstanceState.getBoolean(KEY_ACTIVE, false);
            String savedQuery = savedInstanceState.getString(KEY_QUERY, "");
            searchInput.setText(savedQuery);

            ArrayList<CurrencyRate> savedResults =
                    (ArrayList<CurrencyRate>) savedInstanceState.getSerializable(KEY_RESULTS);

            if (isSearchActive && savedResults != null) {
                resultsAdapter.addAll(savedResults);
                resultsUiContainer.setVisibility(View.VISIBLE);
                toggleResultsVisibility(!savedResults.isEmpty());
                setButtonState(true);
            } else {
                setButtonState(false);
            }
        } else {
            setButtonState(false);
        }

        viewModel.fetchData(() -> {
            if (!isAdded()) return;
            List<CurrencyRate> rates = viewModel.getCurrencyRates();
            if (rates == null) return;
            currentRates.clear();
            currentRates.addAll(rates);
        });

        searchButton.setOnClickListener(v -> handleSearchClearClick());

        if (savedInstanceState == null) {
            resultsUiContainer.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_QUERY, searchInput.getText().toString());
        outState.putBoolean(KEY_ACTIVE, isSearchActive);

        if (resultsAdapter != null && isSearchActive) {
            ArrayList<CurrencyRate> resultsToSave = new ArrayList<>();
            for (int i = 0; i < resultsAdapter.getCount(); i++) {
                resultsToSave.add(resultsAdapter.getItem(i));
            }
            outState.putSerializable(KEY_RESULTS, resultsToSave);
        }
    }

    private void handleSearchClearClick() {
        if (!isSearchActive) {
            executeSearch();
            isSearchActive = true;
            setButtonState(true);
        } else {
            clearSearch();
            isSearchActive = false;
            setButtonState(false);
        }
    }

    private void executeSearch() {
        String query = searchInput.getText().toString().trim();
        List<CurrencyRate> filteredList = new ArrayList<>();
        Map<String, String> aliasMap = CurrencyAliasMap.ALIAS_TO_ISO;

        if (query.isEmpty()) {
            clearSearch();
            return;
        }

        String matchedIso = null;
        for (String alias : aliasMap.keySet()) {
            if (alias.equalsIgnoreCase(query)) {
                matchedIso = aliasMap.get(alias);
                break;
            }
        }

        if (matchedIso != null) {
            for (CurrencyRate rate : currentRates) {
                if (rate.getCurrencyCode().equalsIgnoreCase(matchedIso)) {
                    filteredList.add(rate);
                }
            }
        }

        resultsAdapter.clear();
        resultsAdapter.addAll(filteredList);
        resultsAdapter.notifyDataSetChanged();

        // Toggle results vs "No results" message in the same container
        toggleResultsVisibility(!filteredList.isEmpty());

        resultsUiContainer.setVisibility(View.VISIBLE);

        // Hide keyboard
        InputMethodManager imm = (InputMethodManager) requireActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
        }
    }

    private void clearSearch() {
        searchInput.setText("");
        resultsAdapter.clear();
        resultsAdapter.notifyDataSetChanged();

        resultsUiContainer.setVisibility(View.GONE);
        noResultsText.setVisibility(View.GONE);
    }

    // Toggle visibility between results list and "No results" message
    private void toggleResultsVisibility(boolean hasResults) {
        if (hasResults) {
            searchResultsListView.setVisibility(View.VISIBLE);
            noResultsText.setVisibility(View.GONE);
        } else {
            searchResultsListView.setVisibility(View.GONE);
            noResultsText.setVisibility(View.VISIBLE);
        }
    }

    // Update button text and color based on state
    private void setButtonState(boolean searchActive) {
        if (searchActive) {
            searchButton.setText(R.string.clear_button_label);
            searchButton.setBackgroundTintList(
                    ContextCompat.getColorStateList(requireContext(), R.color.search_button_red)
            );
        } else {
            searchButton.setText(R.string.search_button_label);
            searchButton.setBackgroundTintList(
                    ContextCompat.getColorStateList(requireContext(), R.color.main_button_blue)
            );
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        searchInput = null;
        searchButton = null;
        resultsUiContainer = null;
        noResultsText = null;
        searchResultsListView = null;
        resultsAdapter = null;
    }
}
