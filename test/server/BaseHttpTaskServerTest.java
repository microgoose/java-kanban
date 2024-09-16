package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import common.DurationTimeAdapter;
import common.LocalDateTimeAdapter;
import managers.Managers;
import managers.common.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseHttpTaskServerTest {
    protected String baseUrl;
    protected TaskManager taskManager;
    protected HttpTaskServer server;
    protected Gson gson;

    protected Integer id = 0;

    public BaseHttpTaskServerTest() {
        this.baseUrl = "http://localhost:8080";
        this.taskManager = Managers.getDefault();
        this.server = new HttpTaskServer();
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTimeAdapter())
                .create();
    }

    @BeforeEach
    public void startServer() {
        server.start(taskManager);
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    protected HttpResponse<String> sendGet(String urlPath) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create(baseUrl + urlPath);
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected HttpResponse<String> sendPost(String urlPath, String json) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create(baseUrl + urlPath);
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected HttpResponse<String> sendDelete(String urlPath) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create(baseUrl + urlPath);
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected int nextId() {
        return id++;
    }

    protected Task createTask(Integer id) {
        return new Task(id, "Task", "Task desc",
                TaskStatus.NEW, LocalDateTime.now().plus(Duration.ofHours(id)), Duration.ofMinutes(5));
    }

    protected Task createTask() {
        return createTask(nextId());
    }

    protected Subtask createSubtask(Integer id) {
        return new Subtask(id, "Subtask", "Subtask desc",
                TaskStatus.NEW, LocalDateTime.now().plus(Duration.ofHours(id)), Duration.ofMinutes(5));
    }

    protected Subtask createSubtask() {
        return createSubtask(nextId());
    }

    protected Epic createEpic(Integer id) {
        return new Epic(id, "Epic", "Epic desc");
    }

    protected Epic createEpic() {
        return createEpic(nextId());
    }
}
