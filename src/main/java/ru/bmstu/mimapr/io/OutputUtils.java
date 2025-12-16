package ru.bmstu.mimapr.io;

import ru.bmstu.mimapr.config.FileData;
import ru.bmstu.mimapr.model.DoubleList;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public final class OutputUtils {
    private OutputUtils() {}

    public static String fmt(double v) {
        return String.format(Locale.US, "%.6g", v);
    }

    public static void printToFile(String name, DoubleList values) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(name, true))) {
            for (int i = 0; i < values.size(); i++) {
                bw.write(fmt(values.get(i)));
                bw.newLine();
            }
        }
    }

    public static void clearFiles() throws IOException {
        deleteIfExists(FileData.PHI1_FILE);
        deleteIfExists(FileData.PHI2_FILE);
        deleteIfExists(FileData.PHI4_FILE);
        deleteIfExists(FileData.PHI5_FILE);
        deleteIfExists(FileData.T_FILE);
    }

    public static void deleteIfExists(String filename) throws IOException {
        Files.deleteIfExists(Path.of(filename));
    }
}
