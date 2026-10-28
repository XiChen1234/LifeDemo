package dev.xichen.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sleep_records")
@Getter
@Setter
@NoArgsConstructor
public class SleepRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private LocalDate sleepDate;
    private LocalDateTime sleepAt;
    private LocalDateTime wakeAt;
    private Integer durationMinutes;
    private Integer deepMinutes;
    private Integer lightMinutes;
    private Integer remMinutes;
    private Integer awakeMinutes;
    private Integer qualityScore;
    private String source;
    private String note;
}
