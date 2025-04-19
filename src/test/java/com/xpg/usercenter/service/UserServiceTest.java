package com.xpg.usercenter.service;

import com.xpg.usercenter.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void addUser() {
        User user = new User();
        user.setUsername("xpgg");
        user.setUserAccount("xpgg123");
        user.setAvatarUrl("https://i-blog.csdnimg.cn/blog_migrate/890e8f7d9d8b01820ae54b67a715d9d3.png");
        user.setGender(0);
        user.setUserPassword("123456");
        user.setPhone("123");
        user.setEmail("456");

        boolean result = userService.save(user);

        Assertions.assertTrue(result);
    }

//    @Test
//    void userRegister() {
//        //test null
//        String userAcount = "xttttt";
//        String userPassword = "";
//        String checkPassword = "xssfasfsaf";
//        long result = userService.userRegister(userAcount, userPassword, checkPassword);
//        Assertions.assertEquals(-1,result);
//
//        //test length
//        userAcount = "xt";
//        userPassword = "12345678";
//        result = userService.userRegister(userAcount, userPassword, checkPassword);
//        Assertions.assertEquals(-1,result);
//
//        userAcount = "xttt";
//        userPassword = "1234567";
//        result = userService.userRegister(userAcount, userPassword, checkPassword);
//        Assertions.assertEquals(-1,result);
//
//        userAcount = "xpgg";
//        userPassword = "12345678";
//        result = userService.userRegister(userAcount, userPassword, checkPassword);
//        Assertions.assertEquals(-1,result);
//
//        userAcount = "xpg?";
//        result = userService.userRegister(userAcount, userPassword, checkPassword);
//        Assertions.assertEquals(-1,result);
//
//        userAcount = "xpgggg";
//        userPassword = "12345678";
//        checkPassword = userPassword;
//        result = userService.userRegister(userAcount, userPassword, checkPassword);
//        Assertions.assertEquals(-1,result);
//
//        checkPassword = "1260";
//        result = userService.userRegister(userAcount, userPassword, checkPassword);
//        Assertions.assertTrue(result>0);
//    }
}