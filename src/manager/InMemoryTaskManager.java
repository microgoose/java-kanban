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

    private final TreeMap<LocalDateTime, Task> sortedTasks = new TreeMap<>();

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
        getAllTasks().forEach(task -> historyManager.remove(task.getId()));
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        getAllSubtasks().forEach(subtask -> {
            Epic epic = epics.get(subtask.getEpicId());

            if (epic != null) {
                updateEpicState(epic);
            }

            historyManager.remove(subtask.getId());
        });

        subtasks.clear();
    }

    @Override
    public void removeAllEpics() {
        getAllEpic().forEach(epic -> historyManager.remove(epic.getId()));
        getAllSubtasks().forEach(subtask -> historyManager.remove(subtask.getId()));
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

        return setTask(task);
    }

    @Override
    public int addSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            throw new IllegalArgumentException("Подзадача с таким ID уже существует!");
        }

        return setSubtask(subtask);
    }

    @Override
    public int addEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            throw new IllegalArgumentException("Эпик с таким ID уже существует!");
        }

        return setEpic(epic);
    }


    @Override
    public void updateTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Задача не может быть null");
        }

        if (tasks.containsKey(task.getId())) {
            setTask(task);
        } else {
            throw new IllegalArgumentException("Задача не найденна!");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            throw new IllegalArgumentException("Подзадача не может быть null");
        }

        if (subtasks.containsKey(subtask.getId())) {
            setSubtask(subtask);
        } else {
            throw new IllegalArgumentException("Подзадача не найденна!");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            throw new IllegalArgumentException("Эпик не может быть null");
        }

        if (epics.containsKey(epic.getId())) {
            setEpic(epic);
        } else {
            throw new IllegalArgumentException("Эпик не найден!");
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
        getEpicSubtasks(id).forEach(epicSubtask -> {
            subtasks.remove(epicSubtask.getId());
            historyManager.remove(epicSubtask.getId());
        });

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

    @Override
    public Collection<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortedTasks.values());
    }

    protected int setTask(Task task) {
        if (subtasks.values().stream().anyMatch(st -> Task.isExecutionTimeOverlap(task, st))) {
            throw new IllegalArgumentException(String.format(
                "Задача с таким временным диапазоном (%s - %s) уже существует!",
                task.getStartTime(), task.getEndTime()
            ));
        }

        tasks.put(task.getId(), task);
        updateTaskCounter(task);

        if (task.getStartTime() != null) {
            sortedTasks.put(task.getStartTime(), task);
        }

        return task.getId();
    }

    protected int setSubtask(Subtask subtask) {
        if (subtasks.values().stream().anyMatch(st -> Task.isExecutionTimeOverlap(subtask, st))) {
            throw new IllegalArgumentException(String.format(
                "Подзадача с таким временным диапазоном (%s - %s) уже существует!",
                subtask.getStartTime(), subtask.getEndTime()
            ));
        }

        subtasks.put(subtask.getId(), subtask);
        updateTaskCounter(subtask);

        if (subtask.getStartTime() != null) {
            sortedTasks.put(subtask.getStartTime(), subtask);
        }

        Epic epic = epics.get(subtask.getEpicId());

        if (epic != null) {
            updateEpicState(epic);
        }

        return subtask.getId();
    }

    protected int setEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateTaskCounter(epic);
        updateEpicState(epic);

        return epic.getId();
    }

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
        TaskStatus epicStatus = null;

        //for длинный, так читабильнее
        for (Subtask subtask : getEpicSubtasks(epic.getId())) {
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

            if (epicStatus != TaskStatus.IN_PROGRESS && epicStatus != subtask.getStatus()) {
                switch (subtask.getStatus()) {
                    case NEW:
                        if (epicStatus == null) {
                            epicStatus = TaskStatus.NEW;
                        } else {
                            epicStatus = TaskStatus.IN_PROGRESS;
                        }

                        break;
                    case IN_PROGRESS:
                        epicStatus = TaskStatus.IN_PROGRESS;
                        break;
                    case DONE:
                        if (epicStatus == null) {
                            epicStatus = TaskStatus.DONE;
                        } else {
                            epicStatus = TaskStatus.IN_PROGRESS;
                        }

                        break;
                }
            }
        }

        if (epicStatus != null) {
            epic.setStatus(epicStatus);
        }

        if (epic.getStartTime() != null && epic.getEndTime() != null) {
            epic.setDuration(Duration.between(epic.getStartTime(), epic.getEndTime()));
        }
    }
}
