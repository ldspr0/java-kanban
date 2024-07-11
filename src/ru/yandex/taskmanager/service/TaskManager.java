package ru.yandex.taskmanager.service;

import ru.yandex.taskmanager.model.Epic;
import ru.yandex.taskmanager.model.Subtask;
import ru.yandex.taskmanager.model.Task;

import java.util.List;

public interface TaskManager {
    int createRecord(Task task);

    int createRecord(Epic epic);

    int createRecord(Subtask subtask);

    void updateRecord(Task task);

    void updateRecord(Epic epic);

    void updateRecord(Subtask subtask);

    Task getTask(int id);

    Subtask getSubtask(int id);

    Epic getEpic(int id);

    List<Subtask> getSubtasksByEpicId(int epicId);

    void deleteEpic(int id);

    void deleteTask(int id);

    void deleteSubtask(int id);

    void clearTasks();

    void clearEpics();

    void clearSubtasks();

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    List<Task> getHistory();
}
