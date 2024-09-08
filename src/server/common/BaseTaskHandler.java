package server.common;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.common.TaskManager;

public abstract class BaseTaskHandler extends BaseHttpHandler implements HttpHandler {
    public BaseTaskHandler(TaskManager taskManager) {
        super(taskManager);
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
            ex.printStackTrace();
        }
    }

    protected abstract void handleGet(HttpExchange exchange);

    protected abstract void handlePost(HttpExchange exchange);

    protected abstract void handleDelete(HttpExchange exchange);
}
