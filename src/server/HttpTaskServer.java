package server;

import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.common.TaskManager;
import server.handlers.*;

import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;

    HttpServer httpServer = null;

    public void start() {
        if (httpServer != null)
            return;

        try {
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            TaskManager taskManager = Managers.getDefault();

            httpServer.createContext("/tasks", new TasksHandler(taskManager));
            httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
            httpServer.createContext("/epics", new EpicsHandler(taskManager));
            httpServer.createContext("/history", new HistoryHandler(taskManager));
            httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));

            httpServer.start();
            System.out.println("Сервер запущен!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void stop() {
        if (httpServer == null) {
            throw new IllegalStateException("Сервер не был инициализирован!");
        }

        httpServer.stop(0);
        System.out.println("Сервер остановлен!");
    }

    public static void main(String[] args) {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
    }
}
