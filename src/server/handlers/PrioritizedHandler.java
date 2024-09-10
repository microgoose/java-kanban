package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.common.TaskManager;
import model.Task;
import server.common.BaseTaskManagerHandler;

import java.util.Collection;

public class PrioritizedHandler extends BaseTaskManagerHandler {
    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange) {
        Collection<Task> tasks = taskManager.getPrioritizedTasks();
        Gson gson = new Gson();
        sendText(exchange, gson.toJson(tasks));
    }
}
