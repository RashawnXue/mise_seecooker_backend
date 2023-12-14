package com.seecooker.service;

/**
 * 定时任务服务
 *
 * @author xueruichen
 * @date 2023.12.10
 */
public interface ScheduledService {
    /**
     * 定时更新帖子点赞信息
     */
    void scheduledUpdatePostLike();

    /**
     * 定时更新收藏菜谱信息
     */
    void scheduledUpdateRecipeFavorite();
}
