package ru.yandex.taskmanager.service;

import ru.yandex.taskmanager.enums.Status;
import ru.yandex.taskmanager.enums.TaskType;
import ru.yandex.taskmanager.model.Epic;
import ru.yandex.taskmanager.model.Subtask;
import ru.yandex.taskmanager.model.Task;
import ru.yandex.taskmanager.utility.ManagerSaveException;

import java.io.*;
import java.util.HashMap;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static final String DEFAULT_DATA_FILE = "data//test.csv";
    private static File dataFile;
    private static HashMap<Integer, Integer> epicIdToNewIdAfterLoad = new HashMap<>();

    public FileBackedTaskManager(String[] filePath) {
        dataFile = new File(DEFAULT_DATA_FILE);
        if (filePath.length != 0) {
            dataFile = new File(filePath[0]);
        }
        if (dataFile.exists()) {
            loadFromFile(dataFile);
        }
    }

    public void save() {
        try {
            if (!dataFile.exists()) {
                if (!dataFile.createNewFile()) {
                    throw new ManagerSaveException("Ошибка: Файл не может быть создан.");
                }
            }
            if (dataFile.exists()) {
                Writer fileWriter = new FileWriter(dataFile);
                fileWriter.write("id,type,subject,status,epicId,description\n");

                for (Task task : getAllTasks()) {
                    fileWriter.write(toString(task) + "\n");
                }
                for (Task epic : getAllEpics()) {
                    fileWriter.write(toString(epic) + "\n");
                }
                for (Task subtask : getAllSubtasks()) {
                    fileWriter.write(toString(subtask) + "\n");
                }

                fileWriter.close();
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    private static String toString(Task task) {
        if (task == null) {
            return "";
        }

        String type;
        String epicId = "";
        switch (task) {
            case Epic e -> type = TaskType.EPIC.name();
            case Subtask s -> {
                epicId = String.valueOf(s.getEpicId());
                type = TaskType.SUBTASK.name();
            }
            default -> type = TaskType.TASK.name();
        }

        return task.getId() + "," + type + "," + task.getTitle() + "," + task.getStatus() + "," + epicId + "," + task.getDescription();

    }

    private static <T extends Task> T fromString(String value) {
        if (value == null) {
            return null;
        }

        String[] fieldValues = value.split(",");

        // игнорурем первую линию с тайтлами
        if ("id".equals(fieldValues[0])) {
            return null;
        }

        int id = fieldValues[0].isBlank() ? -1 : Integer.parseInt(fieldValues[0]);

        TaskType type = TaskType.valueOf(fieldValues[1]);
        String title = fieldValues[2];
        Status status = Status.valueOf(fieldValues[3]);
        int epicId = fieldValues[4].isBlank() ? -1 : Integer.parseInt(fieldValues[4]);
        String description = fieldValues[5];
        if (type == TaskType.SUBTASK && epicIdToNewIdAfterLoad.get(epicId) != null) {
            epicId = epicIdToNewIdAfterLoad.get(epicId);
        }

        return switch (type) {
            case TaskType.TASK -> (T) new Task(id, title, description, status);
            case TaskType.SUBTASK -> (T) new Subtask(id, title, description, status, epicId);
            case TaskType.EPIC -> (T) new Epic(id, title, description);
        };
    }

    public void loadFromFile(File file) {

        try (BufferedReader fileReader = new BufferedReader(new FileReader(file));) {
            while (fileReader.ready()) {
                String line = fileReader.readLine();

                switch (fromString(line)) {
                    case Epic e -> {int newEpicId = super.createRecord(e); epicIdToNewIdAfterLoad.put(e.getId(), newEpicId);}
                    case Subtask s -> super.createRecord(s);
                    case Task t -> super.createRecord(t);
                    case null -> {
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    @Override
    public int createRecord(Task task) {
        int id = super.createRecord(task);
        save();
        return id;
    }

    @Override
    public int createRecord(Epic epic) {
        int id = super.createRecord(epic);
        save();
        return id;
    }

    @Override
    public int createRecord(Subtask subtask) {
        int id = super.createRecord(subtask);
        save();
        return id;
    }

    @Override
    public void updateRecord(Task task) {
        super.updateRecord(task);
        save();
    }

    @Override
    public void updateRecord(Epic epic) {
        super.updateRecord(epic);
        save();
    }

    @Override
    public void updateRecord(Subtask subtask) {
        super.updateRecord(subtask);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

}
