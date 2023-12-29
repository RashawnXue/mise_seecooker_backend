package com.seecooker.recipe.service.dao;

import com.seecooker.recipe.service.pojo.po.RecipePO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 菜谱业务dao层
 *
 * @author xueruichen
 * @date 2023.11.27
 */
@Repository
public interface RecipeDao extends JpaRepository<RecipePO, Long> {
    List<RecipePO> findByNameLike(String query);
}
