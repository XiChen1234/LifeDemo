package dev.xichen.backend.repository;

import dev.xichen.backend.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    Optional<TaskEntity> findByIdAndUserIdAndDeletedAtIsNull(Long id, Long userId);
}
