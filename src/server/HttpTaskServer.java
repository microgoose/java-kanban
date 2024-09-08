package server;

import com.sun.net.httpserver.HttpServer;
import common.TaskManager;
import manager.Managers;
import server.handlers.*;

import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        try {
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            TaskManager taskManager = Managers.getDefault();

            httpServer.createContext("/tasks", new TasksHandler(taskManager));
            httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
            httpServer.createContext("/epics", new EpicsHandler(taskManager));
            httpServer.createContext("/history", new HistoryHandler(taskManager));
            httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));

            httpServer.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
