package model;

import java.time.LocalDateTime;

public class Epic extends Task {
    protected LocalDateTime endTime;

    public Epic(Integer id, String name, String description) {
        super(id, name, description, TaskStatus.NEW);
    }

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        //1,EPIC,Task1,NEW,Description task1,null,null,null
        return String.format("%s,%S,%s,%S,%s,null,null,null", getId(), TaskTypes.EPIC, getName(), getStatus(), getDescription());
    }

    public static Epic fromString(String value) {
        String[] parts = value.split(",");

        if (parts.length != 8)
            throw new IllegalArgumentException("Некоректный формат строки-эпика");

        return new Epic(Integer.parseInt(parts[0]), parts[2], parts[4]);
    }
}
