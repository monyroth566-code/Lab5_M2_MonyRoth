package com.example.expensetracker;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ExpenseApiService {

    @GET("expenses")
    Call<List<Expense>> getExpenses(
            @Query("page") int page,
            @Query("limit") int limit
    );

    @POST("expenses")
    Call<Expense> addExpense(@Body Expense expense);

    @GET("expenses/{id}")
    Call<Expense> getExpenseById(@Path("id") String id);

    @DELETE("expenses/{id}")
    Call<Void> deleteExpense(@Path("id") String id);
}