package managers.common;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(final Throwable cause) {
        super(String.format("Ошибка сохранения данных в файл: %s", cause.getMessage()), cause);
    }
}
