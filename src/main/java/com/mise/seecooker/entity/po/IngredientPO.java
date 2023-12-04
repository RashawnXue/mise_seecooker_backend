package com.mise.seecooker.entity.po;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 配料持久层实体类PO
 *
 * @author xueruichen
 * @date 2023.12.04
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "ingredient")
public class IngredientPO {
    /**
     * 配料id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * 配料名
     */
    @NotNull
    private String name;
}
