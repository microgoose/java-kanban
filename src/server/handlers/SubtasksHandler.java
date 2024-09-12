package server.handlers;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import managers.common.NotFoundException;
import managers.common.TaskManager;
import model.Subtask;
import server.common.BaseTaskManagerHandler;
import server.common.ParseTaskURLParam;

import java.util.Collection;
import java.util.Optional;

public class SubtasksHandler extends BaseTaskManagerHandler {
    protected Integer subtaskIdParam;

    public SubtasksHandler(TaskManager taskManager) {
        super(taskManager);
        subtaskIdParam = null;
    }

    @Override
    protected void handleGet(HttpExchange exchange) {
        setupParams(exchange);

        if (subtaskIdParam == null) {
            Collection<Subtask> subtasks = taskManager.getAllSubtasks();
            sendText(exchange, gson.toJson(subtasks));
            return;
        }

        try {
            Subtask subtask = taskManager.getSubtaskById(subtaskIdParam);
            sendText(exchange, gson.toJson(subtask));
        } catch (NotFoundException ex) {
            sendNotFound(exchange, "Не найденна подздача с ID: " + subtaskIdParam);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange) {
        String body = readPostBody(exchange);
        Optional<Subtask> optionalSubtask = getTask(Subtask.class, body);

        if (optionalSubtask.isEmpty()) {
            sendBadRequest(exchange, "Не удалось получить подзадачу. Проверьте тело запроса.");
            return;
        }

        Subtask subtask = optionalSubtask.get();
        Integer subtaskId = subtask.getId();

        if (subtaskId == null) {
            try {
                taskManager.addSubtask(subtask);
                sendText(exchange, gson.toJson(subtask));
                return;
            } catch (IllegalArgumentException ex) {
                sendHasInteractions(exchange, ex.getMessage());
            }
        }

        try {
            taskManager.updateSubtask(subtask);
            sendText(exchange, gson.toJson(subtask));
        } catch (NotFoundException nfe) {
            sendNotFound(exchange, "Не удалось найти подзадачу с таким ID: " + subtaskId);
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange) {
        setupParams(exchange);

        if (subtaskIdParam == null) {
            sendNotFound(exchange, "Не передан ID подзадачи.");
            return;
        }

        try {
            taskManager.removeTaskById(subtaskIdParam);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("taskId", subtaskIdParam);
            sendText(exchange, gson.toJson(jsonObject));
        } catch (NotFoundException ex) {
            sendNotFound(exchange, "Не найденна подзадача для удаления с ID: " + subtaskIdParam);
        }
    }

    private void setupParams(HttpExchange exchange) {
        String basePath = "subtasks";
        String path = exchange.getRequestURI().getPath();
        subtaskIdParam = ParseTaskURLParam.parse(path, basePath);
    }
}
