package org.me.gcu.todd_ross_s1933591;

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
import androidx.core.content.ContextCompat; // Needed for changing button color
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.button.MaterialButton; // Import MaterialButton if used in XML

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

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

        // Fetch currency data
        viewModel.fetchData(() -> {
            if (!isAdded()) return;
            List<CurrencyRate> rates = viewModel.getCurrencyRates();
            if (rates == null) return;
            currentRates.clear();
            currentRates.addAll(rates);
        });

        // --- DUAL-FUNCTION BUTTON LISTENER ---
        searchButton.setOnClickListener(v -> handleSearchClearClick());

        resultsUiContainer.setVisibility(View.GONE); // Hide results initially
        return view;
    }

    // Handles the combined Search/Clear logic
    private void handleSearchClearClick() {
        if (!isSearchActive) {
            // STATE 1: SEARCH ACTION
            executeSearch();

            // UI Update: Change to CLEAR button state
            searchButton.setText(R.string.clear_button_label);

            // Optional: Change color (Requires R.color.clear_red to be defined)
            searchButton.setBackgroundTintList(
                    ContextCompat.getColorStateList(requireContext(), R.color.clear_red));

            isSearchActive = true;
        } else {
            // Clear action
            clearSearch();

            // Change back to SEARCH button state
            searchButton.setText(R.string.search_button_label);

            // Change color back to original
            searchButton.setBackgroundTintList(
                    ContextCompat.getColorStateList(requireContext(), R.color.google_yellow));

            isSearchActive = false;
        }
    }

    // Executes search filtering and updates UI
    private void executeSearch() {
        String query = searchInput.getText().toString().toLowerCase().trim();
        List<CurrencyRate> filteredList = new ArrayList<>();

        if (query.isEmpty()) {
            // Treat empty query as needing a clear state (though the button will remain 'Search' for now)
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

        // Show message if no results, otherwise display filtered list
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Nullify view references for cleanup
        searchInput = null;
        searchButton = null;
        resultsUiContainer = null;
        noResultsText = null;
        searchResultsListView = null;
        resultsAdapter = null;
    }
}