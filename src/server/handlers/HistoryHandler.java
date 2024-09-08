package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.common.TaskManager;
import model.Task;
import server.common.BaseHttpHandler;

import java.io.IOException;
import java.util.Collection;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) {
        String method = exchange.getRequestMethod();

        try {
            if (!method.equals("GET")) {
                sendNotFound(exchange, "Передан некорректный метод запроса!");
                return;
            }

            Collection<Task> tasks = taskManager.getHistory();
            Gson gson = new Gson();
            sendText(exchange, gson.toJson(tasks));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
