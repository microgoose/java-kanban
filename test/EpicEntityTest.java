import model.Epic;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicEntityTest {
    String serializedEpic = "1,EPIC,EPIC,NEW,EPIC DESC,";

    @Test
    public void shouldCorrectlySerialize() {
        Epic epic = new Epic(1, "EPIC", "EPIC DESC");
        assertEquals(serializedEpic, epic.toString());
    }

    @Test
    public void shouldCorrectlyDeserialize() {
        Epic epic = Epic.fromString(serializedEpic);
        assertEquals(1, epic.getId());
        assertEquals(TaskStatus.NEW, epic.getStatus());
        assertEquals("EPIC", epic.getName());
        assertEquals("EPIC DESC", epic.getDescription());
    }
}
