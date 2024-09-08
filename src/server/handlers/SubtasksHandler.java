package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import common.BaseHttpHandler;
import common.TaskManager;

public class SubtasksHandler extends BaseHttpHandler  implements HttpHandler {
    public SubtasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {

    }
}
