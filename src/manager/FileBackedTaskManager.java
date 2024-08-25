package manager;

import common.ManagerReadException;
import common.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskTypes;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    protected String csvTableHead = "id, type, name, status, description, startTime, duration, epic";
    protected final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
        loadFromFile(file);
    }

    public String getCsvTableHead() {
        return csvTableHead;
    }

    public void loadFromFile(File file) {
        if (!file.exists()) {
            throw new ManagerReadException("Файл не существует!");
        }

        if (file.isDirectory()) {
            throw new ManagerReadException("Передана директория!");
        }

        if (file.length() == 0)
            return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String csvTableHead = br.readLine();

            if (!csvTableHead.equals(this.csvTableHead))
                throw new IOException("Файл содержит недопустимые столбы таблицы!");

            while (br.ready()) {
                String dataLine = br.readLine();
                String[] data = dataLine.split(",");

                switch (TaskTypes.valueOf(data[1])) {
                    case TASK:
                        addTask(Task.fromString(dataLine));
                        break;
                    case SUBTASK:
                        addSubtask(Subtask.fromString(dataLine));
                        break;
                    case EPIC:
                        addEpic(Epic.fromString(dataLine));
                        break;
                }
            }

        } catch (Exception e) {
            throw new ManagerReadException(e);
        }
    }

    public void save() {
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(csvTableHead);
            fw.append("\n");

            for (Task task : getAllTasks()) {
                fw.append(task.toString()).append("\n");
            }

            for (Subtask subtask : getAllSubtasks()) {
                fw.append(subtask.toString()).append("\n");
            }

            for (Epic epic : getAllEpic()) {
                fw.append(epic.toString()).append("\n");
            }
        } catch (Exception e) {
            throw new ManagerSaveException(e);
        }
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    protected int setTask(Task task) {
        int id = super.setTask(task);
        save();
        return id;
    }

    @Override
    protected int setSubtask(Subtask subtask) {
        int id = super.setSubtask(subtask);
        save();
        return id;
    }

    @Override
    protected int setEpic(Epic epic) {
        int id = super.setEpic(epic);
        save();
        return id;
    }
}
