package model;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(Integer id, String name, String description, TaskStatus status, Integer epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public Subtask(Integer id, String name, String description, TaskStatus status) {
        this(id, name, description, status, null);
    }
    
    public Subtask(Integer id, String name, String description) {
        this(id, name, description, TaskStatus.NEW, null);
    }

    public Subtask(Integer id, String name, String description, Integer epicId) {
        this(null, name, description, TaskStatus.NEW, epicId);
    }
    
    public Subtask(String name, String description, TaskStatus status) {
        this(null, name, description, status, null);
    }
    
    public Subtask(String name, String description) {
        this(null, name, description, TaskStatus.NEW, null);
    }

    public Subtask(String name, String description, Integer epicId) {
        this(null, name, description, TaskStatus.NEW, epicId);
    }
    
    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }
}
