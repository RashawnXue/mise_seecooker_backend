package com.seecooker.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.seecooker"})
@ComponentScan("com.seecooker")
@EnableJpaRepositories("com.seecooker.dao")
@EntityScan("com.seecooker.pojo.po")
public class SeecookerWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeecookerWebApplication.class, args);
    }
}
