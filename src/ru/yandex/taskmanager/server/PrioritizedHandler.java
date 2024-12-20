package ru.yandex.taskmanager.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static ru.yandex.taskmanager.server.HttpTaskServer.*;

public class PrioritizedHandler extends BaseHttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();

        String response = "";
        try {
            switch (getEndpoint(requestPath, exchange.getRequestMethod())) {
                case GET_RECORDS -> {
                    response = gson.toJson(taskManager.getPrioritizedTasks());
                    sendText(exchange, response, 200);
                }
                case null, default -> {
                    sendText(exchange, LABEL_NOT_FOUND, 404);
                }
            }
        } catch (RuntimeException e) {
            sendText(exchange, LABEL_REQUEST_IS_MALFORMED, 406);
        }
    }
}
