package model;

import java.util.Objects;

public class Task {
    protected Integer id;
    protected String name;
    protected String description;
    protected TaskStatus status;

    public Task(Integer id, String name, String description, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(Integer id, String name, String description) {
        this(id, name, description, TaskStatus.NEW);
    }

    public Task(String name, String description, TaskStatus status) {
        this(null, name, description, status);
    }

    public Task(String name, String description) {
        this(name, description, TaskStatus.NEW);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return Objects.equals(id, task.id) && Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public String toString() {
        //1,TASK,Task1,NEW,Description task1,
        return String.format("%s,%S,%s,%S,%s,", getId(), TaskTypes.TASK, getName(), getStatus(), getDescription());
    }

    public static Task fromString(String value) {
        String[] parts = value.split(",");

        if (parts.length != 5) {
            throw new IllegalArgumentException("Неккоректный формат строки-задачи");
        }

        return new Task(Integer.parseInt(parts[0]), parts[2], parts[4], TaskStatus.valueOf(parts[3]));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (id == null ? 0 : id);
        hash = 31 * hash + (name == null ? 0 : name.hashCode());
        hash = 31 * hash + (description == null ? 0 : description.hashCode());
        return hash;
    }
}
