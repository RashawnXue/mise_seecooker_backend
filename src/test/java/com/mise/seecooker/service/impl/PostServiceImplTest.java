package com.mise.seecooker.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.github.javafaker.Faker;
import com.mise.seecooker.dao.PostDao;
import com.mise.seecooker.dao.UserDao;
import com.mise.seecooker.entity.po.PostPO;
import com.mise.seecooker.entity.po.UserPO;
import com.mise.seecooker.entity.vo.community.PostVO;
import com.mise.seecooker.service.PostService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 帖子服务实现类测试
 *
 * @author xueruichen
 * @date 2023.11.25
 */
@SpringBootTest
public class PostServiceImplTest {
    @Autowired
    private PostService postService;
    @Autowired
    private PostDao postDao;
    @Autowired
    private UserDao userDao;

    private final Faker faker = new Faker(Locale.CHINA);

    @BeforeEach
    void registerAndLogin() {
        String username = "testUser";
        String password = "12345678abc";
        Long id = userDao.save(UserPO.builder()
                .username(username)
                .password(password)
                .posts(List.of())
                .build()).getId();
        StpUtil.login(id);
    }

    @AfterEach
    void clear() {
        postDao.deleteAll();
        StpUtil.logout();
        userDao.deleteAll();
    }

    @Test
    void addPostTest() throws Exception {
        String title = faker.animal().name();
        Long postId = postService.addPost(title, faker.address().buildingNumber(), null);
        assertEquals(title, postDao.findById(postId).get().getTitle());
        // 测试长数据
        title = faker.animal().name();
        StringBuilder content = new StringBuilder();
        for (int i = 0 ; i < 1000 ; ++i) {
            content.append(faker.name().fullName());
        }
        postId = postService.addPost(title, content.toString(), null);
        assertEquals(title, postDao.findById(postId).get().getTitle());
        List<Long> posts = userDao.findById(StpUtil.getLoginIdAsLong()).get().getPosts();
        assertEquals(postId, posts.get(posts.size()-1));
    }

    @Test
    void getPostsTest() {
        for (int i = 0 ; i < 10 ; ++i) {
            String title = faker.name().title();
            String content = faker.address().cityName();
            postDao.save(PostPO.builder()
                    .title(title)
                    .content(content)
                    .images(List.of(faker.internet().url()))
                    .posterId(StpUtil.getLoginIdAsLong())
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build());
        }
        List<PostVO> posts = postService.getPosts();
        assertEquals(10, posts.size());
        posts.forEach(postVO -> assertEquals(StpUtil.getLoginIdAsLong(), userDao.findByUsername(postVO.getPosterName()).getId()));
    }
}
