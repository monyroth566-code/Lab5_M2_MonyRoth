package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int ADD_EXPENSE_REQUEST = 1;

    // UI Components
    private TextView tvLastExpense;
    private Button btnAddExpense;
    private Button btnViewDetail;

    // Data Storage
    private String amount = "";
    private String currency = "";
    private String category = "";
    private String remark = "";
    private String createdDate = "";
    private boolean hasExpense = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        tvLastExpense = findViewById(R.id.tvLastExpense);
        btnAddExpense = findViewById(R.id.btnAddExpense);
        btnViewDetail = findViewById(R.id.btnViewDetail);

        // Set Initial State
        updateUI();

        // Add New Expense Button Click
        btnAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivityForResult(intent, ADD_EXPENSE_REQUEST);
        });

        // View Detail Button Click
        btnViewDetail.setOnClickListener(v -> {
            if (hasExpense) {
                Intent intent = new Intent(MainActivity.this, ExpenseDetailActivity.class);

                // Pass all data to detail activity
                intent.putExtra("amount", amount);
                intent.putExtra("currency", currency);
                intent.putExtra("category", category);
                intent.putExtra("remark", remark);
                intent.putExtra("createdDate", createdDate);

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_EXPENSE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Retrieve data from AddExpenseActivity
            amount = data.getStringExtra("amount");
            currency = data.getStringExtra("currency");
            category = data.getStringExtra("category");
            remark = data.getStringExtra("remark");
            createdDate = data.getStringExtra("createdDate");

            hasExpense = true;
            updateUI();
        }
    }

    private void updateUI() {
        if (hasExpense) {
            // Update TextView with last expense
            String lastExpenseText = getString(R.string.last_expense_format, amount, currency);
            tvLastExpense.setText(lastExpenseText);

            // Enable View Detail button
            btnViewDetail.setEnabled(true);
            btnViewDetail.setBackgroundResource(R.drawable.btn_secondary_bg);
        } else {
            // Initial state
            tvLastExpense.setText(R.string.last_expense_initial);
            btnViewDetail.setEnabled(false);
            btnViewDetail.setBackgroundResource(R.drawable.btn_disabled_bg);
        }
    }
}