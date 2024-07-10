package ru.yandex.taskmanager.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskmanager.enums.Status;
import ru.yandex.taskmanager.model.Epic;
import ru.yandex.taskmanager.model.Subtask;
import ru.yandex.taskmanager.model.Task;
import ru.yandex.taskmanager.service.TaskManager;
import ru.yandex.taskmanager.utility.Managers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class FunctionalityTests {
    private final TaskManager taskManager = Managers.getDefault();
    private final ArrayList<Integer> taskIds = new ArrayList<>();
    private final ArrayList<Integer> subtaskIds = new ArrayList<>();
    private final ArrayList<Integer> epicIds = new ArrayList<>();
    private final Random random = new Random();

    @BeforeEach
    public void beforeEach() {

        for (int i = 0; i < 5; i++) {
            int taskId = taskManager.createRecord(new Task(i, "Задача " + i, "description", Status.NEW));
            taskIds.add(taskId);
        }

        int epicId1 = taskManager.createRecord(new Epic(0, "Эпик 1", "description"));
        epicIds.add(epicId1);
        for (int i = 0; i < 4; i++) {
            String title = "Подзадача " + epicId1 + "-" + i;
            int subtaskId = taskManager.createRecord(new Subtask(i, title, "description", Status.NEW, epicId1));
            subtaskIds.add(subtaskId);
        }

        int epicId2 = taskManager.createRecord(new Epic(0, "Эпик 2", "description"));
        epicIds.add(epicId2);
        String title = "Подзадача " + epicId2 + "-" + 0;
        int subtaskId = taskManager.createRecord(new Subtask(0, title, "description", Status.NEW, epicId2));
        subtaskIds.add(subtaskId);

        int epicId3 = taskManager.createRecord(new Epic(0, "Эпик 3", "description"));
        epicIds.add(epicId3);

    }

    @Test
    public void isPositiveToGetTask() {
        int id = taskIds.get(random.nextInt(taskIds.size()));
        Assertions.assertNotNull(taskManager.getTask(id));
    }

    @Test
    public void isPositiveToGetSubtask() {
        int id = subtaskIds.get(random.nextInt(subtaskIds.size()));
        Assertions.assertNotNull(taskManager.getSubtask(id));
    }

    @Test
    public void isPositiveToGetEpic() {
        int id = epicIds.get(random.nextInt(epicIds.size()));
        Assertions.assertNotNull(taskManager.getEpic(id));
    }

    @Test
    public void isNegativeToGetTaskFromEpics() {
        int id = taskIds.get(random.nextInt(taskIds.size()));
        Assertions.assertNull(taskManager.getSubtask(id));
    }

    @Test
    public void isNegativeToGetTaskWithIncorrectId() {
        int id = taskIds.get(random.nextInt(taskIds.size()));
        Assertions.assertNull(taskManager.getTask(id + 100));
    }

    @Test
    public void isPositiveToGetAllTasks() {
        Assertions.assertEquals(5, taskManager.getAllTasks().size());
    }

    @Test
    public void isPositiveToGetAllSubtasks() {
        Assertions.assertEquals(5, taskManager.getAllSubtasks().size());
    }

    @Test
    public void isPositiveToGetAllEpics() {
        Assertions.assertEquals(3, taskManager.getAllEpics().size());
    }

    @Test
    public void isPossibleToUpdateTask() {
        int id = taskIds.get(random.nextInt(taskIds.size()));
        Task originalTask = taskManager.getTask(id);
        Assertions.assertNotEquals("Новая Задача 555", originalTask.getTitle());
        Assertions.assertNotEquals(Status.IN_PROGRESS, originalTask.getStatus());

        Task updatedTask = new Task(id, "Новая Задача 555", "description", Status.IN_PROGRESS);
        updatedTask.setStatus(Status.DONE);
        taskManager.updateRecord(updatedTask);
        Task modifiedTask = taskManager.getTask(id);

        Assertions.assertEquals(originalTask.getId(), modifiedTask.getId());
        Assertions.assertEquals("Новая Задача 555", modifiedTask.getTitle());
        Assertions.assertEquals(Status.DONE, modifiedTask.getStatus());
    }

    @Test
    public void isPossibleToUpdateEpic() {
        int id = epicIds.get(random.nextInt(epicIds.size()));
        Epic originalEpic = taskManager.getEpic(id);
        Assertions.assertNotEquals("Новый Эпик 555", originalEpic.getTitle());
        Assertions.assertNotEquals(Status.IN_PROGRESS, originalEpic.getStatus());

        // общее
        Epic updatedEpic = new Epic(id, "Новый Эпик 555", "description");
        updatedEpic.setStatus(Status.IN_PROGRESS);
        taskManager.updateRecord(updatedEpic);
        Epic modifiedEpic = taskManager.getEpic(id);

        Assertions.assertEquals(originalEpic.getId(), modifiedEpic.getId());
        Assertions.assertEquals("Новый Эпик 555", modifiedEpic.getTitle());
        // shouldn't be possible to set status for epic
        Assertions.assertNotEquals(Status.IN_PROGRESS, modifiedEpic.getStatus());
    }

    @Test
    public void isPossibleToUpdateSubtask() {
        int id = subtaskIds.get(random.nextInt(subtaskIds.size()));
        Subtask originalSubtask = taskManager.getSubtask(id);
        Assertions.assertNotEquals("Новая Подзадача 555", originalSubtask.getTitle());
        Assertions.assertNotEquals(Status.IN_PROGRESS, originalSubtask.getStatus());

        Subtask updatedSubtask = new Subtask(id, "Новая Подзадача 555", "description", Status.IN_PROGRESS, originalSubtask.getEpicId());
        updatedSubtask.setStatus(Status.DONE);
        taskManager.updateRecord(updatedSubtask);
        Subtask modifiedSubtask = taskManager.getSubtask(id);

        Assertions.assertEquals(originalSubtask.getId(), modifiedSubtask.getId());
        Assertions.assertEquals("Новая Подзадача 555", modifiedSubtask.getTitle());
        Assertions.assertEquals(Status.DONE, modifiedSubtask.getStatus());
        Assertions.assertEquals(originalSubtask.getEpicId(), modifiedSubtask.getEpicId());
    }

    @Test
    public void statusForEpicIsChangedWithSubtaskUpdate() {
        Epic epicWithOneSubtask = null;
        Epic epicWithManySubtasks = null;

        for (int id : epicIds) {
            Epic epic = taskManager.getEpic(id);
            switch (epic.getSubtaskIds().size()) {
                case 1 -> epicWithOneSubtask = epic;
                case 4 -> epicWithManySubtasks = epic;
            }
        }

        // epic with 1 subtask
        Assertions.assertNotNull(epicWithOneSubtask);
        Assertions.assertEquals(Status.NEW, epicWithOneSubtask.getStatus());
        for (Integer subtaskId : epicWithOneSubtask.getSubtaskIds()) {
            Subtask subtask = taskManager.getSubtask(subtaskId);
            subtask.setStatus(Status.DONE);
            taskManager.updateRecord(subtask);
        }
        Assertions.assertEquals(Status.DONE, epicWithOneSubtask.getStatus());
        for (Integer subtaskId : epicWithOneSubtask.getSubtaskIds()) {
            taskManager.deleteSubtask(subtaskId);
        }
        Assertions.assertEquals(Status.NEW, epicWithOneSubtask.getStatus());

        // epic with 2+ subtasks
        Assertions.assertNotNull(epicWithManySubtasks);
        Assertions.assertEquals(Status.NEW, epicWithManySubtasks.getStatus());

        // make 1 subtask done
        for (Integer subtaskId : epicWithManySubtasks.getSubtaskIds()) {
            Subtask subtask = taskManager.getSubtask(subtaskId);
            subtask.setStatus(Status.DONE);
            taskManager.updateRecord(subtask);
            break;
        }
        Assertions.assertEquals(Status.IN_PROGRESS, epicWithManySubtasks.getStatus());

        // delete "done" subtask,
        int subtaskIdToDelete = -1;
        for (Integer subtaskId : epicWithManySubtasks.getSubtaskIds()) {
            Subtask subtask = taskManager.getSubtask(subtaskId);
            if (subtask.getStatus() == Status.DONE) {
                subtaskIdToDelete = subtaskId;
                break;
            }
        }
        taskManager.deleteSubtask(subtaskIdToDelete);
        Assertions.assertEquals(Status.NEW, epicWithManySubtasks.getStatus());

        // make all subtasks done
        for (Integer subtaskId : epicWithManySubtasks.getSubtaskIds()) {
            Subtask subtask = taskManager.getSubtask(subtaskId);
            subtask.setStatus(Status.DONE);
            taskManager.updateRecord(subtask);
        }
        Assertions.assertEquals(Status.DONE, epicWithManySubtasks.getStatus());

        // delete all subtasks
        HashSet<Integer> subtaskIds = new HashSet<>(epicWithManySubtasks.getSubtaskIds());
        for (int subtaskId : subtaskIds) {
            taskManager.deleteSubtask(subtaskId);
        }
        Assertions.assertEquals(Status.NEW, epicWithManySubtasks.getStatus());
    }

    @Test
    public void taskIsRemovableFromTasks() {
        int id = taskIds.get(random.nextInt(taskIds.size()));
        Assertions.assertNotNull(taskManager.getTask(id));
        taskManager.deleteTask(id);
        Assertions.assertNull(taskManager.getTask(id));
    }

    @Test
    public void subtaskIsRemovableFromSubtasks() {
        int id = subtaskIds.get(random.nextInt(subtaskIds.size()));
        Assertions.assertNotNull(taskManager.getSubtask(id));
        taskManager.deleteSubtask(id);
        Assertions.assertNull(taskManager.getSubtask(id));
    }

    @Test
    public void epicIsRemovableFromEpics() {
        int id = epicIds.get(random.nextInt(epicIds.size()));
        Assertions.assertNotNull(taskManager.getEpic(id));
        taskManager.deleteEpic(id);
        Assertions.assertNull(taskManager.getEpic(id));
    }

    @Test
    public void subtasksAreRemovedWithEpic() {
        Epic epicWithManySubtasks = null;

        for (int id : epicIds) {
            Epic epic = taskManager.getEpic(id);
            if (epic.getSubtaskIds().size() > 1) {
                epicWithManySubtasks = epic;
                break;
            }
        }

        Assertions.assertNotNull(epicWithManySubtasks);
        Assertions.assertNotNull(taskManager.getEpic(epicWithManySubtasks.getId()));
        HashSet<Integer> subtaskIds = epicWithManySubtasks.getSubtaskIds();
        Assertions.assertFalse(subtaskIds.isEmpty());
        for (int id : subtaskIds) {
            Assertions.assertNotNull(taskManager.getSubtask(id));
        }

        taskManager.deleteEpic(epicWithManySubtasks.getId());

        Assertions.assertNull(taskManager.getEpic(epicWithManySubtasks.getId()));
        for (int id : subtaskIds) {
            Assertions.assertNull(taskManager.getSubtask(id));
        }

    }

    @Test
    public void taskIsNOTRemovableFromSubtasks() {
        int id = taskIds.get(random.nextInt(taskIds.size()));
        Assertions.assertNotNull(taskManager.getTask(id));
        taskManager.deleteSubtask(id);
        Assertions.assertNotNull(taskManager.getTask(id));
    }

    @Test
    public void noErrorThrownIfIncorrectIdUsedForRemoval() {
        int id = taskIds.get(random.nextInt(taskIds.size()));
        int newId = id + 100;
        taskManager.deleteSubtask(newId);
        Assertions.assertNotNull(taskManager.getTask(id));
    }

    @Test
    public void isPositiveToRemoveAllTasks() {
        Assertions.assertEquals(5, taskManager.getAllTasks().size());
        taskManager.clearTasks();
        Assertions.assertTrue(taskManager.getAllTasks().isEmpty());

    }

    @Test
    public void isPositiveToRemoveAllSubtasks() {
        Assertions.assertEquals(5, taskManager.getAllSubtasks().size());
        taskManager.clearSubtasks();
        Assertions.assertTrue(taskManager.getAllSubtasks().isEmpty());

        for (int id : epicIds) {
            Assertions.assertTrue(taskManager.getEpic(id).getSubtaskIds().isEmpty());
        }
    }

    @Test
    public void isPositiveToRemoveAllEpics() {
        Assertions.assertEquals(3, taskManager.getAllEpics().size());
        Assertions.assertEquals(5, taskManager.getAllSubtasks().size());
        taskManager.clearEpics();
        Assertions.assertTrue(taskManager.getAllEpics().isEmpty());
        Assertions.assertTrue(taskManager.getAllSubtasks().isEmpty());

    }
}
