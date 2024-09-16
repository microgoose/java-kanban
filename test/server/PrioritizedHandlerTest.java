package server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import model.Task;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrioritizedHandlerTest extends BaseHttpTaskServerTest {
    String urlPath = "/prioritized/";

    @Test
    public void testGetPrioritizedTasks() {
        Task firstTask = createTask();
        Task secondTask = createTask();

        firstTask.setStartTime(LocalDateTime.now());
        secondTask.setStartTime(LocalDateTime.now().plus(Duration.ofHours(1)));
        firstTask.setDuration(Duration.ofMinutes(5));
        secondTask.setDuration(Duration.ofMinutes(5));

        taskManager.addTask(firstTask);
        taskManager.addTask(secondTask);

        HttpResponse<String> response = sendGet(urlPath);
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray(), "В ответе пришёл не список!");

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(taskManager.getPrioritizedTasks().size(), jsonArray.size(),
                "Некорректное количество задач в приоритизированном списке!");
    }
}
