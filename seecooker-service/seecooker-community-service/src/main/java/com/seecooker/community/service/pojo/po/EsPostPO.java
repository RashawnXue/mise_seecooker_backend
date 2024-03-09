package com.seecooker.community.service.pojo.po;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@Document(indexName = "seecooker-posts")
public class EsPostPO {
    /**
     * 帖子id
     */
    @Id
    private Long id;

    /**
     * 帖子标题
     */
    @Field(type = FieldType.Text)
    private String title;

    /**
     * 帖子内容
     */
    @Field(type = FieldType.Text)
    private String content;

    /**
     * 数据创建的时间戳
     */
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createTime;
}
