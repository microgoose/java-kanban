package server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubtasksHandlerTest extends BaseHttpTaskServerTest {
    String urlPath = "/subtasks/";
    
    @Test
    public void testGetSubtask() {
        Subtask subtask = createSubtask();
        taskManager.addSubtask(subtask);

        HttpResponse<String> response = sendGet(urlPath + subtask.getId());
        Subtask responseSubtask = gson.fromJson(response.body(), Subtask.class);

        assertEquals(200, response.statusCode());
        assertEquals(subtask, responseSubtask, "подзадачи не совпадают!");

        response = sendGet(urlPath + nextId());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testGetSubtasks() {
        taskManager.addSubtask(createSubtask());
        taskManager.addSubtask(createSubtask());

        HttpResponse<String> response = sendGet(urlPath);
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray(), "В ответе пришёл не список!");

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(taskManager.getAllSubtasks().size(), jsonArray.size(), "Некорректное количество подзадач!");
    }

    @Test
    public void testAddSubtask() {
        int startTaskCount = taskManager.getAllSubtasks().size();
        Subtask subtask = createSubtask();

        subtask.setId(null);

        HttpResponse<String> response = sendPost(urlPath, gson.toJson(subtask));
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = new ArrayList<>(taskManager.getAllSubtasks());
        assertEquals(startTaskCount + 1, tasksFromManager.size(), "Некорректное количество подзадач");

        Task foundedsubtask = tasksFromManager.getLast();
        assertEquals(subtask.getName(), foundedsubtask.getName(), "Добавленная подзадача не совпадает с отправленной!");

        response = sendPost(urlPath, gson.toJson(subtask));
        assertEquals(406, response.statusCode());
    }

    @Test
    public void testUpdateSubtask() {
        Subtask subtask = createSubtask();
        taskManager.addSubtask(subtask);

        subtask.setName("Updated " + subtask.getName());

        HttpResponse<String> response = sendPost(urlPath, gson.toJson(subtask));
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testDeleteSubtask() {
        Subtask subtask = createSubtask();
        taskManager.addSubtask(subtask);

        HttpResponse<String> response = sendDelete(urlPath + subtask.getId());
        assertEquals(200, response.statusCode());
    }
}
