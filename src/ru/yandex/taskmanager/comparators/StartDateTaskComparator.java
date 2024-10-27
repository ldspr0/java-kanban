package ru.yandex.taskmanager.comparators;

import ru.yandex.taskmanager.model.Task;

import java.util.Comparator;

public class StartDateTaskComparator implements Comparator<Task> {
    @Override
    public int compare(Task t1, Task t2) {
        if (t1.getStartTime() == null || t2.getStartTime() == null) {
            return 1;
        } else if (t1.getStartTime().isAfter(t2.getStartTime())) {
            return 1;
        } else if (t1.getStartTime().isEqual(t2.getStartTime())) {
            return 0;
        }else {
            return -1;
        }
    }
}
