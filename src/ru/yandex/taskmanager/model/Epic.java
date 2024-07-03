package ru.yandex.taskmanager.model;

import ru.yandex.taskmanager.enums.Status;

import java.util.ArrayList;
import java.util.HashSet;

public class Epic extends Task {
    private final HashSet<Integer> subtasks;

    public Epic(int id, String title, String description) {
        super(id, title, description, Status.NEW);
        this.subtasks = new HashSet<>();
    }

    public void recalculateStatus(ArrayList<Subtask> subtasks) {
        if (subtasks.isEmpty()) {
            super.setStatus(Status.NEW);
            return;
        }

        int countStatusDone = 0;
        for (Subtask each : subtasks) {
            Status status = each.getStatus();

            // Если хотя бы один сабтаск в ин прогресс значит Эпик в ин прогресс
            // При добавлении других статусов (UAT/Re-Opened и т.д.) ничего не сломается

            /* Вопрос: А если у одной подзадачи статус DONE, а у всех остальных NEW,
                то какой должен быть статус у эпика и какой у тебя?
                Ответ: То будет IN_PROGRESS же, это обрабатывается ниже после всего цикла for
             */
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


    public void setStatus(Status status) {
        // Статус будет меняться в зависимости от Статуса Сабтасок.
    }

    public HashSet<Integer> getSubtasks() {
        return subtasks;
    }

    @Override
    public String toString() {
        return "ru.yandex.taskmanager.model.Epic{" +
                "id=" + super.getId() +
                ", title='" + super.getTitle() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                ", subtasks=" + getSubtasks() +
                '}';
    }
}
