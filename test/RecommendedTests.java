import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskmanager.enums.Status;
import ru.yandex.taskmanager.model.Epic;
import ru.yandex.taskmanager.model.Subtask;
import ru.yandex.taskmanager.model.Task;
import ru.yandex.taskmanager.service.InMemoryTaskManager;
import ru.yandex.taskmanager.service.TaskManager;
import ru.yandex.taskmanager.utility.Managers;

import java.util.ArrayList;

public class RecommendedTests {
    private final int NUMBER_OF_TASKS = 10;
    private final int NUMBER_OF_EPICS = 10;
    private final int NUMBER_OF_SUBTASKS = 10;

    private final TaskManager taskManager = Managers.getDefault();
    private final ArrayList<Integer> taskIds = new ArrayList<>();
    private final ArrayList<Integer> subtaskIds = new ArrayList<>();
    private final ArrayList<Integer> epicIds = new ArrayList<>();

    @BeforeEach
    public void beforeEach() {
        taskIds.addAll(DataFactory.createTasks(taskManager, NUMBER_OF_TASKS));
        epicIds.addAll(DataFactory.createEpics(taskManager, NUMBER_OF_EPICS));
        subtaskIds.addAll(DataFactory.createSubtasks(taskManager, NUMBER_OF_SUBTASKS, epicIds));
    }

    @Test
    public void differentTasksAreEqualIfIdIsEqual() {
        int id = taskIds.getFirst();
        Task createdTask = taskManager.getTask(id);

        Task updatedTask = new Task(id, "Новая Задача 555", "description", Status.IN_PROGRESS);
        taskManager.updateRecord(updatedTask);
        updatedTask = taskManager.getTask(id);

        Assertions.assertEquals(createdTask, updatedTask);
        Assertions.assertNotEquals(createdTask.getTitle(), updatedTask.getTitle());
    }

    @Test
    public void differentEpicsAreEqualIfIdIsEqual() {
        int epicId = epicIds.getFirst();
        Epic createdEpic = taskManager.getEpic(epicId);

        Epic updatedEpic = new Epic(epicId, "Новая Задача 555", "description");
        taskManager.updateRecord(updatedEpic);
        updatedEpic = taskManager.getEpic(epicId);

        Assertions.assertEquals(createdEpic, updatedEpic);
        Assertions.assertNotEquals(createdEpic.getTitle(), updatedEpic.getTitle());
    }

    @Test
    public void differentSubtasksAreEqualIfIdIsEqual() {
        int epicId = epicIds.getFirst();
        int subtaskId = subtaskIds.getFirst();
        Subtask createdSubtask = taskManager.getSubtask(subtaskId);

        Subtask updatedSubtask = new Subtask(subtaskId, "Новая Задача 555", "description", Status.IN_PROGRESS, epicId);
        taskManager.updateRecord(updatedSubtask);
        updatedSubtask = taskManager.getSubtask(subtaskId);

        Assertions.assertEquals(createdSubtask, updatedSubtask);
        Assertions.assertNotEquals(createdSubtask.getTitle(), updatedSubtask.getTitle());
    }

    @Test
    public void isImpossibleToAddEpicAsSubtask() {
        int epicId = epicIds.getFirst();
        int subtaskId = subtaskIds.getFirst();
        Subtask updatedSubtask = new Subtask(epicId, "Новая Задача 555", "description", Status.IN_PROGRESS, epicId);
        taskManager.updateRecord(updatedSubtask);

        Assertions.assertNotNull(taskManager.getEpic(epicId));
        Assertions.assertNotNull(taskManager.getSubtask(subtaskId));
        Assertions.assertNull(taskManager.getSubtask(epicId));
    }

    @Test
    public void isImpossibleToUseSubtaskAsEpic() {
        int epicId = epicIds.getFirst();
        int subtaskId = subtaskIds.getFirst();
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

        updatedTask = taskManager.getTask(updatedTask.getId());

        Task firstTaskInHistory = taskManager.getHistory().getFirst();
        lastTaskInHistory = taskManager.getHistory().getLast();

        Assertions.assertEquals(updatedTask.getTitle(), lastTaskInHistory.getTitle());
    }

    @Test
    public void isHistoryManagerHasCorrectTasksOrder() {
        int id = taskIds.getFirst();
        Task createdTask = taskManager.getTask(id);
        Task firstTaskInHistory = taskManager.getHistory().getFirst();
        Task lastTaskInHistory = taskManager.getHistory().getLast();
        Assertions.assertEquals(createdTask, lastTaskInHistory);

        int id2 = taskIds.get(1);
        int id3 = taskIds.get(2);

        Task createdTask2 = taskManager.getTask(id2);
        Task createdTask3 = taskManager.getTask(id3);

        firstTaskInHistory = taskManager.getHistory().getFirst();
        lastTaskInHistory = taskManager.getHistory().getLast();
        Assertions.assertEquals(createdTask.getTitle(), firstTaskInHistory.getTitle());
        Assertions.assertEquals(createdTask3.getTitle(), lastTaskInHistory.getTitle());

        Task updatedTask = new Task(id, "Новая Задача 555", "description", Status.IN_PROGRESS);
        taskManager.updateRecord(updatedTask);
        updatedTask = taskManager.getTask(updatedTask.getId());

        firstTaskInHistory = taskManager.getHistory().getFirst();
        lastTaskInHistory = taskManager.getHistory().getLast();
        Assertions.assertEquals(createdTask2.getTitle(), firstTaskInHistory.getTitle());
        Assertions.assertEquals(updatedTask.getTitle(), lastTaskInHistory.getTitle());

        updatedTask = new Task(id, "Новая Задача 777", "description", Status.IN_PROGRESS);
        taskManager.updateRecord(updatedTask);
        updatedTask = taskManager.getTask(updatedTask.getId());

        firstTaskInHistory = taskManager.getHistory().getFirst();
        lastTaskInHistory = taskManager.getHistory().getLast();
        Assertions.assertEquals(createdTask2.getTitle(), firstTaskInHistory.getTitle());
        Assertions.assertEquals(updatedTask.getTitle(), lastTaskInHistory.getTitle());
    }
}
