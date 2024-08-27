import common.Config;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class TaskEntityTest {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Config.DATE_TIME_FORMAT);
    LocalDateTime startTime = LocalDateTime.of(1, 1, 1, 1, 1);
    Duration duration = Duration.ofMinutes(78);

    String serializedTask = "1,TASK,TASK,NEW,TASK DESC,null,null,null";
    String serializedTaskWithTime =
            String.format("1,TASK,TASK,NEW,TASK DESC,%s,%s,null", formatter.format(startTime), duration.toMinutes());

    @Test
    public void shouldCorrectlySerialize() {
        Task task = new Task(1, "TASK", "TASK DESC");
        assertEquals(serializedTask, task.toString());

        task.setStartTime(startTime);
        task.setDuration(duration);
        assertEquals(serializedTaskWithTime, task.toString());
    }

    @Test
    public void shouldCorrectlyDeserialize() {
        Task task = Task.fromString(serializedTask);
        assertEquals(1, task.getId());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals("TASK", task.getName());
        assertEquals("TASK DESC", task.getDescription());

        Task taskWithTime = Task.fromString(serializedTaskWithTime);
        assertEquals(startTime, taskWithTime.getStartTime());
        assertEquals(duration, taskWithTime.getDuration());
    }

    @Test
    public void shouldCorrectlyCalculateEndTime() {
        Task taskWithTime = Task.fromString(serializedTaskWithTime);
        assertEquals(startTime.plus(duration), taskWithTime.getEndTime());
    }

    @Test
    public void shouldDetectTimeIntersection() {
        Task taskWithTime = Task.fromString(serializedTaskWithTime);
        Task taskWithTime1 = Task.fromString(serializedTaskWithTime);

        Task taskWithTime2 = Task.fromString(serializedTaskWithTime);
        taskWithTime2.setStartTime(taskWithTime1.getEndTime().plus(Duration.ofMinutes(10)));
        taskWithTime2.setDuration(Duration.ofMinutes(10));

        assertTrue(Task.isExecutionTimeOverlap(taskWithTime, taskWithTime1));
        assertFalse(Task.isExecutionTimeOverlap(taskWithTime, taskWithTime2));
    }
}
