package dev.xichen.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "finance_categories")
@Getter
@Setter
@NoArgsConstructor
public class FinanceCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String name;
    private String type;
    private String color;
    private Integer sortOrder;
    @Column(name = "is_active")
    private Boolean active;
}
