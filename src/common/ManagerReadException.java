package common;

public class ManagerReadException extends RuntimeException {
    public ManagerReadException(final Throwable cause) {
        super(String.format("Ошибка чтения данных из файла: %s", cause.getMessage()), cause);
    }

    public ManagerReadException(String message) {
        super(String.format("Ошибка чтения данных из файла: %s", message));
    }
}
