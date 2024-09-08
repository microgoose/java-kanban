import managers.common.TaskManager;
import managers.InMemoryTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    public void mustUpdateEpicStatusAfterSubtaskAdded() {
        TaskManager tm = createTaskManager();
        Epic epic = new Epic(nextId(), "Эпик", "Описание");
        Subtask subtask = new Subtask(nextId(), "Подзадача", "Описание", epic.getId());
        Subtask subtask1 = new Subtask(nextId(), "Подзадача", "Описание", epic.getId());

        tm.addEpic(epic);
        tm.addSubtask(subtask);
        tm.addSubtask(subtask1);
        assertEquals(TaskStatus.NEW, epic.getStatus());

        subtask.setStatus(TaskStatus.IN_PROGRESS);
        tm.updateSubtask(subtask);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());

        subtask.setStatus(TaskStatus.DONE);
        tm.updateSubtask(subtask);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());

        subtask1.setStatus(TaskStatus.DONE);
        tm.updateSubtask(subtask1);
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    public void shouldCorrectlyCalculateEpicEndTime() {
        TaskManager tm = createTaskManager();

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
        TaskManager tm = createTaskManager();
        Task task = new Task(nextId(), "Задача", "Описание");
        Task task1 = new Task(nextId(), "Задача", "Описание");

        tm.addTask(task);
        tm.addTask(task1);
    }

    @Test
    public void shouldThrowExceptionWhenTaskTimeIntersection() {
        TaskManager tm = createTaskManager();

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
}