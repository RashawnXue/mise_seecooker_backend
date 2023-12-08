package com.seecooker.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.github.javafaker.Faker;
import com.seecooker.dao.CommentDao;
import com.seecooker.dao.PostDao;
import com.seecooker.dao.UserDao;
import com.seecooker.pojo.po.CommentPO;
import com.seecooker.pojo.po.PostPO;
import com.seecooker.pojo.po.UserPO;
import com.seecooker.pojo.vo.community.CommentVO;
import com.seecooker.pojo.vo.community.PostCommentVO;
import com.seecooker.pojo.vo.community.PostDetailVO;
import com.seecooker.pojo.vo.community.PostVO;
import com.seecooker.service.PostService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
    @Autowired
    private CommentDao commentDao;
    private final Faker faker = new Faker(Locale.CHINA);
    @BeforeEach
    void registerAndLogin() {
        String username = "testUser222";
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

    @Test
    void getPostDetailTest() {
        String title = faker.name().title();
        String content = faker.address().cityName();
        PostPO post = postDao.save(PostPO.builder()
                .title(title)
                .content(content)
                .images(List.of(faker.internet().url()))
                .posterId(StpUtil.getLoginIdAsLong())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build());
        PostDetailVO postDetail = postService.getPostDetail(post.getId());
        assertEquals(postDetail.getTitle(), post.getTitle());
        assertEquals(postDetail.getContent(), post.getContent());
        assertEquals(postDetail.getPosterName(), userDao.findById(post.getPosterId()).get().getUsername());
    }

    @Test
    void addCommentTest() {
        String title = faker.name().title();
        String postContent = faker.address().cityName();
        PostPO post = postDao.save(PostPO.builder()
                .title(title)
                .content(postContent)
                .images(List.of(faker.internet().url()))
                .posterId(StpUtil.getLoginIdAsLong())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build());
        String commentContent = faker.address().cityName();
        Long id = postService.addComment(PostCommentVO.builder().postId(post.getId()).content(commentContent).build());
        CommentPO comment = commentDao.findById(id).get();
        assertEquals(comment.getCommenterId(), StpUtil.getLoginIdAsLong());
        assertEquals(comment.getPostId(), post.getId());
    }

    @Test
    void getCommentsByPostIdTest() {
        String title = faker.name().title();
        String postContent = faker.address().cityName();
        PostPO post = postDao.save(PostPO.builder()
                .title(title)
                .content(postContent)
                .images(List.of(faker.internet().url()))
                .posterId(StpUtil.getLoginIdAsLong())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build());
        UserPO user = userDao.findById(StpUtil.getLoginIdAsLong()).get();
        for (int i = 0 ; i < 10 ; ++i) {
            String commentContent = faker.address().cityName();
            commentDao.save(CommentPO.builder()
                    .commenterId(StpUtil.getLoginIdAsLong())
                    .content(commentContent)
                    .createTime(LocalDateTime.now())
                    .postId(post.getId())
                    .build());
        }
        List<CommentVO> comments = postService.getCommentsByPostId(post.getId());
        comments.forEach(commentVO -> assertEquals(user.getUsername(), commentVO.getCommenterName()));
    }
}
