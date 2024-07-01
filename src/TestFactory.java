import java.util.ArrayList;

public class TestFactory {

    public static void createTestData() {
        TaskManager.createRecord("Задача 1", "description", TaskType.TASK); // 0
        TaskManager.createRecord("Задача 2", "description", TaskType.TASK); // 1
        TaskManager.createRecord("Задача 3", "description", TaskType.TASK); // 2
        TaskManager.createRecord("Задача 4", "description", TaskType.TASK); // 3
        TaskManager.createRecord("Задача 5", "description", TaskType.TASK); // 4

        int epicId1 = TaskManager.createRecord("Эпик 1", "description", TaskType.EPIC); // 5

        TaskManager.createRecord("Подзадача 1-1", "description", TaskType.SUBTASK, epicId1);// 6
        TaskManager.createRecord("Подзадача 1-2", "description", TaskType.SUBTASK, epicId1);// 7
        TaskManager.createRecord("Подзадача 1-3", "description", TaskType.SUBTASK, epicId1);// 8
        TaskManager.createRecord("Подзадача 1-4", "description", TaskType.SUBTASK, epicId1);// 9

        int epicId2 = TaskManager.createRecord("Эпик 2", "description", TaskType.EPIC);// 10

        TaskManager.createRecord("Подзадача 2-1", "description", TaskType.SUBTASK, epicId2);// 11

        TaskManager.createRecord("Эпик 3", "description", TaskType.EPIC);// 12
    }

    public static boolean testGetData() {
        // получить Таску из Тасок
        Task testTask = TaskManager.getRecord(2, TaskType.TASK);
        if (!testTask.getTitle().equals("Задача 3")) {
            return false;
        }

        // получить Сабтаск из Сабтасок
        Subtask testTask2 = (Subtask) TaskManager.getRecord(7, TaskType.SUBTASK);
        if (!testTask2.getTitle().equals("Подзадача 1-2")) {
            return false;
        }

        // получить Эпик из Эпиков и проверить что Сабтаски отвязаны (удалены?)
        Epic testTask3 = (Epic) TaskManager.getRecord(10, TaskType.EPIC);
        if (!testTask3.getTitle().equals("Эпик 2")) {
            return false;
        }

        // получить Таску из Сабтасок
        Task testTask4 = TaskManager.getRecord(2, TaskType.SUBTASK);
        if (testTask4 != null) {
            return false;
        }

        // получить все таски
        ArrayList<Task> tasks = TaskManager.getAllRecords();
        if (tasks.size() != 13) {
            return false;
        }

        return true;
    }

    public static boolean testUpdateData() {

        // Обновляем Таску
        Task testTask = new Task(2, "Новая Задача 3", "description", Status.IN_PROGRESS);
        TaskManager.updateRecord(testTask);
        testTask = TaskManager.getRecord(2, TaskType.TASK);
        if (!testTask.getTitle().equals("Новая Задача 3") || testTask.getStatus() != Status.IN_PROGRESS) {
            return false;
        }

        // Обновляем Эпик
        // общее
        Epic testEpic = new Epic(12, "Новый Эпик 3", "description");
        TaskManager.updateRecord(testEpic);
        testEpic.setStatus(Status.IN_PROGRESS);
        testEpic = (Epic) TaskManager.getRecord(12, TaskType.EPIC);
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
        TaskManager.updateRecord(testSubtask);
        testSubtask = (Subtask) TaskManager.getRecord(8, TaskType.SUBTASK);
        if (!testSubtask.getTitle().equals("Новая Подзадача 1-3") || testSubtask.getStatus() != Status.IN_PROGRESS) {
            return false;
        }
        // обновляем статус с 1 эпиком
        testEpic = (Epic) TaskManager.getRecord(10, TaskType.EPIC);
        if (!testEpic.getTitle().equals("Эпик 2") || testEpic.getStatus() != Status.NEW) {
            return false;
        }
        testSubtask = (Subtask) TaskManager.getRecord(11, TaskType.SUBTASK);
        testSubtask.setStatus(Status.DONE);
        if (!testEpic.getTitle().equals("Эпик 2") || testEpic.getStatus() != Status.DONE) {
            return false;
        }
        // обновляем статус с 2+ эпиками
        testEpic = (Epic) TaskManager.getRecord(5, TaskType.EPIC);
        if (!testEpic.getTitle().equals("Эпик 1") || testEpic.getStatus() != Status.IN_PROGRESS) {
            return false;
        }

        return true;
    }

    public static boolean testRemoveData() {

        // удалить Таску из Тасок
        ArrayList<Task> tasks = TaskManager.getAllRecords();
        if (tasks.size() != 13) {
            return false;
        }

        TaskManager.deleteRecord(1, TaskType.TASK);
        tasks = TaskManager.getAllRecords();
        if (tasks.size() != 12) {
            return false;
        }

        // удалить Сабтаск из Сабтасок (проверка пересчета статуса эпика) (из эпика с сабтасками > 1)
        TaskManager.deleteRecord(8, TaskType.SUBTASK);
        tasks = TaskManager.getAllRecords();
        if (tasks.size() != 11) {
            return false;
        }
        if (TaskManager.getRecord(8, TaskType.SUBTASK) != null) {
            return false;
        }
        Epic testEpic = (Epic) TaskManager.getRecord(5, TaskType.EPIC);
        if (!testEpic.getTitle().equals("Эпик 1") || testEpic.getStatus() != Status.NEW) {
            return false;
        }
        if (testEpic.getSubtasks().size() != 3) {
            return false;
        }

        // удалить Сабтаск из Сабтасок (проверка пересчета статуса эпика) (из эпика с сабтасками = 1)
        TaskManager.deleteRecord(11, TaskType.SUBTASK);
        testEpic = (Epic) TaskManager.getRecord(10, TaskType.EPIC);
        if (!testEpic.getTitle().equals("Эпик 2") || testEpic.getStatus() != Status.NEW) {
            return false;
        }
        if (!testEpic.getSubtasks().isEmpty()) {
            return false;
        }

        // удалить Эпик из Эпиков
        TaskManager.createRecord("Подзадача 2-1", "description", TaskType.SUBTASK, 10);// 13
        if (testEpic.getSubtasks().size() != 1) {
            return false;
        }
        TaskManager.deleteRecord(10, TaskType.EPIC);
        testEpic = (Epic) TaskManager.getRecord(10, TaskType.EPIC);
        if (testEpic != null) {
            return false;
        }

        // удалить Таску из Сабтасок
        TaskManager.deleteRecord(2, TaskType.SUBTASK);
        Subtask subtask = (Subtask) TaskManager.getRecord(2, TaskType.SUBTASK);
        if (subtask != null) {
            return false;
        }

        // удалить Таску по id
        TaskManager.deleteRecord(0);
        Task testTask = TaskManager.getRecord(0, TaskType.TASK);
        if (testTask != null) {
            return false;
        }

        // удалить Таску по не существущему id
        TaskManager.deleteRecord(123);


        // удалить все
        TaskManager.clearAllRecords();
        ArrayList<Task> allRecords = TaskManager.getAllRecords();
        if (!allRecords.isEmpty()) {
            return false;
        }

        return true;
    }
}
