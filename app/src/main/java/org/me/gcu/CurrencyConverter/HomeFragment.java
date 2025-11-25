package org.me.gcu.CurrencyConverter;

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

        // Fetch data and update UI when ready
        viewModel.fetchData(this::updateUI);
    }

    // Updates the last update time display
    private void updateUI() {
        String lastUpdate = viewModel.getLastBuildTime();
        if (lastUpdate != null && !lastUpdate.isEmpty()) {
            updateTimeTextView.setText(getString(R.string.last_updated, lastUpdate));
        } else {
            updateTimeTextView.setText(R.string.last_updated_unavailable);
        }
    }
}
