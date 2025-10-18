package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddExpenseActivity extends AppCompatActivity {

    // UI Components
    private EditText etAmount;
    private Spinner spinnerCurrency;
    private Spinner spinnerCategory;
    private EditText etRemark;
    private EditText etCreatedDate;
    private Button btnAddExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Initialize Views
        etAmount = findViewById(R.id.etAmount);
        spinnerCurrency = findViewById(R.id.spinnerCurrency);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        etRemark = findViewById(R.id.etRemark);
        etCreatedDate = findViewById(R.id.etCreatedDate);
        btnAddExpense = findViewById(R.id.btnAddExpense);

        // Setup Currency Spinner
        ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.currency_options,
                android.R.layout.simple_spinner_item
        );
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(currencyAdapter);

        // Setup Category Spinner
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.category_options,
                android.R.layout.simple_spinner_item
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Add Expense Button Click
        btnAddExpense.setOnClickListener(v -> {
            if (validateInput()) {
                saveExpense();
            }
        });
    }

    private boolean validateInput() {
        String amount = etAmount.getText().toString().trim();
        String remark = etRemark.getText().toString().trim();
        String date = etCreatedDate.getText().toString().trim();

        if (amount.isEmpty()) {
            Toast.makeText(this, R.string.toast_enter_amount, Toast.LENGTH_SHORT).show();
            etAmount.requestFocus();
            return false;
        }

        if (remark.isEmpty()) {
            Toast.makeText(this, R.string.toast_enter_notes, Toast.LENGTH_SHORT).show();
            etRemark.requestFocus();
            return false;
        }

        if (date.isEmpty()) {
            Toast.makeText(this, R.string.toast_enter_date, Toast.LENGTH_SHORT).show();
            etCreatedDate.requestFocus();
            return false;
        }

        return true;
    }

    private void saveExpense() {
        // Get all input values
        String amount = etAmount.getText().toString().trim();
        String currency = spinnerCurrency.getSelectedItem().toString();
        String category = spinnerCategory.getSelectedItem().toString();
        String remark = etRemark.getText().toString().trim();
        String createdDate = etCreatedDate.getText().toString().trim();

        // Create result intent
        Intent resultIntent = new Intent();
        resultIntent.putExtra("amount", amount);
        resultIntent.putExtra("currency", currency);
        resultIntent.putExtra("category", category);
        resultIntent.putExtra("remark", remark);
        resultIntent.putExtra("createdDate", createdDate);

        // Set result and finish
        setResult(RESULT_OK, resultIntent);

        Toast.makeText(this, R.string.toast_expense_saved, Toast.LENGTH_SHORT).show();

        finish();
    }
}