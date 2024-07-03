package ru.yandex.taskmanager.service;

import ru.yandex.taskmanager.enums.Status;
import ru.yandex.taskmanager.model.Epic;
import ru.yandex.taskmanager.model.Subtask;
import ru.yandex.taskmanager.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class TaskManager {
    public static int id = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public int createRecord(Task task) {
        this.tasks.put(id, new Task(id, task.getTitle(), task.getDescription(), Status.NEW));
        return id++;
    }

    public int createRecord(Epic epic) {
        epics.put(id, new Epic(id, epic.getTitle(), epic.getDescription()));
        return id++;
    }

    public int createRecord(Subtask subtask) {
        subtasks.put(id, new Subtask(id, subtask.getTitle(), subtask.getDescription(), Status.NEW, subtask.getEpicId()));

        Epic parentRecord = getEpic(subtask.getEpicId());
        if (parentRecord != null) {
            parentRecord.getSubtasks().add(id);
        }

        return id++;
    }

    public void updateRecord(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateRecord(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateRecord(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic parentEpic = getEpic(subtask.getEpicId());
        parentEpic.recalculateStatus(getSubtasksByEpicId(subtask.getEpicId()));
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        ArrayList<Subtask> result = new ArrayList<>();

        for (Integer subtaskId : getEpic(epicId).getSubtasks()) {
            result.add(getSubtask(subtaskId));
        }
        return result;
    }

    public void deleteEpic(int id) {
        /* выбрал именно такую реализацию, чтобы к моменту удаления сабтасок, эпика уже не было, а значит и сам метод
        удаления сабтасок будет работать быстрее.
         */
        Epic epic = getEpic(id);
        if (epic != null) {
            epics.remove(id);
            for (Integer eachId : epic.getSubtasks()) {
                deleteSubtask(eachId);
            }
        }
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteSubtask(int id) {

        Subtask subtask = getSubtask(id);
        if (subtask == null) {
            return;
        }
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


    public void clearTasks() {
        tasks.clear();
    }

    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void clearSubtasks() {
        subtasks.clear();
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }
}
