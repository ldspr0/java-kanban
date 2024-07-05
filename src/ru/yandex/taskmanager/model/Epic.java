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
        /*
        По поводу передачи айди эпика именно сюда, зачем? это и так метод ЭПИКА у кого мы пересчитываем,
        Он и так знает что он за эпик.
        Реализация бы сильно упростилась если бы в сабтасках были бы не айдишники, а сами сабтаски, но увы, причины
        описаны в таск менеджере. Что можно было бы сделать так это вытащить этот метод в Таск Менеджер и передавать
        внутрь него, сразу лист статусов
         */

        if (subtasks.isEmpty()) {
            super.setStatus(Status.NEW);
            return;
        }

        int countStatusDone = 0;
        for (Subtask each : subtasks) {
            Status status = each.getStatus();

            if (status != Status.NEW && status != Status.DONE) {
                super.setStatus(Status.IN_PROGRESS);
                return;
            }
            if (status == Status.DONE) {
                countStatusDone++;
            }
        }

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
        return "Epic{" +
                "id=" + super.getId() +
                ", title='" + super.getTitle() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                ", subtasks=" + getSubtasks() +
                '}';
    }
}
