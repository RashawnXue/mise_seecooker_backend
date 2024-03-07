package com.seecooker.community.service.pojo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLikeDTO {
    private Long userId;

    private Long postId;

    private Boolean status;
}
