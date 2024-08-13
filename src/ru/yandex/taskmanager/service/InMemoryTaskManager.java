package ru.yandex.taskmanager.service;

import ru.yandex.taskmanager.historyTracker.HistoryManager;
import ru.yandex.taskmanager.model.Epic;
import ru.yandex.taskmanager.model.Subtask;
import ru.yandex.taskmanager.model.Task;
import ru.yandex.taskmanager.utility.Managers;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    public static int id = 0;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    @Override
    public int createRecord(Task task) {
        this.tasks.put(id, new Task(id, task.getTitle(), task.getDescription(), task.getStatus()));
        return id++;
    }

    @Override
    public int createRecord(Epic epic) {
        epics.put(id, new Epic(id, epic.getTitle(), epic.getDescription()));
        return id++;
    }

    @Override
    public int createRecord(Subtask subtask) {
        Epic parentRecord = epics.get(subtask.getEpicId());
        if (parentRecord != null) {
            subtasks.put(id, new Subtask(id, subtask.getTitle(), subtask.getDescription(), subtask.getStatus(), subtask.getEpicId()));
            parentRecord.getSubtaskIds().add(id);
        }

        return id++;
    }

    @Override
    public void updateRecord(Task task) {
        if (tasks.containsKey(task.getId())){
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateRecord(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateRecord(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic parentEpic = epics.get(subtask.getEpicId());
            parentEpic.recalculateStatus(getSubtasksByEpicId(subtask.getEpicId()));
        }
    }

    @Override
    public Task getTask(int id) {
        Task result = tasks.get(id);
        historyManager.add(result);
        return result;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask result = subtasks.get(id);
        historyManager.add(result);
        return result;
    }

    @Override
    public Epic getEpic(int id) {
        Epic result = epics.get(id);
        historyManager.add(result);
        return result;
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        ArrayList<Subtask> result = new ArrayList<>();

        for (Integer subtaskId : epics.get(epicId).getSubtaskIds()) {
            result.add(subtasks.get(subtaskId));
        }
        return result;
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.remove(id);
            epics.remove(id);
            for (Integer eachId : epic.getSubtaskIds()) {
                deleteSubtask(eachId);
            }
        }
    }

    @Override
    public void deleteTask(int id) {
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {

        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return;
        }
        // Это проверка для более "быстрого" удаления (случается только при удалении Эпика)
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            historyManager.remove(id);
            subtasks.remove(id);
            return;
        }
        // Удалить из внутреннего листа эпика
        epic.getSubtaskIds().remove(id);
        // Пересчитать статус основываясь на внутреннем листе
        epic.recalculateStatus(getSubtasksByEpicId(epic.getId()));
        // Удалить из внешнего листа сабтасков
        historyManager.remove(id);
        subtasks.remove(id);
    }


    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearSubtasks() {
        ArrayList<Subtask> subtasksToDelete = new ArrayList<>(subtasks.values());
        for (Subtask subtask : subtasksToDelete) {
            deleteSubtask(subtask.getId());
        }
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }
}

