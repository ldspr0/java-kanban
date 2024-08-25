import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.yandex.taskmanager.enums.Status;
import ru.yandex.taskmanager.model.Epic;
import ru.yandex.taskmanager.model.Subtask;
import ru.yandex.taskmanager.model.Task;
import ru.yandex.taskmanager.service.TaskManager;
import ru.yandex.taskmanager.utility.Managers;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;

public class FileBackedTaskManagerTests {
    private final int NUMBER_OF_TASKS = 10;
    private final int NUMBER_OF_EPICS = 10;
    private final int NUMBER_OF_SUBTASKS = 10;

    private TaskManager taskManager;
    private final ArrayList<Integer> taskIds = new ArrayList<>();
    private final ArrayList<Integer> subtaskIds = new ArrayList<>();
    private final ArrayList<Integer> epicIds = new ArrayList<>();
    private final Random random = new Random();

    Path path1;
    @TempDir
    Path tempDir;

    @BeforeEach
    public void beforeEach() {

        try {
            path1 = tempDir.resolve("testFile1.csv");
            taskManager = Managers.getManager("FileManager", String.valueOf(path1));
        } catch (InvalidPathException e) {
            System.err.println("error creating temporary test file in " + this.getClass().getSimpleName());
        }

        taskIds.addAll(DataFactory.createTasks(taskManager, NUMBER_OF_TASKS));
        epicIds.addAll(DataFactory.createEpics(taskManager, NUMBER_OF_EPICS));
        subtaskIds.addAll(DataFactory.createSubtasks(taskManager, NUMBER_OF_SUBTASKS, epicIds));

    }

    @Test
    public void isPositiveToGetTask() {
        int id = taskIds.get(random.nextInt(taskIds.size()));
        Assertions.assertNotNull(taskManager.getTask(id));
    }

    @Test
    public void isPossibleToSaveEmptyFile() {

    }

    @Test
    public void isPossibleToLoadEmptyFile() {

    }

    @Test
    public void isPossibleToSaveNOTEmptyFile() {

    }

    @Test
    public void isPossibleToLoadNOTEmptyFile() {

    }
}