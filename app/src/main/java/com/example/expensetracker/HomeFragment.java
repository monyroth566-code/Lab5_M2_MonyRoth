package com.example.expensetracker;

import android.os.Bundle;
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
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private TextView tvAmount;
    private TextView tvCategory;
    private TextView tvDate;
    private CardView summaryCard;
    private TextView noDataText;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        // Refresh data when fragment becomes visible
        loadLatestExpense();
    }

    // Public method to refresh data (called by MainActivity)
    public void refreshData() {
        loadLatestExpense();
    }

    private void loadLatestExpense() {
        progressBar.setVisibility(View.VISIBLE);
        summaryCard.setVisibility(View.GONE);
        noDataText.setVisibility(View.GONE);

        ExpenseApiService apiService = RetrofitClient.getApiService();
        // Get first page with 1 item, sorted by newest first
        Call<List<Expense>> call = apiService.getExpenses(1, 1, "-createdDate");

        call.enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Expense latestExpense = response.body().get(0);
                    displayExpense(latestExpense);
                    summaryCard.setVisibility(View.VISIBLE);
                    noDataText.setVisibility(View.GONE);
                } else {
                    // No expenses found
                    summaryCard.setVisibility(View.GONE);
                    noDataText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Expense>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                summaryCard.setVisibility(View.GONE);
                noDataText.setVisibility(View.VISIBLE);
                noDataText.setText("Error loading data");
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
    }
}