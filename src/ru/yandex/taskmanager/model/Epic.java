package ru.yandex.taskmanager.model;

import ru.yandex.taskmanager.enums.Status;

import java.util.ArrayList;
import java.util.HashSet;

public class Epic extends Task {
    private final HashSet<Integer> subtaskIds;

    public Epic(int id, String title, String description) {
        super(id, title, description, Status.NEW);
        this.subtaskIds = new HashSet<>();
    }

    /*
     Очень сильно туплю и не понимаю, если мы храним в этом объекте только Айдишники сабтасок,
     есть какой-то способ получить информацию о самих сабтасках из этого объекта?
     Они ведь находятся в taskManager'е и мне значит чтобы их передать сюда, нужно либо прокинуть сюда целиком
     taskManager или целиком список всех сабтасок, или только ограниченный список сабтасок, или нет?
     */

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


    public void setStatus(Status status) {
        // Изменение статуса напрямую запрещено. Статус будет меняться в зависимости от Статуса Сабтасок.
        /* То же самое здесь, если я уберу метод, то сетСтатус станет доступен из родителя
        заколлить recalculate, я не могу, у меня тут нет списка сабтасок, только айдишники.
         */

    }

    public HashSet<Integer> getSubtaskIds() {
        return subtaskIds;
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
