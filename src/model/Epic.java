package model;

public class Epic extends Task {
    public Epic(Integer id, String name, String description) {
        super(id, name, description, TaskStatus.NEW);
    }

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
    }

    @Override
    public String toString() {
        //1,EPIC,Task1,NEW,Description task1,
        return String.format("%s,%S,%s,%S,%s,", getId(), TaskTypes.EPIC, getName(), getStatus(), getDescription());
    }

    public static Epic fromString(String value) {
        String[] parts = value.split(",");

        if (parts.length != 5) {
            throw new IllegalArgumentException("Неккоректный формат строки-эпика");
        }

        return new Epic(Integer.parseInt(parts[0]), parts[2], parts[4]);
    }
}
