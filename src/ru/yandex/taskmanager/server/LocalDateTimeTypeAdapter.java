package ru.yandex.taskmanager.server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        jsonWriter.value(localDateTime != null ? localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "");
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        String localDateTimeValue = jsonReader.nextString();
        return localDateTimeValue.isEmpty() ? null : LocalDateTime.parse(localDateTimeValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
