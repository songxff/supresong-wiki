package com.supresong.wiki.controller;

import com.supresong.wiki.domain.Test;
import com.supresong.wiki.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;


@RestController
public class TestController {

    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);
    @Autowired
    private TestService testService;

    @Resource
    private RedisTemplate redisTemplate;

    @Value("${test.hello:TEST}")//冒号后面是默认值,以防配置信息未传进来,导致项目启动失败.
    private String testHello;

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!!!"+ testHello;
    }

    @PostMapping("/hello/post")
    public String helloPost(String name) {
        return "你好 世界!!!,"+name;
    }

    @GetMapping("/test/list")
    public List<Test> list() {
        return testService.list();
    }

    @RequestMapping("/redis/set/{key}/{value}")
    public String set(@PathVariable Long key, @PathVariable String value) {
        redisTemplate.opsForValue().set(key, value, 3600, TimeUnit.SECONDS);
        LOG.info("key: {}, value: {}", key, value);
        return "success";
    }

    @RequestMapping("/redis/get/{key}")
    public Object get(@PathVariable Long key) {
        Object object = redisTemplate.opsForValue().get(key);
        LOG.info("key: {}, value: {}", key, object);
        return object;
    }}
