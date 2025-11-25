package org.me.gcu.CurrencyConverter;

import android.content.Context; // ADDED
import android.content.SharedPreferences; // ADDED
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

public class HomeFragment extends Fragment {

    private TextView updateTimeTextView;
    private CurrencyViewModel viewModel;

    // Called to create the fragment's UI layout
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    // Called after the view is created, sets up child fragments and fetches data
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateTimeTextView = view.findViewById(R.id.updateTimeTextView);
        viewModel = new ViewModelProvider(requireActivity()).get(CurrencyViewModel.class);

        // Only add child fragments on first creation
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.container_search, new SearchFragment());
            transaction.replace(R.id.container_main_curr, new MainCurrFragment());
            transaction.replace(R.id.container_all_curr, new AllCurrFragment());
            transaction.commit();
        }

        // Fetch data and update UI when ready (This primarily handles the initial XML fetch)
        viewModel.fetchData(this::updateUI);

        // Initial reading from SharedPreferences
        readAndUpdateTimeFromPrefs();
    }

    // ADDED: Override onResume to update the time whenever the user returns to the fragment
    @Override
    public void onResume() {
        super.onResume();
        readAndUpdateTimeFromPrefs();
    }

    // ADDED: Logic to read the LastUpdatedTime saved by the Worker
    private void readAndUpdateTimeFromPrefs() {
        if (getContext() == null) return;

        // Get the SharedPreferences file where the Worker saved the timestamp
        SharedPreferences prefs =
                getContext().getSharedPreferences("CurrencyPrefs", Context.MODE_PRIVATE);

        // Read the timestamp string. Use a default value if not found.
        String savedTime = prefs.getString("LastUpdatedTime", null);

        if (savedTime != null) {
            updateTimeTextView.setText(getString(R.string.last_updated, savedTime));
        } else {
            // Fallback for when the worker hasn't run yet
            updateTimeTextView.setText(R.string.last_updated_unavailable);
        }
    }

    // Updates the last update time display (This method still handles ViewModel data)
    private void updateUI() {
        // This is still useful if the ViewModel handles the initial fetch data
        String lastUpdate = viewModel.getLastBuildTime();
        if (lastUpdate != null && !lastUpdate.isEmpty()) {
            // Only update the TextView if the ViewModel has a time (which happens after the initial fetch)
            updateTimeTextView.setText(getString(R.string.last_updated, lastUpdate));
        } else {
            updateTimeTextView.setText(R.string.last_updated_unavailable);
        }
    }
}