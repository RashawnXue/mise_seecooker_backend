package com.mise.seecooker.dao;

import com.mise.seecooker.entity.po.RecipePO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 菜谱业务dao层
 *
 * @author xueruichen
 * @date 2023.11.27
 */
public interface RecipeDao extends JpaRepository<RecipePO, Long> {
    List<RecipePO> findByNameLike(String query);
}
