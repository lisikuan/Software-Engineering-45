package edu.bupt.tarecruitment.persistence.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.common.exception.JsonFormatException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Responsibility: provide centralized JSON file read/write operations, convert
 * Jackson parsing problems into explicit persistence exceptions, and write via
 * temp-file replacement to reduce partial-write risk.
 *
 * JSON structure design used by this class:
 * - data/students.json: array of Student objects with id, name, userId.
 * - data/jobs.json: array of Job objects with id, title, description.
 * - data/applications.json: array of Application objects with id, studentId,
 *   jobId, status.
 * - data/users.json: array of User objects with id, username, role.
 * - JSON field order is not treated as a strong contract.
 * - [待确认] Additional non-Student fields, enum values, and schema
 *   constraints from course documents or team report.
 */
public class JsonDataStore {
    private final Path dataDirectory;
    private final ObjectMapper objectMapper;

    public JsonDataStore(Path dataDirectory) {
        this(dataDirectory, JsonMapper.builder().enable(SerializationFeature.INDENT_OUTPUT).build());
    }

    public JsonDataStore(Path dataDirectory, ObjectMapper objectMapper) {
        this.dataDirectory = dataDirectory;
        this.objectMapper = objectMapper;
    }

    public Path resolve(String fileName) {
        return dataDirectory.resolve(fileName);
    }

    public <T> List<T> readList(String fileName, Class<T> elementType) throws DataAccessException {
        Path filePath = resolve(fileName);
        ensureReadableFileExists(filePath);
        JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, elementType);
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            return objectMapper.readValue(inputStream, javaType);
        } catch (JsonProcessingException exception) {
            throw new JsonFormatException("Malformed JSON in file: " + filePath, exception);
        } catch (IOException exception) {
            throw new DataAccessException("Failed to read JSON file: " + filePath, exception);
        }
    }

    public void writeList(String fileName, List<?> data) throws DataAccessException {
        Path filePath = resolve(fileName);
        Path tempFilePath = filePath.resolveSibling(filePath.getFileName() + ".tmp");
        ensureParentDirectoryExists(filePath);
        try (OutputStream outputStream = Files.newOutputStream(tempFilePath)) {
            objectMapper.writeValue(outputStream, data);
        } catch (JsonProcessingException exception) {
            throw new JsonFormatException("Failed to serialize JSON for file: " + filePath, exception);
        } catch (IOException exception) {
            throw new DataAccessException("Failed to write temporary JSON file: " + tempFilePath, exception);
        }

        replaceFile(tempFilePath, filePath);
    }

    private void ensureReadableFileExists(Path filePath) throws DataAccessException {
        if (Files.notExists(filePath)) {
            throw new DataAccessException("Required JSON file does not exist: " + filePath);
        }
        if (!Files.isRegularFile(filePath)) {
            throw new DataAccessException("JSON path is not a regular file: " + filePath);
        }
    }

    private void ensureParentDirectoryExists(Path filePath) throws DataAccessException {
        try {
            Files.createDirectories(filePath.getParent());
        } catch (IOException exception) {
            throw new DataAccessException("Failed to create data directory for file: " + filePath, exception);
        }
    }

    private void replaceFile(Path tempFilePath, Path targetFilePath) throws DataAccessException {
        try {
            Files.move(
                    tempFilePath,
                    targetFilePath,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE
            );
        } catch (IOException atomicMoveException) {
            try {
                Files.move(tempFilePath, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException fallbackException) {
                throw new DataAccessException("Failed to replace JSON file: " + targetFilePath, fallbackException);
            }
        }
    }
}