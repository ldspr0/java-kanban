import java.util.HashMap;

public class TaskManager {
    private static int id;
    private static HashMap<Integer, Task> tasks;
    private static HashMap<Integer, Subtask> subtasks;
    private static HashMap<Integer, Epic> epics;

    public static void init() {
        id = 0;
    }

    public static Task createRecord(String title, String description, TaskType type, int... epicId) {

        switch (type) {
            case TASK -> {
                Task result = new Task(id, title, description, Status.NEW);
                tasks.put(id++, result);
            }
            case EPIC -> {
                Epic epic = new Epic(id, title, description, Status.NEW);
                epics.put(id++, epic);
            }
            case SUBTASK -> {
                if (epicId.length > 0) {
                    Subtask subtask = new Subtask(id, title, description, Status.NEW, epicId[0]);
                    subtasks.put(id++, subtask);
                } else {
                    System.out.println("Не возможно создать запись без epicId");
                    return null;
                }
            }
        }

        System.out.println("Пожалуйста задайте корректный тип таски для создания");
        return null;
    }


    public static Task updateTask(Task task) {

        return task;
    }

    public static Subtask updateSubtask(Subtask subtask) {

        return subtask;
    }

    public static Epic updateEpic(Epic epic) {

        return epic;
    }

    public static void deleteRecord(int id) {

    }

    public static void clearAllRecords() {

    }

    public static Task getTask(int id) {
        return tasks.get(id);
    }

    public static Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public static Epic getEpic(int id) {
        return epics.get(id);
    }

    // TODO: получение списка всех задач?

}
