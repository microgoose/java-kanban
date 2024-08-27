import common.Config;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SubtaskEntityTest {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Config.DATE_TIME_FORMAT);
    LocalDateTime startTime = LocalDateTime.of(1, 1, 1, 1, 1);
    Duration duration = Duration.ofMinutes(78);

    String serializedSubtaskWithEpic = "1,SUBTASK,SUBTASK,NEW,SUBTASK DESC,null,null,1";
    String serializedSubtaskWithoutEpic = "1,SUBTASK,SUBTASK,NEW,SUBTASK DESC,null,null,null";

    String serializedSubtaskWithEpicAndTime =
            String.format("1,SUBTASK,SUBTASK,NEW,SUBTASK DESC,%s,%s,1",
                    formatter.format(startTime), duration.toMinutes());
    String serializedSubtaskWithoutEpicAndTime =
            String.format("1,SUBTASK,SUBTASK,NEW,SUBTASK DESC,%s,%s,null",
                    formatter.format(startTime), duration.toMinutes());

    @Test
    public void shouldCorrectlySerialize() {
        Subtask subtaskWithEpic = new Subtask(1, "SUBTASK", "SUBTASK DESC", 1);
        Subtask subtaskWithoutEpic = new Subtask(1, "SUBTASK", "SUBTASK DESC");
        Subtask subtaskWithEpicAndTime =
                new Subtask(1, "SUBTASK", "SUBTASK DESC", TaskStatus.NEW, startTime, duration, 1);
        Subtask subtaskWithoutEpicAndTime =
                new Subtask(1, "SUBTASK", "SUBTASK DESC", TaskStatus.NEW, startTime, duration);

        assertEquals(serializedSubtaskWithoutEpic, subtaskWithoutEpic.toString());
        assertEquals(serializedSubtaskWithEpic, subtaskWithEpic.toString());
        assertEquals(serializedSubtaskWithEpicAndTime, subtaskWithEpicAndTime.toString());
        assertEquals(serializedSubtaskWithoutEpicAndTime, subtaskWithoutEpicAndTime.toString());
    }

    @Test
    public void shouldCorrectlyDeserialize() {
        Subtask subtaskWithEpic = Subtask.fromString(serializedSubtaskWithEpic);
        assertEquals(1, subtaskWithEpic.getId());
        assertEquals(TaskStatus.NEW, subtaskWithEpic.getStatus());
        assertEquals("SUBTASK", subtaskWithEpic.getName());
        assertEquals("SUBTASK DESC", subtaskWithEpic.getDescription());
        assertEquals(1, subtaskWithEpic.getEpicId());

        Subtask subtaskWithoutEpic = Subtask.fromString(serializedSubtaskWithoutEpic);
        assertEquals(1, subtaskWithoutEpic.getId());
        assertEquals(TaskStatus.NEW, subtaskWithoutEpic.getStatus());
        assertEquals("SUBTASK", subtaskWithoutEpic.getName());
        assertEquals("SUBTASK DESC", subtaskWithoutEpic.getDescription());
        assertNull(subtaskWithoutEpic.getEpicId());

        Subtask subtaskWithEpicAndTime = Subtask.fromString(serializedSubtaskWithEpicAndTime);
        assertEquals(startTime, subtaskWithEpicAndTime.getStartTime());
        assertEquals(duration, subtaskWithEpicAndTime.getDuration());
        assertEquals(1, subtaskWithEpicAndTime.getEpicId());

        Subtask subtaskWithoutEpicAndTime = Subtask.fromString(serializedSubtaskWithoutEpicAndTime);
        assertEquals(startTime, subtaskWithoutEpicAndTime.getStartTime());
        assertEquals(duration, subtaskWithoutEpicAndTime.getDuration());
        assertNull(subtaskWithoutEpicAndTime.getEpicId());
    }
}
