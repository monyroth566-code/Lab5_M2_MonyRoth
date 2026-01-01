package com.example.expensetracker;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private TextView tvAmount;
    private TextView tvCategory;
    private TextView tvDate;
    private CardView summaryCard;
    private TextView noDataText;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        tvAmount = view.findViewById(R.id.tvHomeAmount);
        tvCategory = view.findViewById(R.id.tvHomeCategory);
        tvDate = view.findViewById(R.id.tvHomeDate);
        summaryCard = view.findViewById(R.id.summaryCard);
        noDataText = view.findViewById(R.id.tvNoData);
        progressBar = view.findViewById(R.id.progressBar);

        // Load latest expense
        loadLatestExpense();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Don't auto-refresh on resume to avoid duplicate calls
        Log.d(TAG, "Fragment resumed");
    }

    // Public method to refresh data (called by MainActivity)
    public void refreshData() {
        Log.d(TAG, "Refreshing home data");
        loadLatestExpense();
    }

    private void loadLatestExpense() {
        Log.d(TAG, "Loading latest expense...");
        progressBar.setVisibility(View.VISIBLE);
        summaryCard.setVisibility(View.GONE);
        noDataText.setVisibility(View.GONE);

        ExpenseApiService apiService = RetrofitClient.getApiService();
        // Get first 10 items to ensure we have data, then client-side sort
        Call<List<Expense>> call = apiService.getExpenses(1, 10, "-createdDate");

        Log.d(TAG, "API call created: page=1, limit=10, sort=-createdDate");

        call.enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                progressBar.setVisibility(View.GONE);

                Log.d(TAG, "Response received - Success: " + response.isSuccessful() +
                        ", Code: " + response.code());

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<Expense> expenses = response.body();
                    Log.d(TAG, "Loaded " + expenses.size() + " expense(s)");

                    // CLIENT-SIDE SORT: Ensure we get the newest (backup if API sort fails)
                    Collections.sort(expenses, (e1, e2) -> {
                        if (e1.getCreatedDate() == null || e2.getCreatedDate() == null) {
                            return 0;
                        }
                        // Descending order (newest first)
                        return e2.getCreatedDate().compareTo(e1.getCreatedDate());
                    });

                    // Log first 3 expenses for debugging
                    for (int i = 0; i < Math.min(3, expenses.size()); i++) {
                        Expense e = expenses.get(i);
                        Log.d(TAG, "Expense " + i + ": ID=" + e.getId() +
                                ", Date=" + e.getCreatedDate() +
                                ", Amount=" + e.getAmount() + " " + e.getCurrency() +
                                ", Category=" + e.getCategory());
                    }

                    // Get the first (newest) expense after sorting
                    Expense latestExpense = expenses.get(0);
                    Log.d(TAG, "Displaying latest expense: " + latestExpense.getId());

                    displayExpense(latestExpense);
                    summaryCard.setVisibility(View.VISIBLE);
                    noDataText.setVisibility(View.GONE);
                } else {
                    // No expenses found
                    Log.d(TAG, "No expenses found - Body: " +
                            (response.body() != null ? response.body().size() : "null"));
                    summaryCard.setVisibility(View.GONE);
                    noDataText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Expense>> call, Throwable t) {
                Log.e(TAG, "Failed to load expense", t);
                progressBar.setVisibility(View.GONE);
                summaryCard.setVisibility(View.GONE);
                noDataText.setVisibility(View.VISIBLE);
                noDataText.setText("Error loading data: " + t.getMessage());
            }
        });
    }

    private void displayExpense(Expense expense) {
        // Display amount with currency
        String amountText = String.format(Locale.US, "Amount: %.2f %s",
                expense.getAmount(), expense.getCurrency());
        tvAmount.setText(amountText);

        // Display category
        tvCategory.setText("Category: " + expense.getCategory());

        // Format and display date
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        if (expense.getCreatedDate() != null) {
            tvDate.setText("Date: " + sdf.format(expense.getCreatedDate()));
        } else {
            tvDate.setText("Date: N/A");
        }

        Log.d(TAG, "Displayed expense: ID=" + expense.getId() +
                ", Amount=" + expense.getAmount() + " " + expense.getCurrency() +
                ", Category=" + expense.getCategory());
    }
}