package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseDetailActivity extends AppCompatActivity {

    private TextView tvDetailAmount;
    private TextView tvDetailCurrency;
    private TextView tvDetailCategory;
    private TextView tvDetailRemark;
    private TextView tvDetailCreatedDate;
    private Button btnBackToList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);

        // Initialize Views
        tvDetailAmount = findViewById(R.id.tvDetailAmount);
        tvDetailCurrency = findViewById(R.id.tvDetailCurrency);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        tvDetailRemark = findViewById(R.id.tvDetailRemark);
        tvDetailCreatedDate = findViewById(R.id.tvDetailCreatedDate);
        btnBackToList = findViewById(R.id.btnBackToList);
        progressBar = findViewById(R.id.progressBar);

        // Get expenseId from intent (STRING not int!)
        Intent intent = getIntent();
        String expenseId = intent.getStringExtra("expenseId");

        if (expenseId != null && !expenseId.isEmpty()) {
            loadExpenseDetails(expenseId);
        } else {
            Toast.makeText(this, "Error: No expense ID provided", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Back button
        btnBackToList.setOnClickListener(v -> finish());
    }

    private void loadExpenseDetails(String expenseId) {
        progressBar.setVisibility(View.VISIBLE);

        ExpenseApiService apiService = RetrofitClient.getApiService();
        Call<Expense> call = apiService.getExpenseById(expenseId);

        call.enqueue(new Callback<Expense>() {
            @Override
            public void onResponse(Call<Expense> call, Response<Expense> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    displayExpenseDetails(response.body());
                } else {
                    Toast.makeText(ExpenseDetailActivity.this,
                            "Failed to load details: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Expense> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ExpenseDetailActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void displayExpenseDetails(Expense expense) {
        tvDetailAmount.setText(String.format(Locale.US, "%.2f", expense.getAmount()));
        tvDetailCurrency.setText(expense.getCurrency());
        tvDetailCategory.setText(expense.getCategory());
        tvDetailRemark.setText(expense.getRemark());

        // Format date properly
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US);
        if (expense.getCreatedDate() != null) {
            tvDetailCreatedDate.setText(sdf.format(expense.getCreatedDate()));
        } else {
            tvDetailCreatedDate.setText("N/A");
        }
    }
}