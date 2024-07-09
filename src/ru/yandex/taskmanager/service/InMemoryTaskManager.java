package ru.yandex.taskmanager.service;

import ru.yandex.taskmanager.model.Epic;
import ru.yandex.taskmanager.model.Subtask;
import ru.yandex.taskmanager.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class InMemoryTaskManager implements TaskManager {
    public static int id = 0;

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
            parentRecord.getSubtasks().add(id);
        }

        return id++;
    }

    @Override
    public void updateRecord(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateRecord(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateRecord(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic parentEpic = getEpic(subtask.getEpicId());
        parentEpic.recalculateStatus(getSubtasksByEpicId(subtask.getEpicId()));
    }

    @Override
    public Task getTask(int id) {
        // TODO: add history
        return tasks.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        // TODO: add history
        return subtasks.get(id);

    }

    @Override
    public Epic getEpic(int id) {
        // TODO: add history
        return epics.get(id);
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        ArrayList<Subtask> result = new ArrayList<>();

        for (Integer subtaskId : getEpic(epicId).getSubtasks()) {
            result.add(getSubtask(subtaskId));
        }
        return result;
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = getEpic(id);
        if (epic != null) {
            epics.remove(id);
            for (Integer eachId : epic.getSubtasks()) {
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
        // Это проверка для более "быстрого" удаления
        Epic epic = getEpic(subtask.getEpicId());
        if (epic == null) {
            subtasks.remove(id);
            return;
        }
        HashSet<Integer> subtaskFromEpic = epic.getSubtasks();
        for (Integer each : subtaskFromEpic) {
            if (each == id) {
                subtaskFromEpic.remove(each);
                break;
            }
        }
        epic.recalculateStatus(getSubtasksByEpicId(epic.getId()));
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
}

