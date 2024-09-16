package server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import model.Task;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TasksHandlerTest extends BaseHttpTaskServerTest {
    String urlPath = "/tasks/";

    @Test
    public void testGetTask() {
        Task task = createTask();
        taskManager.addTask(task);

        HttpResponse<String> response = sendGet(urlPath + task.getId());
        Task responseTask = gson.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode());
        assertEquals(task, responseTask, "Задачи не совпадают!");

        response = sendGet(urlPath + nextId());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testGetTasks() {
        taskManager.addTask(createTask());
        taskManager.addTask(createTask());

        HttpResponse<String> response = sendGet(urlPath);
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray(), "В ответе пришёл не список!");

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(taskManager.getAllTasks().size(), jsonArray.size(), "Некорректное количество задач!");
    }

    @Test
    public void testAddTask() {
        int startTaskCount = taskManager.getAllTasks().size();
        Task task = createTask();

        task.setId(null);

        HttpResponse<String> response = sendPost(urlPath, gson.toJson(task));
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = new ArrayList<>(taskManager.getAllTasks());
        assertEquals(startTaskCount + 1, tasksFromManager.size(), "Некорректное количество задач");

        Task foundedTask = tasksFromManager.getLast();
        assertEquals(task.getName(), foundedTask.getName(), "Добавленная задача не совпадает с отправленной!");

        response = sendPost(urlPath, gson.toJson(task));
        assertEquals(406, response.statusCode());
    }

    @Test
    public void testUpdateTask() {
        Task task = createTask();
        taskManager.addTask(task);

        task.setName("Updated " + task.getName());

        HttpResponse<String> response = sendPost(urlPath, gson.toJson(task));
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testDeleteTask() {
        Task task = createTask();
        taskManager.addTask(task);

        HttpResponse<String> response = sendDelete(urlPath + task.getId());
        assertEquals(200, response.statusCode());
    }
}
