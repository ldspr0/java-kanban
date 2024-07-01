public class TestFactory {

    public static void createTestData() {
        TaskManager.createRecord("Задача 1", "description", TaskType.TASK);
        TaskManager.createRecord("Задача 2", "description", TaskType.TASK);
        TaskManager.createRecord("Задача 3", "description", TaskType.TASK);
        TaskManager.createRecord("Задача 4", "description", TaskType.TASK);
        TaskManager.createRecord("Задача 5", "description", TaskType.TASK);

        int epicId1 = TaskManager.createRecord("Эпик 1", "description", TaskType.EPIC);

        TaskManager.createRecord("Подзадача 1-1", "description", TaskType.SUBTASK, epicId1);
        TaskManager.createRecord("Подзадача 1-2", "description", TaskType.SUBTASK, epicId1);
        TaskManager.createRecord("Подзадача 1-3", "description", TaskType.SUBTASK, epicId1);
        TaskManager.createRecord("Подзадача 1-4", "description", TaskType.SUBTASK, epicId1);

        int epicId2 = TaskManager.createRecord("Эпик 2", "description", TaskType.EPIC);

        TaskManager.createRecord("Подзадача 2-1", "description", TaskType.SUBTASK, epicId2);

        int epicId3 = TaskManager.createRecord("Эпик 3", "description", TaskType.EPIC);
    }

    public static boolean testGetData() {
        boolean result = false;


        return result;
    }

    public static boolean testUpdateData() {
        boolean result = false;


        return result;
    }

    public static boolean testCreateDuplicateIds() {
        boolean result = false;


        return result;
    }

    public static boolean testRemoveData() {
        boolean result = false;

        // удалить Таску из Тасок
        TaskManager.deleteRecord(1, TaskType.TASK);


        // удалить Сабтаск из Сабтасок (проверка пересчета статуса эпика) (из эпика с сабтасками > 1)
        TaskManager.deleteRecord(6, TaskType.SUBTASK);

        // удалить Сабтаск из Сабтасок (проверка пересчета статуса эпика) (из эпика с сабтасками = 1)
        TaskManager.deleteRecord(11, TaskType.SUBTASK);

        // удалить Эпик из Эпиков и проверить что Сабтаски отвязаны (удалены?)
        TaskManager.deleteRecord(123, TaskType.EPIC);

        // удалить Таску из Сабтасок
        TaskManager.deleteRecord(123, TaskType.SUBTASK);

        // удалить Таску по id
        TaskManager.deleteRecord(123);


        return result;
    }
}
