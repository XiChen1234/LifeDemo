package dev.xichen.backend.repository;

import dev.xichen.backend.entity.DataSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DataSourceRepository extends JpaRepository<DataSource, Long> {
    List<DataSource> findByUserIdOrderByIdAsc(Long userId);
}
