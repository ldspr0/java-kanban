import ru.yandex.taskmanager.enums.Status;
import ru.yandex.taskmanager.model.Epic;
import ru.yandex.taskmanager.model.Subtask;
import ru.yandex.taskmanager.model.Task;
import ru.yandex.taskmanager.service.TaskManager;

import java.util.ArrayList;
import java.util.Random;

public class DataFactory {
    private static final Random random = new Random();

    public static ArrayList<Integer> createTasks(TaskManager taskManager, int numberOfEntities) {
        ArrayList<Integer> taskIds = new ArrayList<>();
        for (int i = 0; i < numberOfEntities; i++) {
            int taskId = taskManager.createRecord(new Task(i, "Задача " + i, "task description " + i, Status.NEW));
            taskIds.add(taskId);
        }
        return taskIds;
    }

    public static ArrayList<Integer> createEpics(TaskManager taskManager, int numberOfEntities) {
        ArrayList<Integer> epicIds = new ArrayList<>();
        for (int i = 0; i < numberOfEntities; i++) {
            int epicId = taskManager.createRecord(new Epic(i, "Эпик " + i, "epic description " + i));
            if (i > 0) {
                // гарантируем 1 эпик с 0 субтасков;
                epicIds.add(epicId);
            }

        }
        return epicIds;
    }

    public static ArrayList<Integer> createSubtasks(TaskManager taskManager, int numberOfEntities, ArrayList<Integer> epicIds) {
        ArrayList<Integer> subtaskIds = new ArrayList<>();
        int epicIdMoreThanOneSub = -1;
        for (int i = 0; i < numberOfEntities; i++) {
            int epicId = -1;
            if (i < 2) {
                // гарантируем 1 эпик с 1 сабтаской
                epicId = epicIds.removeFirst();
                if (i == 1) {
                    epicIdMoreThanOneSub = epicId;
                }
            }
            else if (i == 2) {
                // гарантируем эпик с 2 сабтасками
                epicId = epicIdMoreThanOneSub;
            } else {
                epicId = epicIds.get(random.nextInt(epicIds.size()));
            }
            String title = "Подзадача " + epicId + "-" + i;
            int subtaskId = taskManager.createRecord(new Subtask(i, title, "description", Status.NEW, epicId));
            subtaskIds.add(subtaskId);


        }
        return subtaskIds;
    }
}










