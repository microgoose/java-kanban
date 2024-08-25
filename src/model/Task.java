package model;

import common.Config;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected Integer id;
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(Integer id, String name, String description,
                TaskStatus status, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

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

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (duration == null || startTime == null) {
            return null;
        }

        return this.startTime.plus(duration);
    }

    public static boolean isExecutionTimeOverlap(Task source, Task target) {
        LocalDateTime sourceEndTime = source.getEndTime();
        LocalDateTime targetEndTime = target.getEndTime();

        if (sourceEndTime == null || targetEndTime == null) {
            return false;
        }

        long sourceStartTimeMs = source.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long sourceEndTimeMs = sourceEndTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long targetStartTimeMs = target.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long targetEndTimeMs = targetEndTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        boolean startTimeBetweenStartAndEnd =
                sourceStartTimeMs >= targetStartTimeMs && sourceStartTimeMs <= targetEndTimeMs;
        boolean endTimeBetweenStartAndEnd =
                sourceEndTimeMs >= targetStartTimeMs && sourceEndTimeMs <= targetEndTimeMs;

        return startTimeBetweenStartAndEnd || endTimeBetweenStartAndEnd;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Config.DATE_TIME_FORMAT);

        String startTime = getStartTime() == null? null : formatter.format(getStartTime());
        String duration = getDuration() == null? null : String.valueOf(getDuration().toMinutes());

        //1,TASK,Task1,NEW,Description task1,01.01.0001 01:01,78,
        return String.format("%s,%S,%s,%S,%s,%s,%s,null",
                getId(), TaskTypes.TASK, getName(), getStatus(),
                getDescription(), startTime, duration);
    }

    public static Task fromString(String value) {
        String[] parts = value.split(",");

        if (parts.length != 8)
            throw new IllegalArgumentException("Неккоректный формат строки-задачи");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Config.DATE_TIME_FORMAT);

        return new Task(
            Integer.parseInt(parts[0]),
            parts[2],
            parts[4],
            TaskStatus.valueOf(parts[3]),
            parts[5].equals("null")? null : LocalDateTime.parse(parts[5], formatter),
            parts[6].equals("null")? null : Duration.ofMinutes(Long.parseLong(parts[6]))
        );
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (id == null? 0 : id);
        hash = 31 * hash + (name == null? 0 : name.hashCode());
        hash = 31 * hash + (description == null? 0 : description.hashCode());
        return hash;
    }
}
