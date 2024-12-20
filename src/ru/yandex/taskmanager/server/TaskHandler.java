package ru.yandex.taskmanager.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.taskmanager.model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

import static ru.yandex.taskmanager.server.HttpTaskServer.*;

public class TaskHandler extends BaseHttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();

        String[] pathParts = requestPath.split("/");
        String response = "";

        try {
            switch (getEndpoint(requestPath, exchange.getRequestMethod())) {
                case GET_RECORDS -> {
                    response = gson.toJson(taskManager.getAllTasks());
                    sendText(exchange, response, 200);
                }
                case GET_RECORD -> {
                    Task record = taskManager.getTask(Integer.parseInt(pathParts[2]));
                    if (record == null) {
                        sendText(exchange, LABEL_NOT_FOUND, 404);
                    }

                    response = gson.toJson(record);

                    sendText(exchange, response, 200);
                }
                case CREATE_RECORD -> {
                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Task task = gson.fromJson(body, Task.class);

                    int taskId = taskManager.createRecord(task);

                    if (taskId == -1) {
                        sendText(exchange, LABEL_SLOT_IS_BUSY, 406);
                    } else {
                        sendText(exchange, LABEL_IS_CREATED, 201);
                    }
                }
                case UPDATE_RECORD -> {
                    Task record = taskManager.getTask(Integer.parseInt(pathParts[2]));
                    if (record == null) {
                        sendText(exchange, LABEL_NOT_FOUND, 404);
                    }

                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                    Task task = gson.fromJson(body, Task.class);
                    taskManager.updateRecord(task);

                    sendText(exchange, LABEL_IS_UPDATED, 201);
                }
                case DELETE_RECORD -> {
                    Task record = taskManager.getTask(Integer.parseInt(pathParts[2]));
                    if (record == null) {
                        sendText(exchange, LABEL_NOT_FOUND, 404);
                    }

                    taskManager.deleteTask(Integer.parseInt(pathParts[2]));
                    sendText(exchange, LABEL_IS_DELETED, 200);
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
