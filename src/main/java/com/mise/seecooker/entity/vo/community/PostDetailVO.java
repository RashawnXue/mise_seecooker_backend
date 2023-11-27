package com.mise.seecooker.entity.vo.community;

import com.aliyuncs.exceptions.ClientException;
import com.mise.seecooker.util.AliOSSUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 帖子详细内容VO类
 *
 * @author xueruichen
 * @date 2023.11.25
 */
@Getter
@Setter
@Builder
public class PostDetailVO {
    /**
     * 帖子标题
     */
    private String title;

    /**
     * 帖子内容
     */
    private String content;

    /**
     * 发布者头像url
     */
    private String posterAvatar;

    /**
     * 发布者用户名
     */
    private String posterName;

    /**
     * 帖子图片
     */
    private List<String> images;

    public String getPosterAvatar() throws ClientException {
        return AliOSSUtil.authorizeAccess(this.posterAvatar);
    }

    public List<String> getImages() throws ClientException{
        return AliOSSUtil.authorizeAccess(this.images);
    }
}
