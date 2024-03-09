package com.seecooker.community.service.dao;

import com.seecooker.community.service.pojo.po.UserLikePO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLikeDao extends JpaRepository<UserLikePO, Long> {
    UserLikePO getUserLikePOByUserIdAndPostId(Long userId, Long postId);
}
