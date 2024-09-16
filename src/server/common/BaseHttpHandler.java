package server.common;

import com.sun.net.httpserver.HttpExchange;
import managers.common.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler {
    protected TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected String readPostBody(HttpExchange h) {
        try (InputStream inputStream = h.getRequestBody()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            sendBadRequest(h, "Ошибка при чтении тела запроса: " + ioException.getMessage());
            throw new RuntimeException(ioException);
        }
    }

    protected void send(HttpExchange h, String text, int code) {
        try {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            h.sendResponseHeaders(code, resp.length);
            h.getResponseBody().write(resp);
            h.close();
        } catch (Exception ex) {
            System.out.printf("Ошибка при отправке ответа. Код: %s. Тело: %s\n", code, text);
            throw new RuntimeException(ex);
        }
    }

    protected void sendInternalError(HttpExchange h, String text) {
        send(h, text, 500);
    }

    protected void sendBadRequest(HttpExchange h, String text) {
        send(h, text, 400);
    }

    protected void sendNotFound(HttpExchange h, String text) {
        send(h, text, 404);
    }

    protected void sendHasInteractions(HttpExchange h, String text) {
        send(h, text, 406);
    }

    protected void sendText(HttpExchange h, String text) {
        send(h, text, 200);
    }
}
