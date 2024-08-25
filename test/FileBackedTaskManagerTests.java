import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.yandex.taskmanager.service.FileBackedTaskManager;
import ru.yandex.taskmanager.service.TaskManager;
import ru.yandex.taskmanager.utility.Managers;

import java.io.File;
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
    File file1;
    @TempDir
    Path tempDir;

    @BeforeEach
    public void beforeEach() {

        try {
            path1 = tempDir.resolve("testFile1.csv");
            taskManager = Managers.getManager("FileManager", String.valueOf(path1));
            file1 = path1.toFile();
        } catch (InvalidPathException e) {
            System.err.println("error creating temporary test file in " + this.getClass().getSimpleName());
        }


    }

    @Test
    public void isPossibleToSaveEmptyFile() {
        FileBackedTaskManager fileBasedTM = (FileBackedTaskManager) taskManager;
        Assertions.assertDoesNotThrow(fileBasedTM::save);
    }

    @Test
    public void isPossibleToLoadEmptyFile() {
        FileBackedTaskManager fileBasedTM = (FileBackedTaskManager) taskManager;
        fileBasedTM.save();
        Assertions.assertDoesNotThrow(() -> fileBasedTM.loadFromFile(file1));
    }

    @Test
    public void isPossibleToSaveNOTEmptyFile() {
        taskIds.addAll(DataFactory.createTasks(taskManager, NUMBER_OF_TASKS));
        epicIds.addAll(DataFactory.createEpics(taskManager, NUMBER_OF_EPICS));
        subtaskIds.addAll(DataFactory.createSubtasks(taskManager, NUMBER_OF_SUBTASKS, epicIds));
    }

    @Test
    public void isPossibleToLoadNOTEmptyFile() {
        taskIds.addAll(DataFactory.createTasks(taskManager, NUMBER_OF_TASKS));
        epicIds.addAll(DataFactory.createEpics(taskManager, NUMBER_OF_EPICS));
        subtaskIds.addAll(DataFactory.createSubtasks(taskManager, NUMBER_OF_SUBTASKS, epicIds));

        FileBackedTaskManager anotherFileBackedTaskManagerToLoad = (FileBackedTaskManager) Managers.getManager("FileManager", String.valueOf(path1));
        Assertions.assertDoesNotThrow(() -> anotherFileBackedTaskManagerToLoad.loadFromFile(file1));
    }

    @Test
    public void subtasksHaveCorrectEpicIds() {

        epicIds.addAll(DataFactory.createEpics(taskManager, NUMBER_OF_EPICS));
        subtaskIds.addAll(DataFactory.createSubtasks(taskManager, NUMBER_OF_SUBTASKS, epicIds));
        epicIds.addAll(DataFactory.createEpics(taskManager, NUMBER_OF_EPICS));
        subtaskIds.addAll(DataFactory.createSubtasks(taskManager, NUMBER_OF_SUBTASKS, epicIds));

        Assertions.assertEquals(NUMBER_OF_EPICS * 2, taskManager.getAllEpics().size());
        Assertions.assertEquals(NUMBER_OF_SUBTASKS * 2, taskManager.getAllSubtasks().size());

        FileBackedTaskManager anotherFileBackedTaskManagerToLoad = (FileBackedTaskManager) Managers.getManager("FileManager", String.valueOf(path1));

        Assertions.assertEquals(NUMBER_OF_EPICS * 2, anotherFileBackedTaskManagerToLoad.getAllEpics().size());
        Assertions.assertEquals(NUMBER_OF_SUBTASKS * 2, anotherFileBackedTaskManagerToLoad.getAllSubtasks().size());


    }
}