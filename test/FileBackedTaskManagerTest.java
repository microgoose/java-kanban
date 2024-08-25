import common.ManagerReadException;
import manager.FileBackedTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    protected File createEmptyDataFile() throws IOException {
        File dataFile = File.createTempFile("tmp", ".csv");
        dataFile.deleteOnExit();
        return dataFile;
    }

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            return new FileBackedTaskManager(createEmptyDataFile());
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    protected FileBackedTaskManager createTaskManager(File dataFile) {
        return new FileBackedTaskManager(dataFile);
    }

    protected FileBackedTaskManager createFilledTaskManager(File dataFile) {
        FileBackedTaskManager taskManager = createTaskManager(dataFile);
        Task task = createTask();
        Subtask subtask = createSubtask();
        Epic epic = createEpic();

        //Порядок важен!
        taskManager.addTask(task);
        taskManager.addSubtask(subtask);
        taskManager.addEpic(epic);

        return new FileBackedTaskManager(dataFile);
    }

    @Test
    public void shouldThrowExceptionWhenDumpIsDirectory() throws IOException {
        Path tempDirWithPrefix = Files.createTempDirectory("tmp");

        assertThrows(ManagerReadException.class, () -> createTaskManager(tempDirWithPrefix.toFile()));
    }

    @Test
    public void shouldSaveDataToFile() throws IOException {
        File dataFile = createEmptyDataFile();
        FileBackedTaskManager taskManager = createFilledTaskManager(dataFile);

        Task task = new ArrayList<>(taskManager.getAllTasks()).getFirst();
        Subtask subtask = new ArrayList<>(taskManager.getAllSubtasks()).getFirst();
        Epic epic = new ArrayList<>(taskManager.getAllEpic()).getFirst();

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
        FileBackedTaskManager taskManagerForLoad = createFilledTaskManager(createEmptyDataFile());
        Task task = new ArrayList<>(taskManagerForLoad.getAllTasks()).getFirst();
        Subtask subtask = new ArrayList<>(taskManagerForLoad.getAllSubtasks()).getFirst();
        Epic epic = new ArrayList<>(taskManagerForLoad.getAllEpic()).getFirst();

        assertEquals(task.toString(), taskManagerForLoad.getTaskById(task.getId()).toString());
        assertEquals(epic.toString(), taskManagerForLoad.getEpicById(epic.getId()).toString());
        assertEquals(subtask.toString(), taskManagerForLoad.getSubtaskById(subtask.getId()).toString());
    }
}
