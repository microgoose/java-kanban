package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.common.TaskManager;
import server.common.BaseTaskHandler;

public class SubtasksHandler extends BaseTaskHandler {
    public SubtasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange) {

    }

    @Override
    protected void handlePost(HttpExchange exchange) {

    }

    @Override
    protected void handleDelete(HttpExchange exchange) {

    }
}
