package ru.yandex.taskmanager.server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.taskmanager.enums.EndPoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BaseHttpHandler implements HttpHandler {

    public static class DurationTypeAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
            jsonWriter.value(duration != null ? String.valueOf(duration.toMinutes()) : "");
        }

        @Override
        public Duration read(JsonReader jsonReader) throws IOException {
            String duraitonValue = jsonReader.nextString();
            return duraitonValue.isEmpty() ? null : Duration.ofMinutes(Long.parseLong(duraitonValue));
        }
    }

    public static class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
            jsonWriter.value(localDateTime != null ? localDateTime.format(dtf) : "");
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            String localDateTimeValue = jsonReader.nextString();
            return localDateTimeValue.isEmpty() ? null : LocalDateTime.parse(localDateTimeValue, dtf);
        }
    }

    protected void sendText(HttpExchange exchange, String text, int responseCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(responseCode, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected EndPoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        // проверка на валидные айди
        try {
            if (pathParts.length == 3 || pathParts.length == 4) {
                Integer.parseInt(pathParts[2]);
            }
        } catch (RuntimeException e) {
            return EndPoint.UNKNOWN;
        }

        switch (requestMethod) {
            case "GET" -> {
                if (pathParts.length == 2) {
                    return EndPoint.GET_RECORDS;
                } else if (pathParts.length == 3) {
                    return EndPoint.GET_RECORD;
                } else if (pathParts.length == 4) {
                    return EndPoint.GET_SUBTASKS_FROM_EPIC;
                }
            }
            case "POST" -> {
                if (pathParts.length == 2) {
                    return EndPoint.CREATE_RECORD;
                } else if (pathParts.length == 3) {
                    return EndPoint.UPDATE_RECORD;
                }
            }
            case "DELETE" -> {
                if (pathParts.length == 3) {
                    return EndPoint.DELETE_RECORD;
                }
            }
        }

        return EndPoint.UNKNOWN;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "No context found for request";
        sendText(exchange, response, 404);
        exchange.close();

    }
}