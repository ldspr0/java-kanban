package ru.yandex.taskmanager.tests;

import ru.yandex.taskmanager.enums.Status;
import ru.yandex.taskmanager.model.Epic;
import ru.yandex.taskmanager.model.Subtask;
import ru.yandex.taskmanager.model.Task;
import ru.yandex.taskmanager.service.TaskManager;

import java.util.ArrayList;

public class TestFactory {

    public static void createTestData(TaskManager taskManager) {

        taskManager.createRecord(new Task(0, "Задача 1", "description", Status.NEW));
        taskManager.createRecord(new Task(1, "Задача 2", "description", Status.NEW));
        taskManager.createRecord(new Task(2, "Задача 3", "description", Status.NEW));
        taskManager.createRecord(new Task(3, "Задача 4", "description", Status.NEW));
        taskManager.createRecord(new Task(4, "Задача 5", "description", Status.NEW));

        int epicId1 = taskManager.createRecord(new Epic(5, "Эпик 1", "description"));

        taskManager.createRecord(new Subtask(6, "Подзадача 1-1", "description", Status.NEW, epicId1));
        taskManager.createRecord(new Subtask(7, "Подзадача 1-2", "description", Status.NEW, epicId1));
        taskManager.createRecord(new Subtask(8, "Подзадача 1-3", "description", Status.NEW, epicId1));
        taskManager.createRecord(new Subtask(9, "Подзадача 1-4", "description", Status.NEW, epicId1));

        int epicId2 = taskManager.createRecord(new Epic(10, "Эпик 2", "description"));

        taskManager.createRecord(new Subtask(11, "Подзадача 2-1", "description", Status.NEW, epicId2));

        taskManager.createRecord(new Epic(12, "Эпик 3", "description"));
    }

    public static boolean testGetData(TaskManager taskManager) {
        // получить Таску из Тасок
        Task testTask = taskManager.getTask(2);
        if (!testTask.getTitle().equals("Задача 3")) {
            return false;
        }

        // получить Сабтаск из Сабтасок
        Subtask testTask2 = taskManager.getSubtask(7);
        if (!testTask2.getTitle().equals("Подзадача 1-2")) {
            return false;
        }

        // получить Эпик из Эпиков и проверить что Сабтаски отвязаны (удалены?)
        Epic testTask3 = taskManager.getEpic(10);
        if (!testTask3.getTitle().equals("Эпик 2")) {
            return false;
        }

        // получить Таску из Сабтасок
        Task testTask4 = taskManager.getSubtask(2);
        if (testTask4 != null) {
            return false;
        }

        // получить все таски
        ArrayList<Task> tasks = taskManager.getAllTasks();
        if (tasks.size() != 5) {
            return false;
        }

        // получить все сабтаски
        ArrayList<Subtask> subtasks = taskManager.getAllSubtasks();
        if (subtasks.size() != 5) {
            return false;
        }

        // получить все эпики
        ArrayList<Epic> epics = taskManager.getAllEpics();
        if (epics.size() != 3) {
            return false;
        }

        return true;
    }

    public static boolean testUpdateData(TaskManager taskManager) {

        // Обновляем Таску
        Task testTask = new Task(2, "Новая Задача 3", "description", Status.IN_PROGRESS);
        taskManager.updateRecord(testTask);
        testTask = taskManager.getTask(2);
        if (!testTask.getTitle().equals("Новая Задача 3") || testTask.getStatus() != Status.IN_PROGRESS) {
            return false;
        }

        // Обновляем Эпик
        // общее
        Epic testEpic = new Epic(12, "Новый Эпик 3", "description");
        taskManager.updateRecord(testEpic);
        testEpic.setStatus(Status.IN_PROGRESS);
        testEpic = taskManager.getEpic(12);
        if (!testEpic.getTitle().equals("Новый Эпик 3") || testEpic.getStatus() != Status.NEW) {
            return false;
        }

        // пытаемся обновить статус
        testEpic.setStatus(Status.DONE);
        if (!testEpic.getTitle().equals("Новый Эпик 3") || testEpic.getStatus() != Status.NEW) {
            return false;
        }

        // Обновляем Сабтаску
        // общее
        Subtask testSubtask = new Subtask(8, "Новая Подзадача 1-3", "description", Status.IN_PROGRESS, 5);
        taskManager.updateRecord(testSubtask);
        testSubtask = taskManager.getSubtask(8);
        if (!testSubtask.getTitle().equals("Новая Подзадача 1-3") || testSubtask.getStatus() != Status.IN_PROGRESS) {
            return false;
        }
        // обновляем статус с 1 эпиком
        testEpic = taskManager.getEpic(10);
        if (!testEpic.getTitle().equals("Эпик 2") || testEpic.getStatus() != Status.NEW) {
            return false;
        }
        testSubtask = taskManager.getSubtask(11);
        testSubtask.setStatus(Status.DONE);
        taskManager.updateRecord(testSubtask);
        if (!testEpic.getTitle().equals("Эпик 2") || testEpic.getStatus() != Status.DONE) {
            return false;
        }
        // обновляем статус с 2+ эпиками
        testEpic = taskManager.getEpic(5);
        if (!testEpic.getTitle().equals("Эпик 1") || testEpic.getStatus() != Status.IN_PROGRESS) {
            return false;
        }

        return true;
    }

    public static boolean testRemoveData(TaskManager taskManager) {

        // удалить Таску из Тасок
        ArrayList<Task> tasks = taskManager.getAllTasks();
        if (tasks.size() != 5) {
            return false;
        }

        taskManager.deleteTask(1);
        tasks = taskManager.getAllTasks();
        if (tasks.size() != 4) {
            return false;
        }

        // удалить Сабтаск из Сабтасок (проверка пересчета статуса эпика) (из эпика с сабтасками > 1)
        taskManager.deleteSubtask(8);
        ArrayList<Subtask> subtasks = taskManager.getAllSubtasks();
        if (subtasks.size() != 4) {
            return false;
        }
        if (taskManager.getSubtask(8) != null) {
            return false;
        }
        Epic testEpic = taskManager.getEpic(5);
        if (!testEpic.getTitle().equals("Эпик 1") || testEpic.getStatus() != Status.NEW) {
            return false;
        }
        if (testEpic.getSubtasks().size() != 3) {
            return false;
        }

        // удалить Сабтаск из Сабтасок (проверка пересчета статуса эпика) (из эпика с сабтасками = 1)
        taskManager.deleteSubtask(11);
        testEpic = taskManager.getEpic(10);
        if (!testEpic.getTitle().equals("Эпик 2") || testEpic.getStatus() != Status.NEW) {
            return false;
        }
        if (!testEpic.getSubtasks().isEmpty()) {
            return false;
        }

        // удалить Эпик из Эпиков
        taskManager.createRecord(new Subtask(13, "Подзадача 2-1", "description", Status.NEW, 10));
        if (testEpic.getSubtasks().size() != 1) {
            return false;
        }
        taskManager.deleteEpic(10);
        testEpic = taskManager.getEpic(10);
        if (testEpic != null) {
            return false;
        }

        // удалить Таску из Сабтасок
        taskManager.deleteSubtask(2);
        Subtask subtask = taskManager.getSubtask(2);
        if (subtask != null) {
            return false;
        }

        // удалить Таску по id
        taskManager.deleteTask(0);
        Task testTask = taskManager.getTask(0);
        if (testTask != null) {
            return false;
        }

        // удалить Таску по не существущему id
        taskManager.deleteTask(123);
        taskManager.deleteEpic(123);
        taskManager.deleteSubtask(123);

        // удалить все таски
        taskManager.clearTasks();
        ArrayList<Task> allTasks = taskManager.getAllTasks();
        ArrayList<Subtask> allSubtasks = taskManager.getAllSubtasks();
        ArrayList<Epic> allEpics = taskManager.getAllEpics();

        if (!allTasks.isEmpty()) {
            return false;
        }
        if (allSubtasks.isEmpty()) {
            return false;
        }
        if (allEpics.isEmpty()) {
            return false;
        }

        // удалить все сабтаски
        taskManager.clearSubtasks();
        allTasks = taskManager.getAllTasks();
        allSubtasks = taskManager.getAllSubtasks();
        allEpics = taskManager.getAllEpics();

        if (!allTasks.isEmpty()) {
            return false;
        }
        if (!allSubtasks.isEmpty()) {
            return false;
        }
        if (allEpics.isEmpty()) {
            return false;
        }

        // удалить все эпики
        // добавим 1 сабтаску обратно
        taskManager.createRecord(new Subtask(14, "Подзадача 1-5", "description", Status.NEW, 5));
        allSubtasks = taskManager.getAllSubtasks();
        if (allSubtasks.isEmpty()) {
            return false;
        }

        taskManager.clearEpics();

        allSubtasks = taskManager.getAllSubtasks();
        allEpics = taskManager.getAllEpics();

        if (!allTasks.isEmpty()) {
            return false;
        }
        if (!allSubtasks.isEmpty()) {
            return false;
        }
        if (!allEpics.isEmpty()) {
            return false;
        }

        return true;
    }
}
