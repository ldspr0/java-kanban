package ru.yandex.taskmanager.server;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.taskmanager.service.TaskManager;
import ru.yandex.taskmanager.utility.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;


public class HttpTaskServer {
    private static final int DEFAULT_PORT = 8080;
    protected static final String LABEL_REQUEST_IS_MALFORMED = "Not Acceptable: Request is malformed";
    protected static final String LABEL_SLOT_IS_BUSY = "Not Acceptable: Time slot is busy";
    protected static final String LABEL_NOT_FOUND = "No context found for request";
    protected static final String LABEL_IS_CREATED = "Record is successfully created";
    protected static final String LABEL_IS_UPDATED = "Record is successfully updated";
    protected static final String LABEL_IS_DELETED = "Record is successfully deleted";

    private static HttpServer httpServer;
    protected static TaskManager taskManager;

    public static void start(TaskManager taskManager) throws IOException {
        httpServer = HttpServer.create();

        httpServer.bind(new InetSocketAddress(DEFAULT_PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
        httpServer.createContext("/subtasks", new SubTaskHandler());
        httpServer.createContext("/epics", new EpicHandler());
        httpServer.createContext("/history", new HistoryHandler());
        httpServer.createContext("/prioritized", new PrioritizedHandler());

        httpServer.start();
        HttpTaskServer.taskManager = taskManager;

        System.out.println("HTTP-сервер запущен на " + DEFAULT_PORT + " порту!");
    }

    public static void stop() throws IOException {
        httpServer.stop(1);
    }

    public static void main(String[] args) throws IOException {
        start(Managers.getDefault());
    }


}
