package com.example.expensetracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Date;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://expense-tracker-db-one.vercel.app/";
    private static final String DB_NAME = "31c9f531-1205-4a37-b226-3a5de1304e8b";

    private static Retrofit retrofit = null;
    private static ExpenseApiService apiService = null;

    public static ExpenseApiService getApiService() {
        if (apiService == null) {
            retrofit = getRetrofitInstance();
            apiService = retrofit.create(ExpenseApiService.class);
        }
        return apiService;
    }

    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            // Gson with custom date adapter
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new ISO8601DateAdapter())
                    .create();

            // Logging interceptor for debugging
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // OkHttp client with interceptor for X-DB-NAME header
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("X-DB-NAME", DB_NAME)
                                .method(original.method(), original.body())
                                .build();
                        return chain.proceed(request);
                    })
                    .build();

            // Build Retrofit instance
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}