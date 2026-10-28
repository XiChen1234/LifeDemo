package dev.xichen.backend.repository;

import dev.xichen.backend.entity.MediaItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MediaItemRepository extends JpaRepository<MediaItem, Long> {
    Optional<MediaItem> findByIdAndUserIdAndDeletedAtIsNull(Long id, Long userId);
}
