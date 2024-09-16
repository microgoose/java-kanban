package server.handlers;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import managers.common.NotFoundException;
import managers.common.TaskManager;
import model.Epic;
import model.Subtask;
import server.common.BaseTaskManagerHandler;
import server.common.ParseTaskURLParam;

import java.util.Collection;
import java.util.Optional;

public class EpicsHandler extends BaseTaskManagerHandler {
    protected Integer epicIdParam;

    public EpicsHandler(TaskManager taskManager) {
        super(taskManager);
        epicIdParam = null;
    }

    @Override
    protected void handleGet(HttpExchange exchange) {
        setupParams(exchange);

        if (epicIdParam == null) {
            Collection<Epic> epics = taskManager.getAllEpic();
            sendText(exchange, gson.toJson(epics));
            return;
        }

        try {
            String requestURI = exchange.getRequestURI().getPath();

            if (requestURI.contains("/subtasks")) {
                Collection<Subtask> subtasks = taskManager.getEpicSubtasks(epicIdParam);
                sendText(exchange, gson.toJson(subtasks));
                return;
            }

            Epic epic = taskManager.getEpicById(epicIdParam);
            sendText(exchange, gson.toJson(epic));
        } catch (NotFoundException ex) {
            sendNotFound(exchange, "Не найден эпик с ID: " + epicIdParam);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange) {
        String body = readPostBody(exchange);
        Optional<Epic> epicOptional = getTask(Epic.class, body);

        if (epicOptional.isEmpty()) {
            sendBadRequest(exchange, "Не удалось получить эпик. Проверьте тело запроса.");
            return;
        }

        Epic epic = epicOptional.get();
        Integer epicId = epic.getId();

        if (epicId == null) {
            try {
                taskManager.addEpic(epic);
                sendText(exchange, gson.toJson(epic));
            } catch (IllegalArgumentException ex) {
                sendHasInteractions(exchange, ex.getMessage());
            }

            return;
        }

        try {
            taskManager.updateEpic(epic);
            sendText(exchange, gson.toJson(epic));
        } catch (NotFoundException nfe) {
            sendNotFound(exchange, "Не удалось найти эпик с таким ID: " + epicId);
        } catch (IllegalArgumentException ex) {
            sendHasInteractions(exchange, ex.getMessage());
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange) {
        setupParams(exchange);

        if (epicIdParam == null) {
            sendNotFound(exchange, "Не передан ID эпика.");
            return;
        }

        try {
            taskManager.removeEpicById(epicIdParam);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("epicId", epicIdParam);
            sendText(exchange, gson.toJson(jsonObject));
        } catch (NotFoundException ex) {
            sendNotFound(exchange, "Не найден эпик для удаления с ID: " + epicIdParam);
        }
    }

    private void setupParams(HttpExchange exchange) {
        String basePath = "epics";
        String path = exchange.getRequestURI().getPath();
        epicIdParam = ParseTaskURLParam.parse(path, basePath);
    }
}
