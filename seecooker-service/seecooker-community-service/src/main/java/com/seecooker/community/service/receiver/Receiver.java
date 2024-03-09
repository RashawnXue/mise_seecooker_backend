package com.seecooker.community.service.receiver;

import com.seecooker.community.service.service.PostService;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class Receiver {
    private final PostService postService;

    public Receiver(PostService postService) {
        this.postService = postService;
    }

    @RabbitHandler
    @RabbitListener(queuesToDeclare = @Queue("likePostQueue"))
    public void receiveLikePost(String arg) {
        String[] split = arg.split(":");
        postService.likePost(Long.parseLong(split[0]), Long.parseLong(split[1]));
    }
}
