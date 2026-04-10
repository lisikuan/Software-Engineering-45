package edu.bupt.tarecruitment.persistence.json;

import edu.bupt.tarecruitment.common.PathsConfig;
import edu.bupt.tarecruitment.common.exception.DataAccessException;
import edu.bupt.tarecruitment.persistence.repository.CvRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileCvRepository implements CvRepository {
    private final Path dataDirectory;
    private final Path cvDirectory;

    public FileCvRepository() {
        this(PathsConfig.DATA_DIRECTORY);
    }

    public FileCvRepository(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
        this.cvDirectory = dataDirectory.resolve("cvs");
    }

    @Override
    public String storePdf(String studentId, Path sourceFile) throws DataAccessException {
        Path targetFile = cvDirectory.resolve(studentId + ".pdf");
        try {
            Files.createDirectories(cvDirectory);
            Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
            return dataDirectory.relativize(targetFile).toString().replace('\\', '/');
        } catch (IOException exception) {
            throw new DataAccessException("Failed to store CV file for student id: " + studentId, exception);
        }
    }

    @Override
    public Path resolveStoredFile(String relativePath) throws DataAccessException {
        if (relativePath == null || relativePath.isBlank()) {
            throw new DataAccessException("CV file path must not be blank.");
        }

        Path resolvedPath = dataDirectory.resolve(relativePath).normalize();
        if (!resolvedPath.startsWith(dataDirectory.normalize())) {
            throw new DataAccessException("CV file path resolves outside data directory: " + relativePath);
        }
        return resolvedPath;
    }
}
