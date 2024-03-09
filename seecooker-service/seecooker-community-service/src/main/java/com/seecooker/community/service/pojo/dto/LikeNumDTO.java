package com.seecooker.community.service.pojo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikeNumDTO {
    private Long postId;
    private Integer likeNum;
}
