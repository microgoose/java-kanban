import common.ManagerReadException;
import manager.FileBackedTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    @Test
    public void shouldThrowExceptionWhenDumpIsDirectory() throws IOException {
        Path tempDirWithPrefix = Files.createTempDirectory("tmp");

        assertThrows(ManagerReadException.class, () -> new FileBackedTaskManager(tempDirWithPrefix.toFile()));
    }

    @Test
    public void shouldSaveDataToFile() throws IOException {
        File dataFile = File.createTempFile("tmp", ".csv");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(dataFile);
        Task task = new Task(1, "Задача", "Описание");
        Epic epic = new Epic(2, "Эпик", "Описание");
        Subtask subtask = new Subtask(3, "Подзадача", "Описание", 2);

        dataFile.deleteOnExit();

        //Порядок важен!
        taskManager.addTask(task);
        taskManager.addSubtask(subtask);
        taskManager.addEpic(epic);

        BufferedReader br = new BufferedReader(new FileReader(dataFile));
        int lineNumber = 0;

        while (br.ready()) {
            String line = br.readLine();

            switch (lineNumber) {
                case 0:
                    assertEquals(taskManager.getCsvTableHead(), line);
                    break;
                case 1:
                    assertEquals(task.toString(), line);
                    break;
                case 2:
                    assertEquals(subtask.toString(), line);
                    break;
                case 3:
                    assertEquals(epic.toString(), line);
                    break;
                default:
                    throw new IOException(String.format("В файле найдено более %s строк(и)!", lineNumber + 1));
            }

            lineNumber++;
        }
    }

    @Test
    public void shouldLoadDataFromFile() throws IOException {
        File dataFile = File.createTempFile("tmp", ".csv");
        FileBackedTaskManager taskManagerForSave = new FileBackedTaskManager(dataFile);
        Task task = new Task(1, "Задача", "Описание");
        Epic epic = new Epic(2, "Эпик", "Описание");
        Subtask subtask = new Subtask(3, "Подзадача", "Описание", 2);

        dataFile.deleteOnExit();

        taskManagerForSave.addTask(task);
        taskManagerForSave.addSubtask(subtask);
        taskManagerForSave.addEpic(epic);

        FileBackedTaskManager taskManagerForLoad = new FileBackedTaskManager(dataFile);

        assertEquals(task.toString(), taskManagerForLoad.getTaskById(1).toString());
        assertEquals(epic.toString(), taskManagerForLoad.getEpicById(2).toString());
        assertEquals(subtask.toString(), taskManagerForLoad.getSubtaskById(3).toString());
    }
}
