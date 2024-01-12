package com.seecooker.app.gateway.config;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.seecooker.common.core.model.Result;
import com.seecooker.common.core.exception.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SaTokenConfigure {
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截地址
                .addInclude("/**")    /* 拦截全部path */
                // 鉴权方法：每次访问进入
                .setAuth(obj -> {
                    // feign-api只能在服务间调用
                    SaRouter.match("/feign/**").stop();
                    // 用户服务鉴权
                    SaRouter.match(SaHttpMethod.DELETE)
                            .match("/user/session")
                            .check(r -> StpUtil.checkLogin());
                    SaRouter.match(SaHttpMethod.GET)
                            .match("/user")
                            .check(r -> StpUtil.checkLogin());
                    SaRouter.match(SaHttpMethod.PUT)
                            .match("/user/modify/**")
                            .check(r -> StpUtil.checkLogin());
                    // 菜谱服务鉴权
                    SaRouter.match(SaHttpMethod.POST)
                            .match("/recipe")
                            .check(r -> StpUtil.checkLogin());
                    SaRouter.match(SaHttpMethod.PUT)
                            .match("/recipe/favorite/**")
                            .check(r -> StpUtil.checkLogin());
                    SaRouter.match(SaHttpMethod.POST)
                            .match("/recipe/score")
                            .check(r -> StpUtil.checkLogin());
                    SaRouter.match(SaHttpMethod.GET)
                            .match("/recipe/favorites/**")
                            .check(r -> StpUtil.checkLogin());
                    // 社区服务鉴权
                    SaRouter.match(SaHttpMethod.POST)
                            .match("/community/**")
                            .check(r -> StpUtil.checkLogin());
                    SaRouter.match(SaHttpMethod.PUT)
                            .match("/community/like/**")
                            .check(r -> StpUtil.checkLogin());
                    SaRouter.match(SaHttpMethod.DELETE)
                            .match("/community/post/**")
                            .check(r -> StpUtil.checkLogin());
                    SaRouter.match(SaHttpMethod.GET)
                            .match("/community/user/posts/**")
                            .check(r -> StpUtil.checkLogin());
                })
                // 异常处理方法：每次setAuth函数出现异常时进入
                .setError(e -> Result.error(ErrorType.ILLEGAL_ARGUMENTS))
                ;
    }
}
