package server.handlers;

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
        sendText(exchange, gson.toJson(tasks));
    }
}
