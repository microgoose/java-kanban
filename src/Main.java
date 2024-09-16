import managers.InMemoryTaskManager;
import managers.common.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

public class Main {
    public static void printAllTasks(TaskManager taskManager) {
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }
        for (Epic epic : taskManager.getAllEpic()) {
            System.out.println(epic);
        }
        for (Subtask subtask : taskManager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println();
        System.out.println("История просмотров:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        System.out.println();
    }

    public static void main(String[] args) {
        try {
            InMemoryTaskManager taskManager = new InMemoryTaskManager();

            Task task1 = new Task("task1", "Описание");
            Task task2 = new Task("task2", "Описание");

            Epic epic1 = new Epic("epic1", "Описание");
            Subtask subtask1 = new Subtask("subtask1", "Описание", 1);
            Subtask subtask2 = new Subtask("subtask2", "Описание", 1);

            Epic epic2 = new Epic("epic2", "Описание");
            Subtask subtask3 = new Subtask("subtask3", "Описание", 2);

            taskManager.addTask(task1);
            taskManager.addTask(task2);
            taskManager.addSubtask(subtask1);
            taskManager.addSubtask(subtask2);
            taskManager.addSubtask(subtask3);
            taskManager.addEpic(epic1);
            taskManager.addEpic(epic2);
            printAllTasks(taskManager);

            taskManager.getTaskById(task1.getId());
            taskManager.getSubtaskById(subtask1.getId());
            printAllTasks(taskManager);

            task1.setStatus(TaskStatus.IN_PROGRESS);
            taskManager.updateTask(task1);
            task2.setStatus(TaskStatus.DONE);
            taskManager.updateTask(task2);
            subtask1.setStatus(TaskStatus.IN_PROGRESS);
            taskManager.updateSubtask(subtask1);
            subtask2.setStatus(TaskStatus.DONE);
            taskManager.updateSubtask(subtask2);
            subtask3.setStatus(TaskStatus.DONE);
            taskManager.updateSubtask(subtask3);
            printAllTasks(taskManager);

            taskManager.removeTaskById(task1.getId());
            taskManager.removeEpicById(epic1.getId());
            printAllTasks(taskManager);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
