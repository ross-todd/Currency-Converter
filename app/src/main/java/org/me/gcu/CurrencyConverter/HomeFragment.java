package org.me.gcu.CurrencyConverter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class HomeFragment extends Fragment {

    private TextView updateTimeTextView;
    private CurrencyViewModel viewModel;

    private static final String INPUT_DATE_FORMAT = "EEE MMM dd yyyy HH:mm:ss zzz";
    private static final String OUTPUT_DATE_FORMAT = "MMM dd, yyyy h:mm a (zzz)";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateTimeTextView = view.findViewById(R.id.updateTimeTextView);
        viewModel = new ViewModelProvider(requireActivity()).get(CurrencyViewModel.class);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.container_search, new SearchFragment());
            transaction.replace(R.id.container_main_curr, new MainCurrFragment());
            transaction.replace(R.id.container_all_curr, new AllCurrFragment());
            transaction.commit();
        }

        viewModel.fetchData(this::updateUI);
        readAndUpdateTimeFromPrefs();
    }

    @Override
    public void onResume() {
        super.onResume();
        readAndUpdateTimeFromPrefs();
    }

    private void readAndUpdateTimeFromPrefs() {
        if (getContext() == null) return;

        SharedPreferences prefs =
                getContext().getSharedPreferences("CurrencyPrefs", Context.MODE_PRIVATE);

        String rawTime = prefs.getString("LastUpdatedTime", null);

        String formattedTime = formatDateTimeString(rawTime);

        if (formattedTime != null) {
            updateTimeTextView.setText(getString(R.string.last_updated, formattedTime));
        } else {
            updateTimeTextView.setText(R.string.last_updated_unavailable);
        }
    }

    private String formatDateTimeString(String rawDateString) {
        if (rawDateString == null || rawDateString.isEmpty()) return null;

        SimpleDateFormat inputFormat = new SimpleDateFormat(INPUT_DATE_FORMAT, Locale.US);
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        SimpleDateFormat outputFormat = new SimpleDateFormat(OUTPUT_DATE_FORMAT, Locale.getDefault());

        try {
            Date date = inputFormat.parse(rawDateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            Log.e("HomeFragment", "Failed to parse date string: " + rawDateString, e);
            return null;
        }
    }

    private void updateUI() {
        String lastUpdate = viewModel.getLastBuildTime();
        String formattedTime = formatDateTimeString(lastUpdate);

        if (formattedTime != null) {
            updateTimeTextView.setText(getString(R.string.last_updated, formattedTime));
        } else {
            updateTimeTextView.setText(R.string.last_updated_unavailable);
        }
    }
}
