package ru.yandex.taskmanager.model;

import ru.yandex.taskmanager.enums.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

public class Epic extends Task {
    private final HashSet<Integer> subtaskIds;

    public Epic(int id, String title, String description) {
        super(id, title, description, Status.NEW);
        this.subtaskIds = new HashSet<>();
    }

    public void recalculateStatus(ArrayList<Subtask> subtasks) {

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

    public HashSet<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setStatus(Status status) {
    }

    public Long getDuration(ArrayList<Subtask> subtasks) {
        return subtasks.stream()
                .filter(subtask -> subtask.getDuration() != null && subtask.getStartTime() != null)
                .map(subtask -> subtask.getDuration().toMinutes())
                .reduce(0L, Long::sum);
    }

    public LocalDateTime getStartTime(ArrayList<Subtask> subtasks) {
        LocalDateTime startTime = null;
        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime() == null) {
                continue;
            }
            if (startTime == null || startTime.isAfter(subtask.getStartTime())) {
                startTime = subtask.getStartTime();
            }
        }

        return startTime;
    }


    public LocalDateTime getEndTime(ArrayList<Subtask> subtasks) {
        LocalDateTime endTime = null;
        for (Subtask subtask : subtasks) {
            if (subtask.getEndTime() == null) {
                continue;
            }
            if (endTime == null || endTime.isBefore(subtask.getEndTime())) {
                endTime = subtask.getEndTime();
            }
        }

        return endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + super.getId() +
                ", title='" + super.getTitle() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                ", subtasks=" + getSubtaskIds() +
                '}';
    }
}
