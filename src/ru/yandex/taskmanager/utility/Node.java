package ru.yandex.taskmanager.utility;

import ru.yandex.taskmanager.model.Task;
import java.util.Objects;

public class Node {
    private final Task entity;
    private Node next;
    private Node prev;

    public Node(Task entity, Node next, Node prev) {
        this.entity = entity;
        this.next = next;
        this.prev = prev;
    }

    public Task getEntity() {
        return entity;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return entity.getId() == node.entity.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(entity.getId());
    }
}
