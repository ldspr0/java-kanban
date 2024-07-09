package ru.yandex.taskmanager;

import ru.yandex.taskmanager.enums.Status;
import ru.yandex.taskmanager.model.Epic;
import ru.yandex.taskmanager.model.Subtask;
import ru.yandex.taskmanager.model.Task;
import ru.yandex.taskmanager.service.TaskManager;
import ru.yandex.taskmanager.utility.Managers;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");
        TaskManager taskManager = Managers.getDefault();

        // some data
        for (int i = 0; i < 5; i++) {
            taskManager.createRecord(new Task(i, "Задача " + i, "description", Status.NEW));
        }

        int epicId1 = taskManager.createRecord(new Epic(0, "Эпик 1", "description"));
        for (int i = 0; i < 4; i++) {
            String title = "Подзадача " + epicId1 + "-" + i;
            taskManager.createRecord(new Subtask(i, title, "description", Status.NEW, epicId1));
        }

        int epicId2 = taskManager.createRecord(new Epic(0, "Эпик 2", "description"));
        String title = "Подзадача " + epicId2 + "-" + 0;
        taskManager.createRecord(new Subtask(0, title, "description", Status.NEW, epicId2));

        taskManager.createRecord(new Epic(0, "Эпик 3", "description"));
        // -----

        printAllTasks(taskManager);

    }

    /*
    Мои сомнения:
        1. Каждый раз при создании Сабтаска в историю сохраняется просмотр Эпика (для добавления в него информации)
        2. getSubtasksByEpicId тоже наваливает в историю и по каждому эпику и по каждому сабтаску
     Как решение для первых 2 я вижу, это сделать отдельный метод для системных гетов и отдельный для пользовательских
        3. В данной реализации getAllTask(Subtask/Epic) не сохраняется в историю просмотров, нужно ли?
     */

    private static void printAllTasks(TaskManager manager) {
        System.out.println("\n\tЗадачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("\n\tЭпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksByEpicId(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("\n\tПодзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("\n\tИстория:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}