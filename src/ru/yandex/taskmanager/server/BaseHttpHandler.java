package ru.yandex.taskmanager.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.taskmanager.enums.EndPoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static ru.yandex.taskmanager.server.HttpTaskServer.LABEL_NOT_FOUND;

public class BaseHttpHandler implements HttpHandler {

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
        sendText(exchange, LABEL_NOT_FOUND, 404);
        exchange.close();

    }
}