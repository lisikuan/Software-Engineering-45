package edu.bupt.tarecruitment.persistence.repository;

import edu.bupt.tarecruitment.model.Application;

import java.util.List;
import java.util.Optional;

/**
 * Responsibility: define storage-facing CRUD operations for Application
 * entities while keeping status rules and review logic out of the DAO layer.
 */
public interface ApplicationRepository {
    List<Application> findAll();

    Optional<Application> findById(String id);

    Application insert(Application application);

    Application update(Application application);

    boolean deleteById(String id);
}
