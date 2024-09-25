package ru.yandex.taskmanager.utility;

public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException(String message) {
        super(message);
    }

    public ManagerSaveException(Exception exception, String message) {
        super(message, exception);
    }
}
