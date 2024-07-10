package ru.yandex.taskmanager.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.taskmanager.enums.Status;
import ru.yandex.taskmanager.model.Epic;
import ru.yandex.taskmanager.model.Subtask;
import ru.yandex.taskmanager.model.Task;
import ru.yandex.taskmanager.service.InMemoryTaskManager;
import ru.yandex.taskmanager.service.TaskManager;
import ru.yandex.taskmanager.utility.Managers;

public class RecommendedTests {
    private final TaskManager taskManager = Managers.getDefault();

    @Test
    public void differentTasksAreEqualIfIdIsEqual() {
        int id = taskManager.createRecord(new Task(0, "Задача " + 1, "description", Status.NEW));
        Task createdTask = taskManager.getTask(id);

        Task updatedTask = new Task(id, "Новая Задача 555", "description", Status.IN_PROGRESS);
        taskManager.updateRecord(updatedTask);
        updatedTask = taskManager.getTask(id);

        Assertions.assertEquals(createdTask, updatedTask);
        Assertions.assertNotEquals(createdTask.getTitle(), updatedTask.getTitle());
    }

    @Test
    public void differentEpicsAreEqualIfIdIsEqual() {
        int epicId = taskManager.createRecord(new Epic(0, "Эпик 1", "description"));
        Epic createdEpic = taskManager.getEpic(epicId);

        Epic updatedEpic = new Epic(epicId, "Новая Задача 555", "description");
        taskManager.updateRecord(updatedEpic);
        updatedEpic = taskManager.getEpic(epicId);

        Assertions.assertEquals(createdEpic, updatedEpic);
        Assertions.assertNotEquals(createdEpic.getTitle(), updatedEpic.getTitle());
    }

    @Test
    public void differentSubtasksAreEqualIfIdIsEqual() {

        int epicId = taskManager.createRecord(new Epic(0, "Эпик 1", "description"));
        int subtaskId = taskManager.createRecord(new Subtask(0, "Подзадача 1", "description", Status.NEW, epicId));
        Subtask createdSubtask = taskManager.getSubtask(subtaskId);

        Subtask updatedSubtask = new Subtask(subtaskId, "Новая Задача 555", "description", Status.IN_PROGRESS, epicId);
        taskManager.updateRecord(updatedSubtask);
        updatedSubtask = taskManager.getSubtask(subtaskId);

        Assertions.assertEquals(createdSubtask, updatedSubtask);
        Assertions.assertNotEquals(createdSubtask.getTitle(), updatedSubtask.getTitle());
    }

    @Test
    public void isImpossibleToAddEpicAsSubtask() {
        int epicId = taskManager.createRecord(new Epic(0, "Эпик 1", "description"));
        int subtaskId = taskManager.createRecord(new Subtask(epicId, "Подзадача 1", "description", Status.NEW, epicId));
        Subtask updatedSubtask = new Subtask(epicId, "Новая Задача 555", "description", Status.IN_PROGRESS, epicId);
        taskManager.updateRecord(updatedSubtask);

        Assertions.assertNotNull(taskManager.getEpic(epicId));
        Assertions.assertNotNull(taskManager.getSubtask(subtaskId));
        Assertions.assertNull(taskManager.getSubtask(epicId));
    }

    @Test
    public void isImpossibleToUseSubtaskAsEpic() {
        int epicId = taskManager.createRecord(new Epic(0, "Эпик 1", "description"));
        int subtaskId = taskManager.createRecord(new Subtask(0, "Подзадача 1", "description", Status.NEW, epicId));
        int subtaskId2 = taskManager.createRecord(new Subtask(0, "Подзадача 2", "description", Status.NEW, subtaskId));

        Assertions.assertNotNull(taskManager.getEpic(epicId));
        Assertions.assertNotNull(taskManager.getSubtask(subtaskId));
        Assertions.assertNull(taskManager.getEpic(subtaskId2));
        Assertions.assertNull(taskManager.getSubtask(subtaskId2));
    }

    @Test
    public void isDefaultManagerReturnCorrectServices() {
        Assertions.assertNotNull(taskManager);
        Assertions.assertInstanceOf(InMemoryTaskManager.class, taskManager);
    }

    @Test
    public void isHistoryManagerHasCorrectVersionOfTask() {

        int id = taskManager.createRecord(new Task(0, "Задача " + 1, "description", Status.NEW));
        Task createdTask = taskManager.getTask(id);
        Task lastTaskInHistory = taskManager.getHistory().getLast();
        Assertions.assertEquals(createdTask, lastTaskInHistory);

        Task updatedTask = new Task(id, "Новая Задача 555", "description", Status.IN_PROGRESS);
        taskManager.updateRecord(updatedTask);

        updatedTask = taskManager.getTask(id);
        lastTaskInHistory = taskManager.getHistory().getLast();
        Task firstTaskInHistory = taskManager.getHistory().getFirst();

        Assertions.assertEquals(createdTask.getTitle(), firstTaskInHistory.getTitle());
        Assertions.assertEquals(updatedTask.getTitle(), lastTaskInHistory.getTitle());
    }
}
