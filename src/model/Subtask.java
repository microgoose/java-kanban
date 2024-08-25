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
        this(id, name, description, TaskStatus.NEW, epicId);
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

    @Override
    public String toString() {
        //3,SUBTASK,Sub Task2,DONE,Description sub task3,2
        String epicId = getEpicId() == null ? "" : getEpicId().toString();
        return String.format("%s,%S,%s,%S,%s,%s", getId(), TaskTypes.SUBTASK, getName(), getStatus(), getDescription(), epicId);
    }

    public static Subtask fromString(String value) {
        String[] parts = value.split(",");

        if (parts.length == 6) {
            return new Subtask(
                    Integer.parseInt(parts[0]),
                    parts[2],
                    parts[4],
                    TaskStatus.valueOf(parts[3]),
                    Integer.parseInt(parts[5])
            );
        }

        if (parts.length == 5) {
            return new Subtask(
                    Integer.parseInt(parts[0]),
                    parts[2],
                    parts[4],
                    TaskStatus.valueOf(parts[3])
            );
        }

        throw new IllegalArgumentException("Неккоректный формат строки-подзадчи");
    }
}
