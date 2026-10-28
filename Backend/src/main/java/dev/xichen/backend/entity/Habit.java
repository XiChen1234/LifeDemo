package dev.xichen.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "habits")
@Getter
@Setter
@NoArgsConstructor
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String icon;
    private String name;
    private String unit;
    private BigDecimal targetValue;
    private String period;
    private String color;
    private Integer sortOrder;
    @Column(name = "is_active")
    private Boolean active;
}
