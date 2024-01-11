package com.seecooker.user.service.reciver;

import com.seecooker.user.service.dao.UserDao;
import com.seecooker.user.service.pojo.po.UserPO;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 消息队列生产者
 *
 * @author xueruichen
 * @date 2024.01.11
 */
@Component
@RabbitListener(queues = "modifySignature")
public class Reciver {
    private final UserDao userDao;

    public Reciver(UserDao userDao) {
        this.userDao = userDao;
    }

    @RabbitHandler
    @RabbitListener(queuesToDeclare = @Queue("modifySignature"))
    public void modifySignature(String idSignature) {
        Long userId = Long.parseLong(idSignature.split(":")[0]);
        String signature = idSignature.split(":")[1];
        UserPO user = userDao.findById(userId).get();
        user.setSignature(signature);
        userDao.save(user);
    }
}
