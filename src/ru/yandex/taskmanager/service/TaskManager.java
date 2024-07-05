package ru.yandex.taskmanager.service;

import ru.yandex.taskmanager.model.Epic;
import ru.yandex.taskmanager.model.Subtask;
import ru.yandex.taskmanager.model.Task;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class TaskManager {
    public static int id = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    /*
    1. Дословно от ментора, указания для выполнения тз4:
        (важно - при добавлении таски менеджер возвращает присвоенный айдишник!!!)
        далее по полученному айдишнику получили таску - и в коде можно написать проверку, что таска есть и т.д.

    2. Для меня было логичным, что все таски создаются в NEW, но если мы хотим расширить функционал, то давайте
    3. Специально не хочу открывать доступ к методу setId(),
        потому что присвоением айдшиников таскам должен управлять TaskManager
    */

    public int createRecord(Task task) {
        this.tasks.put(id, new Task(id, task.getTitle(), task.getDescription(), task.getStatus()));
        return id++;
    }

    public int createRecord(Epic epic) {
        epics.put(id, new Epic(id, epic.getTitle(), epic.getDescription()));
        return id++;
    }

    public int createRecord(Subtask subtask) {
        Epic parentRecord = getEpic(subtask.getEpicId());
        if (parentRecord != null) {
            subtasks.put(id, new Subtask(id, subtask.getTitle(), subtask.getDescription(), subtask.getStatus(), subtask.getEpicId()));
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

        /* For нужен потому что epic хранит только айдишники сабтасок, а не сами сабтаски.
        --
        Дословно ментор:
        * в эпике хранятся только айдишники сабтасков.
        --
         */
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
                /*
                 Такой метод мне показался более логичным, так как вместе с удалением сабтасок, может происходить
                 какая-то доп. калькуляция и то что мы удаляем, в одном месте одним способом, а в другом другим,
                 это не очень хорошо, поэтому я в самом методе предусмотрел, чтобы при отсутсвии эпика, он делал
                 минимум действий.
                 */
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


    public void clearTasks() {
        tasks.clear();
    }

    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void clearSubtasks() {
        ArrayList<Subtask> subtasksToDelete = new ArrayList<>(subtasks.values());
        for (Subtask subtask : subtasksToDelete) {
            deleteSubtask(subtask.getId());
        }
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
