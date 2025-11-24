
// Name                 Ross Todd
// Student ID           S1933591
// Programme of Study   BSc Software Development - Mobile Platform Development


package org.me.gcu.todd_ross_s1933591;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.activity.OnBackPressedCallback;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private static final int FRAGMENT_CONTAINER_ID = R.id.main_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBack = findViewById(R.id.btnBack);
        CurrencyViewModel viewModel = new ViewModelProvider(this).get(CurrencyViewModel.class);

        if (savedInstanceState == null) {
            // First run: Show ProgressBar
            showFragment(new ProgressBarFragment(), false);
        } else {
            // ⭐ FIX 1: If restoring state (i.e., after rotation), update visibility immediately.
            updateBackButtonVisibility();
        }

        // Back button handling
        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStackImmediate();
                } else {
                    finish();
                }
                // Visibility is updated after a standard back press
                updateBackButtonVisibility();
            }
        });

        // Fetch currency data and switch fragment to home screen
        viewModel.fetchData(() -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(FRAGMENT_CONTAINER_ID);
            if (currentFragment instanceof ProgressBarFragment) {
                // This branch is only executed once, after initial data fetch.
                showFragment(new HomeFragment(), false);
            }
        });
    }

    // ⭐ FIX 2: Override onResume to update visibility in case of fragment changes
    // outside of your control, ensuring consistency after restoration.
    @Override
    protected void onResume() {
        super.onResume();
        updateBackButtonVisibility();
    }

    // Replace current fragment
    private void showFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(FRAGMENT_CONTAINER_ID);
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }

        transaction.add(FRAGMENT_CONTAINER_ID, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
            setBackButtonVisible(true);
        }

        transaction.commit();
        // Visibility is updated after a transaction
        updateBackButtonVisibility();
    }

    // Navigate to converter fragment
    public void navigateToConverter(CurrencyRate rate) {
        ConvertFragment fragment = ConvertFragment.newInstance(rate);
        showFragment(fragment, true);
    }

    // Show or hide back button depending if user is on home screen or convertor screen
    private void setBackButtonVisible(boolean visible) {
        if (btnBack != null) btnBack.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    // The back button should be visible IF there is anything in the back stack.
    public void updateBackButtonVisibility() {
        boolean isRootScreen = getSupportFragmentManager().getBackStackEntryCount() == 0;
        setBackButtonVisible(!isRootScreen);
    }
}