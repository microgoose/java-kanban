import common.ManagerReadException;
import manager.FileBackedTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    File dataFile;
    FileBackedTaskManager taskManager;
    Task task;
    Epic epic;
    Subtask subtask;

    @BeforeEach
    public void init() throws IOException {
        dataFile = File.createTempFile("tmp", ".csv");
        taskManager = new FileBackedTaskManager(dataFile);
        task = new Task(1, "Задача", "Описание");
        epic = new Epic(2, "Эпик", "Описание");
        subtask = new Subtask(3, "Подзадача", "Описание", 2);

        dataFile.deleteOnExit();

        //Порядок важен!
        taskManager.addTask(task);
        taskManager.addSubtask(subtask);
        taskManager.addEpic(epic);
    }

    @Test
    public void shouldThrowExceptionWhenDumpIsDirectory() throws IOException {
        Path tempDirWithPrefix = Files.createTempDirectory("tmp");

        assertThrows(ManagerReadException.class, () -> new FileBackedTaskManager(tempDirWithPrefix.toFile()));
    }

    @Test
    public void shouldSaveDataToFile() throws IOException {
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
    public void shouldLoadDataFromFile() {
        FileBackedTaskManager taskManagerForLoad = new FileBackedTaskManager(dataFile);

        assertEquals(task.toString(), taskManagerForLoad.getTaskById(1).toString());
        assertEquals(epic.toString(), taskManagerForLoad.getEpicById(2).toString());
        assertEquals(subtask.toString(), taskManagerForLoad.getSubtaskById(3).toString());
    }
}
