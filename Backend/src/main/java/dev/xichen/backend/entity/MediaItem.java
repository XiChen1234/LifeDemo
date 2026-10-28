package dev.xichen.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "media_items")
@Getter
@Setter
@NoArgsConstructor
public class MediaItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String mediaType;
    private String title;
    private String subtitle;
    private String creator;
    private String status;
    private BigDecimal progressCurrent;
    private BigDecimal progressTotal;
    private String progressUnit;
    private Integer rating;
    private LocalDate startedOn;
    private LocalDate finishedOn;
    private LocalDateTime lastActivityAt;
    private String coverUrl;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
