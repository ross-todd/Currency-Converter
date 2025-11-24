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
import java.util.List;

public class MainCurrFragment extends Fragment {

    private CurrencyAdapter mainAdapter;
    private CurrencyViewModel viewModel;

    // Called to create the fragment's UI layout
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_curr, container, false);
        ListView mainCurrListView = view.findViewById(R.id.main_currency_listview);

        mainAdapter = new CurrencyAdapter(requireContext(), new ArrayList<>());
        mainCurrListView.setAdapter(mainAdapter);

        return view;
    }

    // Fetch currency data and display main currencies
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(CurrencyViewModel.class);

        viewModel.fetchData(() -> {
            if (!isAdded()) return;
            List<CurrencyRate> allRates = viewModel.getCurrencyRates();
            if (allRates == null || allRates.isEmpty()) return;

            List<CurrencyRate> mainRates = new ArrayList<>();
            for (CurrencyRate rate : allRates) {
                String code = rate.getCurrencyCode();
                if ("USD".equals(code) || "EUR".equals(code) || "JPY".equals(code)) {
                    mainRates.add(rate);
                }
            }

            mainAdapter.clear();
            mainAdapter.addAll(mainRates);
            mainAdapter.notifyDataSetChanged();
        });
    }
}
