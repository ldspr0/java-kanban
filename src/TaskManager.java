import java.util.HashMap;

public class TaskManager {
    private static int id;
    private static HashMap<Integer, Task> tasks;
    private static HashMap<Integer, Subtask> subtasks;
    private static HashMap<Integer, Epic> epics;

    public static Task createTask() {

        return new Task();
    }

    public static Subtask createSubtask() {

        return new Subtask();
    }

    public static Epic createEpic() {

        return new Epic();
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
