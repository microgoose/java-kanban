package manager;

import common.HistoryManager;
import common.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private int taskCounter = 0;

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();

    protected void updateTaskCounter(Task task) {
        if (task.getId() == null) {
            task.setId(taskCounter++);
        } else if (task.getId() > taskCounter) {
            taskCounter = task.getId() + 1;
        } else {
            taskCounter++;
        }
    }

    protected void updateEpicState(Epic epic) {
        epic.setStatus(TaskStatus.NEW);

        for (Subtask subtask : getAllSubtasks()) {
            if (!Objects.equals(subtask.getEpicId(), epic.getId()))
                continue;

            LocalDateTime subtaskStartTime = subtask.getStartTime();
            LocalDateTime subtaskEndTime = subtask.getEndTime();
            LocalDateTime startTime = epic.getStartTime();
            LocalDateTime endTime = epic.getEndTime();

            if (subtaskStartTime != null) {
                if (startTime == null || subtaskStartTime.isBefore(startTime)) {
                    epic.setStartTime(subtaskStartTime);
                }
            }

            if (subtaskEndTime != null) {
                if (endTime == null || subtaskEndTime.isAfter(endTime)) {
                    epic.setEndTime(subtaskEndTime);
                }
            }

            if (epic.getStatus() != TaskStatus.IN_PROGRESS) {
                switch (subtask.getStatus()) {
                    case IN_PROGRESS:
                        epic.setStatus(TaskStatus.IN_PROGRESS);
                        break;
                    case DONE:
                        epic.setStatus(TaskStatus.DONE);
                        break;
                }
            }
        }

        if (epic.getStartTime() != null && epic.getEndTime() != null) {
            epic.setDuration(Duration.between(epic.getStartTime(), epic.getEndTime()));
        }
    }

    @Override
    public Collection<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Collection<Task> getAllTasks() {
        return this.tasks.values();
    }

    @Override
    public Collection<Subtask> getAllSubtasks() {
        return this.subtasks.values();
    }

    @Override
    public Collection<Epic> getAllEpic() {
        return this.epics.values();
    }


    @Override
    public void removeAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }

        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Subtask subtask : getAllSubtasks()) {
            Epic epic = epics.get(subtask.getEpicId());

            if (epic != null) {
                updateEpicState(epic);
            }

            historyManager.remove(subtask.getId());
        }

        subtasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }

        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }

        epics.clear();
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int index) {
        Task task = tasks.get(index);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(int index) {
        Subtask subtask = subtasks.get(index);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(int index) {
        Epic epic = epics.get(index);
        historyManager.add(epic);
        return epic;
    }


    @Override
    public int addTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            throw new IllegalArgumentException("Задача с таким ID уже существует!");
        }

        if (subtasks.values().stream().anyMatch(st -> Task.isExecutionTimeOverlap(task, st))) {
            throw new IllegalArgumentException(String.format(
                "Задача с таким временным диапазоном (%s - %s) уже существует!",
                task.getStartTime(), task.getEndTime()
            ));
        }

        tasks.put(task.getId(), task);
        updateTaskCounter(task);

        return task.getId();
    }

    @Override
    public int addSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            throw new IllegalArgumentException("Подзадача с таким ID уже существует!");
        }

        if (subtasks.values().stream().anyMatch(st -> Task.isExecutionTimeOverlap(subtask, st))) {
            throw new IllegalArgumentException(String.format(
                "Подзадача с таким временным диапазоном (%s - %s) уже существует!",
                subtask.getStartTime(), subtask.getEndTime()
            ));
        }

        subtasks.put(subtask.getId(), subtask);
        updateTaskCounter(subtask);

        Epic epic = epics.get(subtask.getEpicId());

        if (epic != null) {
            updateEpicState(epic);
        }

        return subtask.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            throw new IllegalArgumentException("Эпик с таким ID уже существует!");
        }

        epics.put(epic.getId(), epic);
        updateTaskCounter(epic);
        updateEpicState(epic);

        return epic.getId();
    }


    @Override
    public void updateTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Задача не может быть null");
        }

        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            throw new IllegalArgumentException("Задача не найденна!");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            throw new IllegalArgumentException("Задача не может быть null");
        }

        if (subtasks.containsKey(subtask.getId())) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                updateEpicState(epic);
            }

            subtasks.put(subtask.getId(), subtask);
        } else {
            throw new IllegalArgumentException("Задача не найденна!");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            throw new IllegalArgumentException("Задача не может быть null");
        }

        if (epics.containsKey(epic.getId())) {
            updateEpicState(epic);
            epics.put(epic.getId(), epic);
        } else {
            throw new IllegalArgumentException("Задача не найденна!");
        }
    }


    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());

        if (epic != null) {
            updateEpicState(epic);
        }

        subtasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.get(id);

        for (Subtask epicSubtask : getEpicSubtasks(id)) {
            if (Objects.equals(epicSubtask.getEpicId(), epic.getId())) {
                subtasks.remove(epicSubtask.getId());
                historyManager.remove(epicSubtask.getId());
            }
        }

        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public Collection<Subtask> getEpicSubtasks(int id) {
        Epic epic = epics.get(id);

        if (epic == null) {
            throw new IllegalArgumentException("Не найден эпик с заданным идентификатором: " + id);
        }

        return getAllSubtasks()
                .stream()
                .filter(subtask -> Objects.equals(subtask.getEpicId(), epic.getId()))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
