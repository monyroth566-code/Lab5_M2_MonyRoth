package com.example.expensetracker;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Expense {

    @SerializedName("id")
    private String id;

    private double amount;
    private String currency;
    private String category;
    private String remark;

    @JsonAdapter(ISO8601DateAdapter.class)
    private Date createdDate;

    private String createdBy;

    // Constructors
    public Expense() {}

    public Expense(String id, double amount, String currency, String category,
                   String remark, Date createdDate, String createdBy) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.category = category;
        this.remark = remark;
        this.createdDate = createdDate;
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}