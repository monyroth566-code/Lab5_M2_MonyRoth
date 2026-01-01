package com.example.expensetracker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements AddExpenseFragment.OnExpenseAddedListener {

    private FragmentManager fragmentManager;
    private FirebaseAuth mAuth;

    // Keep references to fragments
    private HomeFragment homeFragment;
    private AddExpenseFragment addExpenseFragment;
    private ExpenseListFragment expenseListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fragmentManager = getSupportFragmentManager();

        // Initialize fragments
        homeFragment = new HomeFragment();
        addExpenseFragment = new AddExpenseFragment();
        expenseListFragment = new ExpenseListFragment();

        if (savedInstanceState == null) {
            loadFragment(homeFragment);
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = homeFragment;
                // Refresh home when navigating to it
                if (homeFragment.isAdded()) {
                    homeFragment.refreshData();
                }
            } else if (itemId == R.id.nav_add_expense) {
                selectedFragment = addExpenseFragment;
            } else if (itemId == R.id.nav_expense_list) {
                selectedFragment = expenseListFragment;
                // Refresh list when navigating to it
                if (expenseListFragment.isAdded()) {
                    expenseListFragment.refreshData();
                }
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_sign_out) {
            mAuth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Callback method when expense is added
    @Override
    public void onExpenseAdded() {
        // Refresh both home and list fragments
        if (homeFragment.isAdded()) {
            homeFragment.refreshData();
        }
        if (expenseListFragment.isAdded()) {
            expenseListFragment.refreshData();
        }
    }
}