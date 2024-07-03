package ru.yandex.taskmanager.tests;

import ru.yandex.taskmanager.enums.Status;
import ru.yandex.taskmanager.enums.TaskType;
import ru.yandex.taskmanager.model.Epic;
import ru.yandex.taskmanager.model.Subtask;
import ru.yandex.taskmanager.model.Task;
import ru.yandex.taskmanager.service.TaskManager;

import java.util.ArrayList;

public class TestFactory {

    public static void createTestData(TaskManager taskManager) {
        taskManager.createRecord("Задача 1", "description", TaskType.TASK); // 0
        taskManager.createRecord("Задача 2", "description", TaskType.TASK); // 1
        taskManager.createRecord("Задача 3", "description", TaskType.TASK); // 2
        taskManager.createRecord("Задача 4", "description", TaskType.TASK); // 3
        taskManager.createRecord("Задача 5", "description", TaskType.TASK); // 4

        int epicId1 = taskManager.createRecord("Эпик 1", "description", TaskType.EPIC); // 5

        taskManager.createRecord("Подзадача 1-1", "description", TaskType.SUBTASK, epicId1);// 6
        taskManager.createRecord("Подзадача 1-2", "description", TaskType.SUBTASK, epicId1);// 7
        taskManager.createRecord("Подзадача 1-3", "description", TaskType.SUBTASK, epicId1);// 8
        taskManager.createRecord("Подзадача 1-4", "description", TaskType.SUBTASK, epicId1);// 9

        int epicId2 = taskManager.createRecord("Эпик 2", "description", TaskType.EPIC);// 10

        taskManager.createRecord("Подзадача 2-1", "description", TaskType.SUBTASK, epicId2);// 11

        taskManager.createRecord("Эпик 3", "description", TaskType.EPIC);// 12
    }

    public static boolean testGetData(TaskManager taskManager) {
        // получить Таску из Тасок
        Task testTask = taskManager.getRecord(2, TaskType.TASK);
        if (!testTask.getTitle().equals("Задача 3")) {
            return false;
        }

        // получить Сабтаск из Сабтасок
        Subtask testTask2 = (Subtask) taskManager.getRecord(7, TaskType.SUBTASK);
        if (!testTask2.getTitle().equals("Подзадача 1-2")) {
            return false;
        }

        // получить Эпик из Эпиков и проверить что Сабтаски отвязаны (удалены?)
        Epic testTask3 = (Epic) taskManager.getRecord(10, TaskType.EPIC);
        if (!testTask3.getTitle().equals("Эпик 2")) {
            return false;
        }

        // получить Таску из Сабтасок
        Task testTask4 = taskManager.getRecord(2, TaskType.SUBTASK);
        if (testTask4 != null) {
            return false;
        }

        // получить все таски
        ArrayList<Task> tasks = taskManager.getAllRecords();
        if (tasks.size() != 13) {
            return false;
        }

        return true;
    }

    public static boolean testUpdateData(TaskManager taskManager) {

        // Обновляем Таску
        Task testTask = new Task(2, "Новая Задача 3", "description", Status.IN_PROGRESS);
        taskManager.updateRecord(testTask);
        testTask = taskManager.getRecord(2, TaskType.TASK);
        if (!testTask.getTitle().equals("Новая Задача 3") || testTask.getStatus() != Status.IN_PROGRESS) {
            return false;
        }

        // Обновляем Эпик
        // общее
        Epic testEpic = new Epic(12, "Новый Эпик 3", "description");
        taskManager.updateRecord(testEpic);
        testEpic.setStatus(Status.IN_PROGRESS);
        testEpic = (Epic) taskManager.getRecord(12, TaskType.EPIC);
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
        testSubtask = (Subtask) taskManager.getRecord(8, TaskType.SUBTASK);
        if (!testSubtask.getTitle().equals("Новая Подзадача 1-3") || testSubtask.getStatus() != Status.IN_PROGRESS) {
            return false;
        }
        // обновляем статус с 1 эпиком
        testEpic = (Epic) taskManager.getRecord(10, TaskType.EPIC);
        if (!testEpic.getTitle().equals("Эпик 2") || testEpic.getStatus() != Status.NEW) {
            return false;
        }
        testSubtask = (Subtask) taskManager.getRecord(11, TaskType.SUBTASK);
        testSubtask.setStatus(Status.DONE);
        if (!testEpic.getTitle().equals("Эпик 2") || testEpic.getStatus() != Status.DONE) {
            return false;
        }
        // обновляем статус с 2+ эпиками
        testEpic = (Epic) taskManager.getRecord(5, TaskType.EPIC);
        if (!testEpic.getTitle().equals("Эпик 1") || testEpic.getStatus() != Status.IN_PROGRESS) {
            return false;
        }

        return true;
    }

    public static boolean testRemoveData(TaskManager taskManager) {

        // удалить Таску из Тасок
        ArrayList<Task> tasks = taskManager.getAllRecords();
        if (tasks.size() != 13) {
            return false;
        }

        taskManager.deleteRecord(1, TaskType.TASK);
        tasks = taskManager.getAllRecords();
        if (tasks.size() != 12) {
            return false;
        }

        // удалить Сабтаск из Сабтасок (проверка пересчета статуса эпика) (из эпика с сабтасками > 1)
        taskManager.deleteRecord(8, TaskType.SUBTASK);
        tasks = taskManager.getAllRecords();
        if (tasks.size() != 11) {
            return false;
        }
        if (taskManager.getRecord(8, TaskType.SUBTASK) != null) {
            return false;
        }
        Epic testEpic = (Epic) taskManager.getRecord(5, TaskType.EPIC);
        if (!testEpic.getTitle().equals("Эпик 1") || testEpic.getStatus() != Status.NEW) {
            return false;
        }
        if (testEpic.getSubtasks().size() != 3) {
            return false;
        }

        // удалить Сабтаск из Сабтасок (проверка пересчета статуса эпика) (из эпика с сабтасками = 1)
        taskManager.deleteRecord(11, TaskType.SUBTASK);
        testEpic = (Epic) taskManager.getRecord(10, TaskType.EPIC);
        if (!testEpic.getTitle().equals("Эпик 2") || testEpic.getStatus() != Status.NEW) {
            return false;
        }
        if (!testEpic.getSubtasks().isEmpty()) {
            return false;
        }

        // удалить Эпик из Эпиков
        taskManager.createRecord("Подзадача 2-1", "description", TaskType.SUBTASK, 10);// 13
        if (testEpic.getSubtasks().size() != 1) {
            return false;
        }
        taskManager.deleteRecord(10, TaskType.EPIC);
        testEpic = (Epic) taskManager.getRecord(10, TaskType.EPIC);
        if (testEpic != null) {
            return false;
        }

        // удалить Таску из Сабтасок
        taskManager.deleteRecord(2, TaskType.SUBTASK);
        Subtask subtask = (Subtask) taskManager.getRecord(2, TaskType.SUBTASK);
        if (subtask != null) {
            return false;
        }

        // удалить Таску по id
        taskManager.deleteRecord(0);
        Task testTask = taskManager.getRecord(0, TaskType.TASK);
        if (testTask != null) {
            return false;
        }

        // удалить Таску по не существущему id
        taskManager.deleteRecord(123);


        // удалить все
        taskManager.clearAllRecords();
        ArrayList<Task> allRecords = taskManager.getAllRecords();
        if (!allRecords.isEmpty()) {
            return false;
        }

        return true;
    }
}
