import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class TaskManager {
    public static int id;
    private static HashMap<Integer, Task> tasks;
    private static HashMap<Integer, Epic> epics;
    private static HashMap<Integer, Subtask> subtasks;

    public static void init() {
        id = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public static int createRecord(String title, String description, TaskType type, int... epicId) {
        switch (type) {
            case TASK:
                Task task = new Task(id, title, description, Status.NEW);
                tasks.put(id, task);
                break;
            case EPIC:
                Epic epic = new Epic(id, title, description);
                epics.put(id, epic);
                break;
            case SUBTASK:
                if (epicId.length > 0) {

                    Subtask subtask = new Subtask(id, title, description, Status.NEW, epicId[0]);
                    subtasks.put(id, subtask);
                } else {
                    System.out.println("Не возможно создать запись без epicId");
                    return -1;
                }
                break;
            default:
                System.out.println("Пожалуйста задайте корректный тип таски для создания");
                return -1;
        }

        return id++;
    }

    public static void updateRecord(Task task) {
        tasks.put(task.getId(), task);
    }

    public static void updateRecord(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public static void updateRecord(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic parentEpic = (Epic) getRecord(subtask.getEpicId(), TaskType.EPIC);
        parentEpic.recalculateStatus();
    }


    public static void deleteRecord(int id, TaskType type) {
        switch (type) {
            case EPIC:
                epics.remove(id);
                break;
            case SUBTASK:
                Subtask subtask = (Subtask) getRecord(id, TaskType.SUBTASK);
                if (subtask == null) {
                    break;
                }
                Epic epic = (Epic) getRecord(subtask.getEpicId(), TaskType.EPIC);
                if (epic == null) {
                    break;
                }
                HashSet<Integer> subtaskFromEpic = epic.getSubtasks();
                for (Integer each : subtaskFromEpic) {
                    if (each == id) {
                        subtaskFromEpic.remove(each);
                        break;
                    }
                }
                epic.recalculateStatus();
                subtasks.remove(id);
                break;
            case TASK:
                tasks.remove(id);
        }
    }

    public static void deleteRecord(int id) {
        if (tasks.remove(id) == null) {
            if (epics.remove(id) == null) {
                subtasks.remove(id);
            }
        }
    }

    public static void clearAllRecords() {
        epics.clear();
        subtasks.clear();
        tasks.clear();
    }

    // Либо разбивать на 3 разных метода getTask и т.д. либо ставить Таск как Тип, в обоих случаях нужно знать тип
    // возвращаемого значения
    public static Task getRecord(int id, TaskType type) {
        return switch (type) {
            case EPIC -> epics.get(id);
            case SUBTASK -> subtasks.get(id);
            case TASK -> tasks.get(id);
        };

    }

    public static ArrayList<Task> getAllRecords() {
        ArrayList<Task> result = new ArrayList<>(tasks.values());
        result.addAll(epics.values());
        result.addAll(subtasks.values());

        return result;
    }
}
