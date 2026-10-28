package dev.xichen.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "annual_goals")
@Getter
@Setter
@NoArgsConstructor
public class AnnualGoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Integer year;
    private String name;
    private String metricKey;
    private String unit;
    private BigDecimal targetValue;
    private String color;
    private Integer sortOrder;
}
