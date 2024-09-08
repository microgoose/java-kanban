package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.common.NotFoundException;
import managers.common.TaskManager;
import model.Task;
import server.common.BaseTaskHandler;

import java.io.IOException;
import java.util.Collection;

public class TasksHandler extends BaseTaskHandler {
    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        Gson gson = new Gson();

        if (path.equals("/tasks")) {
            Collection<Task> tasks = taskManager.getAllTasks();
            sendText(exchange, gson.toJson(tasks));
            return;
        }

        if (pathParts.length != 2 && !pathParts[0].equals("task")) {
            sendBadRequest(exchange, "Некоектный адресс");
            return;
        }

        int taskId;

        try {
            taskId = Integer.parseInt(pathParts[1]);
        } catch (NumberFormatException  ex) {
            sendInternalError(exchange, "Не удалось получить ID задачи: " + ex.getMessage());
            ex.printStackTrace();
            return;
        }

        try {
            Task task = taskManager.getTaskById(taskId);
            sendText(exchange, gson.toJson(task));
        } catch (NotFoundException ex) {
            sendNotFound(exchange, "Не найденна задача с ID: " + taskId);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        sendBadRequest(exchange, "Некоректный адресс");
    }

    @Override
    protected void handleDelete(HttpExchange exchange) throws IOException {
        sendBadRequest(exchange, "Некоректный адресс");
    }
}
