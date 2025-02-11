package org.example.expensetrackerui.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateTypeAdapter extends TypeAdapter<LocalDate> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void write(JsonWriter jsonWriter, LocalDate localDate) throws IOException {
        if(localDate != null) {
            jsonWriter.value(localDate.format(formatter));
        }else{
            jsonWriter.nullValue();
        }

    }

    @Override
    public LocalDate read(JsonReader jsonReader) throws IOException {
        String date = jsonReader.nextString();
        return LocalDate.parse(date, formatter);
    }
}
