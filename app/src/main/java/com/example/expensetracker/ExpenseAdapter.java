package com.example.expensetracker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private Context context;
    private List<Expense> expenseList;

    public ExpenseAdapter(Context context, List<Expense> expenseList) {
        this.context = context;
        this.expenseList = expenseList;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);

        holder.tvAmount.setText(String.format(Locale.US, "%.2f", expense.getAmount()));
        holder.tvCurrency.setText(expense.getCurrency());
        holder.tvCategory.setText(expense.getCategory());
        holder.tvRemark.setText(expense.getRemark());

        // ✅ FIX: Format date properly
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        if (expense.getCreatedDate() != null) {
            holder.tvCreatedDate.setText(sdf.format(expense.getCreatedDate()));
        } else {
            holder.tvCreatedDate.setText("N/A");
        }

        // Set click listener to open ExpenseDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ExpenseDetailActivity.class);
            intent.putExtra("expenseId", expense.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount;
        TextView tvCurrency;
        TextView tvCategory;
        TextView tvRemark;
        TextView tvCreatedDate;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tvItemAmount);
            tvCurrency = itemView.findViewById(R.id.tvItemCurrency);
            tvCategory = itemView.findViewById(R.id.tvItemCategory);
            tvRemark = itemView.findViewById(R.id.tvItemRemark);
            tvCreatedDate = itemView.findViewById(R.id.tvItemCreatedDate);
        }
    }
}