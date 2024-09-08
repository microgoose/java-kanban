import managers.InMemoryHistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    Integer id = 0;

    private int nextId() {
        return id++;
    }

    @Test
    public void mustAddToHistory() {
        Task task = new Task(nextId(), "Задача", "Описание");
        Epic epic = new Epic(nextId(), "Эпик", "Описание");
        Subtask subtask = new Subtask(nextId(), "Подзадача", "Описание", epic.getId());

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        assertTrue(historyManager.getHistory().contains(task), "Отсутствует задача");
        assertTrue(historyManager.getHistory().contains(epic), "Отсутствует эпик");
        assertTrue(historyManager.getHistory().contains(subtask), "Отсутствует подзадача");
    }

    @Test
    public void mustRemoveFromHistory() {
        Task task = new Task(nextId(), "Задача", "Описание");

        historyManager.add(task);
        historyManager.remove(task.getId());

        assertFalse(historyManager.getHistory().contains(task), "Присутствует задача");
    }

    @Test
    public void mustRemoveAllFromHistory() {
        Task task = new Task(nextId(), "Задача", "Описание");

        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        for (Task value : history) {
            historyManager.remove(value.getId());
        }

        assertFalse(historyManager.getHistory().contains(task), "История не пуста!");
    }

    @Test
    public void mustContainOnlyUnique() {
        int index = nextId();
        Task task = new Task(index, "Задача", "Описание", TaskStatus.NEW);
        historyManager.add(task);
        historyManager.add(task);

        int numberOfSamples = 0;
        List<Task> history = historyManager.getHistory();

        for (Task value : history) {
            if (value.getId() == index) {
                numberOfSamples++;
            }
        }

        assertFalse(numberOfSamples != 1, "Задача имеет дубликат в истории");
    }
}