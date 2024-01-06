package com.seecooker.recipe.service.dao;

import com.seecooker.recipe.service.pojo.po.IngredientAmountPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 配料量dao
 *
 * @author xueruichen
 * @date 2203.12.28
 */
@Repository
public interface IngredientAmountDao extends JpaRepository<IngredientAmountPO, Long> {
    List<IngredientAmountPO> getIngredientAmountPOSByRecipeId(Long recipeId);
}
