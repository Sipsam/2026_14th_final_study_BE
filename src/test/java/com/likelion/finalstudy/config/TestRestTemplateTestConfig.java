package com.likelion.finalstudy.config;

import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestRestTemplateTestConfig {

    @Bean
    @Primary
    TestRestTemplate testRestTemplate() {
        return new TestRestTemplate();
    }
}
