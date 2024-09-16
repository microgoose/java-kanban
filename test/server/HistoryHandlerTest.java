package server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import model.Task;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryHandlerTest extends BaseHttpTaskServerTest {
    String urlPath = "/history/";

    @Test
    public void testGetHistoryTasks() {
        Task task = createTask();
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());

        HttpResponse<String> response = sendGet(urlPath);
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray(), "В ответе пришёл не список!");

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(taskManager.getHistory().size(), jsonArray.size(), "Некорректное количество задач в истории!");
    }
}
