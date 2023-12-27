package com.seecooker.dao;

import com.seecooker.pojo.po.IngredientPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 配料dao
 *
 * @author xueruichen
 * @date 2023.12.27
 */
@Repository
public interface IngredientDao extends JpaRepository<IngredientPO, Long> {
}
