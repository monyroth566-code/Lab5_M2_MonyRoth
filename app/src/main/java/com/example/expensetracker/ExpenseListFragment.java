package com.example.expensetracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ExpenseAdapter expenseAdapter;
    private List<Expense> expenseList;
    private ProgressBar progressBar;
    private TextView emptyView;
    private SwipeRefreshLayout swipeRefreshLayout;

    // Pagination
    private int currentPage = 1;
    private int pageSize = 10;
    private boolean isLoading = false;
    private boolean hasMoreData = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expense_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewExpenses);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.emptyView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);

        expenseList = new ArrayList<>();
        expenseAdapter = new ExpenseAdapter(requireContext(), expenseList);
        recyclerView.setAdapter(expenseAdapter);

        // Setup swipe refresh
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);

        setupSwipeToDelete();
        setupInfiniteScroll();
        loadExpenses(currentPage);
    }

    private void loadExpenses(int page) {
        if (isLoading || !hasMoreData) return;

        isLoading = true;
        if (page == 1) {
            progressBar.setVisibility(View.VISIBLE);
        }

        ExpenseApiService apiService = RetrofitClient.getApiService();
        // Sort by createdDate descending (newest first)
        Call<List<Expense>> call = apiService.getExpenses(page, pageSize, "-createdDate");

        call.enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                isLoading = false;

                if (response.isSuccessful() && response.body() != null) {
                    List<Expense> newExpenses = response.body();

                    if (newExpenses.isEmpty()) {
                        hasMoreData = false;
                        if (page == 1 && expenseList.isEmpty()) {
                            emptyView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    } else {
                        emptyView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        if (page == 1) {
                            expenseList.clear();
                        }
                        expenseList.addAll(newExpenses);
                        expenseAdapter.notifyDataSetChanged();

                        hasMoreData = newExpenses.size() >= pageSize;
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load expenses", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Expense>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                isLoading = false;
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupInfiniteScroll() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && hasMoreData && !isLoading) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        currentPage++;
                        loadExpenses(currentPage);
                    }
                }
            }
        });
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Expense expense = expenseList.get(position);
                deleteExpense(expense, position);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void deleteExpense(Expense expense, int position) {
        ExpenseApiService apiService = RetrofitClient.getApiService();
        Call<Void> call = apiService.deleteExpense(expense.getId());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    expenseList.remove(position);
                    expenseAdapter.notifyItemRemoved(position);
                    Toast.makeText(requireContext(), "Expense deleted", Toast.LENGTH_SHORT).show();
                } else {
                    expenseAdapter.notifyItemChanged(position);
                    Toast.makeText(requireContext(),
                            "Failed to delete: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                expenseAdapter.notifyItemChanged(position);
                Toast.makeText(requireContext(),
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    // Make this public so it can be called from MainActivity
    public void refreshData() {
        currentPage = 1;
        hasMoreData = true;
        expenseList.clear();
        expenseAdapter.notifyDataSetChanged();
        loadExpenses(currentPage);
    }
}