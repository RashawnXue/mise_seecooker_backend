package com.seecooker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.seecooker.service.impl", "com.seecooker.dao"})
@EnableJpaRepositories("com.seecooker.dao")
@EntityScan("com.seecooker.pojo.po")
public class SeecookerServiceApplicationTest {
    @Test
    public void contextLoad() {}
}
