import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SubtaskEntityTest {
    String serializedSubtaskWithoutEpic = "1,SUBTASK,SUBTASK,NEW,SUBTASK DESC,";
    String serializedSubtaskWithEpic = "1,SUBTASK,SUBTASK,NEW,SUBTASK DESC,1";

    @Test
    public void shouldCorrectlySerialize() {
        Subtask subtaskWithEpic = new Subtask(1, "SUBTASK", "SUBTASK DESC", 1);
        Subtask subtaskWithoutEpic = new Subtask(1, "SUBTASK", "SUBTASK DESC");
        assertEquals(serializedSubtaskWithoutEpic, subtaskWithoutEpic.toString());
        assertEquals(serializedSubtaskWithEpic, subtaskWithEpic.toString());
    }

    @Test
    public void shouldCorrectlyDeserialize() {
        Subtask subtaskWithEpic = Subtask.fromString(serializedSubtaskWithEpic);
        Subtask subtaskWithoutEpic = Subtask.fromString(serializedSubtaskWithoutEpic);

        assertEquals(1, subtaskWithEpic.getId());
        assertEquals(TaskStatus.NEW, subtaskWithEpic.getStatus());
        assertEquals("SUBTASK", subtaskWithEpic.getName());
        assertEquals("SUBTASK DESC", subtaskWithEpic.getDescription());
        assertEquals(1, subtaskWithEpic.getEpicId());

        assertEquals(1, subtaskWithoutEpic.getId());
        assertEquals(TaskStatus.NEW, subtaskWithoutEpic.getStatus());
        assertEquals("SUBTASK", subtaskWithoutEpic.getName());
        assertEquals("SUBTASK DESC", subtaskWithoutEpic.getDescription());
        assertNull(subtaskWithoutEpic.getEpicId());
    }
}
