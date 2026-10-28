package dev.xichen.backend.repository;

import dev.xichen.backend.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByUserIdAndStatusNotOrderBySortOrderAsc(Long userId, String status);

    Optional<Project> findByIdAndUserId(Long id, Long userId);
}
