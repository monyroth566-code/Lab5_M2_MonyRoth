package com.example.expensetracker;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ISO8601DateAdapter extends TypeAdapter<Date> {

    private final SimpleDateFormat dateFormat;

    public ISO8601DateAdapter() {
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        this.dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            String formatted = dateFormat.format(value);
            out.value(formatted);
        }
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        try {
            String dateString = in.nextString();
            return dateFormat.parse(dateString);
        } catch (Exception e) {
            return null;
        }
    }
}
