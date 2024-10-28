package ru.yandex.taskmanager.model;

import ru.yandex.taskmanager.enums.Status;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    private int id;
    private String title;
    private String description;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;


    public Task(int id, String title, String description, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(int id, String title, String description, Status status, LocalDateTime startTime, Integer duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        setDuration(duration);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Integer minutes) {
        if (minutes != null) {
            this.duration = Duration.ofMinutes(minutes);
        }
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return getStartTime() == null ? null : getDuration() == null ? null : getStartTime().plus(getDuration());
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // проверяем адреса объектов
        if (obj == null) return false; // проверяем ссылку на null
        if (this.getClass() != obj.getClass()) return false; // сравниваем классы
        Task otherTask = (Task) obj; // открываем доступ к полям другого объекта
        return this.getId() == otherTask.getId();
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                ", endTime=" + getEndTime() +
                '}';
    }
}
