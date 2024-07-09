package ru.yandex.taskmanager.utility;

import ru.yandex.taskmanager.historyTracker.HistoryManager;
import ru.yandex.taskmanager.historyTracker.InMemoryHistoryManager;
import ru.yandex.taskmanager.service.InMemoryTaskManager;
import ru.yandex.taskmanager.service.TaskManager;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
