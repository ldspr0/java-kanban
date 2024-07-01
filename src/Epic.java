import java.util.ArrayList;
import java.util.HashSet;

public class Epic extends Task {
    private HashSet<Integer> subtasks;

    public Epic(int id, String title, String description) {
        super(id, title, description, Status.NEW);
        this.subtasks = new HashSet<>();
    }

    public void recalculateStatus() {
        if (subtasks.isEmpty()) {
            super.setStatus(Status.NEW);
            return;
        }

        int countStatusDone = 0;
        for (Integer id : subtasks) {
            Status status = TaskManager.getRecord(id, TaskType.SUBTASK).getStatus();

            // Если хотя бы один сабтаск в ин прогресс значит Эпик в ин прогресс
            // При добавлении других статусов (UAT/Re-Opened и т.д.) ничего не сломается
            if (status != Status.NEW && status != Status.DONE) {
                super.setStatus(Status.IN_PROGRESS);
                return;
            }
            if (status == Status.DONE) {
                countStatusDone++;
            }
        }

        /*
         Проверку на текущий статус не добавляю, по скольку не работаем сейчас с БД, поэтому вместо усложнения кода,
         я лучше еще раз запишу в поле тоже самое значение.
         */

        if (countStatusDone == subtasks.size()) {
            super.setStatus(Status.DONE);
        } else if (countStatusDone > 0 && countStatusDone < subtasks.size()) {
            super.setStatus(Status.IN_PROGRESS);
        } else {
            super.setStatus(Status.NEW);
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + super.getId() +
                ", title='" + super.getTitle() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                ", subtasks=" + subtasks +
                '}';
    }

    public void setStatus(Status status) {
        recalculateStatus();
    }

    public HashSet<Integer> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(HashSet<Integer> subtasks) {
        this.subtasks = subtasks;
    }
}
