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

    // Search button becomes clear button after search
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

        if (savedInstanceState != null) {
            isSearchActive = savedInstanceState.getBoolean(KEY_ACTIVE, false);
            String savedQuery = savedInstanceState.getString(KEY_QUERY, "");
            searchInput.setText(savedQuery);

            ArrayList<CurrencyRate> savedResults =
                    (ArrayList<CurrencyRate>) savedInstanceState.getSerializable(KEY_RESULTS);

            if (isSearchActive && savedResults != null) {
                resultsAdapter.addAll(savedResults);
                resultsUiContainer.setVisibility(View.VISIBLE);
                noResultsText.setVisibility(savedResults.isEmpty() ? View.VISIBLE : View.GONE);

                searchButton.setText(R.string.clear_button_label);
                searchButton.setBackgroundTintList(
                        ContextCompat.getColorStateList(requireContext(), R.color.clear_red));
            } else {
                searchButton.setText(R.string.search_button_label);
                searchButton.setBackgroundTintList(
                        ContextCompat.getColorStateList(requireContext(), R.color.main_button_blue));
                resultsUiContainer.setVisibility(View.GONE);
                noResultsText.setVisibility(View.GONE);
            }
        }

        // Fetch currency data
        viewModel.fetchData(() -> {
            if (!isAdded()) return;
            List<CurrencyRate> rates = viewModel.getCurrencyRates();
            if (rates == null) return;
            currentRates.clear();
            currentRates.addAll(rates);
        });

        // Duel function button listener
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
            searchButton.setText(R.string.clear_button_label);
            searchButton.setBackgroundTintList(
                    ContextCompat.getColorStateList(requireContext(), R.color.clear_red));
            isSearchActive = true;
        } else {
            clearSearch();
            searchButton.setText(R.string.search_button_label);
            searchButton.setBackgroundTintList(
                    ContextCompat.getColorStateList(requireContext(), R.color.main_button_blue));
            isSearchActive = false;
        }
    }

    // --- SEARCH FUNCTION INCORPORATING ALIAS MAP ---
    private void executeSearch() {
        String query = searchInput.getText().toString().trim();
        List<CurrencyRate> filteredList = new ArrayList<>();
        Map<String, String> aliasMap = CurrencyAliasMap.ALIAS_TO_ISO;

        if (query.isEmpty()) {
            clearSearch();
            return;
        }

        // Look for alias match (case-insensitive)
        String matchedIso = null;
        for (String alias : aliasMap.keySet()) {
            if (alias.equalsIgnoreCase(query)) {
                matchedIso = aliasMap.get(alias);
                break;
            }
        }

        // If a match is found, filter the current rates
        if (matchedIso != null) {
            for (CurrencyRate rate : currentRates) {
                if (rate.getCurrencyCode().equalsIgnoreCase(matchedIso)) {
                    filteredList.add(rate);
                }
            }
        }

        // Update adapter & UI
        resultsAdapter.clear();
        resultsAdapter.addAll(filteredList);
        resultsAdapter.notifyDataSetChanged();

        noResultsText.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
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
