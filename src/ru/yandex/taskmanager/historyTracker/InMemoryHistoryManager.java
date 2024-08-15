package ru.yandex.taskmanager.historyTracker;

import ru.yandex.taskmanager.model.Task;
import ru.yandex.taskmanager.utility.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


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
    }

    public void linkLast(Task task) {
        Node newNode = new Node(task, null, tail);

        if (tail == null) {
            head = newNode;
        } else {
            tail.setNext(newNode);
        }

        tail = newNode;

        viewHistoryMap.put(task.getId(), newNode);
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> result = new ArrayList<>();
        Node node = head;

        while (node != null) {
            result.add(node.getEntity());
            node = node.getNext();
        }

        return result;
    }

    @Override
    public void remove(int id) {
        removeNode(viewHistoryMap.get(id));
        viewHistoryMap.remove(id);
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
        if (head == tail) {
            // only if you need to "rewrite" first item
            head = null;
            tail = null;

        } else if (node.getPrev() == null && node.getNext() != null) {
            head = node.getNext();
            head.setPrev(null);

        } else if (node.getNext() == null && node.getPrev() != null) {
            tail = node.getPrev();
            tail.setNext(null);

        } else {
            // somewhere in the middle
            Node nextNode = node.getNext();
            Node prevNode = node.getPrev();

            if (nextNode != null) {
                nextNode.setPrev(prevNode);
            }

            if (prevNode != null) {
                prevNode.setNext(nextNode);
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(getTasks());
    }


}