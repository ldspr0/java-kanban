package ru.yandex.taskmanager.historyTracker;

import ru.yandex.taskmanager.model.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();
}
