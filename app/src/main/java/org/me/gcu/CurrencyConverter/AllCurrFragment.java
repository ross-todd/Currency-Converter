package org.me.gcu.CurrencyConverter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AllCurrFragment extends Fragment {

    private CurrencyViewModel viewModel;
    private CurrencyAdapter currencyAdapter;

    // Called to create the fragment's UI layout
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_curr, container, false);
        ListView currencyListView = view.findViewById(R.id.all_currency_listview);

        currencyAdapter = new CurrencyAdapter(requireContext(), new ArrayList<>());
        currencyListView.setAdapter(currencyAdapter);

        return view;
    }

    // Called after the view is created, connects ViewModel and starts data fetch
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(CurrencyViewModel.class);

        // Fetch data and update list when ready
        viewModel.fetchData(() -> {
            if (isAdded()) {
                updateList(viewModel.getCurrencyRates());
            }
        });
    }

    // Updates the ListView with the latest currency data and sorts it alphabetically based on currency code
    private void updateList(List<CurrencyRate> allRates) {
        if (allRates == null || currencyAdapter == null) {
            return;
        }

        allRates.sort(Comparator.comparing(CurrencyRate::getCurrencyCode));

        currencyAdapter.clear();
        if (!allRates.isEmpty()) {
            currencyAdapter.addAll(allRates);
        }
        currencyAdapter.notifyDataSetChanged();
    }
}
