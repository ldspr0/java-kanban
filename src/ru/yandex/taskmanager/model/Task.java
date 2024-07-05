package ru.yandex.taskmanager.model;

import ru.yandex.taskmanager.enums.Status;

public class Task {
    private int id;
    private String title;
    private String description;
    private Status status;

    public Task(int id, String title, String description, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
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

    public void setStatus(Status status) {
        this.status = status;
    }

    /* Стоит ли писать название пакета в переопредленном toString? ide'шный авторефактор вот добавил, но мне кажется
    как будто, бы это не нужно, возможно это должна быть какая-то отдельная (первая) строка?
     */
    @Override
    public String toString() {
        return "Task{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
