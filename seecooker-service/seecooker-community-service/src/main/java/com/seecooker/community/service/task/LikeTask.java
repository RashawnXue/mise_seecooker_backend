package com.seecooker.community.service.task;

import com.seecooker.community.service.service.LikeService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
public class LikeTask extends QuartzJobBean {
    @Autowired
    private LikeService likeService;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    protected void executeInternal(JobExecutionContext context) {
        log.info("likeTask------ {}", sdf.format(new Date()));
        likeService.transLikeFromRedis2DB();
        likeService.transLikeNumFromRedis2DB();
    }
}
