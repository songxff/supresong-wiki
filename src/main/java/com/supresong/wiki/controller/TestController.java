package com.supresong.wiki.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {
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
}
