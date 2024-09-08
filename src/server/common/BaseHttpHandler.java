package server.common;

import com.sun.net.httpserver.HttpExchange;
import managers.common.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler {
    protected TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected void send(HttpExchange h, String text, int code) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        send(h, text, 200);
    }

    protected void sendNotFound(HttpExchange h, String text) throws IOException {
        send(h, text, 404);
    }

    protected void sendHasInteractions(HttpExchange h, String text) throws IOException {
        send(h, text, 406);
    }
}
