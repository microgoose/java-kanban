import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskEntityTest {
    String serializedTask = "1,TASK,TASK,NEW,TASK DESC,";

    @Test
    public void shouldCorrectlySerialize() {
        Task task = new Task(1, "TASK", "TASK DESC");
        assertEquals(serializedTask, task.toString());
    }

    @Test
    public void shouldCorrectlyDeserialize() {
        Task task = Task.fromString(serializedTask);
        assertEquals(1, task.getId());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals("TASK", task.getName());
        assertEquals("TASK DESC", task.getDescription());
    }
}
