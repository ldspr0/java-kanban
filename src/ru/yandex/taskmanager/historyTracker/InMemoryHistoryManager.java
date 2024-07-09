package ru.yandex.taskmanager.historyTracker;

import ru.yandex.taskmanager.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    //private final int HISTORY_SIZE_LIMIT = 10;  кажется, что наличе ее тут, более читаемо, или если она нужна
    // только для add, то лучше локально?
    private final ArrayList<Task> viewHistory = new ArrayList<>();

    @Override
    public void add(Task task) {
        int HISTORY_SIZE_LIMIT = 10; //intellij настойчиво предлагает такие переменные делать локальными, правильно ли?

        viewHistory.add(task);
        if (viewHistory.size() > HISTORY_SIZE_LIMIT) {
            viewHistory.removeFirst();
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return viewHistory;
    }
}
