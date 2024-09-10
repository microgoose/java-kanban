package server.handlers;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import managers.common.NotFoundException;
import managers.common.TaskManager;
import model.Task;
import server.common.BaseTaskManagerHandler;

import java.util.Collection;
import java.util.Optional;

public class TasksHandler extends BaseTaskManagerHandler {
    protected Integer taskIdParam;

    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
        taskIdParam = null;
    }

    @Override
    protected void handleGet(HttpExchange exchange) {
        setupParams(exchange);

        if (taskIdParam == null) {
            Collection<Task> tasks = taskManager.getAllTasks();
            sendText(exchange, gson.toJson(tasks));
            return;
        }

        try {
            Task task = taskManager.getTaskById(taskIdParam);
            sendText(exchange, gson.toJson(task));
        } catch (NotFoundException ex) {
            sendNotFound(exchange, "Не найденна задача с ID: " + taskIdParam);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange) {
        String body = readPostBody(exchange);
        Optional<Task> optionalTask = getTask(Task.class, body);

        if (optionalTask.isEmpty()) {
            sendBadRequest(exchange, "Не удалось получить задачу. Проверьте тело запроса.");
            return;
        }

        Task task = optionalTask.get();
        Integer taskId = task.getId();

        if (taskId == null) {
            try {
                taskManager.addTask(task);
                sendText(exchange, gson.toJson(task));
                return;
            } catch (IllegalArgumentException ex) {
                sendHasInteractions(exchange, ex.getMessage());
            }
        }

        try {
            taskManager.updateTask(task);
            sendText(exchange, gson.toJson(task));
        } catch (NotFoundException nfe) {
            sendNotFound(exchange, "Не удалось найти задачу с таким ID: " + taskId);
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange) {
        setupParams(exchange);

        if (taskIdParam == null) {
            sendNotFound(exchange, "Не передан ID задачи.");
            return;
        }

        try {
            taskManager.removeTaskById(taskIdParam);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("taskId", taskIdParam);
            sendText(exchange, gson.toJson(jsonObject));
        } catch (NotFoundException ex) {
            sendNotFound(exchange, "Не найденна задача для удаления с ID: " + taskIdParam);
        }
    }

    private void setupParams(HttpExchange exchange) {
        String basePath = "tasks";
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        taskIdParam = null;

        if (path.equals(basePath)) {
            return;
        }

        if (pathParts.length != 3 && !pathParts[1].equals(basePath)) {
            throw new IllegalArgumentException("Некоректный адресс");
        }

        try {
            taskIdParam = Integer.parseInt(pathParts[2]);
        } catch (NumberFormatException  ex) {
            throw new IllegalArgumentException("Неккоректный ID задачи: " + pathParts[2]);
        }
    }
}
