import managers.common.HistoryManager;
import managers.common.TaskManager;
import managers.InMemoryHistoryManager;
import managers.InMemoryTaskManager;
import managers.Managers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {
    @Test
    public void shouldReturnInitializedManagers() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(taskManager, "Менеджер задач не создан");
        assertNotNull(historyManager, "Менеджер истоии задач не создан");
        assertInstanceOf(InMemoryTaskManager.class, taskManager, "Неккоректный менеджер задач по умолчанию");
        assertInstanceOf(InMemoryHistoryManager.class, historyManager, "Неккоректный менеджер истории по умолчанию");
    }
}