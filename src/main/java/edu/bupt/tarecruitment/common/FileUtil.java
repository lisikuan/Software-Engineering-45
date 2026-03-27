package edu.bupt.tarecruitment.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.bupt.tarecruitment.common.exception.DataAccessException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static void ensureDirectoryExists(String dirPath) {
        try {
            Path path = Paths.get(dirPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new DataAccessException("Failed to create directory: " + e.getMessage(), dirPath, "MKDIR");
        }
    }

    public static <T> List<T> readJsonArray(String filePath, Class<T> elementType) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            
            List<T> result = objectMapper.readValue(
                file,
                objectMapper.getTypeFactory().constructCollectionType(List.class, elementType)
            );
            return result != null ? result : new ArrayList<>();
        } catch (IOException e) {
            throw new DataAccessException("Failed to read JSON array: " + e.getMessage(), filePath, "READ", e);
        }
    }

    public static <T> T readJsonObject(String filePath, Class<T> valueType) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new DataAccessException("File not found", filePath, "READ");
            }
            return objectMapper.readValue(file, valueType);
        } catch (IOException e) {
            throw new DataAccessException("Failed to read JSON object: " + e.getMessage(), filePath, "READ", e);
        }
    }

    public static <T> void writeJsonArray(String filePath, List<T> data) {
        try {
            ensureDirectoryExists(new File(filePath).getParent());
            
            File tempFile = new File(filePath + ".tmp");
            objectMapper.writeValue(tempFile, data);
            
            File originalFile = new File(filePath);
            if (originalFile.exists()) {
                if (!originalFile.delete()) {
                    tempFile.delete();
                    throw new DataAccessException("Failed to delete original file", filePath, "WRITE");
                }
            }
            
            if (!tempFile.renameTo(originalFile)) {
                throw new DataAccessException("Failed to rename temporary file", filePath, "WRITE");
            }
        } catch (IOException e) {
            throw new DataAccessException("Failed to write JSON array: " + e.getMessage(), filePath, "WRITE", e);
        }
    }

    public static <T> void writeJsonObject(String filePath, T data) {
        try {
            ensureDirectoryExists(new File(filePath).getParent());
            
            File tempFile = new File(filePath + ".tmp");
            objectMapper.writeValue(tempFile, data);
            
            File originalFile = new File(filePath);
            if (originalFile.exists()) {
                if (!originalFile.delete()) {
                    tempFile.delete();
                    throw new DataAccessException("Failed to delete original file", filePath, "WRITE");
                }
            }
            
            if (!tempFile.renameTo(originalFile)) {
                throw new DataAccessException("Failed to rename temporary file", filePath, "WRITE");
            }
        } catch (IOException e) {
            throw new DataAccessException("Failed to write JSON object: " + e.getMessage(), filePath, "WRITE", e);
        }
    }

    public static boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }

    public static boolean deleteFile(String filePath) {
        return new File(filePath).delete();
    }
}