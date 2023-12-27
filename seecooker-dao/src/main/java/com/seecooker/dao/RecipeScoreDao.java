package com.seecooker.dao;

import com.seecooker.pojo.po.RecipeScorePO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 菜谱评分dao层
 *
 * @author xueruichen
 * @date 2023.12.27
 */
@Repository
public interface RecipeScoreDao extends JpaRepository<RecipeScorePO, Long> {
    RecipeScorePO findRecipeScorePOByUserIdAndRecipeId(Long userId, Long recipeId);

    List<RecipeScorePO> findRecipeScorePOSByRecipeId(Long recipeId);
}
