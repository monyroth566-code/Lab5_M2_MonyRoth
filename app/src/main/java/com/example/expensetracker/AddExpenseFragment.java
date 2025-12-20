package com.example.expensetracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Date;
import java.util.UUID;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddExpenseFragment extends Fragment {

    private EditText etAmount;
    private Spinner spinnerCurrency;
    private Spinner spinnerCategory;
    private EditText etRemark;
    private Button btnAddExpense;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_expense, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etAmount = view.findViewById(R.id.etAmount);
        spinnerCurrency = view.findViewById(R.id.spinnerCurrency);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        etRemark = view.findViewById(R.id.etRemark);
        btnAddExpense = view.findViewById(R.id.btnAddExpense);
        progressBar = view.findViewById(R.id.progressBar);

        ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.currency_options,
                android.R.layout.simple_spinner_item
        );
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(currencyAdapter);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.category_options,
                android.R.layout.simple_spinner_item
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        btnAddExpense.setOnClickListener(v -> {
            if (validateInput()) {
                addExpense();
            }
        });
    }

    private boolean validateInput() {
        String amount = etAmount.getText().toString().trim();
        String remark = etRemark.getText().toString().trim();

        if (amount.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter amount", Toast.LENGTH_SHORT).show();
            etAmount.requestFocus();
            return false;
        }

        try {
            double amountValue = Double.parseDouble(amount);
            if (amountValue <= 0) {
                Toast.makeText(requireContext(), "Amount must be greater than 0",
                        Toast.LENGTH_SHORT).show();
                etAmount.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
            etAmount.requestFocus();
            return false;
        }

        if (remark.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter notes", Toast.LENGTH_SHORT).show();
            etRemark.requestFocus();
            return false;
        }

        return true;
    }

    private void addExpense() {
        double amount = Double.parseDouble(etAmount.getText().toString().trim());
        String currency = spinnerCurrency.getSelectedItem().toString();
        String category = spinnerCategory.getSelectedItem().toString();
        String remark = etRemark.getText().toString().trim();

        // Create expense with auto-generated fields
        Expense expense = new Expense();
        expense.setId(UUID.randomUUID().toString());
        expense.setAmount(amount);
        expense.setCurrency(currency);
        expense.setCategory(category);
        expense.setRemark(remark);
        expense.setCreatedDate(new Date()); // ✅ AUTO-GENERATED from computer time
        expense.setCreatedBy(FirebaseAuth.getInstance().getCurrentUser().getUid());

        btnAddExpense.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        // POST to API
        ExpenseApiService apiService = RetrofitClient.getApiService();
        Call<Expense> call = apiService.addExpense(expense);  // ✅ FIXED LINE 126

        call.enqueue(new Callback<Expense>() {
            @Override
            public void onResponse(Call<Expense> call, Response<Expense> response) {
                btnAddExpense.setEnabled(true);
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireContext(),
                            "Expense added successfully!",
                            Toast.LENGTH_SHORT).show();
                    clearForm();
                } else {
                    Toast.makeText(requireContext(),
                            "Failed to add expense: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Expense> call, Throwable t) {
                btnAddExpense.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(),
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearForm() {
        etAmount.setText("");
        etRemark.setText("");
        spinnerCurrency.setSelection(0);
        spinnerCategory.setSelection(0);
    }
}