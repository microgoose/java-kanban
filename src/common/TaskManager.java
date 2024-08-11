package common;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.Collection;

public interface TaskManager {
    Collection<Task> getHistory();

    Collection<Task> getAllTasks();

    Collection<Subtask> getAllSubtasks();

    Collection<Epic> getAllEpic();

    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    Task getTaskById(int index);

    Subtask getSubtaskById(int index);

    Epic getEpicById(int index);

    int addTask(Task task);

    int addSubtask(Subtask subtask);

    int addEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void removeTaskById(int id);

    void removeSubtaskById(int id);

    void removeEpicById(int id);

    Collection<Subtask> getEpicSubtasks(int id);
}
