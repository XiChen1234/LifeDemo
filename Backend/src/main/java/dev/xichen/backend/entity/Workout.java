package dev.xichen.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "workouts")
@Getter
@Setter
@NoArgsConstructor
public class Workout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String workoutType;
    private LocalDateTime startedAt;
    private Integer durationMinutes;
    private BigDecimal distanceKm;
    private Integer caloriesKcal;
    private String note;
    private String source;
}
