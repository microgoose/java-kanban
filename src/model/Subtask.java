package model;

import common.Config;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(Integer id, String name, String description, TaskStatus status,
                   LocalDateTime startTime, Duration duration, Integer epicId) {
        super(id, name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(Integer id, String name, String description, TaskStatus status,
                   LocalDateTime startTime, Duration duration) {
        this(id, name, description, status, startTime, duration, null);
    }

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Config.DATE_TIME_FORMAT);

        String startTime = getStartTime() == null ? null : formatter.format(getStartTime());
        String duration = getDuration() == null ? null : String.valueOf(getDuration().toMinutes());
        String epicId = getEpicId() == null ? null : getEpicId().toString();

        //3,SUBTASK,Sub Task2,DONE,Description sub task3,01.01.0001 01:01,78,2
        return String.format("%s,%S,%s,%S,%s,%s,%s,%s",
                getId(), TaskTypes.SUBTASK, getName(), getStatus(), getDescription(), startTime, duration, epicId);
    }

    public static Subtask fromString(String value) {
        String[] parts = value.split(",");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Config.DATE_TIME_FORMAT);

        if (parts.length != 8)
            throw new IllegalArgumentException("Неккоректный формат строки-подзадчи");

        return new Subtask(
                Integer.parseInt(parts[0]),
                parts[2],
                parts[4],
                TaskStatus.valueOf(parts[3]),
                parts[5].equals("null") ? null : LocalDateTime.parse(parts[5], formatter),
                parts[6].equals("null") ? null : Duration.ofMinutes(Long.parseLong(parts[6])),
                parts[7].equals("null") ? null : Integer.parseInt(parts[7])
        );
    }
}
