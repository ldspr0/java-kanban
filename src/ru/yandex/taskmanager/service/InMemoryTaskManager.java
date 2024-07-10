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
        Epic parentRecord = getEpic(subtask.getEpicId());
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
            Epic parentEpic = getEpic(subtask.getEpicId());
            parentEpic.recalculateStatus(getSubtasksByEpicId(subtask.getEpicId()));
        }
    }

    // Как лучше? Как написано или как в комменте? Я вижу что как в комменте короче, но как будто бы, тогда ему
    // придется 2 раза проходить по листу тасок, один раз для истории, один раз, чтобы вернуть значение.
    /*
        historyManager.add(tasks.get(id));
        return tasks.get(id);
     */
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

        for (Integer subtaskId : getEpic(epicId).getSubtaskIds()) {
            result.add(getSubtask(subtaskId));
        }
        return result;
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = getEpic(id);
        if (epic != null) {
            epics.remove(id);
            for (Integer eachId : epic.getSubtaskIds()) {
                deleteSubtask(eachId);
            }
        }
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {

        Subtask subtask = getSubtask(id);
        if (subtask == null) {
            return;
        }
        // Это проверка для более "быстрого" удаления (случается только при удалении Эпика)
        Epic epic = getEpic(subtask.getEpicId());
        if (epic == null) {
            subtasks.remove(id);
            return;
        }
        // Удалить из внутреннего листа эпика
        epic.getSubtaskIds().remove(id);
        // Пересчитать статус основываясь на внутреннем листе
        epic.recalculateStatus(getSubtasksByEpicId(epic.getId()));
        // Удалить из внешнего листа сабтасков
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
        return historyManager.getHistory();
    }
}

