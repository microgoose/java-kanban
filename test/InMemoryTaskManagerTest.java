import manager.InMemoryTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    InMemoryTaskManager tm = new InMemoryTaskManager();
    Integer id = 0;

    private int nextId() {
        return id++;
    }
    
    @Test
    public void shouldThrowErrorIfTaskAlreadyExist() {
        Task task = new Task(nextId(), "Задача", "Описание");
        Epic epic = new Epic(nextId(), "Эпик", "Описание");
        Subtask subtask = new Subtask(nextId(), "Подзадача", "Описание", epic.getId());

        assertThrows(IllegalArgumentException.class, () -> {
            tm.addTask(task);
            tm.addTask(task);
            tm.addSubtask(subtask);
            tm.addSubtask(subtask);
            tm.addEpic(epic);
            tm.addEpic(epic);
        });
    }

    @Test
    public void taskShouldBeSameAfterAdd() {
        Task task = new Task(nextId(), "Задача", "Описание");
        Epic epic = new Epic(nextId(), "Эпик", "Описание");
        Subtask subtask = new Subtask(nextId(), "Подзадача", "Описание", epic.getId());

        int taskId = tm.addTask(task);
        int subTaskId = tm.addSubtask(subtask);
        int epicId = tm.addEpic(epic);

        assertEquals(task, tm.getTaskById(taskId), "Задачи отличаются");
        assertEquals(subtask, tm.getSubtaskById(subTaskId), "Подзадачи отличаются");
        assertEquals(epic, tm.getEpicById(epicId), "Эпики отличаются");
    }

    @Test
    public void mustReturnSameTaskById() {
        Task task = new Task(nextId(), "Задача", "Описание");
        Epic epic = new Epic(nextId(), "Эпик", "Описание");
        Subtask subtask = new Subtask(nextId(), "Подзадача", "Описание", epic.getId());

        int taskId = tm.addTask(task);
        int subTaskId = tm.addSubtask(subtask);
        int epicId = tm.addEpic(epic);

        assertNotNull(task, "Задача не найденна");
        assertEquals(task, tm.getTaskById(taskId), "Задачи не совпадают");
        assertNotNull(subtask, "Подзадача не найденна");
        assertEquals(subtask, tm.getSubtaskById(subTaskId), "Подзадачи не совпадают");
        assertNotNull(epic, "Эпик не найденн");
        assertEquals(epic, tm.getEpicById(epicId), "Эпики не совпадают");
    }

    @Test
    public void mustContainCorrectNumberOfTasks() {
        int tasksCurrentSize = tm.getAllTasks().size();
        int subtasksCurrentSize = tm.getAllSubtasks().size();
        int epicsCurrentSize = tm.getAllEpic().size();

        Task task = new Task(nextId(), "Задача", "Описание");
        Epic epic = new Epic(nextId(), "Эпик", "Описание");
        Subtask subtask = new Subtask(nextId(), "Подзадача", "Описание", epic.getId());

        tm.addTask(task);
        tm.addSubtask(subtask);
        tm.addEpic(epic);

        assertEquals(tasksCurrentSize + 1, tm.getAllTasks().size(), "Неверное количество задач");
        assertEquals(subtasksCurrentSize + 1, tm.getAllSubtasks().size(), "Неверное количество подзадач");
        assertEquals(epicsCurrentSize + 1, tm.getAllEpic().size(), "Неверное количество эпиков");
    }

    @Test
    public void mustRemoveAll() {
        Task task = new Task(nextId(), "Задача", "Описание");
        Epic epic = new Epic(nextId(), "Эпик", "Описание");
        Subtask subtask = new Subtask(nextId(), "Подзадача", "Описание", epic.getId());

        tm.addTask(task);
        tm.addSubtask(subtask);
        tm.addEpic(epic);

        tm.getTaskById(task.getId());
        tm.getSubtaskById(subtask.getId());
        tm.getEpicById(epic.getId());

        tm.removeAllTasks();
        tm.removeAllSubtasks();
        tm.removeAllEpics();

        assertEquals(tm.getAllTasks().size(), 0, "После удаления остались задачи");
        assertEquals(tm.getAllSubtasks().size(), 0, "После удаления остались подзадачи");
        assertEquals(tm.getAllEpic().size(), 0, "После удаления остались эпики");
        assertEquals(tm.getHistory().size(), 0, "После очищения менеджера в истории остались задачи");
    }

    @Test
    public void mustUpdateEpicStatusAfterSubtaskAdded() {
        Epic epic = new Epic(nextId(), "Эпик", "Описание");
        Subtask subtask = new Subtask(nextId(), "Подзадача", "Описание", TaskStatus.IN_PROGRESS, epic.getId());
        Subtask subtask1 = new Subtask(nextId(), "Подзадача", "Описание", TaskStatus.DONE, epic.getId());

        tm.addEpic(epic);
        tm.addSubtask(subtask);
        tm.addSubtask(subtask1);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Эпик имеет некоректный статус");
    }

    @Test
    public void shouldCorrectlyCalculateEpicEndTime() {
        Epic epic = new Epic(nextId(), "Эпик", "Описание");

        LocalDateTime startTime = LocalDateTime.of(1, 1, 1, 1, 0);
        Duration duration10min = Duration.ofMinutes(10);
        LocalDateTime startTimePlus20Minutes = LocalDateTime.of(1, 1, 1, 1, 20);
        Duration duration15min = Duration.ofMinutes(15);
        Duration totalDuration = Duration.ofMinutes(10 + 10 + 15);
        LocalDateTime endTime = startTime.plus(totalDuration);

        Subtask subtaskEarly = new Subtask(nextId(), "Подзадача", "Описание", epic.getId());
        subtaskEarly.setStartTime(startTime);
        subtaskEarly.setDuration(duration10min);

        Subtask subtaskLate = new Subtask(nextId(), "Подзадача", "Описание", epic.getId());
        subtaskLate.setStartTime(startTimePlus20Minutes);
        subtaskLate.setDuration(duration15min);

        tm.addEpic(epic);
        tm.addSubtask(subtaskEarly);
        tm.addSubtask(subtaskLate);

        assertEquals(startTime, tm.getEpicById(epic.getId()).getStartTime(), "Неккоректное время начала эпика");
        assertEquals(totalDuration, tm.getEpicById(epic.getId()).getDuration(), "Неккоректная продолжительность эпика");
        assertEquals(endTime, tm.getEpicById(epic.getId()).getEndTime(), "Неккоректное время конца эпика");
    }

    @Test
    public void shouldSuccessfulAddTaskWithNullTime() {
        Task task = new Task(nextId(), "Задача", "Описание");
        Task task1 = new Task(nextId(), "Задача", "Описание");

        tm.addTask(task);
        tm.addTask(task1);
    }

    @Test
    public void shouldThrowExceptionWhenTaskTimeIntersection() {
        LocalDateTime startTime = LocalDateTime.of(1, 1, 1, 1, 1);
        Duration duration10min = Duration.ofMinutes(10);

        Subtask subtask = new Subtask(nextId(), "Подзадача", "Описание");
        subtask.setStartTime(startTime);
        subtask.setDuration(duration10min);

        Subtask subtask1 = new Subtask(nextId(), "Подзадача", "Описание");
        subtask1.setStartTime(startTime.plus(duration10min));
        subtask1.setDuration(duration10min);

        assertThrows(IllegalArgumentException.class, () -> {
            tm.addSubtask(subtask);
            tm.addSubtask(subtask1);
        }, "Отсутствует ошибка о пересечении интервалов времени выполнения задач");
    }

    @Test
    public void shouldReturnSortedByTimePriority() {
        Duration duration10min = Duration.ofMinutes(10);
        Duration startTimeStep = Duration.ofMinutes(20);
        LocalDateTime startTime = LocalDateTime.of(1, 1, 1, 1, 0);

        Subtask subtaskWithoutTime = new Subtask(nextId(), "Подзадача", "Описание");

        Task task = new Task(nextId(), "Задача", "Описание");
        task.setStartTime(startTime);
        task.setDuration(duration10min);

        startTime = startTime.plus(startTimeStep);
        Task task1 = new Task(nextId(), "Задача", "Описание");
        task1.setStartTime(startTime);
        task1.setDuration(duration10min);

        startTime = startTime.plus(startTimeStep);
        Subtask subtask = new Subtask(nextId(), "Подзадача", "Описание");
        subtask.setStartTime(startTime);
        subtask.setDuration(duration10min);

        startTime = startTime.plus(startTimeStep);
        Subtask subtask2 = new Subtask(nextId(), "Подзадача", "Описание");
        subtask2.setStartTime(startTime);
        subtask2.setDuration(duration10min);

        tm.addTask(subtaskWithoutTime);
        tm.addTask(task);
        tm.addSubtask(subtask2);
        tm.addTask(task1);
        tm.addSubtask(subtask);

        List<Task> sortedTasks = tm.getPrioritizedTasks();

        if (sortedTasks.isEmpty() || sortedTasks.size() == 1)
            return;

        for (int i = 1; i < sortedTasks.size(); i++) {
            LocalDateTime prevStartTime = sortedTasks.get(i - 1).getStartTime();
            LocalDateTime currStartTime = sortedTasks.get(i).getStartTime();

            assertTrue(prevStartTime.isBefore(currStartTime), "Список неккоректно отсортирован по времени начала задач");
        }
    }
}