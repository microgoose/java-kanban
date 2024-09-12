package server.common;

public class ParseTaskURLParam {
    public static Integer parse(String path, String basePath) {
        String[] pathParts = path.split("/");

        if (pathParts.length != 2 && pathParts.length != 3) {
            throw new IllegalArgumentException("Некоректный адресс");
        }

        if (pathParts.length == 2 && pathParts[1].equals(basePath)) {
            return null;
        }

        try {
            return Integer.parseInt(pathParts[2]);
        } catch (NumberFormatException  ex) {
            throw new IllegalArgumentException("Неккоректный ID задачи: " + pathParts[2]);
        }
    }
}
