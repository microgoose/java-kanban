package model;

public class TaskNode {
    private final Task task;
    private TaskNode prev;
    private TaskNode next;

    public TaskNode(Task task, TaskNode prev, TaskNode next) {
        this.task = task;
        this.prev = prev;
        this.next = next;
    }

    public TaskNode(Task task) {
        this(task, null, null);
    }

    public void setNext(TaskNode next) {
        this.next = next;
    }

    public TaskNode getNext() {
        return next;
    }

    public void setPrev(TaskNode prev) {
        this.prev = prev;
    }

    public TaskNode getPrev() {
        return prev;
    }

    public Task getTask() {
        return task;
    }
}
