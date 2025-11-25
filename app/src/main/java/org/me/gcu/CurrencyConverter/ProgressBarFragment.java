package org.me.gcu.CurrencyConverter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// Displays a progress bar layout while data is being fetched for home screen
public class ProgressBarFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the specified progress bar layout
        return inflater.inflate(R.layout.fragment_progress_bar, container, false);
    }
}