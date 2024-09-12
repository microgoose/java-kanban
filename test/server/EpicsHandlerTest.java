package server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EpicsHandlerTest extends BaseHttpTaskServerTest {
    String urlPath = "/epics/";
    
    @Test
    public void testGetEpic() {
        Epic epic = createEpic();
        taskManager.addEpic(epic);

        HttpResponse<String> response = sendGet(urlPath + epic.getId());
        Epic responseEpic = gson.fromJson(response.body(), Epic.class);

        assertEquals(200, response.statusCode());
        assertEquals(epic, responseEpic, "Эпики не совпадают!");

        response = sendGet(urlPath + nextId());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testGetEpicSubtasks() {
        Epic epic = createEpic();
        Subtask subtask = createSubtask();
        Subtask subtask1 = createSubtask();

        subtask.setEpicId(epic.getId());
        subtask1.setEpicId(epic.getId());

        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
        taskManager.addSubtask(subtask1);

        HttpResponse<String> response = sendGet(urlPath + epic.getId() + "/subtasks");
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray(), "В ответе пришёл не список!");

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(taskManager.getEpicSubtasks(epic.getId()).size(), jsonArray.size(), "Некорректное количество подзадач эпика!");

        response = sendGet(urlPath + nextId() + "/subtasks");
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testGetEpics() {
        taskManager.addEpic(createEpic());
        taskManager.addEpic(createEpic());

        HttpResponse<String> response = sendGet(urlPath);
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray(), "В ответе пришёл не список!");

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertEquals(taskManager.getAllEpic().size(), jsonArray.size(), "Некорректное количество эпиков!");
    }

    @Test
    public void testAddEpic() {
        int startTaskCount = taskManager.getAllEpic().size();
        Epic epic = createEpic();

        epic.setId(null);

        HttpResponse<String> response = sendPost(urlPath, gson.toJson(epic));
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = new ArrayList<>(taskManager.getAllEpic());
        assertEquals(startTaskCount + 1, tasksFromManager.size(), "Некорректное количество эпиков");

        Task foundedEpic = tasksFromManager.getLast();
        assertEquals(epic.getName(), foundedEpic.getName(), "Добавленный эпик не совпадает с отправленным!");
    }

    @Test
    public void testUpdateEpic() {
        Epic epic = createEpic();
        taskManager.addEpic(epic);

        epic.setName("Updated " + epic.getName());

        HttpResponse<String> response = sendPost(urlPath, gson.toJson(epic));
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testDeleteEpic() {
        Epic epic = createEpic();
        taskManager.addEpic(epic);

        HttpResponse<String> response = sendDelete(urlPath + epic.getId());
        assertEquals(200, response.statusCode());
    }
}
