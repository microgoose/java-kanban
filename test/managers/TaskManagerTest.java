package managers;

import managers.common.NotFoundException;
import managers.common.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected Integer id = 0;

    protected abstract T createTaskManager();

    protected int nextId() {
        return id++;
    }

    protected Task createTask() {
        int id = nextId();
        return new Task(id, String.format("Задача %s", id), String.format("Описание %s", id));
    }

    protected Subtask createSubtask(int id, int epicId) {
        return new Subtask(id, String.format("Подзача %s", id), String.format("Описание %s", id), epicId);
    }

    protected Subtask createSubtask(int id) {
        return new Subtask(id, String.format("Подзача %s", id), String.format("Описание %s", id));
    }

    protected Subtask createSubtask() {
        return createSubtask(nextId());
    }

    protected Epic createEpic() {
        int id = nextId();
        return new Epic(id, String.format("Эпик %s", id), String.format("Описание %s", id));
    }

    @Test
    public void testGetHistory() throws NotFoundException {
        TaskManager tm = createTaskManager();
        Task task = createTask();
        Subtask subtask = createSubtask();
        Epic epic = createEpic();

        tm.addTask(task);
        tm.addSubtask(subtask);
        tm.addEpic(epic);

        assertEquals(0, tm.getHistory().size());

        tm.getTaskById(task.getId());
        tm.getSubtaskById(subtask.getId());
        tm.getEpicById(epic.getId());

        assertEquals(3, tm.getHistory().size());
    }

    @Test
    public void testGetAllTasks() {
        TaskManager tm = createTaskManager();
        tm.addTask(createTask());
        tm.addTask(createTask());
        assertEquals(2, tm.getAllTasks().size());
    }

    @Test
    public void testGetAllSubtasks() {
        TaskManager tm = createTaskManager();
        tm.addSubtask(createSubtask());
        tm.addSubtask(createSubtask());
        assertEquals(2, tm.getAllSubtasks().size());
    }

    @Test
    public void testGetAllEpic() {
        TaskManager tm = createTaskManager();
        tm.addEpic(createEpic());
        tm.addEpic(createEpic());
        assertEquals(2, tm.getAllEpic().size());
    }

    @Test
    public void testRemoveAllTasks() {
        TaskManager tm = createTaskManager();
        tm.addTask(createTask());
        assertEquals(1, tm.getAllTasks().size());
        tm.removeAllTasks();
        assertEquals(0, tm.getAllTasks().size());
    }

    @Test
    public void testRemoveAllSubtasks() {
        TaskManager tm = createTaskManager();
        tm.addSubtask(createSubtask());
        assertEquals(1, tm.getAllSubtasks().size());
        tm.removeAllSubtasks();
        assertEquals(0, tm.getAllSubtasks().size());
    }

    @Test
    public void testRemoveAllEpics() {
        TaskManager tm = createTaskManager();
        tm.addEpic(createEpic());
        assertEquals(1, tm.getAllEpic().size());
        tm.removeAllEpics();
        assertEquals(0, tm.getAllEpic().size());
    }

    @Test
    public void testGetTaskById() throws NotFoundException {
        TaskManager tm = createTaskManager();
        Task task = createTask();
        tm.addTask(task);
        assertEquals(task, tm.getTaskById(task.getId()));
    }

    @Test
    public void testGetSubtaskById() throws NotFoundException {
        TaskManager tm = createTaskManager();
        Subtask subtask = createSubtask();
        tm.addSubtask(subtask);
        assertEquals(subtask, tm.getSubtaskById(subtask.getId()));
    }

    @Test
    public void testGetEpicById() throws NotFoundException {
        TaskManager tm = createTaskManager();
        Epic epic = createEpic();
        tm.addEpic(epic);
        assertEquals(epic, tm.getEpicById(epic.getId()));
    }

    @Test
    public void testAddTask() {
        TaskManager tm = createTaskManager();
        Task task = createTask();
        tm.addTask(task);
        assertThrows(IllegalArgumentException.class, () -> tm.addTask(task));
    }

    @Test
    public void testAddSubtask() {
        TaskManager tm = createTaskManager();
        Subtask subtask = createSubtask();
        tm.addSubtask(subtask);
        assertThrows(IllegalArgumentException.class, () -> tm.addSubtask(subtask));
    }

    @Test
    public void testAddEpic() {
        TaskManager tm = createTaskManager();
        Epic epic = createEpic();
        tm.addEpic(epic);
        assertThrows(IllegalArgumentException.class, () -> tm.addEpic(epic));
    }

    @Test
    public void testUpdateTask() throws NotFoundException {
        TaskManager tm = createTaskManager();
        Task task = createTask();
        tm.addTask(task);

        task.setDescription(task.getDescription() + " updated.");
        tm.updateTask(task);

        assertEquals(task.getDescription(), tm.getTaskById(task.getId()).getDescription());
    }

    @Test
    public void testUpdateSubtask() throws NotFoundException {
        TaskManager tm = createTaskManager();
        Subtask subtask = createSubtask();
        tm.addSubtask(subtask);

        subtask.setDescription(subtask.getDescription() + " updated.");
        tm.updateSubtask(subtask);

        assertEquals(subtask.getDescription(), tm.getSubtaskById(subtask.getId()).getDescription());
    }

    @Test
    public void testUpdateEpic() throws NotFoundException {
        TaskManager tm = createTaskManager();
        Epic epic = createEpic();
        tm.addEpic(epic);

        epic.setDescription(epic.getDescription() + " updated.");
        tm.updateEpic(epic);

        assertEquals(epic.getDescription(), tm.getEpicById(epic.getId()).getDescription());
    }

    @Test
    public void testRemoveTaskById() throws NotFoundException {
        TaskManager tm = createTaskManager();
        Task task = createTask();
        tm.addTask(task);
        assertEquals(1, tm.getAllTasks().size());
        tm.removeTaskById(task.getId());
        assertEquals(0, tm.getAllTasks().size());
    }

    @Test
    public void testRemoveSubtaskById() throws NotFoundException {
        TaskManager tm = createTaskManager();
        Subtask subtask = createSubtask();
        tm.addSubtask(subtask);
        assertEquals(1, tm.getAllSubtasks().size());
        tm.removeSubtaskById(subtask.getId());
        assertEquals(0, tm.getAllSubtasks().size());
    }

    @Test
    public void testRemoveEpicById() throws NotFoundException {
        TaskManager tm = createTaskManager();
        Epic epic = createEpic();
        tm.addEpic(epic);
        assertEquals(1, tm.getAllEpic().size());
        tm.removeEpicById(epic.getId());
        assertEquals(0, tm.getAllEpic().size());
    }

    @Test
    public void testGetEpicSubtasks() {
        TaskManager tm = createTaskManager();
        Epic epic = createEpic();
        Subtask subtask = createSubtask(nextId(), epic.getId());

        tm.addSubtask(subtask);
        tm.addEpic(epic);

        assertEquals(1, tm.getEpicSubtasks(epic.getId()).size());
    }

    @Test
    public void testGetPrioritizedTasks() {
        TaskManager tm = createTaskManager();
        Task task = createTask();
        Task task1 = createTask();
        Subtask subtask = createSubtask();
        Subtask subtask1 = createSubtask();

        task.setStartTime(LocalDateTime.MAX);
        task1.setStartTime(LocalDateTime.MIN);
        subtask.setStartTime(LocalDateTime.MAX);
        subtask.setStartTime(LocalDateTime.MIN);

        tm.addTask(task1);
        tm.addTask(task);
        tm.addSubtask(subtask);
        tm.addSubtask(subtask1);

        List<Task> sortedTasks = new ArrayList<>(tm.getPrioritizedTasks());

        for (int i = 1; i < sortedTasks.size(); i++) {
            LocalDateTime prevStartTime = sortedTasks.get(i - 1).getStartTime();
            LocalDateTime currStartTime = sortedTasks.get(i).getStartTime();

            assertTrue(prevStartTime.isBefore(currStartTime), "Список неккоректно отсортирован по времени начала задач");
        }
    }
}