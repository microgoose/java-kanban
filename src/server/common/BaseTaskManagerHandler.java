package server.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import common.DurationTimeAdapter;
import common.LocalDateTimeAdapter;
import managers.common.TaskManager;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public abstract class BaseTaskManagerHandler extends BaseHttpHandler implements HttpHandler {
    public final String responseErrorKey = "error";

    protected Gson gson;

    public BaseTaskManagerHandler(TaskManager taskManager) {
        super(taskManager);
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTimeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) {
        String method = exchange.getRequestMethod();

        try {
            switch (method.toUpperCase()) {
                case "GET":
                    handleGet(exchange);
                    return;
                case "POST":
                    handlePost(exchange);
                    return;
                case "DELETE":
                    handleDelete(exchange);
                    return;
            }

            sendNotFound(exchange, "Передан некорректный метод запроса!");
        } catch (Exception ex) {
            sendInternalError(exchange, ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    protected void handleGet(HttpExchange exchange) {

    }

    protected void handlePost(HttpExchange exchange) {

    }

    protected void handleDelete(HttpExchange exchange) {

    }

    @Override
    protected void sendInternalError(HttpExchange h, String text) {
        send(h, toErrorJson(text), 500);
    }

    @Override
    protected void sendBadRequest(HttpExchange h, String text) {
        send(h, toErrorJson(text), 400);
    }

    @Override
    protected void sendNotFound(HttpExchange h, String text) {
        send(h, toErrorJson(text), 404);
    }

    @Override
    protected void sendHasInteractions(HttpExchange h, String text) {
        send(h, toErrorJson(text), 406);
    }

    protected <T extends Task> Optional<T> getTask(Class<T> type, String json) {
        if (type == null || json == null || json.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(gson.fromJson(json, type));
    }

    private String toErrorJson(String text) {
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty(responseErrorKey, text);
        return gson.toJson(responseJson);
    }
}
