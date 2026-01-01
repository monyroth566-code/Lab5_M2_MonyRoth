package com.example.expensetracker;

import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;
import java.util.Date;
import java.util.UUID;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddExpenseFragment extends Fragment {

    private static final String TAG = "AddExpenseFragment";
    private EditText etAmount;
    private Spinner spinnerCurrency;
    private Spinner spinnerCategory;
    private EditText etRemark;
    private Button btnAddExpense;
    private ProgressBar progressBar;

    // Interface for notifying when expense is added
    public interface OnExpenseAddedListener {
        void onExpenseAdded();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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

        // Setup currency spinner
        ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.currency_options,
                android.R.layout.simple_spinner_item
        );
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(currencyAdapter);

        // Setup category spinner
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

        // Get current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create expense with auto-generated fields
        Expense expense = new Expense();
        expense.setId(UUID.randomUUID().toString());
        expense.setAmount(amount);
        expense.setCurrency(currency);
        expense.setCategory(category);
        expense.setRemark(remark);
        expense.setCreatedDate(new Date());
        expense.setCreatedBy(currentUser.getUid());

        Log.d(TAG, "Adding expense: " + expense.getId());

        btnAddExpense.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        // POST to API
        ExpenseApiService apiService = RetrofitClient.getApiService();
        Call<Expense> call = apiService.addExpense(expense);

        call.enqueue(new Callback<Expense>() {
            @Override
            public void onResponse(Call<Expense> call, Response<Expense> response) {
                btnAddExpense.setEnabled(true);
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    Expense addedExpense = response.body();
                    Log.d(TAG, "Expense added successfully: " + addedExpense.getId());
                    Log.d(TAG, "Amount: " + addedExpense.getAmount() + " " + addedExpense.getCurrency());
                    Log.d(TAG, "Category: " + addedExpense.getCategory());

                    Toast.makeText(requireContext(),
                            "Expense added successfully!",
                            Toast.LENGTH_SHORT).show();
                    clearForm();

                    // Notify MainActivity that expense was added
                    if (getActivity() instanceof OnExpenseAddedListener) {
                        Log.d(TAG, "Triggering onExpenseAdded callback");
                        ((OnExpenseAddedListener) getActivity()).onExpenseAdded();
                    }
                } else {
                    Log.e(TAG, "Failed to add expense: " + response.code());
                    Toast.makeText(requireContext(),
                            "Failed to add expense: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Expense> call, Throwable t) {
                Log.e(TAG, "Error adding expense", t);
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
        etAmount.requestFocus();
    }
}