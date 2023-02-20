package com.supresong.wiki.controller;

import com.supresong.wiki.req.UserQueryReq;
import com.supresong.wiki.req.UserSaveReq;
import com.supresong.wiki.resp.CommonResp;
import com.supresong.wiki.resp.PageResp;
import com.supresong.wiki.resp.UserQueryResp;
import com.supresong.wiki.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public CommonResp list(@Valid UserQueryReq req) {
        CommonResp<PageResp<UserQueryResp>> resp = new CommonResp<>();
        PageResp<UserQueryResp> list = userService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody UserSaveReq req) {
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        userService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        userService.delete(id);
        return resp;
    }

}
