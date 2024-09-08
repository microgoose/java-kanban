package managers.common;

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

    Task getTaskById(int index) throws NotFoundException;

    Subtask getSubtaskById(int index) throws NotFoundException;

    Epic getEpicById(int index) throws NotFoundException;

    int addTask(Task task);

    int addSubtask(Subtask subtask);

    int addEpic(Epic epic);

    void updateTask(Task task) throws NotFoundException;

    void updateSubtask(Subtask subtask) throws NotFoundException;

    void updateEpic(Epic epic) throws NotFoundException;

    void removeTaskById(int id) throws NotFoundException;

    void removeSubtaskById(int id) throws NotFoundException;

    void removeEpicById(int id) throws NotFoundException;

    Collection<Subtask> getEpicSubtasks(int id);

    Collection<Task> getPrioritizedTasks();
}
