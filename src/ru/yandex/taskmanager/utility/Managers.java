package ru.yandex.taskmanager.utility;

import ru.yandex.taskmanager.historyTracker.HistoryManager;
import ru.yandex.taskmanager.historyTracker.InMemoryHistoryManager;
import ru.yandex.taskmanager.service.FileBackedTaskManager;
import ru.yandex.taskmanager.service.InMemoryTaskManager;
import ru.yandex.taskmanager.service.TaskManager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getManager(String name, String... options) {
        if (!name.isBlank() && name.equals("FileManager")) {
            return new FileBackedTaskManager(options);
        } else {
            return getDefault();
        }
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
