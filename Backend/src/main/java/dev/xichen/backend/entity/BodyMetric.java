package dev.xichen.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "body_metrics")
@Getter
@Setter
@NoArgsConstructor
public class BodyMetric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private LocalDate measuredOn;
    private BigDecimal weightKg;
    private BigDecimal bodyFatPercent;
    private BigDecimal muscleKg;
    private String source;
}
