package dev.xichen.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "daily_activities")
@Getter
@Setter
@NoArgsConstructor
public class DailyActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private LocalDate activityDate;
    private Integer steps;
    private Integer stepGoal;
    private BigDecimal distanceKm;
    private Integer activeCaloriesKcal;
    private String source;
}
