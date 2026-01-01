package com.example.expensetracker;

import android.util.Log;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ISO8601DateAdapter extends TypeAdapter<Date> {

    private static final String TAG = "ISO8601DateAdapter";
    private final SimpleDateFormat[] dateFormats;

    public ISO8601DateAdapter() {
        // Support multiple date formats
        dateFormats = new SimpleDateFormat[]{
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US),
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US),
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US),
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US),
                new SimpleDateFormat("yyyy-MM-dd", Locale.US)
        };

        // Set UTC timezone for all formats
        for (SimpleDateFormat format : dateFormats) {
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
    }

    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            // Always write in ISO 8601 format with milliseconds
            String formatted = dateFormats[0].format(value);
            out.value(formatted);
            Log.d(TAG, "Write date: " + formatted);
        }
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        try {
            String dateString = in.nextString();
            Log.d(TAG, "Read date string: " + dateString);

            // Try each format until one works
            for (SimpleDateFormat format : dateFormats) {
                try {
                    Date parsed = format.parse(dateString);
                    Log.d(TAG, "Successfully parsed with format: " + format.toPattern());
                    return parsed;
                } catch (ParseException e) {
                    // Try next format
                }
            }

            // If no format worked, log warning and return current date
            Log.w(TAG, "Could not parse date: " + dateString + ", using current date");
            return new Date();

        } catch (Exception e) {
            Log.e(TAG, "Error reading date", e);
            return new Date();
        }
    }
}