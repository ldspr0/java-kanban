package ru.yandex.taskmanager.historyTracker;

import ru.yandex.taskmanager.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node> viewHistoryMap = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (viewHistoryMap.containsKey(task.getId())) {
            removeNode(viewHistoryMap.get(task.getId()));
        }
        linkLast(task);

        /*
        V В ключах будут храниться id задач, а в значениях Node — узлы связного списка.
        V Она будет заполняться по мере добавления новых задач.
        V Напишите реализацию метода add(Task task).
        V Теперь с помощью HashMap и метода удаления removeNode метод add(Task task) будет быстро удалять задачу из списка, если она там есть,
        V а затем вставлять её в конец двусвязного списка.
        V После добавления задачи не забудьте обновить значение узла в HashMap.
         */
    }

    public void linkLast(Task task) {
        // linkLast будет добавлять задачу в конец этого списка,
        Node newNode = new Node(task, null, tail);
        if (tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.setNext(newNode);
        }

        viewHistoryMap.put(task.getId(), newNode);
    }

    public ArrayList<Task> getTasks() {
        // getTasks — собирать все задачи из него в обычный ArrayList.
        ArrayList<Task> result = new ArrayList<>();
        Node node = head;

        while(node != null) {
            result.add(node.getEntity());
            node = node.getNext();
        }

        return result;
    }

    @Override
    public void remove(int id) {
        viewHistoryMap.remove(id);
        removeNode(viewHistoryMap.get(id));
    }

    private void removeNode(Node node) {
        if (node.getPrev() == null) {
            head = node.getNext();
            head.setPrev(null);
        } else if (node.getNext() == null) {
            tail = node.getPrev();
            tail.setNext(null);
        } else {
            // somewhere in the middle
            Node nextNode = node.getNext();
            Node prevNode = node.getPrev();
            nextNode.setPrev(prevNode);
            prevNode.setNext(nextNode);
        }
    }

    @Override
    public List<Task> getHistory() {
        /*
        Реализация метода getHistory должна перекладывать задачи из связного списка в ArrayList для формирования ответа.
         */
        return List.copyOf(getTasks());
    }

    private class Node {
        final private Task entity;
        private Node next;
        private Node prev;

        Node(Task entity, Node next, Node prev) {
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
        public String toString() {
            return super.toString();
        }
    }

}


// TODO:
/*
Сделать историю посещений неограниченной по размеру.
Избавиться от повторных просмотров в истории. Если какую-либо задачу посещали несколько раз,
то в истории должен остаться только её последний просмотр. Предыдущий должен быть удалён.
 */