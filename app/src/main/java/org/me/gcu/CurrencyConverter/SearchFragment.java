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

    // Called to create the fragment's UI layout
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

        // To start restoring values on configuration change
        if (savedInstanceState != null) {
            isSearchActive = savedInstanceState.getBoolean(KEY_ACTIVE, false);
            String savedQuery = savedInstanceState.getString(KEY_QUERY, "");

            // Restore the query input
            searchInput.setText(savedQuery);

            // Restore the filtered list
            ArrayList<CurrencyRate> savedResults =
                    (ArrayList<CurrencyRate>) savedInstanceState.getSerializable(KEY_RESULTS);

            // Restore CLEAR state if active
            if (isSearchActive && savedResults != null) {
                resultsAdapter.addAll(savedResults);
                resultsUiContainer.setVisibility(View.VISIBLE);

                if (savedResults.isEmpty()) {
                    noResultsText.setVisibility(View.VISIBLE);
                } else {
                    noResultsText.setVisibility(View.GONE);
                }

                // Restore CLEAR button appearance
                searchButton.setText(R.string.clear_button_label);
                searchButton.setBackgroundTintList(
                        ContextCompat.getColorStateList(requireContext(), R.color.clear_red));
            }
            //  Restore search state if not active
            else {
                searchButton.setText(R.string.search_button_label);
                searchButton.setBackgroundTintList(
                        ContextCompat.getColorStateList(requireContext(), R.color.main_button_blue));

                // Ensure results UI hidden if not active
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

        // Only hide if we are not restoring state, otherwise the restoration logic above handles visibility
        if (savedInstanceState == null) {
            resultsUiContainer.setVisibility(View.GONE);
        }

        return view;
    }

    // To save instance state
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the input query
        outState.putString(KEY_QUERY, searchInput.getText().toString());

        // Save the search active status
        outState.putBoolean(KEY_ACTIVE, isSearchActive);

        // Save the current results displayed in the adapter
        if (resultsAdapter != null && isSearchActive) {
            ArrayList<CurrencyRate> resultsToSave = new ArrayList<>();
            for (int i = 0; i < resultsAdapter.getCount(); i++) {
                resultsToSave.add(resultsAdapter.getItem(i));
            }
            outState.putSerializable(KEY_RESULTS, resultsToSave);
        }
    }

    // Handles the combined Search/Clear logic
    private void handleSearchClearClick() {
        if (!isSearchActive) {
            executeSearch();

            // Update button to CLEAR state
            searchButton.setText(R.string.clear_button_label);

            // Change color to indicate clear action
            searchButton.setBackgroundTintList(
                    ContextCompat.getColorStateList(requireContext(), R.color.clear_red));

            isSearchActive = true;
        } else {
            // Clear action
            clearSearch();

            // Change back to Search button state
            searchButton.setText(R.string.search_button_label);

            // Change color back to original
            searchButton.setBackgroundTintList(
                    ContextCompat.getColorStateList(requireContext(), R.color.main_button_blue));

            isSearchActive = false;
        }
    }

    // Executes search filtering and updates UI
    private void executeSearch() {
        String query = searchInput.getText().toString().toLowerCase().trim();
        List<CurrencyRate> filteredList = new ArrayList<>();

        if (query.isEmpty()) {
            // Treat empty query as needing a clear state
            resultsAdapter.clear();
            resultsAdapter.notifyDataSetChanged();
            resultsUiContainer.setVisibility(View.GONE);
            noResultsText.setVisibility(View.GONE);
            return;
        }

        // Filter currencies that match the query
        for (CurrencyRate rate : currentRates) {
            String combinedData = (rate.getTitle() + " " + rate.getCurrencyCode()).toLowerCase();
            if (combinedData.contains(query)) {
                filteredList.add(rate);
            }
        }

        resultsAdapter.clear();
        resultsAdapter.addAll(filteredList);
        resultsAdapter.notifyDataSetChanged();

        // Show message if no results or display filtered list
        if (filteredList.isEmpty()) {
            noResultsText.setVisibility(View.VISIBLE);
        } else {
            noResultsText.setVisibility(View.GONE);
        }

        resultsUiContainer.setVisibility(View.VISIBLE);

        // Hide keyboard after search
        InputMethodManager imm = (InputMethodManager) requireActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
        }
    }

    // Clears search input and hides results
    private void clearSearch() {
        searchInput.setText("");
        resultsAdapter.clear();
        resultsAdapter.notifyDataSetChanged();
        resultsUiContainer.setVisibility(View.GONE);
        noResultsText.setVisibility(View.GONE);

    }

    // Clears the fragment's view references to prevent memory leaks when the view is destroyed.
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