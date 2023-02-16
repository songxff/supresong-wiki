package com.supresong.wiki.controller;

import com.supresong.wiki.domain.Test;
import com.supresong.wiki.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class TestController {
    @Autowired
    private TestService testService;
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
}
