package server.common;

public class ParseTaskURLParam {
    public static Integer parse(String path, String basePath) {
        String[] pathParts = path.split("/");

        if (path.equals(basePath)) {
            return null;
        }

        if (pathParts.length != 3 && !pathParts[1].equals(basePath)) {
            throw new IllegalArgumentException("Некоректный адресс");
        }

        try {
            return Integer.parseInt(pathParts[2]);
        } catch (NumberFormatException  ex) {
            throw new IllegalArgumentException("Неккоректный ID задачи: " + pathParts[2]);
        }
    }
}
