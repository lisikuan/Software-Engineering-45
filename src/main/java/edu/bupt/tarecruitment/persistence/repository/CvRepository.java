package edu.bupt.tarecruitment.persistence.repository;

import edu.bupt.tarecruitment.common.exception.DataAccessException;

import java.nio.file.Path;

public interface CvRepository {
    String storePdf(String studentId, Path sourceFile) throws DataAccessException;

    Path resolveStoredFile(String relativePath) throws DataAccessException;
}
