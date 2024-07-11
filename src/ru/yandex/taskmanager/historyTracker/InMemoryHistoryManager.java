package ru.yandex.taskmanager.historyTracker;

import ru.yandex.taskmanager.model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final int HISTORY_SIZE_LIMIT = 10;
    private final LinkedList<Task> viewHistory = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (viewHistory.size() == HISTORY_SIZE_LIMIT) {
            viewHistory.removeFirst();
        }
        viewHistory.addLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(viewHistory);
    }
}
