package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ExpenseDetailActivity extends AppCompatActivity {

    // UI Components
    private TextView tvAmount;
    private TextView tvCurrency;
    private TextView tvCategory;
    private TextView tvRemark;
    private TextView tvCreatedDate;
    private Button btnAddNewExpense;
    private Button btnBackToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);

        // Initialize Views
        tvAmount = findViewById(R.id.tvAmount);
        tvCurrency = findViewById(R.id.tvCurrency);
        tvCategory = findViewById(R.id.tvCategory);
        tvRemark = findViewById(R.id.tvRemark);
        tvCreatedDate = findViewById(R.id.tvCreatedDate);
        btnAddNewExpense = findViewById(R.id.btnAddNewExpense);
        btnBackToHome = findViewById(R.id.btnBackToHome);

        // Get data from intent
        Intent intent = getIntent();
        String amount = intent.getStringExtra("amount");
        String currency = intent.getStringExtra("currency");
        String category = intent.getStringExtra("category");
        String remark = intent.getStringExtra("remark");
        String createdDate = intent.getStringExtra("createdDate");

        // Display data
        tvAmount.setText(amount != null ? amount : getString(R.string.default_amount));
        tvCurrency.setText(currency != null ? currency : getString(R.string.default_value));
        tvCategory.setText(category != null ? category : getString(R.string.default_value));
        tvRemark.setText(remark != null ? remark : getString(R.string.default_value));
        tvCreatedDate.setText(createdDate != null ? createdDate : getString(R.string.default_value));

        // Add New Expense Button
        btnAddNewExpense.setOnClickListener(v -> {
            Intent addIntent = new Intent(ExpenseDetailActivity.this, AddExpenseActivity.class);
            startActivity(addIntent);
            finish();
        });

        // Back to Home Button
        btnBackToHome.setOnClickListener(v -> {
            finish();
        });
    }
}