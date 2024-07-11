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
    Если я не буду знать информацию подзадач, то как я сравню статусы других подзадач, информация по одной подзадаче
    мне не поможет поменять статус на корректный.
    Ваши слова:
    Из эпика ты получаешь коллекцию id подзадач и по этой коллекции уже делаешь все, что тебе нужно (получаешь объекты, добавляешь, удаляешь)
    Мой вопрос:
    Каким вызовом из этого метода я могу получить объект подзадачи вместе с ее статусом? Не понимаю.
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

    public HashSet<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setStatus(Status status) {
        // Если я сделаю сетСтатус приватным на Таске, то тогда я не смогу вызывать сетСтатус отсюда, а он мне нужен.
        // Я возможно мог бы заморочиться и сделать сложную абстрактную структуру, распихать модель по разным пакетам,
        // но делать это только ради статуса?
        // Когда я могу просто переопределить в этом конкретном объекте этот метод оставив его пустым и с комментарием
        // для других разработчиков. Что это сделано, только для того, чтобы блокировать вызов сет статус для Эпиков,
        // как по мне это логичнее и более здраво.
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
