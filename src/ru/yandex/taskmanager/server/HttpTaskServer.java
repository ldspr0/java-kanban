package ru.yandex.taskmanager.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.taskmanager.model.Epic;
import ru.yandex.taskmanager.model.Subtask;
import ru.yandex.taskmanager.service.TaskManager;
import ru.yandex.taskmanager.utility.Managers;
import ru.yandex.taskmanager.model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class HttpTaskServer {
    private static final int DEFAULT_PORT = 8080;
    private static final TaskManager TASK_MANAGER = Managers.getDefault();
    private static HttpServer httpServer;

    public static void start() throws IOException {
        httpServer = HttpServer.create();

        httpServer.bind(new InetSocketAddress(DEFAULT_PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
        httpServer.createContext("/subtasks", new SubTaskHandler());
        httpServer.createContext("/epics", new EpicHandler());
        httpServer.createContext("/history", new HistoryHandler());
        httpServer.createContext("/prioritized", new PrioritizedHandler());

        httpServer.start();

        System.out.println("HTTP-сервер запущен на " + DEFAULT_PORT + " порту!");
    }

    public static void stop() throws IOException {
        httpServer.stop(1);
    }

    public static void main(String[] args) throws IOException {
        start();
    }

    public static class TaskHandler extends BaseHttpHandler {

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

            switch (getEndpoint(requestPath, exchange.getRequestMethod())) {
                case GET_RECORDS -> {
                    response = gson.toJson(TASK_MANAGER.getAllTasks());
                    sendText(exchange, response, 200);
                }
                case GET_RECORD -> {
                    Task record = TASK_MANAGER.getTask(Integer.parseInt(pathParts[2]));
                    if (record == null) {
                        sendText(exchange, "No context found for request", 404);
                    }

                    response = gson.toJson(record);

                    sendText(exchange, response, 200);
                }
                case CREATE_RECORD -> {
                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Task task = gson.fromJson(body, Task.class);

                    int taskId = TASK_MANAGER.createRecord(task);

                    if (taskId == -1) {
                        response = "Not Acceptable: Time slot is busy";
                        sendText(exchange, response, 406);
                    } else {
                        response = "Record is successfully created";
                        sendText(exchange, response, 201);
                    }
                }
                case UPDATE_RECORD -> {
                    Task record = TASK_MANAGER.getTask(Integer.parseInt(pathParts[2]));
                    if (record == null) {
                        sendText(exchange, "No context found for request", 404);
                    }

                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                    Task task = gson.fromJson(body, Task.class);
                    TASK_MANAGER.updateRecord(task);

                    response = "Record is successfully updated";
                    sendText(exchange, response, 201);
                }
                case DELETE_RECORD -> {
                    Task record = TASK_MANAGER.getTask(Integer.parseInt(pathParts[2]));
                    if (record == null) {
                        sendText(exchange, "No context found for request", 404);
                    }

                    TASK_MANAGER.deleteTask(Integer.parseInt(pathParts[2]));
                    response = "Record is successfully deleted";
                    sendText(exchange, response, 200);
                }
                case null, default -> {
                    sendText(exchange, "No context found for request", 404);
                }
            }
        }

    }

    public static class SubTaskHandler extends BaseHttpHandler {
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

            switch (getEndpoint(requestPath, exchange.getRequestMethod())) {
                case GET_RECORDS -> {
                    response = gson.toJson(TASK_MANAGER.getAllSubtasks());
                    sendText(exchange, response, 200);
                }
                case GET_RECORD -> {
                    Subtask record = TASK_MANAGER.getSubtask(Integer.parseInt(pathParts[2]));
                    if (record == null) {
                        sendText(exchange, "No context found for request", 404);
                    }

                    response = gson.toJson(record);

                    sendText(exchange, response, 200);
                }
                case CREATE_RECORD -> {
                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Subtask subtask = gson.fromJson(body, Subtask.class);

                    int subtaskId = TASK_MANAGER.createRecord(subtask);

                    if (subtaskId == -1) {
                        response = "Not Acceptable: Time slot is busy";
                        sendText(exchange, response, 406);
                    } else {
                        response = "Record is successfully created";
                        sendText(exchange, response, 201);
                    }
                }
                case UPDATE_RECORD -> {
                    Subtask record = TASK_MANAGER.getSubtask(Integer.parseInt(pathParts[2]));
                    if (record == null) {
                        sendText(exchange, "No context found for request", 404);
                    }

                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                    Subtask subtask = gson.fromJson(body, Subtask.class);
                    TASK_MANAGER.updateRecord(subtask);

                    response = "Record is successfully updated";
                    sendText(exchange, response, 201);
                }
                case DELETE_RECORD -> {
                    Subtask record = TASK_MANAGER.getSubtask(Integer.parseInt(pathParts[2]));
                    if (record == null) {
                        sendText(exchange, "No context found for request", 404);
                    }

                    TASK_MANAGER.deleteSubtask(Integer.parseInt(pathParts[2]));
                    response = "Record is successfully deleted";
                    sendText(exchange, response, 200);
                }
                case null, default -> {
                    sendText(exchange, "No context found for request", 404);
                }
            }
        }
    }

    public static class EpicHandler extends BaseHttpHandler {
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

            switch (getEndpoint(requestPath, exchange.getRequestMethod())) {
                case GET_RECORDS -> {
                    response = gson.toJson(TASK_MANAGER.getAllEpics());
                    sendText(exchange, response, 200);
                }
                case GET_RECORD -> {
                    Epic record = TASK_MANAGER.getEpic(Integer.parseInt(pathParts[2]));
                    if (record == null) {
                        sendText(exchange, "No context found for request", 404);
                    }

                    response = gson.toJson(record);

                    sendText(exchange, response, 200);
                }
                case GET_SUBTASKS_FROM_EPIC -> {
                    List<Subtask> records = TASK_MANAGER.getSubtasksByEpicId(Integer.parseInt(pathParts[2]));
                    response = gson.toJson(records);
                    sendText(exchange, response, 200);
                }
                case CREATE_RECORD -> {
                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Epic epic = gson.fromJson(body, Epic.class);

                    int epicId = TASK_MANAGER.createRecord(epic);

                    if (epicId == -1) {
                        response = "Not Acceptable: Time slot is busy";
                        sendText(exchange, response, 406);
                    } else {
                        response = "Record is successfully created";
                        sendText(exchange, response, 201);
                    }
                }
                case UPDATE_RECORD -> {
                    Epic record = TASK_MANAGER.getEpic(Integer.parseInt(pathParts[2]));
                    if (record == null) {
                        sendText(exchange, "No context found for request", 404);
                    }

                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                    Epic epic = gson.fromJson(body, Epic.class);
                    TASK_MANAGER.updateRecord(epic);

                    response = "Record is successfully updated";
                    sendText(exchange, response, 201);
                }
                case DELETE_RECORD -> {
                    Epic record = TASK_MANAGER.getEpic(Integer.parseInt(pathParts[2]));
                    if (record == null) {
                        sendText(exchange, "No context found for request", 404);
                    }

                    TASK_MANAGER.deleteEpic(Integer.parseInt(pathParts[2]));
                    response = "Record is successfully deleted";
                    sendText(exchange, response, 200);
                }
                case null, default -> {
                    sendText(exchange, "No context found for request", 404);
                }
            }
        }
    }

    public static class HistoryHandler extends BaseHttpHandler {
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

            switch (getEndpoint(requestPath, exchange.getRequestMethod())) {
                case GET_RECORDS -> {
                    response = gson.toJson(TASK_MANAGER.getHistory());
                    sendText(exchange, response, 200);
                }
                case null, default -> {
                    sendText(exchange, "No context found for request", 404);
                }
            }
        }
    }

    public static class PrioritizedHandler extends BaseHttpHandler {
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

            switch (getEndpoint(requestPath, exchange.getRequestMethod())) {
                case GET_RECORDS -> {
                    response = gson.toJson(TASK_MANAGER.getPrioritizedTasks());
                    sendText(exchange, response, 200);
                }
                case null, default -> {
                    sendText(exchange, "No context found for request", 404);
                }
            }
        }
    }
}
